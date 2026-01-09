package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para transferência de dados de Funcionalidade")
public class FuncionalidadeDTO {

    @Schema(description = "ID da funcionalidade", accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;

    @NotBlank(message = "Código é obrigatório")
    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    @Schema(description = "Código único da funcionalidade", required = true, example = "MAPA")
    private String codigo;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Schema(description = "Nome da funcionalidade", required = true, example = "Mapa Principal")
    private String nome;

    @NotBlank(message = "Categoria é obrigatória")
    @Size(max = 50, message = "Categoria deve ter no máximo 50 caracteres")
    @Schema(description = "Categoria da funcionalidade", required = true, example = "Principal")
    private String categoria;

    @Schema(description = "Ordem no menu", example = "1")
    private Integer ordemMenu;
}
