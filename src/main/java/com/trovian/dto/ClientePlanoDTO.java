package com.trovian.dto;

import com.trovian.enums.StatusClientePlano;
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
@Schema(description = "DTO para transferência de dados de ClientePlano")
public class ClientePlanoDTO {

    @Schema(description = "ID do cliente-plano", accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;

    @Schema(description = "UUID único do cliente-plano", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID uuid;

    @NotNull(message = "Cliente é obrigatório")
    @Schema(description = "ID do cliente", required = true, example = "1")
    private Long clienteId;

    @Schema(description = "Nome do cliente", accessMode = Schema.AccessMode.READ_ONLY)
    private String clienteNome;

    @NotNull(message = "Plano é obrigatório")
    @Schema(description = "ID do plano", required = true, example = "1")
    private Long planoId;

    @Schema(description = "Nome do plano", accessMode = Schema.AccessMode.READ_ONLY)
    private String planoNome;

    @Schema(description = "Valor contratado na data da contratação", example = "99.90")
    private BigDecimal valorContratado;

    @Schema(description = "Desconto aplicado", example = "0.00")
    private BigDecimal desconto;

    @Schema(description = "Valor final (valorContratado - desconto)", example = "99.90")
    private BigDecimal valorFinal;

    @Schema(description = "Data de início do contrato", example = "2026-01-01")
    private LocalDate dataInicio;

    @Schema(description = "Data final do contrato", example = "2026-12-12")
    private LocalDate dataFinal;

    @Schema(description = "Data do próximo vencimento", example = "2026-02-01")
    private LocalDate dataVencimento;

    @Schema(description = "Dia do mês para vencimento (1-31)", example = "1")
    private Integer diaVencimento;

    @Schema(description = "Status do contrato", example = "ATIVO")
    private StatusClientePlano status;

    @Schema(description = "Forma de pagamento", example = "PIX")
    private TipoFormaPagamento formaPagamento;

    @Schema(description = "Data de cancelamento", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate dataCancelamento;

    @Schema(description = "Motivo do cancelamento", accessMode = Schema.AccessMode.READ_ONLY)
    private String motivoCancelamento;

    @Schema(description = "Data de criação", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataCriacao;

    @Schema(description = "Data da última atualização", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
