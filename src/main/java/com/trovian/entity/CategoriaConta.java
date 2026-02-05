package com.trovian.entity;

import com.trovian.enums.TipoConta;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "categoria_conta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaConta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoConta tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_pai_id")
    private CategoriaConta categoriaPai;

    @Column(length = 50)
    private String codigo;

    @Column(nullable = false)
    private Boolean status = true;

    @Column(nullable = false)
    private Boolean podeEditar = true;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false, updatable = false)
    private Date dataCadastro = new Date();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @NotNull(message = "Cliente é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
