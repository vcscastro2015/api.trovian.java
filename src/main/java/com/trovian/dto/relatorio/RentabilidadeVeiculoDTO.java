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
@Schema(description = "Relatório de Rentabilidade por Veículo")
public class RentabilidadeVeiculoDTO {

    @Schema(description = "Período analisado")
    private PeriodoDTO periodo;

    @Schema(description = "Resumo geral dos veículos")
    private ResumoGeralDTO resumoGeral;

    @Schema(description = "Detalhamento por veículo")
    private List<VeiculoDetalheDTO> veiculos;

    @Schema(description = "Destaques dos veículos")
    private TopVeiculoDTO topVeiculos;

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
        private Integer totalVeiculos;
        private BigDecimal receitaTotal;
        private BigDecimal lucroTotal;
        private BigDecimal margemMediaGeral;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VeiculoDetalheDTO {
        private Long veiculoId;
        private String placa;
        private Long totalViagens;
        private BigDecimal receitaBruta;
        private BigDecimal custoTotal;
        private BigDecimal lucroLiquido;
        private BigDecimal margemPercentual;
        private BigDecimal kmRodados;
        private BigDecimal consumoMedioKmL;
        private BigDecimal ticketMedio;
        private Integer posicaoRanking;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopVeiculoDTO {
        private String maisRentavel;
        private String maisUtilizado;
        private String menorConsumo;
    }
}
