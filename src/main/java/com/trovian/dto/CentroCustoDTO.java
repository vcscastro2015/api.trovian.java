package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para Centro de Custo")
public class CentroCustoDTO {

    @Schema(description = "ID do centro de custo", example = "1")
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Schema(description = "Nome do centro de custo", example = "Frota SP")
    private String nome;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Schema(description = "Descrição")
    private String descricao;

    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    @Schema(description = "Código", example = "CC-001")
    private String codigo;

    @Schema(description = "ID do cliente associado")
    private Long clienteId;

    @Schema(description = "Nome do cliente associado")
    private String clienteNome;

    @Schema(description = "ID do veículo associado")
    private Long veiculoId;

    @Schema(description = "Placa do veículo associado")
    private String veiculoPlaca;

    @Schema(description = "Status ativo/inativo", example = "true")
    private Boolean status;
}
