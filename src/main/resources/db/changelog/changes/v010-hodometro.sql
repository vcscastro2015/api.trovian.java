--liquibase formatted sql

--changeset trovian:v010-hodometro
CREATE TABLE hodometro (
    id SERIAL NOT NULL,
    data_cadastro TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP NOT NULL,
    hodometro FLOAT8 NOT NULL,
    hodometro_rastreador FLOAT8 NULL,
    veiculo INTEGER NOT NULL,
    tipo TEXT NULL,
    id_transmissao INTEGER NULL,
    nome_motorista TEXT NULL,
    id_motorista INTEGER NULL,
    CONSTRAINT hodometro_pkey PRIMARY KEY (id),
    CONSTRAINT fk_hodometro_veiculo FOREIGN KEY (veiculo) REFERENCES veiculo(id)
);
