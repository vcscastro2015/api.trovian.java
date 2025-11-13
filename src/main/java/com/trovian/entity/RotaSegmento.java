package com.trovian.entity;

import com.trovian.enums.DirecaoSegmento;
import com.trovian.enums.NivelInclinacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity que representa um segmento colorido da rota
 */
@Entity
@Table(name = "rota_segmento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RotaSegmento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rota_id", nullable = false)
    @NotNull(message = "Rota é obrigatória")
    private Rota rota;

    @NotNull(message = "Sequência é obrigatória")
    @Column(nullable = false)
    private Integer sequencia;

    @NotNull(message = "Latitude de início é obrigatória")
    @Column(name = "latitude_inicio", nullable = false)
    private Double latitudeInicio;

    @NotNull(message = "Longitude de início é obrigatória")
    @Column(name = "longitude_inicio", nullable = false)
    private Double longitudeInicio;

    @NotNull(message = "Elevação de início é obrigatória")
    @Column(name = "elevacao_inicio", nullable = false)
    private Double elevacaoInicio;

    @NotNull(message = "Latitude de fim é obrigatória")
    @Column(name = "latitude_fim", nullable = false)
    private Double latitudeFim;

    @NotNull(message = "Longitude de fim é obrigatória")
    @Column(name = "longitude_fim", nullable = false)
    private Double longitudeFim;

    @NotNull(message = "Elevação de fim é obrigatória")
    @Column(name = "elevacao_fim", nullable = false)
    private Double elevacaoFim;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DirecaoSegmento direcao; // SUBIDA, DESCIDA, PLANO

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_inclinacao", length = 50)
    private NivelInclinacao nivelInclinacao; // PLANO, LEVE, MODERADO, FORTE, MUITO_FORTE

    @Column(name = "porcentagem_inclinacao")
    private Double porcentagemInclinacao;

    @Column(name = "variacao_elevacao")
    private Double variacaoElevacao; // metros

    @Column
    private Double distancia; // metros

    @Column(length = 20)
    private String cor; // hex color
}
