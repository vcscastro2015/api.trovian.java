package com.trovian.entity;

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
@Table(name = "historico_pagamento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_plano_id", nullable = false)
    private ClientePlano clientePlano;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorPago;

    private LocalDate dataPagamento;

    private LocalDate dataReferencia;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private TipoFormaPagamento formaPagamento;

    @Column(length = 500)
    private String observacao;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        dataCriacao = LocalDateTime.now();
    }
}
