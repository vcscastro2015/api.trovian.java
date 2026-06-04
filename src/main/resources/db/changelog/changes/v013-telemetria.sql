seja se --liquibase formatted sql
--changeset trovian:v013-telemetria

CREATE TABLE telemetria (
    id                                      SERIAL          PRIMARY KEY,
    veiculo                                 INT4,
    data_cadastro                           TIMESTAMP,
    interruptor_de_freio                    INT4,
    controle_de_cruzeiro_ativo              INT4,
    chave_de_embreagem                      INT4,
    estado_pto                              INT4,
    carga_atual_do_motor                    INT4,
    temperatura_liquido_arrefecimento_motor INT4,
    velocidade_baseada_na_roda              INT4,
    posicao_do_pedal_de_acelaracao          INT4,
    rpm                                     INT4,
    temperatura_motor                       INT4,
    id_transmissao                          INT4,
    combustivel_total_usado_pelo_motor      FLOAT8,
    fuel_level                              INT4,
    total_horas_operacao_motor              INT4,
    taxa_combustivel                        INT4,
    economia_combustivel_instantanea        INT4,
    axix_x                                  INT4,
    axix_y                                  INT4,
    axix_z                                  INT4,
    trip_odometer                           FLOAT8,
    total_odometer                          FLOAT8,
    acelaracao_brusca                       BOOLEAN,
    freada_brusca                           BOOLEAN,
    curva_brusca                            BOOLEAN,
    green_driving_value                     INT4,
    processado                              BOOLEAN,
    data_processamento                      TIME
);

-- Filtragem por período (queries de KPI, heatmap, RPM e top veículos)
CREATE INDEX idx_telemetria_data_cadastro ON telemetria (data_cadastro);

-- DISTINCT ON (veiculo) ORDER BY veiculo, data_cadastro DESC (posições atuais)
CREATE INDEX idx_telemetria_veiculo_data ON telemetria (veiculo, data_cadastro DESC);

-- Contagem e filtro de eventos bruscos
CREATE INDEX idx_telemetria_eventos_bruscos ON telemetria (data_cadastro)
    WHERE acelaracao_brusca = true OR freada_brusca = true OR curva_brusca = true;