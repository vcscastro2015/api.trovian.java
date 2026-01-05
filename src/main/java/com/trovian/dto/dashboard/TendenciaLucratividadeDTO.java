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
public class TendenciaLucratividadeDTO {
    private String periodo;
    private BigDecimal receita;
    private BigDecimal custoTotal;
    private BigDecimal lucro;
    private Double margemPercentual;
    private Integer totalViagens;
}
