package com.trovian.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardCompletoDTO {
    private DashboardResumoDTO resumo;
    private List<LucratividadeRotaDTO> lucratividadeRotas;
    private ComparacaoRotasDTO comparacaoRotas;
    private List<AlertaBaixaMargemDTO> alertas;
    private List<EficienciaVeiculoDTO> eficienciaVeiculos;
    private List<PerformanceMotoristaDTO> performanceMotoristas;
    private List<TendenciaLucratividadeDTO> tendencias;
    private CustoOperacionalDTO custosOperacionais;
    private List<IndicadorRotaDTO> indicadoresRotas;
    private List<MapaCalorRotaDTO> mapaCalor;
    private PrevisaoMetaDTO previsaoMeta;
}
