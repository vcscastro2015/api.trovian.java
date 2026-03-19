package com.trovian.dto.relatorio;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Relatório de Análise de Rotas")
public class AnaliseRotaDTO {

    @Schema(description = "Período analisado")
    private PeriodoDTO periodo;

    @Schema(description = "Resumo geral das rotas")
    private ResumoGeralDTO resumoGeral;

    @Schema(description = "Detalhamento por rota")
    private List<RotaDetalheDTO> rotas;

    @Schema(description = "Destaques das rotas")
    private TopRotaDTO topRotas;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodoDTO {
        private LocalDate dataInicio;
        private LocalDate dataFim;
        private String descricao;
        private Integer totalDias;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumoGeralDTO {
        private Integer totalRotas;
        private Long totalViagens;
        private BigDecimal distanciaTotalKm;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RotaDetalheDTO {
        private Long rotaId;
        private String nome;
        private Long totalViagens;
        private BigDecimal receitaTotal;
        private BigDecimal custoTotal;
        private BigDecimal lucroTotal;
        private BigDecimal margemPercentual;
        private BigDecimal distanciaKm;
        private BigDecimal custoPedagioMedio;
        private BigDecimal receitaPorKm;
        private Integer posicaoRanking;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopRotaDTO {
        private String maisRentavel;
        private String maisUtilizada;
        private String maiorDistancia;
    }
}
