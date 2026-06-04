package com.trovian.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopVeiculoDTO {
    private String placa;
    private long eventos;
    private double km;
    private Double eventosPor100km;
}
