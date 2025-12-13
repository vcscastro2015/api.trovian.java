package com.trovian.entity;

import com.trovian.enums.StatusOrdemServico;
import com.trovian.enums.TipoManutencao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordem_servico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_os", unique = true, nullable = false, length = 50)
    @NotNull(message = "Número da OS é obrigatório")
    private String numeroOs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    @NotNull(message = "Veículo é obrigatório")
    private Veiculo veiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motorista_id")
    private Motorista motorista;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_manutencao", nullable = false, length = 30)
    @NotNull(message = "Tipo de manutenção é obrigatório")
    private TipoManutencao tipoManutencao;

    @Column(name = "km_veiculo")
    private Integer kmVeiculo;

    @Column(name = "data_abertura", nullable = false)
    @NotNull(message = "Data de abertura é obrigatória")
    private LocalDate dataAbertura;

    @Column(name = "data_prevista")
    private LocalDate dataPrevista;

    @Column(name = "data_conclusao")
    private LocalDate dataConclusao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @NotNull(message = "Status é obrigatório")
    private StatusOrdemServico status;

    @Column(name = "descricao_problema", columnDefinition = "TEXT")
    private String descricaoProblema;

    @Column(name = "diagnostico", columnDefinition = "TEXT")
    private String diagnostico;

    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemManutencao> itens = new ArrayList<>();

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.dataCadastro = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusOrdemServico.ABERTA;
        }
        if (this.dataAbertura == null) {
            this.dataAbertura = LocalDate.now();
        }
        if (this.valorTotal == null) {
            this.valorTotal = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void calcularValorTotal() {
        this.valorTotal = itens.stream()
            .map(ItemManutencao::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
