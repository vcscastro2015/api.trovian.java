package com.trovian.dto;

import com.trovian.enums.TipoMovimentacaoEstoque;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para Movimentação de Estoque")
public class MovimentacaoEstoqueDTO {

    @Schema(description = "ID da movimentação", example = "1")
    private Long id;

    @NotNull(message = "Peça é obrigatória")
    @Schema(description = "ID da peça", example = "1")
    private Long pecaId;

    @Schema(description = "Código da peça")
    private String pecaCodigo;

    @Schema(description = "Descrição da peça")
    private String pecaDescricao;

    @Schema(description = "ID da ordem de serviço (opcional)", example = "1")
    private Long ordemServicoId;

    @Schema(description = "Número da ordem de serviço")
    private String ordemServicoNumero;

    @NotNull(message = "Tipo de movimentação é obrigatório")
    @Schema(description = "Tipo de movimentação", example = "ENTRADA")
    private TipoMovimentacaoEstoque tipoMovimentacao;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    @Schema(description = "Quantidade movimentada", example = "10")
    private Integer quantidade;

    @NotNull(message = "Data de movimentação é obrigatória")
    @Schema(description = "Data e hora da movimentação", example = "2024-01-15T10:30:00")
    private LocalDateTime dataMovimentacao;

    @DecimalMin(value = "0.00", message = "Valor unitário deve ser maior ou igual a zero")
    @Schema(description = "Valor unitário da peça na movimentação", example = "45.50")
    private BigDecimal valorUnitario;

    @Size(max = 5000, message = "Observação deve ter no máximo 5000 caracteres")
    @Schema(description = "Observação sobre a movimentação")
    private String observacao;

    @Size(max = 100, message = "Usuário deve ter no máximo 100 caracteres")
    @Schema(description = "Usuário que realizou a movimentação", example = "admin")
    private String usuario;

    @NotNull(message = "ID do cliente é obrigatório")
    @Schema(description = "ID do cliente proprietário", example = "1", required = true)
    private Long clienteId;
}
