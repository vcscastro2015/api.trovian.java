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
public class IndicadorRotaDTO {
    private Long rotaId;
    private String nomeRota;
    private Double taxaOcupacao;
    private Double kmProdutivo;
    private Double kmVazio;
    private Double taxaRetornoVazio;
    private BigDecimal receitaPorKm;
    private Double tempoMedioViagem;
}
