package com.trovian.entity;

import com.trovian.enums.CategoriaItem;
import com.trovian.enums.TipoResposta;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entidade que representa um Item de um Modelo de Checklist
 */
@Entity
@Table(name = "item_modelo_checklist")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemModeloChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Modelo de checklist é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modelo_checklist_id", nullable = false)
    private ModeloChecklist modeloChecklist;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    @Column(name = "descricao", nullable = false, length = 255)
    private String descricao;

    @NotNull(message = "Ordem é obrigatória")
    @Column(name = "ordem", nullable = false)
    private Integer ordem;

    @NotNull(message = "Campo obrigatório é obrigatório")
    @Column(name = "obrigatorio", nullable = false)
    private Boolean obrigatorio = true;

    @NotNull(message = "Permite foto é obrigatório")
    @Column(name = "permite_foto", nullable = false)
    private Boolean permiteFoto = false;

    @NotNull(message = "Permite observação é obrigatório")
    @Column(name = "permite_observacao", nullable = false)
    private Boolean permiteObservacao = false;

    @NotNull(message = "Categoria é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false, length = 20)
    private CategoriaItem categoria;

    @NotNull(message = "Tipo de resposta é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_resposta", nullable = false, length = 20)
    private TipoResposta tipoResposta;

    @PrePersist
    protected void onCreate() {
        if (this.obrigatorio == null) {
            this.obrigatorio = true;
        }
        if (this.permiteFoto == null) {
            this.permiteFoto = false;
        }
        if (this.permiteObservacao == null) {
            this.permiteObservacao = false;
        }
    }
}
