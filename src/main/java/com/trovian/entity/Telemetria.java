package com.trovian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.Date;

/**
 * Mapeia a tabela public.telemetria existente no banco (serial4 / int4 / float8).
 * Sem FK constraints no DDL real — veiculo e id_transmissao são colunas int4 simples.
 */
@Entity
@Table(name = "telemetria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Telemetria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "veiculo")
    private Integer veiculo;

    @Column(name = "data_cadastro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;

    @Column(name = "interruptor_de_freio")
    private Integer interruptorDeFreio;

    @Column(name = "controle_de_cruzeiro_ativo")
    private Integer controleDeCruzeiroAtivo;

    @Column(name = "chave_de_embreagem")
    private Integer chaveDeEmbreagem;

    @Column(name = "estado_pto")
    private Integer estadoPto;

    @Column(name = "carga_atual_do_motor")
    private Integer cargaAtualDoMotor;

    @Column(name = "temperatura_liquido_arrefecimento_motor")
    private Integer temperaturaLiquidoArrefecimentoMotor;

    @Column(name = "velocidade_baseada_na_roda")
    private Integer velocidadeBaseadaNaRoda;

    @Column(name = "posicao_do_pedal_de_acelaracao")
    private Integer posicaoDoPedalDeAcelaracao;

    @Column(name = "rpm")
    private Integer rpm;

    @Column(name = "temperatura_motor")
    private Integer temperaturaMotor;

    @Column(name = "id_transmissao")
    private Integer idTransmissao;

    @Column(name = "combustivel_total_usado_pelo_motor")
    private Double combustivelTotalUsadoPeloMotor;

    @Column(name = "fuel_level")
    private Integer fuelLevel;

    @Column(name = "total_horas_operacao_motor")
    private Integer totalHorasOperacaoMotor;

    @Column(name = "taxa_combustivel")
    private Integer taxaCombustivel;

    @Column(name = "economia_combustivel_instantanea")
    private Integer economiaCombustivelInstantanea;

    @Column(name = "axix_x")
    private Integer axixX;

    @Column(name = "axix_y")
    private Integer axixY;

    @Column(name = "axix_z")
    private Integer axixZ;

    @Column(name = "trip_odometer")
    private Double tripOdometer;

    @Column(name = "total_odometer")
    private Double totalOdometer;

    @Column(name = "acelaracao_brusca")
    private Boolean acelaracaoBrusca;

    @Column(name = "freada_brusca")
    private Boolean freadaBrusca;

    @Column(name = "curva_brusca")
    private Boolean curvaBrusca;

    @Column(name = "green_driving_value")
    private Integer greenDrivingValue;

    @Column(name = "processado")
    private Boolean processado;

    @Column(name = "data_processamento")
    private Time dataProcessamento;
}
