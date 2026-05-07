package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resumo da frota de pneus para dashboard")
public class ResumoFrotaPneusDTO {

    @Schema(description = "Total de pneus cadastrados")
    private Integer totalPneus;

    @Schema(description = "Pneus em uso")
    private Integer emUso;

    @Schema(description = "Pneus em recapagem")
    private Integer emRecapagem;

    @Schema(description = "Pneus em alerta (sulco ou pressão)")
    private Integer emAlerta;

    @Schema(description = "Pneus próximos da troca (sulco < 4mm ou idade > 4 anos)")
    private Integer proximosTroca;

    @Schema(description = "CPK médio da frota em R$/km")
    private BigDecimal cpkMedio;

    @Schema(description = "Custo total do mês atual em R$")
    private BigDecimal custoMesAtual;
}
