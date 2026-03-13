--liquibase formatted sql

--changeset trovian:v009-billing-melhorias
ALTER TABLE cliente_plano ADD COLUMN data_final DATE;

ALTER TABLE conta_receber ADD COLUMN cliente_plano_id BIGINT;
ALTER TABLE conta_receber ADD CONSTRAINT fk_conta_receber_cliente_plano
    FOREIGN KEY (cliente_plano_id) REFERENCES cliente_plano(id);
