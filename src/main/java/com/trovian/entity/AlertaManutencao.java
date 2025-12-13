package com.trovian.entity;

import com.trovian.enums.PrioridadeAlerta;
import com.trovian.enums.TipoAlerta;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerta_manutencao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertaManutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_alerta", nullable = false, length = 30)
    @NotNull(message = "Tipo de alerta é obrigatório")
    private TipoAlerta tipoAlerta;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridade", nullable = false, length = 20)
    @NotNull(message = "Prioridade é obrigatória")
    private PrioridadeAlerta prioridade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    @NotNull(message = "Veículo é obrigatório")
    private Veiculo veiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id")
    private OrdemServico ordemServico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peca_id")
    private Peca peca;

    @Column(name = "titulo", nullable = false, length = 200)
    @NotNull(message = "Título é obrigatório")
    private String titulo;

    @Column(name = "mensagem", nullable = false, columnDefinition = "TEXT")
    @NotNull(message = "Mensagem é obrigatória")
    private String mensagem;

    @Column(name = "km_veiculo")
    private Integer kmVeiculo;

    @Column(name = "data_geracao", nullable = false)
    private LocalDateTime dataGeracao;

    @Column(name = "data_leitura")
    private LocalDateTime dataLeitura;

    @Column(name = "lido", nullable = false)
    private Boolean lido;

    @Column(name = "resolvido", nullable = false)
    private Boolean resolvido;

    @Column(name = "data_resolucao")
    private LocalDateTime dataResolucao;

    @Column(name = "observacao_resolucao", columnDefinition = "TEXT")
    private String observacaoResolucao;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.dataCadastro = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.dataGeracao = LocalDateTime.now();
        if (this.lido == null) {
            this.lido = false;
        }
        if (this.resolvido == null) {
            this.resolvido = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void marcarComoLido() {
        this.lido = true;
        this.dataLeitura = LocalDateTime.now();
    }

    public void marcarComoResolvido(String observacao) {
        this.resolvido = true;
        this.dataResolucao = LocalDateTime.now();
        this.observacaoResolucao = observacao;
    }
}
