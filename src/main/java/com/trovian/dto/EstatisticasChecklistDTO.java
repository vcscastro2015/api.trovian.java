package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para estatísticas de checklists realizados")
public class EstatisticasChecklistDTO {

    @Schema(description = "Total de checklists realizados", example = "150")
    private Long totalRealizados;

    @Schema(description = "Quantidade de checklists aprovados", example = "120")
    private Long aprovados;

    @Schema(description = "Quantidade de checklists reprovados", example = "20")
    private Long reprovados;

    @Schema(description = "Quantidade de checklists em andamento", example = "10")
    private Long emAndamento;

    @Schema(description = "Percentual de conformidade geral (0-100)", example = "85.5")
    private Double conformidadeGeral;
}
