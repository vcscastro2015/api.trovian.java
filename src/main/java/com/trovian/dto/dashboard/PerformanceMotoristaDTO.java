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
public class PerformanceMotoristaDTO {
    private Long motoristaId;
    private String nome;
    private Integer totalViagens;
    private Double mediaConsumoKmL;
    private BigDecimal comissaoTotal;
    private Integer viagensNoPrazo;
    private Integer viagensAtrasadas;
    private Double taxaCumprimentoPrazo;
    private Double kmTotalPercorrido;
}
