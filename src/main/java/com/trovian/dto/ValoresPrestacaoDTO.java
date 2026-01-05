package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para valores da prestação de serviço no CT-e")
public class ValoresPrestacaoDTO {

    @Schema(description = "Valor total da prestação", example = "4364.80")
    private BigDecimal valorTotalPrestacao;

    @Schema(description = "Valor do pedágio", example = "0.00")
    private BigDecimal pedagio;

    @Schema(description = "Valor do despacho/tarifa", example = "133.48")
    private BigDecimal despachoArifa;

    @Schema(description = "Nomes dos componentes de valor")
    private List<String> nomesComponentes;

    @Schema(description = "Outros valores", example = "60.00")
    private BigDecimal outrosValores;
}
