--liquibase formatted sql

--changeset trovian:v007-categoria-assinatura
INSERT INTO categoria_conta (nome, descricao, tipo, codigo, status, pode_editar, data_cadastro, updated_at, cliente_id)
SELECT '002-ASSINATURA',
       'Assinatura de Plano SaaS',
       'RECEBER',
       '002-ASSINATURA',
       false,
       false,
       CURRENT_DATE,
       NOW(),
       id
FROM cliente
ORDER BY id
LIMIT 1;
