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
public class EficienciaVeiculoDTO {
    private Long veiculoId;
    private String placa;
    private String modelo;
    private Integer totalViagens;
    private BigDecimal lucroTotal;
    private BigDecimal receitaTotal;
    private Double kmTotal;
    private Double mediaConsumoKmL;
    private BigDecimal custoManutencao;
    private BigDecimal lucroPorKm;
    private Double taxaOcupacao;
    private String statusManutencao;
}
