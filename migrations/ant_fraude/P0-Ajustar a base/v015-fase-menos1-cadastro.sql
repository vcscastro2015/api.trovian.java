--liquibase formatted sql

-- =====================================================================
-- v015 | Fase -1: cadastro para antifraude
--
-- POR QUE ESTE ARQUIVO EXISTE
-- As alteracoes abaixo foram aplicadas MANUALMENTE na base dev, fora
-- do Liquibase. Isso e drift: o databasechangelog nao sabe delas, e
-- uma base criada do zero nao as teria. Os changesets v016+ dependem
-- dessas estruturas e falhariam em qualquer ambiente novo.
--
-- Tudo aqui e IDEMPOTENTE (IF NOT EXISTS / DROP IF EXISTS + ADD).
-- Na base dev atual passa sem efeito. Em base limpa, cria tudo.
-- =====================================================================


-- changeset trovian:v015-01-veiculo-medicao
ALTER TABLE public.veiculo
    ADD COLUMN IF NOT EXISTS unidade_medicao          varchar(10) NOT NULL DEFAULT 'KM',
    ADD COLUMN IF NOT EXISTS hodometro_tipo           varchar(20),
    ADD COLUMN IF NOT EXISTS hodometro_digitos        smallint    NOT NULL DEFAULT 6,
    ADD COLUMN IF NOT EXISTS hodometro_casas_decimais smallint    NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS hodometro_unidade        varchar(10) NOT NULL DEFAULT 'KM';

ALTER TABLE public.veiculo DROP CONSTRAINT IF EXISTS ck_veiculo_unidade_medicao;
ALTER TABLE public.veiculo ADD  CONSTRAINT ck_veiculo_unidade_medicao
    CHECK (unidade_medicao IN ('KM','HORA','AMBOS'));

ALTER TABLE public.veiculo DROP CONSTRAINT IF EXISTS ck_veiculo_hodometro_tipo;
ALTER TABLE public.veiculo ADD  CONSTRAINT ck_veiculo_hodometro_tipo
    CHECK (hodometro_tipo IS NULL
           OR hodometro_tipo IN ('ANALOGICO','DIGITAL_LCD','DIGITAL_7SEG'));

ALTER TABLE public.veiculo DROP CONSTRAINT IF EXISTS ck_veiculo_hodometro_digitos;
ALTER TABLE public.veiculo ADD  CONSTRAINT ck_veiculo_hodometro_digitos
    CHECK (hodometro_digitos BETWEEN 4 AND 9);

ALTER TABLE public.veiculo DROP CONSTRAINT IF EXISTS ck_veiculo_hodometro_decimais;
ALTER TABLE public.veiculo ADD  CONSTRAINT ck_veiculo_hodometro_decimais
    CHECK (hodometro_casas_decimais BETWEEN 0 AND 2);

ALTER TABLE public.veiculo DROP CONSTRAINT IF EXISTS ck_veiculo_hodometro_unidade;
ALTER TABLE public.veiculo ADD  CONSTRAINT ck_veiculo_hodometro_unidade
    CHECK (hodometro_unidade IN ('KM','MILHA'));
-- rollback ALTER TABLE public.veiculo DROP COLUMN IF EXISTS hodometro_unidade, DROP COLUMN IF EXISTS hodometro_casas_decimais, DROP COLUMN IF EXISTS hodometro_digitos, DROP COLUMN IF EXISTS hodometro_tipo, DROP COLUMN IF EXISTS unidade_medicao;


