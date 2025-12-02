package com.trovian.entity;

import com.trovian.enums.StatusConta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "conta_pagar")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaPagar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String descricao;

    @Column(length = 50)
    private String numeroDocumento;

    @Column(length = 20)
    private String numeroNotaFiscal;

    @Column(length = 50)
    private String numeroControle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private Fornecedor fornecedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaConta categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_custo_id")
    private CentroCusto centroCusto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forma_pagamento_id")
    private FormaPagamento formaPagamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motorista_id")
    private Motorista motorista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorOriginal;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorJuros = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorMulta = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorPago = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDate dataEmissao;

    @Column(nullable = false)
    private LocalDate dataVencimento;

    @Column
    private LocalDate dataPagamento;

    @Column
    private LocalDate dataCompetencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusConta status = StatusConta.PENDENTE;

    @Column
    private Integer numeroParcela;

    @Column
    private Integer totalParcelas;

    @Column(nullable = false)
    private Boolean recorrente = false;

    @Column(length = 20)
    private String periodicidade;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(columnDefinition = "TEXT")
    private String anexos;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false, updatable = false)
    private Date dataCadastro = new Date();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(length = 100)
    private String usuarioCadastro;

    @Column(length = 100)
    private String usuarioPagamento;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal calcularSaldo() {
        return valorTotal.subtract(valorPago);
    }

    public boolean isVencida() {
        if (status == StatusConta.PAGO || status == StatusConta.CANCELADO) {
            return false;
        }
        return LocalDate.now().isAfter(dataVencimento);
    }

    public boolean isPaga() {
        return status == StatusConta.PAGO;
    }
}
