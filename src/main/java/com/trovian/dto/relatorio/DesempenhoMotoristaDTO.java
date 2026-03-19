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
@Schema(description = "Relatório de Desempenho por Motorista")
public class DesempenhoMotoristaDTO {

    @Schema(description = "Período analisado")
    private PeriodoDTO periodo;

    @Schema(description = "Resumo geral dos motoristas")
    private ResumoGeralDTO resumoGeral;

    @Schema(description = "Detalhamento por motorista")
    private List<MotoristaDetalheDTO> motoristas;

    @Schema(description = "Destaques dos motoristas")
    private TopMotoristaDTO topMotoristas;

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
        private Integer totalMotoristas;
        private Long totalViagens;
        private BigDecimal comissaoTotal;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MotoristaDetalheDTO {
        private Long motoristaId;
        private String nome;
        private Long totalViagens;
        private BigDecimal receitaGerada;
        private BigDecimal margemMedia;
        private BigDecimal comissaoTotal;
        private BigDecimal consumoMedio;
        private BigDecimal kmPercorridos;
        private Integer posicaoRanking;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopMotoristaDTO {
        private String maisViagens;
        private String maiorReceita;
        private String melhorMargem;
    }
}
