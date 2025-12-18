package com.trovian.dto;

import com.trovian.enums.CategoriaItem;
import com.trovian.enums.TipoResposta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representação de Item de Modelo de Checklist")
public class ItemModeloChecklistDTO {

    @Schema(description = "ID do item", example = "550e8400-e29b-41d4-a716-446655440000", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @Schema(description = "ID do modelo de checklist", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID modeloChecklistId;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    @Schema(description = "Descrição do item", example = "Verificar nível de óleo", required = true)
    private String descricao;

    @NotNull(message = "Ordem é obrigatória")
    @Schema(description = "Ordem de exibição do item", example = "1", required = true)
    private Integer ordem;

    @NotNull(message = "Campo obrigatório é obrigatório")
    @Schema(description = "Indica se o item é obrigatório", example = "true")
    private Boolean obrigatorio;

    @NotNull(message = "Permite foto é obrigatório")
    @Schema(description = "Indica se permite anexar foto", example = "true")
    private Boolean permiteFoto;

    @NotNull(message = "Permite observação é obrigatório")
    @Schema(description = "Indica se permite adicionar observação", example = "true")
    private Boolean permiteObservacao;

    @NotNull(message = "Categoria é obrigatória")
    @Schema(description = "Categoria do item", example = "MECANICA", required = true,
            allowableValues = {"SEGURANCA", "DOCUMENTACAO", "MECANICA", "ELETRICA", "PNEUS", "CARROCERIA"})
    private CategoriaItem categoria;

    @NotNull(message = "Tipo de resposta é obrigatório")
    @Schema(description = "Tipo de resposta esperada", example = "OK_NAO_OK", required = true,
            allowableValues = {"OK_NAO_OK", "SIM_NAO", "NUMERICO", "TEXTO"})
    private TipoResposta tipoResposta;
}
