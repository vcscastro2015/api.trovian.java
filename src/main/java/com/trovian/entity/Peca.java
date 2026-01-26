package com.trovian.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "peca")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Peca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", unique = true, nullable = false, length = 50)
    @NotNull(message = "Código é obrigatório")
    private String codigo;

    @Column(name = "descricao", nullable = false, length = 500)
    @NotNull(message = "Descrição é obrigatória")
    private String descricao;

    @Column(name = "categoria", nullable = false, length = 100)
    @NotNull(message = "Categoria é obrigatória")
    private String categoria;

    @Column(name = "estoque_minimo", nullable = false)
    @NotNull(message = "Estoque mínimo é obrigatório")
    private Integer estoqueMinimo;

    @Column(name = "estoque_atual", nullable = false)
    @NotNull(message = "Estoque atual é obrigatório")
    private Integer estoqueAtual;

    @Column(name = "valor_medio", precision = 10, scale = 2)
    private BigDecimal valorMedio;

    @Column(name = "aplicacao_veiculos", columnDefinition = "TEXT")
    private String aplicacaoVeiculos;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @NotNull(message = "Cliente é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @PrePersist
    protected void onCreate() {
        this.dataCadastro = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = true;
        }
        if (this.estoqueAtual == null) {
            this.estoqueAtual = 0;
        }
        if (this.valorMedio == null) {
            this.valorMedio = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean verificarEstoqueBaixo() {
        return this.estoqueAtual <= this.estoqueMinimo;
    }

    public void adicionarEstoque(Integer quantidade) {
        this.estoqueAtual += quantidade;
    }

    public void removerEstoque(Integer quantidade) {
        if (this.estoqueAtual >= quantidade) {
            this.estoqueAtual -= quantidade;
        } else {
            throw new RuntimeException("Estoque insuficiente. Disponível: " + this.estoqueAtual + ", Solicitado: " + quantidade);
        }
    }

    public void atualizarValorMedio(BigDecimal novoValor, Integer quantidade) {
        if (this.estoqueAtual == 0) {
            this.valorMedio = novoValor;
        } else {
            BigDecimal valorTotalAtual = this.valorMedio.multiply(BigDecimal.valueOf(this.estoqueAtual));
            BigDecimal valorTotalNovo = novoValor.multiply(BigDecimal.valueOf(quantidade));
            BigDecimal valorTotalFinal = valorTotalAtual.add(valorTotalNovo);
            Integer estoqueTotal = this.estoqueAtual + quantidade;
            this.valorMedio = valorTotalFinal.divide(BigDecimal.valueOf(estoqueTotal), 2, BigDecimal.ROUND_HALF_UP);
        }
    }
}
