package com.trovian.entity;

import com.trovian.enums.TipoPonto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity que representa um ponto da rota (origem, destino ou waypoint)
 */
@Entity
@Table(name = "ponto_rota")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PontoRota {

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

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tipo do ponto é obrigatório")
    @Column(nullable = false, length = 20)
    private TipoPonto tipo;

    @NotNull(message = "Latitude é obrigatória")
    @Column(nullable = false)
    private Double latitude;

    @NotNull(message = "Longitude é obrigatória")
    @Column(nullable = false)
    private Double longitude;

    @Column(length = 500)
    private String endereco;

    @Column(length = 255)
    private String nome;
}
