package com.trovian.entity;

import com.trovian.enums.StatusComando;
import com.trovian.enums.TipoRecorrenciaComando;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "agendamento_comando_veiculo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoComandoVeiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comando", nullable = false, length = 20)
    private StatusComando tipoComando;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_recorrencia", nullable = false, length = 20)
    private TipoRecorrenciaComando tipoRecorrencia;

    @Column(name = "horario", nullable = false)
    private LocalTime horario;

    @Column(name = "dia_do_mes")
    private Integer diaDomes;

    @Column(name = "data_especifica")
    private LocalDate dataEspecifica;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "ultima_execucao")
    private OffsetDateTime ultimaExecucao;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private OffsetDateTime dataCadastro;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        dataCadastro = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
