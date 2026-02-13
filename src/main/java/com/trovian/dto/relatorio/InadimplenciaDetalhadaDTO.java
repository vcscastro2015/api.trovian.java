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
@Schema(description = "Relatório Detalhado de Inadimplência")
public class InadimplenciaDetalhadaDTO {

    private LocalDate dataBase;
    private TipoAnalise tipo;
    private ResumoInadimplenciaDTO resumo;
    private AgingDetalhadoDTO aging;
    private List<InadimplenteDTO> inadimplentes;
    private List<ContaVencidaDTO> contasVencidas;
    private ImpactoFinanceiroDTO impacto;
    private List<AcaoRecomendadaDTO> acoesRecomendadas;
    private HistoricoDTO historico;

    public enum TipoAnalise {
        RECEBER,
        PAGAR,
        AMBOS
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumoInadimplenciaDTO {
        private Integer totalContasVencidas;
        private BigDecimal valorTotalVencido;
        private Integer diasMediaAtraso;
        private BigDecimal taxaInadimplencia;
        private BigDecimal maiorAtraso;
        private Integer quantidadeInadimplentes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgingDetalhadoDTO {
        private FaixaAgingDTO ate30Dias;
        private FaixaAgingDTO de31a60Dias;
        private FaixaAgingDTO de61a90Dias;
        private FaixaAgingDTO de91a180Dias;
        private FaixaAgingDTO acima180Dias;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FaixaAgingDTO {
        private String descricao;
        private Integer quantidade;
        private BigDecimal valor;
        private BigDecimal percentual;
        private String nivelRisco;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InadimplenteDTO {
        private Long entidadeId;
        private String nome;
        private String tipo;
        private Integer quantidadeContasVencidas;
        private BigDecimal valorTotalVencido;
        private Integer diasMediaAtraso;
        private Integer maiorAtraso;
        private BigDecimal taxaInadimplenciaHistorica;
        private String scoreRisco;
        private String recomendacao;
        private List<ContatoDTO> contatos;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContatoDTO {
        private String tipo;
        private String valor;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContaVencidaDTO {
        private Long contaId;
        private String tipo;
        private String descricao;
        private String entidadeNome;
        private BigDecimal valorOriginal;
        private BigDecimal valorPagoRecebido;
        private BigDecimal saldo;
        private LocalDate dataEmissao;
        private LocalDate dataVencimento;
        private Integer diasAtraso;
        private BigDecimal jurosCalculado;
        private BigDecimal multaCalculada;
        private BigDecimal valorAtualizado;
        private String prioridade;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImpactoFinanceiroDTO {
        private BigDecimal valorBloqueado;
        private BigDecimal jurosAcumulados;
        private BigDecimal multasAcumuladas;
        private BigDecimal perdaEstimada;
        private BigDecimal impactoFluxoCaixa;
        private String situacao;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AcaoRecomendadaDTO {
        private String prioridade;
        private String acao;
        private String entidadeNome;
        private BigDecimal valorEnvolvido;
        private String prazo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoricoDTO {
        private List<EvolucaoMensalDTO> evolucao;
        private String tendencia;
        private BigDecimal variacaoMesAnterior;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvolucaoMensalDTO {
        private String mes;
        private Integer quantidadeVencidas;
        private BigDecimal valorVencido;
        private BigDecimal taxaInadimplencia;
    }
}
