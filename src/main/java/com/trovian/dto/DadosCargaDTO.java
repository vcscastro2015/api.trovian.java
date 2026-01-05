package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para dados da carga no CT-e")
public class DadosCargaDTO {

    @Schema(description = "Produto predominante", example = "FARINHA DE SOJA 46% PELLET GR")
    private String produtoPredominante;

    @Schema(description = "Outras características", example = "GRANEL")
    private String outrasCaracteristicas;

    @Schema(description = "Quantidade", example = "4")
    private Integer quantidade;

    @Schema(description = "Unidade", example = "UN")
    private String unidade;

    @Schema(description = "Peso bruto em kg", example = "22400")
    private Double pesoBruto;

    @Schema(description = "Peso líquido em kg", example = "22400")
    private Double pesoLiquido;

    @Schema(description = "Valor total da mercadoria", example = "52000.00")
    private BigDecimal valorTotalMercadoria;
}