-- changeset trovian:v015-02-veiculo-rastreador
-- comment: calibracao da leitura do rastreador. CAN erra ~0,5%, GPS acumulado erra de 2 a 5%.
ALTER TABLE public.veiculo
    ADD COLUMN IF NOT EXISTS hodometro_fonte_rastreador varchar(20),
    ADD COLUMN IF NOT EXISTS hodometro_offset           numeric(12,2) NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS hodometro_fator_correcao   numeric(6,4)  NOT NULL DEFAULT 1.0,
    ADD COLUMN IF NOT EXISTS hodometro_tolerancia_pct   numeric(5,2),
    ADD COLUMN IF NOT EXISTS hodometro_calibrado_em     date,
    ADD COLUMN IF NOT EXISTS hodometro_calibrado_por    int8,
    ADD COLUMN IF NOT EXISTS hodometro_inicial_frota    numeric(12,2),
    ADD COLUMN IF NOT EXISTS horimetro_inicial_frota    numeric(12,2),
    ADD COLUMN IF NOT EXISTS data_aquisicao             date,
    ADD COLUMN IF NOT EXISTS data_desmobilizacao        date;

ALTER TABLE public.veiculo DROP CONSTRAINT IF EXISTS ck_veiculo_fonte_rastreador;
ALTER TABLE public.veiculo ADD  CONSTRAINT ck_veiculo_fonte_rastreador
    CHECK (hodometro_fonte_rastreador IS NULL
           OR hodometro_fonte_rastreador IN ('CAN','GPS_ACUMULADO','PULSO','NENHUM'));

ALTER TABLE public.veiculo DROP CONSTRAINT IF EXISTS ck_veiculo_fator_correcao;
ALTER TABLE public.veiculo ADD  CONSTRAINT ck_veiculo_fator_correcao
    CHECK (hodometro_fator_correcao BETWEEN 0.5 AND 2.0);

ALTER TABLE public.veiculo DROP CONSTRAINT IF EXISTS ck_veiculo_tolerancia_pct;
ALTER TABLE public.veiculo ADD  CONSTRAINT ck_veiculo_tolerancia_pct
    CHECK (hodometro_tolerancia_pct IS NULL
           OR hodometro_tolerancia_pct BETWEEN 0 AND 50);
-- rollback SELECT 1;


-- changeset trovian:v015-03-veiculo-tanques
-- comment: capacidade_tanque unico quebrava VOLUME_ACIMA_TANQUE em cavalo mecanico com dois tanques.
ALTER TABLE public.veiculo
    ADD COLUMN IF NOT EXISTS capacidade_tanque_2    numeric(10,2),
    ADD COLUMN IF NOT EXISTS capacidade_arla        numeric(10,2),
    ADD COLUMN IF NOT EXISTS combustivel_secundario varchar(10),
    ADD COLUMN IF NOT EXISTS consumo_referencia_min numeric(8,3),
    ADD COLUMN IF NOT EXISTS consumo_referencia_max numeric(8,3);

ALTER TABLE public.veiculo DROP CONSTRAINT IF EXISTS ck_veiculo_tanques_positivos;
ALTER TABLE public.veiculo ADD  CONSTRAINT ck_veiculo_tanques_positivos
    CHECK ( (capacidade_tanque   IS NULL OR capacidade_tanque   > 0)
        AND (capacidade_tanque_2 IS NULL OR capacidade_tanque_2 > 0)
        AND (capacidade_arla     IS NULL OR capacidade_arla     > 0) );

ALTER TABLE public.veiculo DROP CONSTRAINT IF EXISTS ck_veiculo_consumo_referencia;
ALTER TABLE public.veiculo ADD  CONSTRAINT ck_veiculo_consumo_referencia
    CHECK (consumo_referencia_min IS NULL OR consumo_referencia_max IS NULL
           OR consumo_referencia_min <= consumo_referencia_max);

ALTER TABLE public.veiculo
    ADD COLUMN IF NOT EXISTS capacidade_tanque_total numeric(10,2)
    GENERATED ALWAYS AS (
        coalesce(capacidade_tanque, 0) + coalesce(capacidade_tanque_2, 0)
    ) STORED;
-- rollback SELECT 1;


