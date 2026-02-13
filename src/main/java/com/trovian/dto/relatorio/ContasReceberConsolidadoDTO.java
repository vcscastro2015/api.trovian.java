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
@Schema(description = "Relatório consolidado de contas a receber")
public class ContasReceberConsolidadoDTO {

    private PeriodoDTO periodo;
    private ResumoGeralDTO resumoGeral;
    private ContasPorStatusDTO contasPorStatus;
    private AgingDTO aging;
    private List<TopFornecedorDTO> topFornecedores;
    private List<ReceitaPorCategoriaDTO> receitasPorCategoria;
    private List<ContaReceberDetalheDTO> contas;
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
        private BigDecimal valorRecebido;
        private BigDecimal saldoAReceber;
        private Integer quantidadeTotal;
        private Integer quantidadeRecebida;
        private Integer quantidadePendente;
        private BigDecimal percentualRecebido;
        private BigDecimal taxaInadimplencia;
        private BigDecimal prazoMedioRecebimento;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContasPorStatusDTO {
        private StatusResumoDTO pendentes;
        private StatusResumoDTO recebidas;
        private StatusResumoDTO vencidas;
        private StatusResumoDTO parciais;
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
    public static class AgingDTO {
        private FaixaAgingDTO emDia;
        private FaixaAgingDTO atraso0a30;
        private FaixaAgingDTO atraso31a60;
        private FaixaAgingDTO atraso61a90;
        private FaixaAgingDTO atrasoMais90;
        private String analise;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FaixaAgingDTO {
        private String descricao;
        private Integer quantidade;
        private BigDecimal valor;
        private BigDecimal percentual;
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
        private BigDecimal prazoMedioRecebimento;
        private BigDecimal taxaInadimplencia;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceitaPorCategoriaDTO {
        private Long categoriaId;
        private String categoriaNome;
        private BigDecimal valor;
        private Integer quantidade;
        private BigDecimal percentual;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContaReceberDetalheDTO {
        private Long id;
        private String descricao;
        private String numeroDocumento;
        private String numeroCte;
        private String fornecedorNome;
        private String categoriaNome;
        private BigDecimal valorOriginal;
        private BigDecimal valorTotal;
        private BigDecimal valorRecebido;
        private BigDecimal saldo;
        private LocalDate dataEmissao;
        private LocalDate dataVencimento;
        private LocalDate dataRecebimento;
        private StatusConta status;
        private Integer diasAtraso;
        private Boolean vencida;
        private String origemFrete;
        private String destinoFrete;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertasDTO {
        private Integer contasVencidas;
        private BigDecimal valorVencido;
        private Integer contasVencemHoje;
        private Integer contasVencemEstaSemana;
        private Integer clientesInadimplentes;
        private List<String> mensagens;
    }
}
