package com.trovian.entity;

import com.trovian.enums.CondicaoVisual;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inspecao_pneu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspecaoPneu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Pneu é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pneu_id", nullable = false)
    private Pneu pneu;

    @NotNull(message = "Veículo é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motorista_id")
    private Motorista motorista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alocacao_pneu_id")
    private AlocacaoPneu alocacao;

    @NotNull(message = "Data da inspeção é obrigatória")
    @Column(name = "data_inspecao", nullable = false)
    private LocalDateTime dataInspecao;

    @Column(name = "km_veiculo")
    private Integer kmVeiculo;

    @Column(name = "pressao_medida", precision = 5, scale = 2)
    private BigDecimal pressaoMedida;

    @Column(name = "pressao_recomendada", precision = 5, scale = 2)
    private BigDecimal pressaoRecomendada;

    @Column(name = "profundidade_sulco", precision = 5, scale = 2)
    private BigDecimal profundidadeSulco;

    @Enumerated(EnumType.STRING)
    @Column(name = "condicao_visual", columnDefinition = "TEXT")
    private CondicaoVisual condicaoVisual;

    @Column(name = "tem_deformacao")
    private Boolean temDeformacao;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "gera_alerta", nullable = false)
    private Boolean geraAlerta;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
        if (dataInspecao == null) dataInspecao = LocalDateTime.now();
        if (geraAlerta == null) geraAlerta = false;
    }
}
