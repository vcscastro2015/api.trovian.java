package com.trovian.entity;

import com.trovian.enums.TipoItemManutencao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "item_manutencao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemManutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    @NotNull(message = "Ordem de serviço é obrigatória")
    private OrdemServico ordemServico;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    @NotNull(message = "Tipo é obrigatório")
    private TipoItemManutencao tipo;

    @Column(name = "descricao", nullable = false, length = 500)
    @NotNull(message = "Descrição é obrigatória")
    private String descricao;

    @Column(name = "quantidade", nullable = false)
    @NotNull(message = "Quantidade é obrigatória")
    private Integer quantidade;

    @Column(name = "valor_unitario", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "Valor unitário é obrigatório")
    private BigDecimal valorUnitario;

    @Column(name = "valor_total", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "Valor total é obrigatório")
    private BigDecimal valorTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.dataCadastro = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        calcularValorTotal();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        calcularValorTotal();
    }

    public void calcularValorTotal() {
        if (this.quantidade != null && this.valorUnitario != null) {
            this.valorTotal = this.valorUnitario.multiply(BigDecimal.valueOf(this.quantidade));
        }
    }
}
