package com.trovian.dto;

import com.trovian.enums.TipoItemManutencao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para Item de Manutenção")
public class ItemManutencaoDTO {

    @Schema(description = "ID do item", example = "1")
    private Long id;

    @Schema(description = "ID da ordem de serviço", example = "1")
    private Long ordemServicoId;

    @NotNull(message = "Tipo é obrigatório")
    @Schema(description = "Tipo do item (SERVICO ou PECA)", example = "PECA")
    private TipoItemManutencao tipo;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Schema(description = "Descrição do item", example = "Filtro de óleo")
    private String descricao;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    @Schema(description = "Quantidade", example = "2")
    private Integer quantidade;

    @NotNull(message = "Valor unitário é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor unitário deve ser maior que zero")
    @Schema(description = "Valor unitário", example = "35.50")
    private BigDecimal valorUnitario;

    @Schema(description = "Valor total (calculado automaticamente)", example = "71.00")
    private BigDecimal valorTotal;

    @Schema(description = "ID do fornecedor", example = "1")
    private Long fornecedorId;

    @Schema(description = "Nome do fornecedor")
    private String fornecedorNome;
}
