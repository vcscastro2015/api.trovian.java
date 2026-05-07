package com.trovian.entity;

import com.trovian.enums.TipoRecapagem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "recapagem_pneu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecapagemPneu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Pneu é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pneu_id", nullable = false)
    private Pneu pneu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    @NotNull(message = "Número de recapagem é obrigatório")
    @Column(name = "numero_recapagem", nullable = false)
    private Integer numeroRecapagem;

    @NotNull(message = "Data de envio é obrigatória")
    @Column(name = "data_envio", nullable = false)
    private LocalDate dataEnvio;

    @Column(name = "data_retorno")
    private LocalDate dataRetorno;

    @Column(name = "km_envio")
    private Integer kmEnvio;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_recapagem", columnDefinition = "TEXT")
    private TipoRecapagem tipoRecapagem;

    @Column(name = "valor_recapagem", precision = 10, scale = 2)
    private BigDecimal valorRecapagem;

    @Column(name = "profundidade_apos_recapagem", precision = 5, scale = 2)
    private BigDecimal profundidadeAposRecapagem;

    @Column(name = "garantia_km")
    private Integer garantiaKm;

    @Column(name = "aprovado")
    private Boolean aprovado;

    @Column(name = "motivo_rejeicao", columnDefinition = "TEXT")
    private String motivoRejeicao;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
    }
}
