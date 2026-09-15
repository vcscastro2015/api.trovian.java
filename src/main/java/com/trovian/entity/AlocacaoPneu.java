package com.trovian.entity;

import com.trovian.enums.MotivoDesmontagem;
import com.trovian.enums.PosicaoPneu;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "alocacao_pneu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlocacaoPneu {

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

    @NotNull(message = "Posição é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(name = "posicao", nullable = false, columnDefinition = "TEXT")
    private PosicaoPneu posicao;

    @NotNull(message = "Data de montagem é obrigatória")
    @Column(name = "data_montagem", nullable = false)
    private OffsetDateTime dataMontagem;

    @Column(name = "data_remocao")
    private OffsetDateTime dataRemocao;

    @Column(name = "km_montagem")
    private Integer kmMontagem;

    @Column(name = "km_remocao")
    private Integer kmRemocao;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_remocao", columnDefinition = "TEXT")
    private MotivoDesmontagem motivoRemocao;

    @Column(name = "responsavel", columnDefinition = "TEXT")
    private String responsavel;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private OffsetDateTime dataCadastro;

    @Column(name = "cliente_id")
    private Long clienteId;

    @PrePersist
    protected void onCreate() {
        dataCadastro = OffsetDateTime.now();
        if (dataMontagem == null) dataMontagem = OffsetDateTime.now();
    }
}
