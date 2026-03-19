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
@Schema(description = "Relatório de Análise de Custos de Viagem")
public class AnaliseCustosViagemDTO {

    @Schema(description = "Período analisado")
    private PeriodoDTO periodo;

    @Schema(description = "Composição dos custos")
    private ComposicaoCustosDTO composicaoCustos;

    @Schema(description = "Viagens com baixa margem")
    private List<ViagemBaixaMargemDTO> viagensBaixaMargem;

    @Schema(description = "Estatísticas de margem")
    private EstatisticasDTO estatisticas;

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
    public static class ComposicaoCustosDTO {
        private BigDecimal combustivel;
        private BigDecimal combustivelPercentual;
        private BigDecimal pedagios;
        private BigDecimal pedagiosPercentual;
        private BigDecimal comissoes;
        private BigDecimal comissoesPercentual;
        private BigDecimal total;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ViagemBaixaMargemDTO {
        private Long viagemId;
        private String rotaNome;
        private String motoristaNome;
        private String veiculoPlaca;
        private BigDecimal margem;
        private BigDecimal lucro;
        private BigDecimal receita;
        private LocalDate dataViagem;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstatisticasDTO {
        private BigDecimal margemMediaGeral;
        private BigDecimal margemMaxima;
        private BigDecimal margemMinima;
        private BigDecimal desvioPadrao;
        private Long viagensComPrejuizo;
        private Long viagensComLucro;
    }
}
