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
public class LucratividadeRotaDTO {
    private Long rotaId;
    private String nomeRota;
    private Integer totalViagens;
    private BigDecimal receitaTotal;
    private BigDecimal custoTotal;
    private BigDecimal lucroTotal;
    private Double margemPercentual;
    private Double distanciaMediaKm;
    private BigDecimal lucroMedioPorViagem;
    private BigDecimal receitaPorKm;
    private String classificacao;
}
