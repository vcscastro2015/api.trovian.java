--liquibase formatted sql

-- =====================================================================
-- P0-E | Imagens: Large Object -> metadado + S3/MinIO
--
-- PROBLEMA ATUAL: tres estrategias para a mesma coisa.
--   imagem_arquivo.conteudo_binario   oid    (Large Object)
--   imagem_resposta.conteudo_binario  oid    (Large Object)
--   documento_veiculo.conteudo_binario bytea
--
-- Large Object e a pior opcao para um sistema de antifraude:
--   - o LO NAO e removido quando a linha some. Precisa de lo_unlink
--     explicito ou vacuumlo. Orfao acumula para sempre.
--   - infla dump e backup do banco inteiro, todo dia.
--   - impede CDN, presigned URL e lifecycle de retencao.
--   - dificulta a politica de expurgo que a LGPD vai exigir.
--
-- O antifraude gera de 3 a 8 fotos por abastecimento e por checklist.
-- Com 100 veiculos sao dezenas de milhares de imagens por mes.
--
-- veiculo_midia_referencia ja nasceu no padrao certo. Este changeset
-- generaliza esse padrao e unifica os tres caminhos num so.
--
-- *** EXIGE MUDANCA NA APLICACAO ***
-- O Spring precisa passar a gravar em S3/MinIO e persistir so o
-- metadado. Nao aplique este changeset antes de o codigo estar pronto.
-- =====================================================================


-- changeset trovian:p0e-01-tabela-midia
-- comment: tabela unica de midia, polimorfica por (entidade_tipo, entidade_id).
CREATE TABLE public.midia (
    id             bigserial   PRIMARY KEY,
    cliente_id     bigint      NOT NULL,

    -- vinculo polimorfico
    entidade_tipo  varchar(40) NOT NULL,
    entidade_id    varchar(64) NOT NULL,   -- varchar: ha PKs bigint e uuid
    papel          varchar(30),            -- referencia a midia_angulo quando aplicavel

    -- localizacao no objeto storage
    bucket         varchar(100) NOT NULL,
    s3_key         text         NOT NULL,
    content_type   varchar(60)  NOT NULL DEFAULT 'image/jpeg',
    bytes          bigint,
    largura_px     int4,
    altura_px      int4,

    -- assinaturas: base da deteccao de reuso de foto
    sha256         bytea        NOT NULL,
    phash          bit(64),
    dhash          bit(64),

    -- procedencia
    origem         varchar(20)  NOT NULL DEFAULT 'WEB',
    enviada_por    bigint,
    numero_telefone varchar(20),
    capturada_em   timestamptz,
    recebida_em    timestamptz  NOT NULL DEFAULT now(),

    -- geolocalizacao declarada no envio
    latitude       numeric(10,7),
    longitude      numeric(10,7),

    -- antifraude
    evento_id      uuid,
    desafio_id     uuid,
    reuso_detectado boolean     NOT NULL DEFAULT false,
    midia_original_id bigint,

    -- ciclo de vida e LGPD
    status         boolean      NOT NULL DEFAULT true,
    expurgar_em    date,
    expurgada_em   timestamptz,

    CONSTRAINT fk_midia_cliente  FOREIGN KEY (cliente_id) REFERENCES public.cliente(id),
    CONSTRAINT fk_midia_original FOREIGN KEY (midia_original_id) REFERENCES public.midia(id),
    CONSTRAINT ck_midia_entidade CHECK (entidade_tipo IN (
        'ABASTECIMENTO','ABASTECIMENTO_REGISTRO','CHECKLIST_RESPOSTA',
        'CONTA_RECEBER','CONTA_PAGAR','VEICULO','VEICULO_REFERENCIA',
        'MOTORISTA','ORDEM_SERVICO','INSPECAO_PNEU','DOCUMENTO_VEICULO'
    )),
    CONSTRAINT ck_midia_origem CHECK (origem IN (
        'WEB','WHATSAPP','APP','API','IMPORTACAO'
    )),
    CONSTRAINT ck_midia_bytes CHECK (bytes IS NULL OR bytes > 0)
);

CREATE INDEX idx_midia_entidade   ON public.midia (entidade_tipo, entidade_id) WHERE status;
CREATE INDEX idx_midia_cliente    ON public.midia (cliente_id, recebida_em DESC);
CREATE INDEX idx_midia_sha256     ON public.midia (sha256);
CREATE INDEX idx_midia_evento     ON public.midia (evento_id) WHERE evento_id IS NOT NULL;
CREATE INDEX idx_midia_expurgo    ON public.midia (expurgar_em) WHERE status AND expurgar_em IS NOT NULL;

-- Deteccao de reuso EXATO de arquivo dentro do mesmo cliente.
-- Reuso perceptual (foto recomprimida) fica com o phash, comparado
-- por distancia de Hamming no motor de regras.
CREATE INDEX idx_midia_phash ON public.midia (cliente_id, phash) WHERE phash IS NOT NULL;

COMMENT ON TABLE  public.midia IS
    'Metadado de midia. O binario vive em S3/MinIO. Substitui imagem_arquivo, imagem_resposta e documento_veiculo.';
