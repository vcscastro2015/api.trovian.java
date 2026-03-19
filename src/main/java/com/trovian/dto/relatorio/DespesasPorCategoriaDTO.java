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
@Schema(description = "Análise de Despesas por Categoria")
public class DespesasPorCategoriaDTO {

    private PeriodoDTO periodo;
    private ResumoGeralDTO resumoGeral;
    private List<CategoriaDetalheDTO> categorias;
    private List<Top10DespesaDTO> top10Despesas;
    private List<EvolucaoMensalDTO> evolucaoMensal;
    private ComparativoDTO comparativo;
    private InsightsDTO insights;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodoDTO {
        private LocalDate dataInicio;
        private LocalDate dataFim;
        private String descricao;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumoGeralDTO {
        private BigDecimal totalDespesas;
        private Integer quantidadeCategorias;
        private Integer quantidadeContas;
        private BigDecimal despesaMediaPorCategoria;
        private BigDecimal despesaMediaPorConta;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoriaDetalheDTO {
        private Long categoriaId;
        private String categoriaNome;
        private BigDecimal valorTotal;
        private Integer quantidadeContas;
        private BigDecimal percentualDoTotal;
        private BigDecimal ticketMedio;
        private BigDecimal variacaoPeriodoAnterior;
        private String tendencia;
        private Integer posicaoRanking;
        private List<FornecedorCategoria> principaisFornecedores;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FornecedorCategoria {
        private String fornecedorNome;
        private BigDecimal valor;
        private Integer quantidade;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Top10DespesaDTO {
        private Long contaId;
        private String descricao;
        private String fornecedorNome;
        private String categoriaNome;
        private BigDecimal valor;
        private LocalDate dataPagamento;
        private String veiculoPlaca;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvolucaoMensalDTO {
        private String mes;
        private List<ValorCategoriaDTO> categorias;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValorCategoriaDTO {
        private String categoriaNome;
        private BigDecimal valor;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComparativoDTO {
        private PeriodoDTO periodoAnterior;
        private BigDecimal totalDespesasAnterior;
        private BigDecimal variacao;
        private List<VariacaoCategoriaDTO> variacoesPorCategoria;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariacaoCategoriaDTO {
        private String categoriaNome;
        private BigDecimal valorAtual;
        private BigDecimal valorAnterior;
        private BigDecimal variacao;
        private String situacao;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InsightsDTO {
        private String principalCategoria;
        private BigDecimal percentualPrincipalCategoria;
        private String categoriaComMaiorAumento;
        private BigDecimal percentualAumento;
        private String categoriaComMaiorReducao;
        private BigDecimal percentualReducao;
        private List<String> alertas;
    }
}
