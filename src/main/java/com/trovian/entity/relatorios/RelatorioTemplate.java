package com.trovian.entity.relatorios;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "relatorio_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CategoriaRelatorio categoria;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String queryBase;

    @Column(columnDefinition = "TEXT")
    private String queryCampos; // SELECT personalizado

    @Column(columnDefinition = "TEXT")
    private String queryJoins; // JOINs necessários

    @Column(columnDefinition = "TEXT")
    private String queryGroupBy; // Agrupamentos

    @Column(columnDefinition = "TEXT")
    private String queryOrderBy; // Ordenações

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(nullable = false)
    private Boolean sistemaTemplate = false; // Templates do sistema não podem ser deletados

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParametroRelatorio> parametros = new ArrayList<>();

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampoRelatorio> campos = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
