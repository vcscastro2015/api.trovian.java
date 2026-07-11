--liquibase formatted sql
--changeset trovian:v014-agendamento_comando

CREATE TABLE agendamento_comando_veiculo (
    id               BIGSERIAL       PRIMARY KEY,
    veiculo_id       BIGINT          NOT NULL,
    tipo_comando     VARCHAR(20)     NOT NULL,
    tipo_recorrencia VARCHAR(20)     NOT NULL,
    horario          TIME            NOT NULL,
    dia_do_mes       INT4,
    data_especifica  DATE,
    ativo            BOOLEAN         NOT NULL DEFAULT TRUE,
    ultima_execucao  TIMESTAMP,
    data_cadastro    TIMESTAMP       NOT NULL,
    updated_at       TIMESTAMP,

    CONSTRAINT fk_agendamento_veiculo
        FOREIGN KEY (veiculo_id) REFERENCES veiculo(id),

    CONSTRAINT chk_tipo_comando
        CHECK (tipo_comando IN ('BLOQUEAR', 'DESBLOQUEAR')),

    CONSTRAINT chk_tipo_recorrencia
        CHECK (tipo_recorrencia IN ('DIARIO', 'MENSAL', 'DIA_ESPECIFICO'))
);

-- Busca de agendamentos ativos (usada pelo scheduler a cada 5 minutos)
CREATE INDEX idx_agendamento_ativo ON agendamento_comando_veiculo (ativo);

-- Listagem de agendamentos por veículo
CREATE INDEX idx_agendamento_veiculo_id ON agendamento_comando_veiculo (veiculo_id);
