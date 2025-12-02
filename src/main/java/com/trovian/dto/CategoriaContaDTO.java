package com.trovian.dto;

import com.trovian.enums.TipoConta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para Categoria de Conta")
public class CategoriaContaDTO {

    @Schema(description = "ID da categoria", example = "1")
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Schema(description = "Nome da categoria", example = "Manutenção de Veículos")
    private String nome;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Schema(description = "Descrição da categoria")
    private String descricao;

    @NotNull(message = "Tipo é obrigatório")
    @Schema(description = "Tipo da conta", example = "PAGAR")
    private TipoConta tipo;

    @Schema(description = "ID da categoria pai (para hierarquia)")
    private Long categoriaPaiId;

    @Schema(description = "Nome da categoria pai")
    private String categoriaPaiNome;

    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    @Schema(description = "Código contábil", example = "1.2.01")
    private String codigo;

    @Schema(description = "Status ativo/inativo", example = "true")
    private Boolean status;
}
