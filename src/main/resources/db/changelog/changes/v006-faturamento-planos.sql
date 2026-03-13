--liquibase formatted sql

--changeset trovian:v006-faturamento-planos
CREATE TABLE plano (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    nome VARCHAR(200) NOT NULL,
    descricao VARCHAR(500),
    valor NUMERIC(10, 2),
    periodicidade VARCHAR(20),
    limite_usuarios INTEGER,
    limite_veiculos INTEGER,
    limite_checklists INTEGER,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE cliente_plano (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    cliente_id BIGINT NOT NULL REFERENCES cliente(id),
    plano_id BIGINT NOT NULL REFERENCES plano(id),
    valor_contratado NUMERIC(10, 2),
    desconto NUMERIC(10, 2) DEFAULT 0,
    valor_final NUMERIC(10, 2),
    data_inicio DATE,
    data_vencimento DATE,
    dia_vencimento INTEGER,
    status VARCHAR(20),
    forma_pagamento VARCHAR(30),
    data_cancelamento DATE,
    motivo_cancelamento VARCHAR(500),
    data_criacao TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE historico_pagamento (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    cliente_plano_id BIGINT NOT NULL REFERENCES cliente_plano(id),
    valor_pago NUMERIC(10, 2),
    data_pagamento DATE,
    data_referencia DATE,
    forma_pagamento VARCHAR(30),
    observacao VARCHAR(500),
    data_criacao TIMESTAMP NOT NULL
);
