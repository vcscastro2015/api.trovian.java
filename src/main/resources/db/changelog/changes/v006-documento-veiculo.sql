--liquibase formatted sql

--changeset trovian:v006-documento-veiculo
--comment: Cria tabela para armazenar documentos de veículos recebidos via WhatsApp
CREATE TABLE documento_veiculo (
    id               BIGSERIAL PRIMARY KEY,
    veiculo_id       BIGINT NOT NULL,
    motorista_id     BIGINT,
    cliente_id       BIGINT NOT NULL,
    nome_arquivo     VARCHAR(255),
    mime_type        VARCHAR(100),
    conteudo_binario BYTEA NOT NULL,
    data_envio       TIMESTAMP,
    numero_telefone  VARCHAR(20),
    CONSTRAINT fk_doc_veiculo_veiculo   FOREIGN KEY (veiculo_id)   REFERENCES veiculo(id),
    CONSTRAINT fk_doc_veiculo_motorista FOREIGN KEY (motorista_id) REFERENCES motorista(id),
    CONSTRAINT fk_doc_veiculo_cliente   FOREIGN KEY (cliente_id)   REFERENCES cliente(id)
);
