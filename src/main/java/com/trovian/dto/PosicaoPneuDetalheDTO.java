package com.trovian.dto;

import com.trovian.enums.PosicaoPneu;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detalhe de uma posição do veículo no mapa de pneus")
public class PosicaoPneuDetalheDTO {

    @Schema(description = "Posição física no veículo")
    private PosicaoPneu posicao;

    @Schema(description = "Dados do pneu montado nessa posição (null se vazia)")
    private PneuDTO pneu;

    @Schema(description = "ID da alocação ativa")
    private Long alocacaoId;

    @Schema(description = "Última inspeção registrada para esta posição")
    private InspecaoPneuDTO ultimaInspecao;

    @Schema(description = "Semáforo de status: VERDE, AMARELO, VERMELHO")
    private String statusSemaforo;
}
