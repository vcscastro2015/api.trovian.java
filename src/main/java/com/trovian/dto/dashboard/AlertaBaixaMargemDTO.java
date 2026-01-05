package com.trovian.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private LocalDateTime dataViagem;
    private String nivelAlerta;
    private String motivo;
}