-- changeset trovian:v015-04-veiculo-identificacao
-- comment: numero_frota e mais legivel em OCR que a placa e distingue veiculos identicos da mesma frota.
ALTER TABLE public.veiculo
    ADD COLUMN IF NOT EXISTS numero_frota         varchar(20),
    ADD COLUMN IF NOT EXISTS vinculo              varchar(15) NOT NULL DEFAULT 'PROPRIO',
    ADD COLUMN IF NOT EXISTS perfil_risco         varchar(10) NOT NULL DEFAULT 'PADRAO',
    ADD COLUMN IF NOT EXISTS antifraude_ativo     boolean     NOT NULL DEFAULT true,
    ADD COLUMN IF NOT EXISTS antifraude_modo      varchar(10) NOT NULL DEFAULT 'SOMBRA',
    ADD COLUMN IF NOT EXISTS cadastro_completo_em timestamptz;

ALTER TABLE public.veiculo DROP CONSTRAINT IF EXISTS ck_veiculo_vinculo;
ALTER TABLE public.veiculo ADD  CONSTRAINT ck_veiculo_vinculo
    CHECK (vinculo IN ('PROPRIO','AGREGADO','TERCEIRO','LOCADO'));

ALTER TABLE public.veiculo DROP CONSTRAINT IF EXISTS ck_veiculo_perfil_risco;
ALTER TABLE public.veiculo ADD  CONSTRAINT ck_veiculo_perfil_risco
    CHECK (perfil_risco IN ('BAIXO','PADRAO','ALTO','CRITICO'));

ALTER TABLE public.veiculo DROP CONSTRAINT IF EXISTS ck_veiculo_antifraude_modo;
ALTER TABLE public.veiculo ADD  CONSTRAINT ck_veiculo_antifraude_modo
    CHECK (antifraude_modo IN ('DESLIGADO','SOMBRA','ATIVO'));
-- rollback SELECT 1;


-- changeset trovian:v015-05-veiculo-placa-normalizada
-- comment: placa bruta admite ABC-1234, ABC1234 e abc 1234. O OCR precisa de um alvo unico de comparacao.
ALTER TABLE public.veiculo
    ADD COLUMN IF NOT EXISTS placa_normalizada varchar(10)
    GENERATED ALWAYS AS (
        upper(regexp_replace(placa, '[^A-Za-z0-9]', '', 'g'))
    ) STORED;

ALTER TABLE public.veiculo DROP CONSTRAINT IF EXISTS ck_veiculo_placa_formato;
ALTER TABLE public.veiculo ADD  CONSTRAINT ck_veiculo_placa_formato
    CHECK (placa_normalizada ~ '^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$') NOT VALID;

CREATE INDEX        IF NOT EXISTS idx_veiculo_placa_norm     ON public.veiculo (placa_normalizada);
CREATE INDEX        IF NOT EXISTS idx_veiculo_cliente_status ON public.veiculo (cliente, status);
CREATE UNIQUE INDEX IF NOT EXISTS uk_veiculo_placa_cliente   ON public.veiculo (cliente, placa_normalizada) WHERE status = true;
CREATE UNIQUE INDEX IF NOT EXISTS uk_veiculo_numero_frota    ON public.veiculo (cliente, numero_frota)      WHERE numero_frota IS NOT NULL AND status = true;
CREATE UNIQUE INDEX IF NOT EXISTS uk_veiculo_chassi          ON public.veiculo (upper(chassi))              WHERE chassi IS NOT NULL AND chassi <> '' AND status = true;
-- rollback SELECT 1;


-- changeset trovian:v015-06-midia-angulo
CREATE TABLE IF NOT EXISTS public.midia_angulo (
    codigo        varchar(30) PRIMARY KEY,
    descricao     text        NOT NULL,
    instrucao_app text,
    sorteavel     boolean  NOT NULL DEFAULT true,
    obrigatorio   boolean  NOT NULL DEFAULT false,
    ordem         smallint NOT NULL DEFAULT 100
);

