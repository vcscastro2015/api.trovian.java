package com.trovian.entity;

import com.trovian.enums.StatusClientePlano;
import com.trovian.enums.TipoFormaPagamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cliente_plano")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientePlano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plano_id", nullable = false)
    private Plano plano;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorContratado;

    @Column(precision = 10, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorFinal;

    private LocalDate dataInicio;

    private LocalDate dataFinal;

    private LocalDate dataVencimento;

    private Integer diaVencimento;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusClientePlano status;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private TipoFormaPagamento formaPagamento;

    private LocalDate dataCancelamento;

    @Column(length = 500)
    private String motivoCancelamento;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        dataCriacao = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
