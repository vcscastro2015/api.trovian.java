package com.trovian.dto;

import com.trovian.enums.StatusComando;
import com.trovian.enums.TipoRecorrenciaComando;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para agendamento de comando por veículo")
public class AgendamentoComandoVeiculoDTO {

    @Schema(description = "ID do agendamento", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "Veículo é obrigatório")
    @Schema(description = "ID do veículo", example = "10", required = true)
    private Long veiculoId;

    @Schema(description = "Placa do veículo (somente leitura)", example = "ABC-1234", accessMode = Schema.AccessMode.READ_ONLY)
    private String veiculoPlaca;

    @NotNull(message = "Tipo de comando é obrigatório")
    @Schema(description = "Tipo do comando a ser executado",
            example = "BLOQUEAR",
            allowableValues = {"BLOQUEAR", "DESBLOQUEAR"},
            required = true)
    private StatusComando tipoComando;

    @NotNull(message = "Tipo de recorrência é obrigatório")
    @Schema(description = "Tipo de recorrência do agendamento",
            example = "DIARIO",
            allowableValues = {"DIARIO", "MENSAL", "DIA_ESPECIFICO"},
            required = true)
    private TipoRecorrenciaComando tipoRecorrencia;

    @NotNull(message = "Horário é obrigatório")
    @Schema(description = "Horário do disparo do comando (HH:mm:ss)", example = "22:00:00", required = true)
    private LocalTime horario;

    @Min(value = 1, message = "Dia do mês deve ser entre 1 e 31")
    @Max(value = 31, message = "Dia do mês deve ser entre 1 e 31")
    @Schema(description = "Dia do mês (obrigatório para recorrência MENSAL, 1–31)", example = "20")
    private Integer diaDomes;

    @Schema(description = "Data específica (obrigatória para recorrência DIA_ESPECIFICO)", example = "2026-06-20")
    private LocalDate dataEspecifica;

    @Schema(description = "Indica se o agendamento está ativo", example = "true")
    private Boolean ativo;

    @Schema(description = "Data e hora da última execução pelo scheduler", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime ultimaExecucao;

    @Schema(description = "Data de cadastro do agendamento", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataCadastro;

    @Schema(description = "Data da última atualização", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