INSERT INTO public.midia_angulo (codigo, descricao, instrucao_app, sorteavel, obrigatorio, ordem)
VALUES
 ('FRONTAL',      'Frente do veiculo com placa visivel', 'Tire uma foto da FRENTE do veiculo mostrando a placa inteira.',  true,  true,  10),
 ('TRASEIRA',     'Traseira com placa visivel',          'Tire uma foto da TRASEIRA do veiculo mostrando a placa inteira.', true, true,  20),
 ('LATERAL_ESQ',  'Lateral esquerda completa',           'Fotografe a LATERAL ESQUERDA inteira.',                          true,  true,  30),
 ('LATERAL_DIR',  'Lateral direita completa',            'Fotografe a LATERAL DIREITA inteira.',                           true,  true,  40),
 ('PAINEL',       'Painel com ignicao ligada',           'Ligue a ignicao e fotografe o PAINEL inteiro.',                   true,  true,  50),
 ('HODOMETRO',    'Recorte do hodometro',                'Aproxime e fotografe SO o hodometro, com os numeros legiveis.',   true,  true,  60),
 ('BOCAL_TANQUE', 'Bocal do tanque',                     'Fotografe o BOCAL do tanque aberto.',                             true,  false, 70),
 ('NUMERO_FROTA', 'Numero de frota na lateral',          'Fotografe o NUMERO DE FROTA pintado no veiculo.',                 true,  false, 80),
 ('CHASSI',       'Etiqueta do chassi',                  'Fotografe a etiqueta do CHASSI.',                                 false, false, 90),
 ('CRLV',         'Documento do veiculo',                'Fotografe o CRLV do veiculo.',                                    false, false, 100),
 ('BOMBA',        'Visor da bomba',                      'Fotografe o VISOR DA BOMBA mostrando litros e valor.',            true,  false, 110),
 ('CUPOM',        'Cupom fiscal',                        'Fotografe o CUPOM do abastecimento.',                             true,  false, 120)
ON CONFLICT (codigo) DO NOTHING;
-- rollback DROP TABLE IF EXISTS public.midia_angulo;


-- changeset trovian:v015-07-veiculo-midia-referencia
CREATE TABLE IF NOT EXISTS public.veiculo_midia_referencia (
    id               bigserial   PRIMARY KEY,
    veiculo          int8        NOT NULL,
    cliente          int8        NOT NULL,
    angulo           varchar(30) NOT NULL,
    bucket           varchar(100) NOT NULL,
    s3_key           text         NOT NULL,
    content_type     varchar(50)  NOT NULL DEFAULT 'image/jpeg',
    bytes            int4,
    largura_px       int4,
    altura_px        int4,
    sha256           bytea        NOT NULL,
    phash            bit(64),
    dhash            bit(64),
    embedding_versao varchar(20),
    principal        boolean     NOT NULL DEFAULT false,
    status           boolean     NOT NULL DEFAULT true,
    capturada_em     timestamptz NOT NULL DEFAULT now(),
    capturada_por    int8,
    origem           varchar(20) NOT NULL DEFAULT 'CADASTRO',
    valida_ate       date,
    substituida_por  int8,
    motivo_baixa     text,
    observacao       text,
    CONSTRAINT fk_midia_ref_veiculo    FOREIGN KEY (veiculo) REFERENCES public.veiculo(id),
    CONSTRAINT fk_midia_ref_angulo     FOREIGN KEY (angulo)  REFERENCES public.midia_angulo(codigo),
    CONSTRAINT fk_midia_ref_substituida FOREIGN KEY (substituida_por) REFERENCES public.veiculo_midia_referencia(id),
    CONSTRAINT ck_midia_ref_origem CHECK (origem IN ('CADASTRO','RECADASTRO','CHECKLIST','VISTORIA','MANUTENCAO')),
    CONSTRAINT ck_midia_ref_bytes  CHECK (bytes IS NULL OR bytes > 0)
);

-- indice PARCIAL: um UNIQUE em (veiculo, angulo, principal) limitaria
-- a UMA foto nao-principal por angulo, o que esta errado.
CREATE UNIQUE INDEX IF NOT EXISTS uk_midia_ref_principal
    ON public.veiculo_midia_referencia (veiculo, angulo) WHERE principal = true AND status = true;
