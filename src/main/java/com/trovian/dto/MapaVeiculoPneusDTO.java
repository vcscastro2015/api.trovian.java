package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mapa visual de posições de pneus de um veículo")
public class MapaVeiculoPneusDTO {

    @Schema(description = "ID do veículo")
    private Long veiculoId;

    @Schema(description = "Placa do veículo")
    private String placa;

    @Schema(description = "Posições com seus pneus atuais")
    private List<PosicaoPneuDetalheDTO> posicoes;
}
