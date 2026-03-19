package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para Peça")
public class PecaDTO {

    @Schema(description = "ID da peça", example = "1")
    private Long id;

    @NotBlank(message = "Código é obrigatório")
    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    @Schema(description = "Código da peça", example = "FLT-001")
    private String codigo;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Schema(description = "Descrição da peça", example = "Filtro de óleo")
    private String descricao;

    @NotBlank(message = "Categoria é obrigatória")
    @Size(max = 100, message = "Categoria deve ter no máximo 100 caracteres")
    @Schema(description = "Categoria da peça", example = "Filtro")
    private String categoria;

    @NotNull(message = "Estoque mínimo é obrigatório")
    @Min(value = 0, message = "Estoque mínimo deve ser maior ou igual a zero")
    @Schema(description = "Estoque mínimo", example = "5")
    private Integer estoqueMinimo;

    @Schema(description = "Estoque atual", example = "15")
    private Integer estoqueAtual;

    @DecimalMin(value = "0.00", message = "Valor médio deve ser maior ou igual a zero")
    @Schema(description = "Valor médio da peça", example = "45.50")
    private BigDecimal valorMedio;

    @Size(max = 5000, message = "Aplicação de veículos deve ter no máximo 5000 caracteres")
    @Schema(description = "Quais modelos de veículos usam esta peça", example = "Scania R450, Volvo FH16")
    private String aplicacaoVeiculos;

    @Schema(description = "Status ativo/inativo", example = "true")
    private Boolean status;

    @Size(max = 5000, message = "Observações devem ter no máximo 5000 caracteres")
    @Schema(description = "Observações gerais")
    private String observacoes;

    @Schema(description = "Indica se o estoque está baixo")
    private Boolean estoqueBaixo;

    @NotNull(message = "ID do cliente é obrigatório")
    @Schema(description = "ID do cliente proprietário", example = "1", required = true)
    private Long clienteId;
}