CREATE INDEX IF NOT EXISTS idx_midia_ref_veiculo  ON public.veiculo_midia_referencia (veiculo, angulo) WHERE status = true;
CREATE INDEX IF NOT EXISTS idx_midia_ref_sha256   ON public.veiculo_midia_referencia (sha256);
CREATE INDEX IF NOT EXISTS idx_midia_ref_cliente  ON public.veiculo_midia_referencia (cliente, status);
CREATE INDEX IF NOT EXISTS idx_midia_ref_validade ON public.veiculo_midia_referencia (valida_ate) WHERE status = true AND valida_ate IS NOT NULL;
-- rollback DROP TABLE IF EXISTS public.veiculo_midia_referencia;


-- changeset trovian:v015-08-hodometro-antifraude
-- comment: hodometro_rastreador ja existia e nao era usado. E a segunda fonte que o motorista nao controla.
ALTER TABLE public.hodometro
    ADD COLUMN IF NOT EXISTS confiavel             boolean NOT NULL DEFAULT true,
    ADD COLUMN IF NOT EXISTS origem_evento_id      uuid,
    ADD COLUMN IF NOT EXISTS origem_sistema        varchar(20),
    ADD COLUMN IF NOT EXISTS hodometro_normalizado numeric(12,2),
    ADD COLUMN IF NOT EXISTS divergencia_km        numeric(12,2),
    ADD COLUMN IF NOT EXISTS divergencia_pct       numeric(8,3),
    ADD COLUMN IF NOT EXISTS horimetro             numeric(12,2),
    ADD COLUMN IF NOT EXISTS horimetro_rastreador  numeric(12,2),
    ADD COLUMN IF NOT EXISTS latitude              numeric(10,7),
    ADD COLUMN IF NOT EXISTS longitude             numeric(10,7);

ALTER TABLE public.hodometro DROP CONSTRAINT IF EXISTS ck_hodometro_origem_sistema;
ALTER TABLE public.hodometro ADD  CONSTRAINT ck_hodometro_origem_sistema
    CHECK (origem_sistema IS NULL
           OR origem_sistema IN ('WEB','WHATSAPP','APP','RASTREADOR','IMPORTACAO','API'));

ALTER TABLE public.hodometro DROP CONSTRAINT IF EXISTS ck_hodometro_tipo;
ALTER TABLE public.hodometro ADD  CONSTRAINT ck_hodometro_tipo
    CHECK (tipo IS NULL OR tipo IN ('MANUAL','RASTREADOR','ABASTECIMENTO',
                                    'CHECKLIST','MANUTENCAO','AJUSTE','IMPORTACAO')) NOT VALID;

ALTER TABLE public.hodometro DROP CONSTRAINT IF EXISTS ck_hodometro_positivo;
ALTER TABLE public.hodometro ADD  CONSTRAINT ck_hodometro_positivo
    CHECK (hodometro >= 0 AND (hodometro_rastreador IS NULL OR hodometro_rastreador >= 0)) NOT VALID;

CREATE INDEX IF NOT EXISTS idx_hodometro_veiculo_data    ON public.hodometro (veiculo, data_cadastro DESC);
CREATE INDEX IF NOT EXISTS idx_hodometro_baseline        ON public.hodometro (veiculo, data_cadastro DESC) WHERE confiavel = true AND veiculo IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_hodometro_divergencia     ON public.hodometro (divergencia_pct DESC) WHERE divergencia_pct IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_hodometro_evento          ON public.hodometro (origem_evento_id) WHERE origem_evento_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_hodometro_motorista_data  ON public.hodometro (id_motorista, data_cadastro DESC) WHERE id_motorista IS NOT NULL;
-- rollback SELECT 1;


-- changeset trovian:v015-09-funcao-hamming
-- comment: distancia de Hamming entre phashes. Base da deteccao de reuso de foto, a fraude mais comum em campo.
CREATE OR REPLACE FUNCTION public.hamming_phash(a bit(64), b bit(64))
RETURNS int LANGUAGE sql IMMUTABLE STRICT PARALLEL SAFE AS
$$ SELECT length(replace((a # b)::text, '0', '')); $$;
-- rollback DROP FUNCTION IF EXISTS public.hamming_phash(bit, bit);
