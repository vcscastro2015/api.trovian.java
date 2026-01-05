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
public class PrevisaoMetaDTO {
    private String periodo;
    private BigDecimal metaReceita;
    private BigDecimal receitaAtual;
    private BigDecimal metaLucro;
    private BigDecimal lucroAtual;
    private Double percentualAlcancadoReceita;
    private Double percentualAlcancadoLucro;
    private BigDecimal projecaoReceita;
    private BigDecimal projecaoLucro;
    private String status;
}
