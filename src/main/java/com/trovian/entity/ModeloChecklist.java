package com.trovian.entity;

import com.trovian.enums.Periodicidade;
import com.trovian.enums.TipoChecklist;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Entidade que representa um Modelo/Template de Checklist reutilizável
 */
@Entity
@Table(name = "modelo_checklist")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModeloChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @NotNull(message = "Tipo é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoChecklist tipo;

    @NotNull(message = "Status ativo é obrigatório")
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @NotNull(message = "Campo obrigatório é obrigatório")
    @Column(name = "obrigatorio", nullable = false)
    private Boolean obrigatorio = false;

    @NotNull(message = "Periodicidade é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(name = "periodicidade", nullable = false, length = 20)
    private Periodicidade periodicidade;

    @OneToMany(mappedBy = "modeloChecklist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemModeloChecklist> itens = new ArrayList<>();

    @NotNull(message = "Cliente é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "criado_em", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = new Date();
        this.atualizadoEm = LocalDateTime.now();
        if (this.ativo == null) {
            this.ativo = true;
        }
        if (this.obrigatorio == null) {
            this.obrigatorio = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
