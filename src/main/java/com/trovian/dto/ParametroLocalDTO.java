package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para parâmetros do local")
public class ParametroLocalDTO {

    @Schema(description = "ID do parâmetro local", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Limite de veículos no mesmo local", example = "10")
    private Integer limiteVeiculosMesmoLocal;

    @Schema(description = "Tempo mínimo de permanência (em minutos)", example = "30")
    private Integer tempoMinimoDePermanencia;

    @Schema(description = "Tempo máximo de permanência (em minutos)", example = "120")
    private Integer tempoMaximoDePermanencia;
}
