package com.trovian.dto;

import com.trovian.enums.TipoFormaPagamento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para transferência de dados de HistoricoPagamento")
public class HistoricoPagamentoDTO {

    @Schema(description = "ID do histórico de pagamento", accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;

    @Schema(description = "UUID único do histórico de pagamento", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID uuid;

    @NotNull(message = "ClientePlano é obrigatório")
    @Schema(description = "ID do cliente-plano", required = true, example = "1")
    private Long clientePlanoId;

    @Schema(description = "Valor pago", example = "99.90")
    private BigDecimal valorPago;

    @Schema(description = "Data do pagamento", example = "2026-01-15")
    private LocalDate dataPagamento;

    @Schema(description = "Data de referência (competência)", example = "2026-01-01")
    private LocalDate dataReferencia;

    @Schema(description = "Forma de pagamento", example = "PIX")
    private TipoFormaPagamento formaPagamento;

    @Schema(description = "Observação (ex: renegociação)", example = "Pagamento em atraso")
    private String observacao;

    @Schema(description = "Data de criação", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataCriacao;
}
