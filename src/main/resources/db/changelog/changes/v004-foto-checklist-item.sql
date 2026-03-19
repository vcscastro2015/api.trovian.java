--liquibase formatted sql

--changeset trovian:v004-imagem-resposta
--comment: Cria tabela para armazenar imagens das respostas do checklist realizado
CREATE TABLE imagem_resposta (
    id          BIGSERIAL PRIMARY KEY,
    resposta_item_checklist_id UUID NOT NULL UNIQUE,
    conteudo_binario           BYTEA NOT NULL,
    content_type               VARCHAR(100) NOT NULL DEFAULT 'image/png',
    CONSTRAINT fk_imagem_resposta_item
        FOREIGN KEY (resposta_item_checklist_id) REFERENCES resposta_item_checklist(id)
);