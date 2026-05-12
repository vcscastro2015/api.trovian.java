--liquibase formatted sql
--changeset trovian:v012-pneu-peca-integracao

-- Corrige bug multi-tenant: unicidade de codigo por cliente, não global
ALTER TABLE peca DROP CONSTRAINT IF EXISTS peca_codigo_key;
ALTER TABLE peca ADD CONSTRAINT uq_peca_codigo_cliente UNIQUE (codigo, cliente_id);

-- Adiciona referência ao tipo de peça no pneu (nullable para dados existentes)
ALTER TABLE pneu
    ADD COLUMN peca_id BIGINT NULL,
    ADD CONSTRAINT fk_pneu_peca FOREIGN KEY (peca_id) REFERENCES peca(id) ON DELETE SET NULL;

CREATE INDEX idx_pneu_peca ON pneu(peca_id);
