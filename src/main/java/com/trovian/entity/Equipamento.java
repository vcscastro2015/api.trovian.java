package com.trovian.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "equipamento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_cadastro", updatable = false)
    private Date dataCadastro;

    @Column(name = "imei", length = 50)
    private String imei;

    @Column(name = "numero_celular", length = 20)
    private String numeroCelular;

    @Column(name = "numero_serial", length = 100)
    private String numeroSerial;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    @NotNull(message = "Operadora é obrigatória")
    @Size(min = 1, max = 15, message = "Operadora deve ter entre 1 e 15 caracteres")
    @Column(name = "operadora", nullable = false, length = 15)
    private String operadora;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "tipo_equipamento", length = 3)
    private String tipoEquipamento; // PR = Proprietario, PA = Particular

    @Column(name = "tipo_chip", length = 3)
    private String tipoChip; // PR = Proprietario, PA = Particular

    @NotNull(message = "Modelo é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modelo_id", nullable = false)
    private Modelo modelo;

    @Column(name = "equipamento_alocado")
    private Boolean equipamentoAlocado;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        dataCadastro = new Date();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
