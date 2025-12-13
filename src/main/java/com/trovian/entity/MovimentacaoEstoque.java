package com.trovian.entity;

import com.trovian.enums.TipoMovimentacaoEstoque;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacao_estoque")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peca_id", nullable = false)
    @NotNull(message = "Peça é obrigatória")
    private Peca peca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id")
    private OrdemServico ordemServico;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimentacao", nullable = false, length = 20)
    @NotNull(message = "Tipo de movimentação é obrigatório")
    private TipoMovimentacaoEstoque tipoMovimentacao;

    @Column(name = "quantidade", nullable = false)
    @NotNull(message = "Quantidade é obrigatória")
    private Integer quantidade;

    @Column(name = "data_movimentacao", nullable = false)
    @NotNull(message = "Data de movimentação é obrigatória")
    private LocalDateTime dataMovimentacao;

    @Column(name = "valor_unitario", precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "usuario", length = 100)
    private String usuario;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @PrePersist
    protected void onCreate() {
        this.dataCadastro = LocalDateTime.now();
        if (this.dataMovimentacao == null) {
            this.dataMovimentacao = LocalDateTime.now();
        }
    }
}
