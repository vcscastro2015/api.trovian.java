--liquibase formatted sql

-- =====================================================================
-- P0-D | Isolamento multi-tenant e Row Level Security
--
-- PROBLEMA: hoje convivem tres padroes.
--   1. cliente_id NOT NULL          (abastecimento, veiculo, motorista)
--   2. cliente_id NULL = global     (peca, fornecedor, modelo, rota...)
--   3. SEM coluna de cliente        (hodometro, coordenada, pneu_*...)
--
-- No grupo 3, toda consulta depende de o desenvolvedor lembrar de fazer
-- join ate uma tabela que tenha cliente_id. Um WHERE esquecido vaza
-- dado entre clientes. Em SaaS, esse e o risco numero um.
--
-- RLS e a unica defesa que nao depende de disciplina de codigo: a
-- politica e aplicada pelo banco, em toda consulta, sempre.
--
-- NULO EM COLUNA DE TENANT: substituido por cliente sentinela id = 0.
-- NULL em coluna de tenant e ambiguidade permanente ("e global ou
-- ficou sem preencher?") e RLS nao consegue expressar politica sobre
-- NULL de forma limpa.
-- =====================================================================


-- changeset trovian:p0d-01-cliente-sentinela
-- comment: cliente 0 representa registro global (catalogo compartilhado). Substitui cliente_id NULL.
INSERT INTO public.cliente (id, cnpj_cpf, nome, cooperado, status, data_cadastro, updated_at, uuid)
VALUES (0, '00000000000000', '[GLOBAL] Registros compartilhados', false, true,
        now(), now(), '00000000-0000-0000-0000-000000000000')
ON CONFLICT (id) DO NOTHING;

SELECT setval('public.cliente_id_seq', GREATEST((SELECT max(id) FROM public.cliente), 1));
-- rollback DELETE FROM public.cliente WHERE id = 0;


-- changeset trovian:p0d-02-cliente-id-onde-falta
-- comment: adiciona a coluna de tenant nas tabelas que nao tinham nenhuma.
ALTER TABLE public.hodometro               ADD COLUMN IF NOT EXISTS cliente_id bigint;
ALTER TABLE public.coordenada              ADD COLUMN IF NOT EXISTS cliente_id bigint;
ALTER TABLE public.alocacao_pneu           ADD COLUMN IF NOT EXISTS cliente_id bigint;
ALTER TABLE public.inspecao_pneu           ADD COLUMN IF NOT EXISTS cliente_id bigint;
ALTER TABLE public.recapagem_pneu          ADD COLUMN IF NOT EXISTS cliente_id bigint;
ALTER TABLE public.resposta_item_checklist ADD COLUMN IF NOT EXISTS cliente_id bigint;
ALTER TABLE public.item_modelo_checklist   ADD COLUMN IF NOT EXISTS cliente_id bigint;
ALTER TABLE public.item_manutencao         ADD COLUMN IF NOT EXISTS cliente_id bigint;
ALTER TABLE public.manutencao              ADD COLUMN IF NOT EXISTS cliente_id bigint;
ALTER TABLE public.ponto_rota              ADD COLUMN IF NOT EXISTS cliente_id bigint;
ALTER TABLE public.rota_pedagio            ADD COLUMN IF NOT EXISTS cliente_id bigint;
ALTER TABLE public.rota_segmento           ADD COLUMN IF NOT EXISTS cliente_id bigint;
ALTER TABLE public.rota_estatisticas       ADD COLUMN IF NOT EXISTS cliente_id bigint;
ALTER TABLE public.consumo_detalhado       ADD COLUMN IF NOT EXISTS cliente_id bigint;
ALTER TABLE public.abastecimento_registro  ADD COLUMN IF NOT EXISTS cliente_id bigint;
-- veiculo_midia_referencia NAO entra aqui: ja tem a coluna 'cliente'.
-- Politica propria em p0d-09b.
-- rollback SELECT 1;


-- changeset trovian:p0d-03-backfill-derivado splitStatements:false
-- comment: deriva o cliente pelo caminho natural de cada tabela. Base vazia, mas o script fica pronto para homologacao.
DO $$
BEGIN
    UPDATE public.hodometro h SET cliente_id = v.cliente
      FROM public.veiculo v WHERE v.id = h.veiculo AND h.cliente_id IS NULL;

    UPDATE public.coordenada c SET cliente_id = l.cliente_id
      FROM public."local" l WHERE l.id = c.local_id AND c.cliente_id IS NULL;

    UPDATE public.alocacao_pneu a SET cliente_id = p.cliente_id
      FROM public.pneu p WHERE p.id = a.pneu_id AND a.cliente_id IS NULL;

    UPDATE public.inspecao_pneu i SET cliente_id = p.cliente_id
      FROM public.pneu p WHERE p.id = i.pneu_id AND i.cliente_id IS NULL;

    UPDATE public.recapagem_pneu r SET cliente_id = p.cliente_id
      FROM public.pneu p WHERE p.id = r.pneu_id AND r.cliente_id IS NULL;

    UPDATE public.resposta_item_checklist ri SET cliente_id = cr.cliente_id
      FROM public.checklist_realizado cr
     WHERE cr.id = ri.checklist_realizado_id AND ri.cliente_id IS NULL;

    UPDATE public.item_modelo_checklist im SET cliente_id = mc.cliente_id
      FROM public.modelo_checklist mc
     WHERE mc.id = im.modelo_checklist_id AND im.cliente_id IS NULL;

    UPDATE public.item_manutencao im SET cliente_id = os.cliente_id
      FROM public.ordem_servico os
     WHERE os.id = im.ordem_servico_id AND im.cliente_id IS NULL;

    UPDATE public.manutencao m SET cliente_id = v.cliente
      FROM public.veiculo v WHERE v.id = m.veiculo_id AND m.cliente_id IS NULL;

    UPDATE public.ponto_rota pr SET cliente_id = r.cliente_id
      FROM public.rota r WHERE r.id = pr.rota_id AND pr.cliente_id IS NULL;

    UPDATE public.rota_pedagio rp SET cliente_id = r.cliente_id
      FROM public.rota r WHERE r.id = rp.rota_id AND rp.cliente_id IS NULL;

    UPDATE public.rota_segmento rs SET cliente_id = r.cliente_id
      FROM public.rota r WHERE r.id = rs.rota_id AND rs.cliente_id IS NULL;

    UPDATE public.rota_estatisticas re SET cliente_id = r.cliente_id
      FROM public.rota r WHERE r.id = re.rota_id AND re.cliente_id IS NULL;
END $$;
-- rollback SELECT 1;


-- changeset trovian:p0d-04-sentinela-nos-globais splitStatements:false
-- comment: NULL vira 0. Elimina a ambiguidade entre "global" e "esqueceram de preencher".
DO $$
DECLARE
    t text;
    tabelas constant text[] := ARRAY[
        'categoria_conta','centro_custo','forma_pagamento','fornecedor',
        'peca','modelo','movimentacao_estoque','alerta_manutencao',
        'ordem_servico','rota','equipamento','cooperativas','usuarios',
        -- CORRECAO: estas duas tem cliente_id NULLABLE e entram na lista
        -- de RLS do p0d-08. Sem NOT NULL aqui, as linhas com NULL ficam
        -- invisiveis para todo mundo depois que a politica subir.
        'checklist_realizado','conta_pagar'
    ];
BEGIN
    FOREACH t IN ARRAY tabelas LOOP
        EXECUTE format('UPDATE public.%I SET cliente_id = 0 WHERE cliente_id IS NULL', t);
        EXECUTE format('ALTER TABLE public.%I ALTER COLUMN cliente_id SET DEFAULT 0', t);
        EXECUTE format('ALTER TABLE public.%I ALTER COLUMN cliente_id SET NOT NULL', t);
    END LOOP;
END $$;
-- rollback SELECT 1;


-- changeset trovian:p0d-05-not-null-e-fk splitStatements:false
DO $$
DECLARE
    t text;
    tabelas constant text[] := ARRAY[
        'hodometro','coordenada','alocacao_pneu','inspecao_pneu',
        'recapagem_pneu','resposta_item_checklist','item_modelo_checklist',
        'item_manutencao','manutencao','ponto_rota','rota_pedagio',
        'rota_segmento','rota_estatisticas','abastecimento_registro'
    ];
BEGIN
    FOREACH t IN ARRAY tabelas LOOP
        EXECUTE format('UPDATE public.%I SET cliente_id = 0 WHERE cliente_id IS NULL', t);
        EXECUTE format('ALTER TABLE public.%I ALTER COLUMN cliente_id SET NOT NULL', t);
        EXECUTE format(
            'ALTER TABLE public.%I ADD CONSTRAINT fk_%s_cliente
             FOREIGN KEY (cliente_id) REFERENCES public.cliente(id)', t, t);
        EXECUTE format('CREATE INDEX IF NOT EXISTS idx_%s_cliente ON public.%I (cliente_id)', t, t);
    END LOOP;
END $$;
-- rollback SELECT 1;


-- =====================================================================
-- ROW LEVEL SECURITY
-- =====================================================================

-- changeset trovian:p0d-06-role-aplicacao
-- comment: o dono da tabela IGNORA RLS. A aplicacao precisa rodar com role proprio, que nao seja dono nem superusuario.
-- *** TROQUE A SENHA ANTES DE APLICAR ***
-- CORRECAO DE ORDEM: trovian_admin precisa existir ANTES do p0d-08,
-- que cria policy referenciando esse role. Os dois nascem aqui.
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'trovian_admin') THEN
        CREATE ROLE trovian_admin NOLOGIN;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'trovian_app') THEN
        CREATE ROLE trovian_app LOGIN PASSWORD 'TROQUE_ESTA_SENHA';
    END IF;
