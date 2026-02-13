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
@Schema(description = "Análise de Receitas por Cliente")
public class ReceitasPorClienteDTO {

    private PeriodoDTO periodo;
    private ResumoGeralDTO resumoGeral;
    private List<FornecedorDetalheDTO> fornecedores;
    private List<Top10ReceitaDTO> top10Receitas;
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
        private BigDecimal totalReceitas;
        private Integer quantidadeFornecedores;
        private Integer quantidadeFretes;
        private BigDecimal receitaMediaPorCliente;
        private BigDecimal ticketMedioFrete;
        private BigDecimal prazoMedioRecebimento;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FornecedorDetalheDTO {
        private Long fornecedorId;
        private String fornecedorNome;
        private BigDecimal valorTotal;
        private Integer quantidadeFretes;
        private BigDecimal percentualDoTotal;
        private BigDecimal ticketMedio;
        private BigDecimal prazoMedioRecebimento;
        private BigDecimal taxaInadimplencia;
        private String scoreCliente;
        private Integer posicaoRanking;
        private String tendencia;
        private List<RotaPrincipalDTO> rotasPrincipais;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RotaPrincipalDTO {
        private String origem;
        private String destino;
        private Integer quantidade;
        private BigDecimal valorTotal;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Top10ReceitaDTO {
        private Long contaId;
        private String descricao;
        private String fornecedorNome;
        private BigDecimal valor;
        private LocalDate dataRecebimento;
        private String origemFrete;
        private String destinoFrete;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvolucaoMensalDTO {
        private String mes;
        private List<ValorClienteDTO> clientes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValorClienteDTO {
        private String clienteNome;
        private BigDecimal valor;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComparativoDTO {
        private PeriodoDTO periodoAnterior;
        private BigDecimal totalReceitasAnterior;
        private BigDecimal variacao;
        private List<VariacaoClienteDTO> variacoesPorCliente;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariacaoClienteDTO {
        private String clienteNome;
        private BigDecimal valorAtual;
        private BigDecimal valorAnterior;
        private BigDecimal variacao;
        private String situacao;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InsightsDTO {
        private String principalCliente;
        private BigDecimal percentualPrincipalCliente;
        private Integer clientesTop5Representam;
        private String clienteComMaiorCrescimento;
        private BigDecimal percentualCrescimento;
        private Integer novosClientes;
        private Integer clientesInativos;
        private List<String> recomendacoes;
    }
}
