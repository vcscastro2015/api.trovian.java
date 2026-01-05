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
public class MapaCalorRotaDTO {
    private Long rotaId;
    private String nomeRota;
    private Double latitudeOrigem;
    private Double longitudeOrigem;
    private Double latitudeDestino;
    private Double longitudeDestino;
    private BigDecimal lucratividade;
    private Integer frequenciaViagens;
    private String intensidade;
    private String cor;
}
