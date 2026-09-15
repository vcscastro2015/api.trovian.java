package com.trovian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "manutencao")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Manutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @Column(name = "data_manutencao", nullable = false)
    private LocalDate dataManutencao;

    @Column(name = "tipo_manutencao", length = 100)
    private String tipoManutencao;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private OffsetDateTime dataCadastro;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "cliente_id")
    private Long clienteId;

    @PrePersist
    protected void onCreate() {
        dataCadastro = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
