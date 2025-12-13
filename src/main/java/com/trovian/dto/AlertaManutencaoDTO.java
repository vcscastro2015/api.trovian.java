package com.trovian.dto;

import com.trovian.enums.PrioridadeAlerta;
import com.trovian.enums.TipoAlerta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para Alerta de Manutenção")
public class AlertaManutencaoDTO {

    @Schema(description = "ID do alerta", example = "1")
    private Long id;

    @NotNull(message = "Tipo de alerta é obrigatório")
    @Schema(description = "Tipo do alerta", example = "REVISAO_PREVENTIVA")
    private TipoAlerta tipoAlerta;

    @NotNull(message = "Prioridade é obrigatória")
    @Schema(description = "Prioridade do alerta", example = "ALTA")
    private PrioridadeAlerta prioridade;

    @NotNull(message = "Veículo é obrigatório")
    @Schema(description = "ID do veículo", example = "1")
    private Long veiculoId;

    @Schema(description = "Placa do veículo")
    private String veiculoPlaca;

    @Schema(description = "ID da ordem de serviço relacionada", example = "1")
    private Long ordemServicoId;

    @Schema(description = "Número da ordem de serviço")
    private String ordemServicoNumero;

    @Schema(description = "ID da peça relacionada", example = "1")
    private Long pecaId;

    @Schema(description = "Código da peça")
    private String pecaCodigo;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
    @Schema(description = "Título do alerta", example = "Revisão preventiva necessária")
    private String titulo;

    @NotBlank(message = "Mensagem é obrigatória")
    @Size(max = 5000, message = "Mensagem deve ter no máximo 5000 caracteres")
    @Schema(description = "Mensagem detalhada do alerta")
    private String mensagem;

    @Schema(description = "Quilometragem do veículo no momento do alerta", example = "50000")
    private Integer kmVeiculo;

    @Schema(description = "Data e hora de geração do alerta")
    private LocalDateTime dataGeracao;

    @Schema(description = "Data e hora de leitura do alerta")
    private LocalDateTime dataLeitura;

    @Schema(description = "Indica se o alerta foi lido", example = "false")
    private Boolean lido;

    @Schema(description = "Indica se o alerta foi resolvido", example = "false")
    private Boolean resolvido;

    @Schema(description = "Data e hora de resolução do alerta")
    private LocalDateTime dataResolucao;

    @Schema(description = "Observação sobre a resolução do alerta")
    private String observacaoResolucao;
}
