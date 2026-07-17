package com.trovian.entity;

import com.trovian.enums.TipoCombustivel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "abastecimento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Abastecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Veículo é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @NotNull(message = "Motorista é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motorista_id")
    private Motorista motorista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rota_id")
    private Rota rota;

    @NotNull(message = "Data e hora são obrigatórios")
    @Column(name = "data_hora", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHora;

    @NotNull(message = "KM do odômetro é obrigatório")
    @PositiveOrZero(message = "KM do odômetro deve ser positivo")
    @Column(name = "km_odometro", nullable = false)
    private Integer kmOdometro;

    @NotNull(message = "Litros abastecidos é obrigatório")
    @PositiveOrZero(message = "Litros abastecidos deve ser positivo")
    @Column(name = "litros_abastecidos", nullable = false, precision = 10, scale = 2)
    private BigDecimal litrosAbastecidos;

    @NotNull(message = "Valor total é obrigatório")
    @PositiveOrZero(message = "Valor total deve ser positivo")
    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @NotNull(message = "Preço por litro é obrigatório")
    @PositiveOrZero(message = "Preço por litro deve ser positivo")
    @Column(name = "preco_litro", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoLitro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id")
    private Local local;

    @NotNull(message = "Tipo de combustível é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "combustivel_tipo", nullable = false, length = 20)
    private TipoCombustivel combustivelTipo;

    @Column(name = "tanque_cheio")
    private Boolean tanqueCheio;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @NotNull(message = "Cliente é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "criado_em", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Column(name = "status")
    private Boolean status;

    private Boolean temImagem = false;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = new Date();
        this.atualizadoEm = LocalDateTime.now();
        if (this.status == null) {
            this.status = true;
        }
        if (this.tanqueCheio == null) {
            this.tanqueCheio = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
