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
public class PosicaoAtualDTO {
    private long veiculoId;
    private String placa;
    private Double latitude;
    private Double longitude;
    private Double velocidadeGps;
    private Boolean ignicaoAtiva;
    private Date dataTransmissao;
}
