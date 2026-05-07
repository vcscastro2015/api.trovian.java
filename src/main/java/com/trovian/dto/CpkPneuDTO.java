package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "KPI de Custo Por Km de um pneu")
public class CpkPneuDTO {

    @Schema(description = "ID do pneu")
    private Long pneuId;

    @Schema(description = "Número DOT do pneu")
    private String numeroDot;

    @Schema(description = "Marca do pneu")
    private String marca;

    @Schema(description = "Modelo do pneu")
    private String modelo;

    @Schema(description = "Total de km rodados")
    private Integer kmTotal;

    @Schema(description = "Custo total (compra + recapagens) em R$")
    private BigDecimal custoTotal;

    @Schema(description = "CPK em R$/km")
    private BigDecimal cpk;
}
