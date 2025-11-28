package com.trovian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidade que armazena dados detalhados de consumo de combustível
 */
@Entity
@Table(name = "consumo_detalhado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumoDetalhado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== TOTAIS ====================

    @Column(name = "consumo_total", precision = 10, scale = 2)
    private BigDecimal consumoTotal;

    @Column(name = "fator_terreno", precision = 10, scale = 2)
    private BigDecimal fatorTerreno;

    @Column(name = "classificacao", length = 50)
    private String classificacao;

    // ==================== POR TRECHO ====================

    @Column(name = "consumo_subida", precision = 10, scale = 2)
    private BigDecimal consumoSubida;

    @Column(name = "consumo_descida", precision = 10, scale = 2)
    private BigDecimal consumoDescida;

    @Column(name = "consumo_plano", precision = 10, scale = 2)
    private BigDecimal consumoPlano;

    // ==================== MÉDIAS (L/km) ====================

    @Column(name = "media_subida_l_km", precision = 10, scale = 2)
    private BigDecimal mediaSubidaLKm;

    @Column(name = "media_descida_l_km", precision = 10, scale = 2)
    private BigDecimal mediaDescidaLKm;

    @Column(name = "media_plano_l_km", precision = 10, scale = 2)
    private BigDecimal mediaPlanoLKm;

    @Column(name = "media_total_l_km", precision = 10, scale = 2)
    private BigDecimal mediaTotalLKm;

    // ==================== MÉDIAS (km/L) ====================

    @Column(name = "media_subida_km_l", precision = 10, scale = 2)
    private BigDecimal mediaSubidaKmL;

    @Column(name = "media_descida_km_l", precision = 10, scale = 2)
    private BigDecimal mediaDescidaKmL;

    @Column(name = "media_plano_km_l", precision = 10, scale = 2)
    private BigDecimal mediaPlanoKmL;

    @Column(name = "media_total_km_l", precision = 10, scale = 2)
    private BigDecimal mediaTotalKmL;

    // ==================== DADOS DO TERRENO ====================

    @Column(name = "elevacao_por_100km")
    private Double elevacaoPor100km;

    @Column(name = "inclinacao_media_subida")
    private Double inclinacaoMediaSubida;

    @Column(name = "inclinacao_media_descida")
    private Double inclinacaoMediaDescida;

    @Column(name = "elevacao_subida")
    private Double elevacaoSubida;

    @Column(name = "elevacao_descida")
    private Double elevacaoDescida;

    @Column(name = "distancia_subida")
    private Double distanciaSubida;

    @Column(name = "distancia_descida")
    private Double distanciaDescida;

    @Column(name = "distancia_plano")
    private Double distanciaPlano;

    @Column(name = "distancia_total")
    private Double distanciaTotal;
}
