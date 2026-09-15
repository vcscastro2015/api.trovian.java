package com.trovian.dto.dashboard;

import lombok.AllArgsConstructor;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertaBaixaMargemDTO {
    private Long viagemId;
    private String nomeRota;
    private String nomeMotorista;
    private String placaVeiculo;
    private BigDecimal margemPercentual;
    private BigDecimal lucro;
    private BigDecimal receita;
    private OffsetDateTime dataViagem;
    private String nivelAlerta;
    private String motivo;
}
