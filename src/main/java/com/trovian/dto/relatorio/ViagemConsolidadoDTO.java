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
@Schema(description = "Relatório Consolidado de Viagens")
public class ViagemConsolidadoDTO {

    @Schema(description = "Período analisado")
    private PeriodoDTO periodo;

    @Schema(description = "Filtros aplicados")
    private FiltrosDTO filtros;

    @Schema(description = "Resumo geral das viagens")
    private ResumoGeralDTO resumoGeral;

    @Schema(description = "Resumo de custos operacionais")
    private CustosResumoDTO custosResumo;

    @Schema(description = "Evolução mensal das viagens")
    private List<EvolucaoMensalDTO> evolucaoMensal;

    @Schema(description = "Comparativo com período anterior")
    private ComparativoDTO comparativo;

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
    public static class FiltrosDTO {
        private Long clienteId;
        private String clienteNome;
        private String statusViagem;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumoGeralDTO {
        private Long totalViagens;
        private Long viagensAbertas;
        private Long viagensAnalise;
        private Long viagensFechadas;
        private BigDecimal receitaBruta;
        private BigDecimal custoTotal;
        private BigDecimal lucroLiquido;
        private BigDecimal margemMedia;
        private BigDecimal kmTotalPercorrido;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustosResumoDTO {
        private BigDecimal combustivel;
        private BigDecimal pedagios;
        private BigDecimal comissoes;
        private BigDecimal totalCustos;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvolucaoMensalDTO {
        private String mes;
        private Long totalViagens;
        private BigDecimal receita;
        private BigDecimal custos;
        private BigDecimal lucro;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComparativoDTO {
        private PeriodoDTO periodoAnterior;
        private BigDecimal receitaAnterior;
        private BigDecimal custoAnterior;
        private BigDecimal lucroAnterior;
        private BigDecimal variacaoReceita;
        private BigDecimal variacaoCusto;
        private BigDecimal variacaoLucro;
        private String tendencia;
    }
}
