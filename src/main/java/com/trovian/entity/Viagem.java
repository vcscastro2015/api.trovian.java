package com.trovian.entity;

import com.trovian.enums.StatusViagem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Entidade que representa uma Viagem completa com cálculos de custos e receitas
 */
@Entity
@Table(name = "viagem")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Viagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== RELACIONAMENTOS ====================

    @NotNull(message = "Veículo é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @NotNull(message = "Motorista é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motorista_id", nullable = false)
    private Motorista motorista;

    @NotNull(message = "Rota de ida é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rota_ida_id", nullable = false)
    private Rota rotaIda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rota_volta_id")
    private Rota rotaVolta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "abastecimento_id")
    private Abastecimento abastecimento;

    @NotNull(message = "Cliente é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "consumo_detalhado_ida_id")
    private ConsumoDetalhado consumoDetalhadoIda;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "consumo_detalhado_volta_id")
    private ConsumoDetalhado consumoDetalhadoVolta;

    // ==================== DADOS BÁSICOS ====================

    @NotNull(message = "Data da viagem é obrigatória")
    @Column(name = "data_viagem", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataViagem;

    @NotNull(message = "Status é obrigatório")
    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @NotNull(message = "Status da viagem é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "status_viagem", nullable = false, length = 20)
    private StatusViagem statusViagem = StatusViagem.ABERTA;

    // ==================== DADOS DA IDA ====================

    @NotNull(message = "Quantidade de toneladas da ida é obrigatória")
    @Column(name = "quantidade_toneladas_ida", nullable = false)
    private Double quantidadeToneladasIda;

    @Column(name = "material_ida", length = 200)
    private String materialIda;

    @NotNull(message = "Valor tonelada bruta da ida é obrigatório")
    @Column(name = "valor_tonelada_bruta_ida", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorToneladaBrutaIda;

    @NotNull(message = "Imposto base da ida é obrigatório")
    @Column(name = "imposto_base_ida", nullable = false, precision = 10, scale = 2)
    private BigDecimal impostoBaseIda;

    @NotNull(message = "Quantidade de pedágios da ida é obrigatória")
    @Column(name = "quantidade_pedagios_ida", nullable = false)
    private Integer quantidadePedagiosIda;

    @NotNull(message = "Valor pedágio por eixo da ida é obrigatório")
    @Column(name = "valor_pedagio_por_eixo_ida", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPedagioPorEixoIda;

    @Column(name = "valor_total_bruto_ida", precision = 10, scale = 2)
    private BigDecimal valorTotalBrutoIda;

    // ==================== DADOS DA VOLTA ====================

    @Column(name = "quantidade_toneladas_volta")
    private Double quantidadeToneladasVolta;

    @Column(name = "material_volta", length = 200)
    private String materialVolta;

    @Column(name = "valor_tonelada_bruta_volta", precision = 10, scale = 2)
    private BigDecimal valorToneladaBrutaVolta;

    @Column(name = "imposto_base_volta", precision = 10, scale = 2)
    private BigDecimal impostoBaseVolta;

    @Column(name = "quantidade_pedagios_volta")
    private Integer quantidadePedagiosVolta;

    @Column(name = "valor_pedagio_por_eixo_volta", precision = 10, scale = 2)
    private BigDecimal valorPedagioPorEixoVolta;

    @Column(name = "valor_total_bruto_volta", precision = 10, scale = 2)
    private BigDecimal valorTotalBrutoVolta;

    // ==================== COMBUSTÍVEL ====================

    @NotNull(message = "Média do veículo é obrigatória")
    @Column(name = "media_veiculo_km_l", nullable = false)
    private Double mediaVeiculoKmL;

    @Column(name = "fator_carga")
    private Double fatorCarga;

    @Column(name = "consumo_estimado_litros")
    private Double consumoEstimadoLitros;

    @Column(name = "valor_total_combustivel", precision = 10, scale = 2)
    private BigDecimal valorTotalCombustivel;

    // ==================== RESULTADO FINAL ====================

    @Column(name = "valor_total_bruto_viagem", precision = 10, scale = 2)
    private BigDecimal valorTotalBrutoViagem;

    @Column(name = "valor_total_pedagios", precision = 10, scale = 2)
    private BigDecimal valorTotalPedagios;

    @Column(name = "comissao_motorista", precision = 10, scale = 2)
    private BigDecimal comissaoMotorista;

    @Column(name = "valor_total_custos", precision = 10, scale = 2)
    private BigDecimal valorTotalCustos;

    @Column(name = "valor_total_liquido", precision = 10, scale = 2)
    private BigDecimal valorTotalLiquido;

    @Column(name = "margem_percentual")
    private Double margemPercentual;

    // ==================== AUDITORIA ====================

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.dataCadastro = new Date();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = true;
        }
        if (this.statusViagem == null) {
            this.statusViagem = StatusViagem.ABERTA;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
