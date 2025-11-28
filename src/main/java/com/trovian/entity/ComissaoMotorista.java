package com.trovian.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "comissao_motorista")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComissaoMotorista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Motorista é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motorista_id", nullable = false)
    private Motorista motorista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viagem_id")
    private Viagem viagem;

    @NotNull(message = "Cliente é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "percentual_comissao", precision = 5, scale = 2)
    private BigDecimal percentualComissao;

    @Column(name = "valor_comissao", precision = 10, scale = 2)
    private BigDecimal valorComissao;

    @Column(name = "valor_liquido_motorista", precision = 10, scale = 2)
    private BigDecimal valorLiquidoMotorista;

    @Column(name = "descontos", precision = 10, scale = 2)
    private BigDecimal descontos = BigDecimal.ZERO;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Column(name = "periodo_referencia", length = 7)
    private String periodoReferencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusComissao status = StatusComissao.PENDENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comissao", length = 20)
    private TipoComissao tipoComissao;

    @Column(name = "forma_pagamento", length = 50)
    private String formaPagamento;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "usuario_registro_id")
    private Long usuarioRegistroId;

    @Column(name = "bonificacoes", precision = 10, scale = 2)
    private BigDecimal bonificacoes = BigDecimal.ZERO;

    @Column(name = "penalidades", precision = 10, scale = 2)
    private BigDecimal penalidades = BigDecimal.ZERO;


    @Column(name = "origem", length = 255)
    private String origem;

    @Column(name = "destino", length = 255)
    private String destino;

    @Column(name = "numero_nota_fiscal", length = 50)
    private String numeroNotaFiscal;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = StatusComissao.PENDENTE;
        }
        if (descontos == null) {
            descontos = BigDecimal.ZERO;
        }
        if (bonificacoes == null) {
            bonificacoes = BigDecimal.ZERO;
        }
        if (penalidades == null) {
            penalidades = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum StatusComissao {
        PENDENTE,
        CALCULADA,
        PAGA,
        CANCELADA
    }

    public enum TipoComissao {
        POR_VIAGEM,
        MENSAL,
        POR_KM
    }
}
