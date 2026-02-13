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
@Schema(description = "Relatório de Receita por Cliente (Viagens)")
public class ReceitaPorClienteViagemDTO {

    @Schema(description = "Período analisado")
    private PeriodoDTO periodo;

    @Schema(description = "Resumo geral")
    private ResumoGeralDTO resumoGeral;

    @Schema(description = "Detalhamento por cliente")
    private List<ClienteDetalheDTO> clientes;

    @Schema(description = "Destaques dos clientes")
    private TopClienteDTO topClientes;

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
        private Integer totalClientes;
        private BigDecimal receitaTotal;
        private BigDecimal ticketMedio;
        private BigDecimal toneladasTransportadas;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClienteDetalheDTO {
        private Long clienteId;
        private String nome;
        private Long totalViagens;
        private BigDecimal receitaBruta;
        private BigDecimal lucroLiquido;
        private BigDecimal margemMedia;
        private BigDecimal toneladasTransportadas;
        private BigDecimal ticketMedio;
        private List<String> materiaisTransportados;
        private Integer posicaoRanking;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopClienteDTO {
        private String maiorReceita;
        private String maisViagens;
        private String melhorMargem;
    }
}
