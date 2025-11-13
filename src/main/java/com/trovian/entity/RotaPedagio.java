package com.trovian.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity que representa um pedágio encontrado na rota
 */
@Entity
@Table(name = "rota_pedagio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RotaPedagio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rota_id", nullable = false)
    @NotNull(message = "Rota é obrigatória")
    private Rota rota;

    @Column(length = 255)
    private String nome;

    @Column(length = 100)
    private String rodovia;

    @Column(length = 50)
    private String km;

    @Column(length = 255)
    private String municipio;

    @Column(length = 2)
    private String uf;

    @Column(length = 255)
    private String concessionaria;

    @NotNull(message = "Latitude é obrigatória")
    @Column(nullable = false)
    private Double latitude;

    @NotNull(message = "Longitude é obrigatória")
    @Column(nullable = false)
    private Double longitude;

    @Column(name = "distancia_rota")
    private Double distanciaRota; // metros
}
