package com.trovian.entity;

import com.trovian.enums.TipoHodometro;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "hodometro")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hodometro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Data de cadastro é obrigatória")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private Date dataCadastro;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @NotNull(message = "Hodômetro é obrigatório")
    @Column(name = "hodometro", nullable = false)
    private Double hodometro;

    @Column(name = "hodometro_rastreador")
    private Double hodometroRastreador;

    @NotNull(message = "Veículo é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo", referencedColumnName = "id", nullable = false)
    private Veiculo veiculo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", columnDefinition = "TEXT")
    private TipoHodometro tipo;

    @Column(name = "id_transmissao")
    private Integer idTransmissao;

    @Column(name = "nome_motorista", columnDefinition = "TEXT")
    private String nomeMotorista;

    @Column(name = "id_motorista")
    private Integer idMotorista;

    @PrePersist
    protected void onCreate() {
        dataCadastro = new Date();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
