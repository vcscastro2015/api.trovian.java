package com.trovian.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "aliquota_imposto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliquotaImposto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Alíquota Simples é obrigatória")
    @DecimalMin(value = "0.0", inclusive = true, message = "Alíquota Simples deve ser maior ou igual a 0")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal aliquotaSimples;

    @NotNull(message = "Alíquota Cooperativa é obrigatória")
    @DecimalMin(value = "0.0", inclusive = true, message = "Alíquota Cooperativa deve ser maior ou igual a 0")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal aliquotaCooperativa;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
