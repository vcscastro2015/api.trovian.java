package com.trovian.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpmDistribuicaoDTO {
    private Double pctLenta;
    private Double pctEco;
    private Double pctVerde;
    private Double pctAmarela;
    private Double pctAzul;
}
