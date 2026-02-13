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
@Schema(description = "Fluxo de Caixa Realizado")
public class FluxoCaixaRealizadoDTO {

    private PeriodoDTO periodo;
    private SaldoInicialDTO saldoInicial;
    private EntradasDTO entradas;
    private SaidasDTO saidas;
    private ResultadoDTO resultado;
    private List<MovimentacaoMensalDTO> evolucaoMensal;
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
    public static class SaldoInicialDTO {
        private BigDecimal valor;
        private LocalDate data;
        private String descricao;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntradasDTO {
        private BigDecimal receitasRecebidas;
        private BigDecimal outrasEntradas;
        private BigDecimal totalEntradas;
        private Integer quantidadeRecebimentos;
        private BigDecimal mediaEntradaDiaria;
        private List<ItemEntradaDTO> detalhamento;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemEntradaDTO {
        private String categoria;
        private BigDecimal valor;
        private BigDecimal percentual;
        private Integer quantidade;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaidasDTO {
        private BigDecimal despesasPagas;
        private BigDecimal outrasSaidas;
        private BigDecimal totalSaidas;
        private Integer quantidadePagamentos;
        private BigDecimal mediaSaidaDiaria;
        private List<ItemSaidaDTO> detalhamento;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemSaidaDTO {
        private String categoria;
        private BigDecimal valor;
        private BigDecimal percentual;
        private Integer quantidade;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultadoDTO {
        private BigDecimal saldoPeriodo;
        private BigDecimal saldoFinal;
        private BigDecimal saldoMedioDiario;
        private String situacao;
        private BigDecimal taxaCrescimento;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovimentacaoMensalDTO {
        private String mes;
        private BigDecimal entradas;
        private BigDecimal saidas;
        private BigDecimal saldo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComparativoDTO {
        private PeriodoDTO periodoAnterior;
        private BigDecimal entradasAnterior;
        private BigDecimal saidasAnterior;
        private BigDecimal saldoAnterior;
        private BigDecimal variacaoEntradas;
        private BigDecimal variacaoSaidas;
        private BigDecimal variacaoSaldo;
    }
}
