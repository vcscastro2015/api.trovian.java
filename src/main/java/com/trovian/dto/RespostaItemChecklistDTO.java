package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representação de Resposta de Item de Checklist")
public class RespostaItemChecklistDTO {

    @Schema(description = "ID da resposta", example = "550e8400-e29b-41d4-a716-446655440000", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @Schema(description = "ID do checklist realizado", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID checklistRealizadoId;

    @NotNull(message = "Item do modelo é obrigatório")
    @Schema(description = "ID do item do modelo", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    private UUID itemModeloId;

    @Schema(description = "Descrição do item (somente leitura)", example = "Verificar nível de óleo", accessMode = Schema.AccessMode.READ_ONLY)
    private String itemModeloDescricao;

    @NotBlank(message = "Resposta é obrigatória")
    @Schema(description = "Resposta do item", example = "OK", required = true)
    private String resposta;

    @Schema(description = "Observação sobre a resposta", example = "Nível de óleo está adequado")
    private String observacao;

    @Schema(description = "URL da foto anexada", example = "/uploads/checklists/foto123.jpg")
    private String fotoUrl;

    @Schema(description = "Indica se a resposta requer atenção (gera alerta)", example = "false")
    private Boolean requerAtencao;
}
