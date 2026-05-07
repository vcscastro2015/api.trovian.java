--liquibase formatted sql
--changeset trovian:v011-gestao-pneus

CREATE TABLE pneu (
    id BIGSERIAL NOT NULL,
    numero_dot TEXT NOT NULL,
    numero_fogo TEXT NULL,
    marca TEXT NULL,
    modelo TEXT NULL,
    dimensao TEXT NULL,
    tipo_pneu TEXT NULL,
    eixo TEXT NULL,
    data_fabricacao DATE NULL,
    data_compra DATE NULL,
    valor_compra NUMERIC(10,2) NULL,
    fornecedor_id BIGINT NULL,
    profundidade_inicial NUMERIC(5,2) NULL,
    profundidade_minima NUMERIC(5,2) NULL,
    km_limite INTEGER NULL,
    status TEXT NOT NULL DEFAULT 'NOVO',
    numero_recapagens INTEGER NOT NULL DEFAULT 0,
    km_acumulado INTEGER NOT NULL DEFAULT 0,
    cliente_id BIGINT NOT NULL,
    observacoes TEXT NULL,
    data_cadastro TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pneu_pkey PRIMARY KEY (id),
    CONSTRAINT fk_pneu_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT fk_pneu_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES fornecedor(id)
);

CREATE UNIQUE INDEX idx_pneu_dot_cliente ON pneu(numero_dot, cliente_id);
CREATE INDEX idx_pneu_cliente_status ON pneu(cliente_id, status);

CREATE TABLE alocacao_pneu (
    id BIGSERIAL NOT NULL,
    pneu_id BIGINT NOT NULL,
    veiculo_id BIGINT NOT NULL,
    posicao TEXT NOT NULL,
    data_montagem TIMESTAMP NOT NULL,
    data_remocao TIMESTAMP NULL,
    km_montagem INTEGER NULL,
    km_remocao INTEGER NULL,
    motivo_remocao TEXT NULL,
    responsavel TEXT NULL,
    data_cadastro TIMESTAMP NOT NULL,
    CONSTRAINT alocacao_pneu_pkey PRIMARY KEY (id),
    CONSTRAINT fk_alocacao_pneu FOREIGN KEY (pneu_id) REFERENCES pneu(id),
    CONSTRAINT fk_alocacao_veiculo FOREIGN KEY (veiculo_id) REFERENCES veiculo(id)
);

CREATE INDEX idx_alocacao_ativa ON alocacao_pneu(veiculo_id, data_remocao);

CREATE TABLE inspecao_pneu (
    id BIGSERIAL NOT NULL,
    pneu_id BIGINT NOT NULL,
    veiculo_id BIGINT NOT NULL,
    motorista_id BIGINT NULL,
    alocacao_pneu_id BIGINT NULL,
    data_inspecao TIMESTAMP NOT NULL,
    km_veiculo INTEGER NULL,
    pressao_medida NUMERIC(5,2) NULL,
    pressao_recomendada NUMERIC(5,2) NULL,
    profundidade_sulco NUMERIC(5,2) NULL,
    condicao_visual TEXT NULL,
    tem_deformacao BOOLEAN NULL,
    observacoes TEXT NULL,
    gera_alerta BOOLEAN NOT NULL DEFAULT FALSE,
    data_cadastro TIMESTAMP NOT NULL,
    CONSTRAINT inspecao_pneu_pkey PRIMARY KEY (id),
    CONSTRAINT fk_inspecao_pneu FOREIGN KEY (pneu_id) REFERENCES pneu(id),
    CONSTRAINT fk_inspecao_veiculo FOREIGN KEY (veiculo_id) REFERENCES veiculo(id),
    CONSTRAINT fk_inspecao_motorista FOREIGN KEY (motorista_id) REFERENCES motorista(id),
    CONSTRAINT fk_inspecao_alocacao FOREIGN KEY (alocacao_pneu_id) REFERENCES alocacao_pneu(id)
);

CREATE INDEX idx_inspecao_pneu_data ON inspecao_pneu(pneu_id, data_inspecao);

CREATE TABLE recapagem_pneu (
    id BIGSERIAL NOT NULL,
    pneu_id BIGINT NOT NULL,
    fornecedor_id BIGINT NULL,
    numero_recapagem INTEGER NOT NULL,
    data_envio DATE NOT NULL,
    data_retorno DATE NULL,
    km_envio INTEGER NULL,
    tipo_recapagem TEXT NULL,
    valor_recapagem NUMERIC(10,2) NULL,
    profundidade_apos_recapagem NUMERIC(5,2) NULL,
    garantia_km INTEGER NULL,
    aprovado BOOLEAN NULL,
    motivo_rejeicao TEXT NULL,
    data_cadastro TIMESTAMP NOT NULL,
    CONSTRAINT recapagem_pneu_pkey PRIMARY KEY (id),
    CONSTRAINT fk_recapagem_pneu FOREIGN KEY (pneu_id) REFERENCES pneu(id),
    CONSTRAINT fk_recapagem_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES fornecedor(id)
);
