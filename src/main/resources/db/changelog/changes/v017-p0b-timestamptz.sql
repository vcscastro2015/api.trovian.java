--liquibase formatted sql

-- =====================================================================
-- P0-B | timestamp -> timestamptz
--
-- POR QUE: timestamp sem timezone registra "parede de relogio". Se o
-- servidor mudar de fuso, ou se houver cliente em outro estado, o
-- instante deixa de ser inequivoco. Para evidencia de fraude usada em
-- demissao por justa causa ou processo trabalhista, timestamptz e a
-- unica escolha defensavel.
--
-- *** IMPACTO NO JAVA ***
-- Hibernate mapeia timestamptz para OffsetDateTime / Instant.
-- Se as entidades usam LocalDateTime, o mapeamento precisa mudar.
-- VALIDE EM HOMOLOGACAO ANTES DE APLICAR EM QUALQUER AMBIENTE VIVO.
--
-- Recomendacao para as entidades:
--   @Column(columnDefinition = "timestamptz")
--   private OffsetDateTime criadoEm;
-- e no application.yml:
--   spring.jpa.properties.hibernate.jdbc.time_zone: America/Sao_Paulo
--
-- NAO trata transmissao, transmissao_basica e telemetria (P0-C recria).
-- NAO trata databasechangelog nem databasechangeloglock: sao tabelas
-- internas do Liquibase e alterar o tipo delas quebra a ferramenta.
-- =====================================================================


-- changeset trovian:p0b-01-timestamptz-global splitStatements:false
-- comment: converte toda coluna timestamp without time zone do schema public, com excecoes.
DO $$
DECLARE
    r        record;
    tz       constant text := 'America/Sao_Paulo';
    excluidas constant text[] := ARRAY[
        'databasechangelog',      -- interna do Liquibase
        'databasechangeloglock',  -- interna do Liquibase
        'transmissao',            -- recriada no P0-C
        'transmissao_basica',     -- recriada no P0-C
        'telemetria'              -- recriada no P0-C
    ];
BEGIN
    FOR r IN
        SELECT c.table_name, c.column_name
        FROM   information_schema.columns c
        JOIN   information_schema.tables  t
               ON t.table_schema = c.table_schema
              AND t.table_name   = c.table_name
              AND t.table_type   = 'BASE TABLE'
        WHERE  c.table_schema = 'public'
          AND  c.data_type    = 'timestamp without time zone'
          AND  c.table_name  <> ALL (excluidas)
          AND  c.is_generated = 'NEVER'
        ORDER BY c.table_name, c.ordinal_position
    LOOP
        EXECUTE format(
            'ALTER TABLE public.%I ALTER COLUMN %I TYPE timestamptz USING %I AT TIME ZONE %L',
            r.table_name, r.column_name, r.column_name, tz
        );
        RAISE NOTICE 'convertido: %.% -> timestamptz', r.table_name, r.column_name;
    END LOOP;
END $$;
-- rollback SELECT 'reversao exige conversao coluna a coluna; restaure de backup' AS aviso;


-- changeset trovian:p0b-02-telemetria-data-processamento
-- comment: era time (so hora, sem data), o que torna o campo inutilizavel para auditoria.
ALTER TABLE public.telemetria DROP COLUMN IF EXISTS data_processamento;
ALTER TABLE public.telemetria ADD  COLUMN data_processamento timestamptz;
-- rollback ALTER TABLE public.telemetria DROP COLUMN data_processamento;
-- rollback ALTER TABLE public.telemetria ADD COLUMN data_processamento time;


-- changeset trovian:p0b-03-default-now
-- comment: defaults em now() garantem carimbo mesmo quando a aplicacao esquece.
ALTER TABLE public.abastecimento  ALTER COLUMN criado_em     SET DEFAULT now();
ALTER TABLE public.veiculo        ALTER COLUMN data_cadastro SET DEFAULT now();
ALTER TABLE public.motorista      ALTER COLUMN data_cadastro SET DEFAULT now();
ALTER TABLE public.hodometro      ALTER COLUMN data_cadastro SET DEFAULT now();
ALTER TABLE public.cliente        ALTER COLUMN data_cadastro SET DEFAULT now();
-- rollback ALTER TABLE public.cliente   ALTER COLUMN data_cadastro DROP DEFAULT;
-- rollback ALTER TABLE public.hodometro ALTER COLUMN data_cadastro DROP DEFAULT;
-- rollback ALTER TABLE public.motorista ALTER COLUMN data_cadastro DROP DEFAULT;
-- rollback ALTER TABLE public.veiculo   ALTER COLUMN data_cadastro DROP DEFAULT;
-- rollback ALTER TABLE public.abastecimento ALTER COLUMN criado_em DROP DEFAULT;


-- changeset trovian:p0b-04-timezone-banco
-- comment: fixa o fuso do banco. Sem isto, now() varia conforme a configuracao do container.
ALTER DATABASE trovian SET timezone TO 'America/Sao_Paulo';
-- rollback SELECT 1;

-- Conferencia apos aplicar:
--   SELECT table_name, column_name, data_type
--   FROM information_schema.columns
--   WHERE table_schema='public' AND data_type LIKE 'timestamp%'
--   ORDER BY data_type, table_name;
