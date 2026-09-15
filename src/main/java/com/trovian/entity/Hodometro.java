package com.trovian.entity;

import com.trovian.enums.TipoHodometro;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "hodometro")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hodometro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Data de cadastro é obrigatória")
    @Column(name = "data_cadastro", nullable = false, updatable = false, columnDefinition = "timestamptz")
    private OffsetDateTime dataCadastro;

    @Column(name = "data_atualizacao")
    private OffsetDateTime dataAtualizacao;

    @NotNull(message = "Hodômetro é obrigatório")
    @Column(name = "hodometro", nullable = false)
    private Double hodometro;

    @Column(name = "hodometro_rastreador")
    private Double hodometroRastreador;

    @NotNull(message = "Veículo é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo", referencedColumnName = "id", nullable = false)
    private Veiculo veiculo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", columnDefinition = "TEXT")
    private TipoHodometro tipo;

    @Column(name = "id_transmissao")
    private Long idTransmissao;

    @Column(name = "id_motorista")
    private Long idMotorista;

    // ==================== v015: ANTIFRAUDE ====================

    @Column(name = "confiavel")
    private Boolean confiavel;

    @Column(name = "origem_evento_id")
    private UUID origemEventoId;

    @Column(name = "origem_sistema", length = 20)
    private String origemSistema; // WEB, WHATSAPP, APP, RASTREADOR, IMPORTACAO, API

    @Column(name = "hodometro_normalizado", precision = 12, scale = 2)
    private BigDecimal hodometroNormalizado;

    @Column(name = "divergencia_km", precision = 12, scale = 2)
    private BigDecimal divergenciaKm;

    @Column(name = "divergencia_pct", precision = 8, scale = 3)
    private BigDecimal divergenciaPct;

    @Column(name = "horimetro", precision = 12, scale = 2)
    private BigDecimal horimetro;

    @Column(name = "horimetro_rastreador", precision = 12, scale = 2)
    private BigDecimal horimetroRastreador;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    // ==================== v019: MULTI-TENANT ====================

    @Column(name = "cliente_id")
    private Long clienteId;

    @PrePersist
    protected void onCreate() {
        dataCadastro = OffsetDateTime.now();
        dataAtualizacao = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = OffsetDateTime.now();
    }
}
