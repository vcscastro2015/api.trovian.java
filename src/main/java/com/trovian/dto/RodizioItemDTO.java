package com.trovian.dto;

import com.trovian.enums.PosicaoPneu;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Item de rodízio — par origem/destino de um pneu")
public class RodizioItemDTO {

    @NotNull(message = "ID do pneu é obrigatório")
    @Schema(required = true, example = "1", description = "ID do pneu a ser movido")
    private Long pneuId;

    @NotNull(message = "Posição de origem é obrigatória")
    @Schema(required = true, description = "Posição atual do pneu")
    private PosicaoPneu posicaoOrigem;

    @NotNull(message = "Posição de destino é obrigatória")
    @Schema(required = true, description = "Nova posição do pneu")
    private PosicaoPneu posicaoDestino;
}
