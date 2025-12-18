package com.trovian.entity;

import com.trovian.enums.StatusChecklist;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidade que representa a execução/realização de um Checklist
 */
@Entity
@Table(name = "checklist_realizado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistRealizado {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "numero_checklist", length = 20, unique = true)
    private String numeroChecklist;

    @NotNull(message = "Modelo de checklist é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modelo_checklist_id", nullable = false)
    private ModeloChecklist modeloChecklist;

    @NotNull(message = "Veículo é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @NotNull(message = "Motorista é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motorista_id", nullable = false)
    private Motorista motorista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viagem_id")
    private Viagem viagem;

    @NotNull(message = "Data/hora de início é obrigatória")
    @Column(name = "data_hora_inicio", nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(name = "data_hora_conclusao")
    private LocalDateTime dataHoraConclusao;

    @Column(name = "km_veiculo")
    private Integer kmVeiculo;

    @NotNull(message = "Status é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusChecklist status;

    @Column(name = "observacoes_gerais", columnDefinition = "TEXT")
    private String observacoesGerais;

    @Column(name = "localizacao", length = 100)
    private String localizacao;

    @OneToMany(mappedBy = "checklistRealizado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RespostaItemChecklist> respostas = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.dataHoraInicio == null) {
            this.dataHoraInicio = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = StatusChecklist.EM_ANDAMENTO;
        }
    }

    /**
     * Finaliza o checklist e determina o status baseado nas respostas
     */
    public void finalizar() {
        this.dataHoraConclusao = LocalDateTime.now();

        // Verifica se há itens reprovados que requerem atenção
        boolean temItemCritico = respostas.stream()
            .anyMatch(r -> r.getRequerAtencao() != null &&
                          r.getRequerAtencao() &&
                          "NAO_OK".equals(r.getResposta()));

        this.status = temItemCritico ?
            StatusChecklist.REPROVADO : StatusChecklist.APROVADO;
    }
}
