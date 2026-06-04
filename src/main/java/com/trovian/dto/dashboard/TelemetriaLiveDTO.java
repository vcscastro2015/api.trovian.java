package com.trovian.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetriaLiveDTO {
    private Integer telemetriaId;
    private long veiculoId;
    private String placa;
    private Date timestamp;
    private String tipoEvento;
    private Integer velocidade;
    private Integer rpm;
    private Double latitude;
    private Double longitude;
}
