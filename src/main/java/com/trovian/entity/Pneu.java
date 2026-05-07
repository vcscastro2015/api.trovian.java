package com.trovian.entity;

import com.trovian.enums.StatusPneu;
import com.trovian.enums.TipoEixo;
import com.trovian.enums.TipoPneu;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pneu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pneu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Número DOT é obrigatório")
    @Column(name = "numero_dot", nullable = false, columnDefinition = "TEXT")
    private String numeroDot;

    @Column(name = "numero_fogo", columnDefinition = "TEXT")
    private String numeroFogo;

    @Column(name = "marca", columnDefinition = "TEXT")
    private String marca;

    @Column(name = "modelo", columnDefinition = "TEXT")
    private String modelo;

    @Column(name = "dimensao", columnDefinition = "TEXT")
    private String dimensao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pneu", columnDefinition = "TEXT")
    private TipoPneu tipoPneu;

    @Enumerated(EnumType.STRING)
    @Column(name = "eixo", columnDefinition = "TEXT")
    private TipoEixo eixo;

    @Column(name = "data_fabricacao")
    private LocalDate dataFabricacao;

    @Column(name = "data_compra")
    private LocalDate dataCompra;

    @Column(name = "valor_compra", precision = 10, scale = 2)
    private BigDecimal valorCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    @Column(name = "profundidade_inicial", precision = 5, scale = 2)
    private BigDecimal profundidadeInicial;

    @Column(name = "profundidade_minima", precision = 5, scale = 2)
    private BigDecimal profundidadeMinima;

    @Column(name = "km_limite")
    private Integer kmLimite;

    @NotNull(message = "Status é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "TEXT")
    private StatusPneu status;

    @Column(name = "numero_recapagens", nullable = false)
    private Integer numeroRecapagens;

    @Column(name = "km_acumulado", nullable = false)
    private Integer kmAcumulado;

    @NotNull(message = "Cliente é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = StatusPneu.NOVO;
        if (numeroRecapagens == null) numeroRecapagens = 0;
        if (kmAcumulado == null) kmAcumulado = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
