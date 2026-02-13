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
@Schema(description = "Relatório de Análise de Combustível")
public class AnaliseCombustivelDTO {

    @Schema(description = "Período analisado")
    private PeriodoDTO periodo;

    @Schema(description = "Resumo geral de combustível")
    private ResumoGeralDTO resumoGeral;

    @Schema(description = "Consumo por veículo")
    private List<ConsumoPorVeiculoDTO> consumoPorVeiculo;

    @Schema(description = "Consumo por rota")
    private List<ConsumoPorRotaDTO> consumoPorRota;

    @Schema(description = "Evolução mensal do combustível")
    private List<EvolucaoMensalDTO> evolucaoMensal;

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
        private BigDecimal custoTotalCombustivel;
        private BigDecimal litrosTotaisEstimados;
        private BigDecimal percentualDaReceita;
        private BigDecimal consumoMedioKmL;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsumoPorVeiculoDTO {
        private Long veiculoId;
        private String placa;
        private BigDecimal consumoEstimadoLitros;
        private BigDecimal custoCombustivel;
        private BigDecimal mediaKmL;
        private BigDecimal kmRodados;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsumoPorRotaDTO {
        private Long rotaId;
        private String nome;
        private BigDecimal consumoMedioLitros;
        private String classificacaoTerreno;
        private BigDecimal fatorTerrenoMedio;
        private BigDecimal distanciaKm;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvolucaoMensalDTO {
        private String mes;
        private BigDecimal custoTotal;
        private BigDecimal litrosTotais;
        private BigDecimal precoMedioLitro;
    }
}
