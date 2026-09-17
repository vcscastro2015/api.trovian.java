package com.trovian.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetriaLiveDTO {
    private Long telemetriaId;
    private long veiculoId;
    private String placa;
    private OffsetDateTime timestamp;
    private String tipoEvento;
    private Integer velocidade;
    private Integer rpm;
    private Double latitude;
    private Double longitude;
}
