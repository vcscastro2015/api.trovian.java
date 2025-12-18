package com.trovian.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entidade que representa a resposta de um item durante a execução do checklist
 */
@Entity
@Table(name = "resposta_item_checklist")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespostaItemChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Checklist realizado é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_realizado_id", nullable = false)
    private ChecklistRealizado checklistRealizado;

    @NotNull(message = "Item do modelo é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_modelo_id", nullable = false)
    private ItemModeloChecklist itemModelo;

    @NotBlank(message = "Resposta é obrigatória")
    @Column(name = "resposta", nullable = false, length = 255)
    private String resposta;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Column(name = "requer_atencao", nullable = false)
    private Boolean requerAtencao = false;

    @PrePersist
    protected void onCreate() {
        if (this.requerAtencao == null) {
            this.requerAtencao = false;
        }
    }
}
