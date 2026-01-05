package com.trovian.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustoOperacionalDTO {
    private BigDecimal combustivel;
    private BigDecimal pedagios;
    private BigDecimal manutencao;
    private BigDecimal comissoes;
    private BigDecimal impostos;
    private BigDecimal outros;
    private BigDecimal total;

    private Double percentualCombustivel;
    private Double percentualPedagios;
    private Double percentualManutencao;
    private Double percentualComissoes;
    private Double percentualImpostos;
}
