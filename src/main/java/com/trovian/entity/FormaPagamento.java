package com.trovian.entity;

import com.trovian.enums.TipoFormaPagamento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Entity
@Table(name = "forma_pagamento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormaPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private TipoFormaPagamento tipo;

    @Column
    private Integer prazoMedioDias;

    @Column(nullable = false)
    private Boolean permiteParcelamento = false;

    @Column(nullable = false)
    private Boolean status = true;

    @Column(nullable = false, updatable = false, columnDefinition = "timestamptz")
    private OffsetDateTime dataCadastro = OffsetDateTime.now();

    @Column(nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @NotNull(message = "Cliente é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
