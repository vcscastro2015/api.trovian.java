package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representação de Modelo")
public class ModeloDTO {

    @Schema(description = "ID do modelo", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Fabricante é obrigatório")
    @Size(max = 200, message = "Fabricante deve ter no máximo 200 caracteres")
    @Schema(description = "Fabricante do modelo", example = "Volkswagen", required = true)
    private String fabricante;

    @NotBlank(message = "Marca é obrigatória")
    @Size(max = 200, message = "Marca deve ter no máximo 200 caracteres")
    @Schema(description = "Marca do modelo", example = "Gol", required = true)
    private String marca;

    @NotBlank(message = "Tipo é obrigatório")
    @Size(max = 50, message = "Tipo deve ter no máximo 50 caracteres")
    @Schema(description = "Tipo do modelo (Equipamento ou Veiculo)", example = "Veiculo", required = true, allowableValues = {"Equipamento", "Veiculo"})
    private String tipo;

    @NotNull(message = "Status é obrigatório")
    @Schema(description = "Indica se o modelo está ativo", example = "true", required = true)
    private Boolean status;

    @Schema(description = "Data de criação do modelo", example = "2025-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Data da última atualização", example = "2025-01-20T14:45:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
