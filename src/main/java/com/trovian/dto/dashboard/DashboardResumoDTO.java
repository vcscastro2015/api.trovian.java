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
public class DashboardResumoDTO {
    private Integer totalViagens;
    private Integer viagensAbertas;
    private Integer viagensFinalizadas;
    private BigDecimal receitaTotal;
    private BigDecimal lucroTotal;
    private Double margemMediaPercentual;
    private Double kmTotalPercorrido;
    private BigDecimal custoTotalCombustivel;
    private BigDecimal custoTotalPedagios;
    private Integer totalVeiculosAtivos;
    private Integer totalMotoristas;
    private String periodoAnalise;
}
