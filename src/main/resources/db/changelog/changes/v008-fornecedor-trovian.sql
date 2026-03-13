--liquibase formatted sql

--changeset trovian:v008-fornecedor-trovian
INSERT INTO fornecedor (razao_social, status, data_cadastro, updated_at, cliente_id)
VALUES ('Cliente Trovian', true, CURRENT_DATE, NOW(), 1);
