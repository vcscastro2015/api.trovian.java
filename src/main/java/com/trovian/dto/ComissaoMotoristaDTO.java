package com.trovian.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trovian.entity.ComissaoMotorista.StatusComissao;
import com.trovian.entity.ComissaoMotorista.TipoComissao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de transferência de dados de Comissão de Motorista")
public class ComissaoMotoristaDTO {

    @Schema(description = "ID único da comissão", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "ID do motorista é obrigatório")
    @Schema(description = "ID do motorista", example = "1", required = true)
    private Long motoristaId;

    @Schema(description = "Nome do motorista")
    private String nomeDoMotorista;

    @Schema(description = "ID da viagem", example = "1")
    private Long viagemId;

    @NotNull(message = "ID do cliente é obrigatório")
    @Schema(description = "ID do cliente", example = "1", required = true)
    private Long clienteId;

    @Schema(description = "Percentual da comissão", example = "10.00")
    private BigDecimal percentualComissao;

    @Schema(description = "Valor da comissão", example = "15.00")
    private BigDecimal valorComissao;

    @Schema(description = "Valor líquido do motorista", example = "135.00")
    private BigDecimal valorLiquidoMotorista;

    @Schema(description = "Descontos aplicados", example = "0.00")
    private BigDecimal descontos = BigDecimal.ZERO;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Data do pagamento", example = "2025-11-30")
    private LocalDate dataPagamento;

    @Schema(description = "Período de referência no formato YYYY-MM", example = "2025-11")
    private String periodoReferencia;

    @Schema(description = "Status da comissão", example = "PENDENTE")
    private StatusComissao status;

    @Schema(description = "Tipo da comissão", example = "POR_VIAGEM")
    private TipoComissao tipoComissao;

    @Schema(description = "Forma de pagamento", example = "PIX")
    private String formaPagamento;

    @Schema(description = "Observações adicionais")
    private String observacoes;

    @Schema(description = "ID do usuário que registrou", example = "1")
    private Long usuarioRegistroId;

    @Schema(description = "Bonificações", example = "0.00")
    private BigDecimal bonificacoes = BigDecimal.ZERO;

    @Schema(description = "Penalidades", example = "0.00")
    private BigDecimal penalidades = BigDecimal.ZERO;

    @Schema(description = "Origem da viagem", example = "São Paulo, SP")
    private String origem;

    @Schema(description = "Destino da viagem", example = "Rio de Janeiro, RJ")
    private String destino;

    @Schema(description = "Número da nota fiscal", example = "NF-123456")
    private String numeroNotaFiscal;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Data de criação", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Data de atualização", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
