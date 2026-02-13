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
@Schema(description = "Demonstrativo de Resultado do Exercício")
public class DreDTO {

    @Schema(description = "Período analisado")
    private PeriodoDTO periodo;

    @Schema(description = "Informações de filtros aplicados")
    private FiltrosDTO filtros;

    @Schema(description = "Receitas do período")
    private ReceitasDTO receitas;

    @Schema(description = "Despesas do período")
    private DespesasDTO despesas;

    @Schema(description = "Resultado financeiro")
    private ResultadoDTO resultado;

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
        private Long centroCustoId;
        private String centroCustoNome;
        private Boolean compararPeriodoAnterior;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceitasDTO {
        private BigDecimal receitasFrete;
        private BigDecimal outrasReceitas;
        private BigDecimal totalReceitas;
        private Integer quantidadeFretes;
        private BigDecimal ticketMedioFrete;
        private List<ItemReceitaDTO> detalhamento;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemReceitaDTO {
        private String categoria;
        private BigDecimal valor;
        private BigDecimal percentual;
        private Integer quantidade;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DespesasDTO {
        private BigDecimal combustivel;
        private BigDecimal manutencao;
        private BigDecimal pneus;
        private BigDecimal pedagios;
        private BigDecimal seguro;
        private BigDecimal licenciamento;
        private BigDecimal salarios;
        private BigDecimal pecas;
        private BigDecimal outrasDespesas;
        private BigDecimal totalDespesas;
        private List<ItemDespesaDTO> detalhamento;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDespesaDTO {
        private String categoria;
        private BigDecimal valor;
        private BigDecimal percentual;
        private Integer quantidade;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultadoDTO {
        private BigDecimal lucroBruto;
        private BigDecimal margemLucro;
        private BigDecimal receitaMedia;
        private BigDecimal despesaMedia;
        private String situacao;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComparativoDTO {
        private PeriodoDTO periodoAnterior;
        private BigDecimal receitaAnterior;
        private BigDecimal despesaAnterior;
        private BigDecimal lucroAnterior;
        private BigDecimal variacaoReceita;
        private BigDecimal variacaoDespesa;
        private BigDecimal variacaoLucro;
        private String tendencia;
    }
}
