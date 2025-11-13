package com.trovian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity que representa as estatísticas de elevação de uma rota
 */
@Entity
@Table(name = "rota_estatisticas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RotaEstatisticas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rota_id", nullable = false, unique = true)
    private Rota rota;

    @Column(name = "ganho_total_elevacao")
    private Double ganhoTotalElevacao; // metros

    @Column(name = "perda_total_elevacao")
    private Double perdaTotalElevacao; // metros

    @Column(name = "elevacao_maxima")
    private Double elevacaoMaxima; // metros

    @Column(name = "elevacao_minima")
    private Double elevacaoMinima; // metros

    @Column(name = "elevacao_media")
    private Double elevacaoMedia; // metros

    @Column(length = 50)
    private String dificuldade; // Fácil, Moderada, Difícil, Muito Difícil

    @Column(name = "distancia_subida")
    private Double distanciaSubida; // metros

    @Column(name = "distancia_descida")
    private Double distanciaDescida; // metros

    @Column(name = "distancia_plano")
    private Double distanciaPlano; // metros

    // Ponto de elevação máxima
    @Column(name = "ponto_elevacao_maxima_lat")
    private Double pontoElevacaoMaximaLat;

    @Column(name = "ponto_elevacao_maxima_lng")
    private Double pontoElevacaoMaximaLng;

    @Column(name = "ponto_elevacao_maxima_elev")
    private Double pontoElevacaoMaximaElev;

    // Ponto de elevação mínima
    @Column(name = "ponto_elevacao_minima_lat")
    private Double pontoElevacaoMinimaLat;

    @Column(name = "ponto_elevacao_minima_lng")
    private Double pontoElevacaoMinimaLng;

    @Column(name = "ponto_elevacao_minima_elev")
    private Double pontoElevacaoMinimaElev;
}
