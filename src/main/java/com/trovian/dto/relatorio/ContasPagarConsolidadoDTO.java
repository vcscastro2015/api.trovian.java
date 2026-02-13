package com.trovian.dto.relatorio;

import com.trovian.enums.StatusConta;
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
@Schema(description = "Relatório consolidado de contas a pagar")
public class ContasPagarConsolidadoDTO {

    @Schema(description = "Período do relatório")
    private PeriodoDTO periodo;

    @Schema(description = "Resumo geral")
    private ResumoGeralDTO resumoGeral;

    @Schema(description = "Contas por status")
    private ContasPorStatusDTO contasPorStatus;

    @Schema(description = "Análise de vencimentos")
    private VencimentosDTO vencimentos;

    @Schema(description = "Top fornecedores")
    private List<TopFornecedorDTO> topFornecedores;

    @Schema(description = "Despesas por categoria")
    private List<DespesaPorCategoriaDTO> despesasPorCategoria;

    @Schema(description = "Lista detalhada de contas")
    private List<ContaPagarDetalheDTO> contas;

    @Schema(description = "Alertas e avisos")
    private AlertasDTO alertas;

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
        private BigDecimal valorTotalContas;
        private BigDecimal valorPago;
        private BigDecimal saldoAPagar;
        private Integer quantidadeTotal;
        private Integer quantidadePaga;
        private Integer quantidadePendente;
        private BigDecimal percentualPago;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContasPorStatusDTO {
        private StatusResumoDTO pendentes;
        private StatusResumoDTO pagas;
        private StatusResumoDTO vencidas;
        private StatusResumoDTO parciais;
        private StatusResumoDTO canceladas;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusResumoDTO {
        private Integer quantidade;
        private BigDecimal valor;
        private BigDecimal percentual;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VencimentosDTO {
        private VencimentoFaixaDTO vencidas;
        private VencimentoFaixaDTO proximos7Dias;
        private VencimentoFaixaDTO proximos30Dias;
        private VencimentoFaixaDTO proximos60Dias;
        private VencimentoFaixaDTO proximos90Dias;
        private VencimentoFaixaDTO apos90Dias;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VencimentoFaixaDTO {
        private Integer quantidade;
        private BigDecimal valor;
        private String descricao;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopFornecedorDTO {
        private Long fornecedorId;
        private String fornecedorNome;
        private BigDecimal valorTotal;
        private Integer quantidadeContas;
        private BigDecimal percentualDoTotal;
        private BigDecimal ticketMedio;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DespesaPorCategoriaDTO {
        private Long categoriaId;
        private String categoriaNome;
        private BigDecimal valor;
        private Integer quantidade;
        private BigDecimal percentual;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContaPagarDetalheDTO {
        private Long id;
        private String descricao;
        private String numeroDocumento;
        private String fornecedorNome;
        private String categoriaNome;
        private BigDecimal valorOriginal;
        private BigDecimal valorTotal;
        private BigDecimal valorPago;
        private BigDecimal saldo;
        private LocalDate dataEmissao;
        private LocalDate dataVencimento;
        private LocalDate dataPagamento;
        private StatusConta status;
        private Integer diasAtraso;
        private Boolean vencida;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertasDTO {
        private Integer contasVencidas;
        private BigDecimal valorVencido;
        private Integer contasVencemHoje;
        private Integer contasVencemEstaSemana;
        private List<String> mensagens;
    }
}
