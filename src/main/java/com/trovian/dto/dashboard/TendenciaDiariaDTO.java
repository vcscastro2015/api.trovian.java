package com.trovian.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Contagem de notificações por dia para gráfico de tendência")
public class TendenciaDiariaDTO {

    @Schema(description = "Data no formato yyyy-MM-dd", example = "2025-06-29")
    private String data;

    @Schema(description = "Quantidade de notificações no dia")
    private long quantidade;
}
