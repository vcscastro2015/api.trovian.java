package com.trovian.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Relatório de comissões do motorista")
public class ComissaoMotoristaRelatorioDTO {

    @Schema(description = "ID do motorista", example = "1")
    private Long motoristaId;

    @Schema(description = "Nome do motorista", example = "João Silva")
    private String nomeMotorista;

    @Schema(description = "Data inicial do período consultado")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime periodoInicial;

    @Schema(description = "Data final do período consultado")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime periodoFinal;

    @Schema(description = "Status filtrado (null se todos)", example = "PAGA")
    private String statusFiltrado;

    @Schema(description = "Quantidade total de comissões no período", example = "15")
    private Integer quantidadeComissoes;

    @Schema(description = "Totais consolidados das comissões")
    private TotaisDTO totais;

    @Schema(description = "Lista de comissões do período")
    private List<ComissaoMotoristaDTO> comissoes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Totais consolidados do relatório")
    public static class TotaisDTO {

        @Schema(description = "Soma total do valor de comissão", example = "5000.00")
        private BigDecimal valorComissao;

        @Schema(description = "Soma total do valor líquido do motorista", example = "4200.00")
        private BigDecimal valorLiquidoMotorista;

        @Schema(description = "Soma total dos descontos", example = "300.00")
        private BigDecimal descontos;

        @Schema(description = "Soma total das bonificações", example = "200.00")
        private BigDecimal bonificacoes;

        @Schema(description = "Soma total das penalidades", example = "100.00")
        private BigDecimal penalidades;
    }
}