END $$;

GRANT USAGE ON SCHEMA public TO trovian_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES    IN SCHEMA public TO trovian_app;
GRANT USAGE, SELECT                  ON ALL SEQUENCES IN SCHEMA public TO trovian_app;
GRANT EXECUTE                        ON ALL FUNCTIONS IN SCHEMA public TO trovian_app;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO trovian_app;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT USAGE, SELECT ON SEQUENCES TO trovian_app;
-- rollback DROP OWNED BY trovian_app; DROP ROLE IF EXISTS trovian_app;


-- changeset trovian:p0d-07-funcao-tenant splitStatements:false
-- comment: le o tenant da sessao. STABLE para o planner poder otimizar.
CREATE OR REPLACE FUNCTION public.tenant_atual() RETURNS bigint
LANGUAGE sql STABLE AS $$
    SELECT nullif(current_setting('app.cliente_id', true), '')::bigint
$$;

COMMENT ON FUNCTION public.tenant_atual IS
    'Tenant da sessao. Definido por SET LOCAL app.cliente_id dentro da transacao. NUNCA por SET simples: com pool de conexao, SET vaza para a proxima requisicao.';
-- rollback DROP FUNCTION public.tenant_atual();


-- changeset trovian:p0d-08-habilitar-rls splitStatements:false
-- comment: FORCE garante que nem o dono escape. Politica permite o tenant corrente e os registros globais (cliente 0).
DO $$
DECLARE
    t text;
    tabelas constant text[] := ARRAY[
        'veiculo','motorista','abastecimento','abastecimento_registro',
        'hodometro','checklist_realizado','resposta_item_checklist',
        'conta_pagar','conta_receber','ordem_servico','manutencao',
        'pneu','alocacao_pneu','inspecao_pneu','recapagem_pneu',
        'rota','local','coordenada','viagem',
        'alerta_manutencao','movimentacao_estoque','comissao_motorista'
        -- veiculo e veiculo_midia_referencia ficam de fora: usam a
        -- coluna 'cliente', nao 'cliente_id'. Politicas em p0d-09/09b.
    ];
