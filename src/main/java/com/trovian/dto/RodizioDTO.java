package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requisição de rodízio de pneus")
public class RodizioDTO {

    @NotNull(message = "Veículo é obrigatório")
    @Schema(required = true, example = "1", description = "ID do veículo")
    private Long veiculoId;

    @Schema(example = "52000", description = "Odômetro atual")
    private Integer km;

    @Schema(example = "João Técnico", description = "Responsável pela operação")
    private String responsavel;

    @Valid
    @NotNull(message = "Lista de posições é obrigatória")
    @Schema(required = true, description = "Pares de origem/destino para o rodízio")
    private List<RodizioItemDTO> posicoes;
}