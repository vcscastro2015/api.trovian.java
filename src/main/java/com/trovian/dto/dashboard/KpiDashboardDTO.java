package com.trovian.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KpiDashboardDTO {
    private double kmTotal;
    private double litrosTotal;
    private Double kmPorLitro;
    private long eventosBruscos;
    private double scoreMedio;
    private long veiculosAtivos;
}