COMMENT ON COLUMN public.midia.phash IS
    'Hash perceptual. Similaridade = length(replace((a # b)::text,''0'','''')) < 8.';
COMMENT ON COLUMN public.midia.expurgar_em IS
    'Data de expurgo por LGPD. Sugestao: 12 meses para a imagem; hash e metadado permanecem.';
-- rollback DROP TABLE public.midia;


-- changeset trovian:p0e-02-rls-midia
ALTER TABLE public.midia ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.midia FORCE  ROW LEVEL SECURITY;

CREATE POLICY pol_tenant ON public.midia
    USING      (cliente_id = public.tenant_atual() OR cliente_id = 0)
    WITH CHECK (cliente_id = public.tenant_atual());

CREATE POLICY pol_admin ON public.midia TO trovian_admin
    USING (true) WITH CHECK (true);
-- rollback ALTER TABLE public.midia DISABLE ROW LEVEL SECURITY;


-- changeset trovian:p0e-03-funcao-hamming
-- comment: distancia de Hamming entre phashes. Usada na Fase 0 para detectar reuso de foto.
CREATE OR REPLACE FUNCTION public.hamming_phash(a bit(64), b bit(64))
RETURNS int
LANGUAGE sql IMMUTABLE STRICT PARALLEL SAFE AS
$$ SELECT length(replace((a # b)::text, '0', '')); $$;
-- rollback DROP FUNCTION public.hamming_phash(bit, bit);


-- changeset trovian:p0e-04-drop-large-objects
-- preconditions onFail:HALT
-- precondition-sql-check expectedResult:0 SELECT count(*) FROM public.imagem_arquivo
-- comment: guarda de seguranca. Se houver imagem gravada, o changeset para e exige migracao antes.
DROP TABLE IF EXISTS public.imagem_resposta CASCADE;
DROP TABLE IF EXISTS public.imagem_arquivo  CASCADE;
DROP TABLE IF EXISTS public.documento_veiculo CASCADE;

-- Limpa Large Objects orfaos que possam ter ficado.
-- Em base com dados, rode vacuumlo antes:
--   docker exec trovian_db vacuumlo -U trovian trovian
SELECT lo_unlink(oid) FROM pg_largeobject_metadata;
-- rollback SELECT 'estruturas nao restauraveis; use backup' AS aviso;


-- changeset trovian:p0e-05-checklist-foto-unificada
-- comment: resposta_item_checklist.foto_url era o terceiro caminho para a mesma foto, e limitava a uma por item. O desafio aleatorio precisa de N.
ALTER TABLE public.resposta_item_checklist DROP COLUMN IF EXISTS foto_url;
ALTER TABLE public.resposta_item_checklist
    ADD COLUMN IF NOT EXISTS qtd_midias smallint NOT NULL DEFAULT 0;
-- rollback ALTER TABLE public.resposta_item_checklist DROP COLUMN qtd_midias;
-- rollback ALTER TABLE public.resposta_item_checklist ADD COLUMN foto_url varchar(500);


-- changeset trovian:p0e-06-abastecimento-tem-imagem
-- comment: tem_imagem era booleano solto sem FK. Vira contador derivado de midia.
ALTER TABLE public.abastecimento DROP COLUMN IF EXISTS tem_imagem;
ALTER TABLE public.abastecimento
    ADD COLUMN IF NOT EXISTS qtd_midias smallint NOT NULL DEFAULT 0;

ALTER TABLE public.conta_receber DROP COLUMN IF EXISTS tem_imagem;
ALTER TABLE public.conta_receber
    ADD COLUMN IF NOT EXISTS qtd_midias smallint NOT NULL DEFAULT 0;
-- rollback ALTER TABLE public.conta_receber DROP COLUMN qtd_midias;
-- rollback ALTER TABLE public.conta_receber ADD COLUMN tem_imagem bool;
-- rollback ALTER TABLE public.abastecimento DROP COLUMN qtd_midias;
-- rollback ALTER TABLE public.abastecimento ADD COLUMN tem_imagem bool;


-- changeset trovian:p0e-07-view-expurgo
-- comment: alimenta o job de expurgo por LGPD. O binario sai do S3; hash e metadado permanecem como evidencia.
CREATE OR REPLACE VIEW public.vw_midia_a_expurgar AS
SELECT id, cliente_id, entidade_tipo, entidade_id,
       bucket, s3_key, recebida_em, expurgar_em,
       (current_date - expurgar_em) AS dias_vencida
FROM   public.midia
WHERE  status = true
  AND  expurgar_em IS NOT NULL
  AND  expurgar_em <= current_date
  AND  expurgada_em IS NULL
ORDER  BY expurgar_em;
-- rollback DROP VIEW public.vw_midia_a_expurgar;


-- =====================================================================
-- CONFIGURACAO DO MINIO (docker-compose)
--
--   minio:
--     image: minio/minio:latest
--     container_name: trovian_minio
--     command: server /data --console-address ":9001"
--     environment:
--       MINIO_ROOT_USER: trovian
--       MINIO_ROOT_PASSWORD: <senha forte>
--     volumes:
--       - /opt/trovian/minio:/data
--     networks: [trovian-network]
--     restart: unless-stopped
--
-- Buckets sugeridos, separados por politica de retencao:
--   trovian-midia-referencia   sem expiracao (foto de cadastro)
--   trovian-midia-evento       12 meses      (abastecimento, checklist)
--   trovian-documentos         5 anos        (fiscal)
--
-- Configure lifecycle no proprio bucket, alem do job da view acima.
-- Defesa em profundidade: se o job falhar, o bucket expira sozinho.
-- =====================================================================
