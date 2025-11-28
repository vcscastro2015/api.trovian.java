package com.trovian.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumoDetalhadoDTO {
    // Totais
    private BigDecimal consumoTotal;
    private BigDecimal fatorTerreno;
    private String classificacao;

    // Por trecho
    private BigDecimal consumoSubida;
    private BigDecimal consumoDescida;
    private BigDecimal consumoPlano;

    // Médias (L/km)
    private BigDecimal mediaSubidaLKm;
    private BigDecimal mediaDescidaLKm;
    private BigDecimal mediaPlanoLKm;
    private BigDecimal mediaTotalLKm;

    // Médias (km/L)
    private BigDecimal mediaSubidaKmL;
    private BigDecimal mediaDescidaKmL;
    private BigDecimal mediaPlanoKmL;
    private BigDecimal mediaTotalKmL;

    // Dados do terreno
    private double elevacaoPor100km;
    private double inclinacaoMediaSubida;
    private double inclinacaoMediaDescida;

    private double elevacaoSubida;
    private double elevacaoDescida;
    private double distanciaSubida;
    private double distanciaDescida;
    private double distanciaPlano;
    private double distanciaTotal;


}
