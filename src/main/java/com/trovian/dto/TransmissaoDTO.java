package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representação de Transmissão")
public class TransmissaoDTO {

    @Schema(description = "ID da transmissão", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Altitude (metros)", example = "850")
    private Integer altitude;

    @Schema(description = "Data e hora da transmissão", example = "2025-01-15T10:30:00")
    private Date dataTransmissao;

    @Schema(description = "Direção do veículo (graus)", example = "180")
    private Integer direcao;

    @Schema(description = "Excesso de velocidade detectado", example = "false")
    private Boolean execessoVelocidade;

    @Schema(description = "GPRS ativo", example = "true")
    private Boolean gprsAtivo;

    @Schema(description = "GPS ativo", example = "true")
    private Boolean gpsAtivo;

    @Schema(description = "Hodômetro (km)", example = "125000.5")
    private Double hodometro;

    @Schema(description = "Horímetro (horas)", example = "3500.0")
    private Double horimetro;

    @Schema(description = "Ignição ativa", example = "true")
    private Boolean ignicaoAtiva;

    @Schema(description = "Latitude", example = "-23.5505")
    private double latitude;

    @Schema(description = "Longitude", example = "-46.6333")
    private double longitude;

    @Schema(description = "Pânico acionado", example = "false")
    private Boolean panico;

    @Schema(description = "Percentual de bateria do equipamento", example = "85")
    private Integer percentualBateriaEquipamento;

    @Schema(description = "Ponto de referência", example = "false")
    private Boolean pontoDeReferencia;

    @Schema(description = "Saída 4 ativa", example = "false")
    private Boolean saida4;

    @Schema(description = "Temperatura (°C)", example = "25.5")
    private Double temperatura;

    @Schema(description = "Velocidade (km/h)", example = "80.0")
    private Double velocidade;

    @Schema(description = "Voltagem (V)", example = "12.6")
    private Double voltagem;

    @Schema(description = "Jamming detectado", example = "false")
    private Boolean jamming;

    @Schema(description = "Sinal de jamming", example = "0")
    private String sinalJamming;

    @Schema(description = "Falha na alimentação externa", example = "false")
    private Boolean falhaAlimentacaoExterna;

    @Schema(description = "Conexão primária ativa", example = "true")
    private Boolean conexaoPrimariaAtiva;

    @Schema(description = "Entrada digital 1", example = "false")
    private Boolean entradaDigital1;

    @Schema(description = "Entrada digital 2", example = "false")
    private Boolean entradaDigital2;

    @Schema(description = "Entrada digital 3", example = "false")
    private Boolean entradaDigital3;

    @Schema(description = "Entrada digital 4", example = "false")
    private Boolean entradaDigital4;

    @Schema(description = "Identificador do evento", example = "0")
    private Integer identificadorDoEvento;

    @Schema(description = "Último envio SMS", example = "false")
    private Boolean ultimoEnvioSms;

    @Schema(description = "Data do envio SMS", example = "2025-01-15T10:30:00")
    private Date dataEnvioSms;

    @Schema(description = "Distância percorrida (km)", example = "250.5")
    private Double distanciaPercorrida;

    @Schema(description = "Modo de operação", example = "1")
    private Integer modo;

    @Schema(description = "RPM corrente", example = "1800.0")
    private Double rpmCorrente;

    @Schema(description = "iButton do motorista", example = "A1B2C3D4E5")
    private String iButton;

    @Schema(description = "Evento ocorrido", example = "false")
    private Boolean evento;

    @Schema(description = "Tipo do evento", example = "0")
    private Integer tipoEvento;

    @Schema(description = "Alerta ativo", example = "false")
    private Boolean alerta;

    @Schema(description = "Tipo do alerta", example = "0")
    private Integer tipoAlerta;

    @Schema(description = "Em viagem", example = "true")
    private Boolean viagem;

    @Schema(description = "Endereço do ponto de transmissão", example = "Av. Paulista, 1000")
    private String endereco;

    @Schema(description = "ID do veículo", example = "1")
    private Long veiculoId;

    @Schema(description = "Placa do veículo", example = "ABC1D23")
    private String veiculoPlaca;

    @Schema(description = "Tipo do veículo", example = "Carro")
    private String veiculoTipo;

    @Schema(description = "Modelo do veículo (fabricante + marca)", example = "Volkswagen Gol")
    private String veiculoModelo;
}