BEGIN
    FOREACH t IN ARRAY tabelas LOOP
        EXECUTE format('ALTER TABLE public.%I ENABLE ROW LEVEL SECURITY', t);
        EXECUTE format('ALTER TABLE public.%I FORCE  ROW LEVEL SECURITY', t);

        EXECUTE format('DROP POLICY IF EXISTS pol_tenant ON public.%I', t);
        EXECUTE format($f$
            CREATE POLICY pol_tenant ON public.%I
            USING      (cliente_id = public.tenant_atual() OR cliente_id = 0)
            WITH CHECK (cliente_id = public.tenant_atual())
        $f$, t);

        -- role de manutencao e relatorio, enxerga tudo
        EXECUTE format('DROP POLICY IF EXISTS pol_admin ON public.%I', t);
        EXECUTE format($f$
            CREATE POLICY pol_admin ON public.%I TO trovian_admin
            USING (true) WITH CHECK (true)
        $f$, t);
    END LOOP;
END $$;
-- rollback SELECT 1;

-- NOTA: veiculo usa a coluna 'cliente', nao 'cliente_id'. O laco acima
-- falha nela. Duas saidas: renomear a coluna (P2.1 da auditoria) ou
-- criar a politica de veiculo manualmente. Ver p0d-09.


-- changeset trovian:p0d-09-politica-veiculo
-- comment: veiculo tem a coluna 'cliente' em vez de 'cliente_id'. Politica escrita a parte ate a renomeacao do P2.
ALTER TABLE public.veiculo ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.veiculo FORCE  ROW LEVEL SECURITY;

DROP POLICY IF EXISTS pol_tenant ON public.veiculo;
CREATE POLICY pol_tenant ON public.veiculo
    USING      (cliente = public.tenant_atual() OR cliente = 0)
    WITH CHECK (cliente = public.tenant_atual());
-- rollback DROP POLICY IF EXISTS pol_tenant ON public.veiculo;
-- rollback ALTER TABLE public.veiculo DISABLE ROW LEVEL SECURITY;


-- changeset trovian:p0d-09b-politica-midia-referencia
-- comment: veiculo_midia_referencia tambem usa a coluna 'cliente'.
ALTER TABLE public.veiculo_midia_referencia ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.veiculo_midia_referencia FORCE  ROW LEVEL SECURITY;

DROP POLICY IF EXISTS pol_tenant ON public.veiculo_midia_referencia;
CREATE POLICY pol_tenant ON public.veiculo_midia_referencia
    USING      (cliente = public.tenant_atual() OR cliente = 0)
    WITH CHECK (cliente = public.tenant_atual());
-- rollback DROP POLICY IF EXISTS pol_tenant ON public.veiculo_midia_referencia;
-- rollback ALTER TABLE public.veiculo_midia_referencia DISABLE ROW LEVEL SECURITY;


-- changeset trovian:p0d-10-vinculo-admin
-- comment: trovian_admin ja foi criado em p0d-06. Aqui so o vinculo.
-- ATENCAO: este GRANT faz a aplicacao ENXERGAR TODOS OS CLIENTES,
-- porque trovian_admin tem policy USING (true). Isso anula o RLS.
-- Mantenha comentado ate ter certeza de que quer esse comportamento.
-- Em producao, o correto e a aplicacao NAO herdar trovian_admin, e
-- esse role ficar so para jobs de relatorio consolidado e manutencao.
-- GRANT trovian_admin TO trovian_app;
SELECT 1;
-- rollback SELECT 1;


-- =====================================================================
-- *** IMPLEMENTACAO NO SPRING: O PONTO MAIS DELICADO ***
--
-- Com HikariCP, a conexao e reutilizada entre requisicoes. Se o tenant
-- for definido com SET (nivel de sessao), ele PERMANECE na conexao e
-- vaza para a proxima requisicao, possivelmente de outro cliente.
-- Isso e pior que nao ter RLS, porque cria falsa seguranca.
--
-- USE SEMPRE "SET LOCAL", que morre no fim da transacao:
--
--   @Component
--   public class TenantConnectionInterceptor {
--       @Transactional
--       public void aplicar(Long clienteId) {
--           entityManager.createNativeQuery(
--               "SELECT set_config('app.cliente_id', :id, true)")  // true = LOCAL
--               .setParameter("id", String.valueOf(clienteId))
--               .getSingleResult();
--       }
--   }
--
-- Chame no inicio de toda transacao, via AOP ou um
-- TransactionSynchronization. Sem tenant definido, tenant_atual()
-- devolve NULL e a politica nao retorna nada: falha fechada, que e o
-- comportamento correto.
--
-- TESTE OBRIGATORIO antes de considerar pronto:
--   SET LOCAL app.cliente_id = '1';
--   SELECT count(*) FROM veiculo;          -- so do cliente 1
--   SET LOCAL app.cliente_id = '2';
--   SELECT count(*) FROM veiculo;          -- so do cliente 2
--   RESET app.cliente_id;
--   SELECT count(*) FROM veiculo;          -- zero
--
-- O Liquibase deve continuar rodando com o usuario DONO das tabelas,
-- nao com trovian_app, senao as migrations esbarram na propria RLS.
-- =====================================================================
