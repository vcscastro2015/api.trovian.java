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
@Schema(description = "DTO para representação de Cooperativa")
public class CooperativaDTO {

    @Schema(description = "ID da cooperativa", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    @Schema(description = "Nome da cooperativa", example = "Cooperativa Central", required = true)
    private String nome;

    @NotBlank(message = "CNPJ é obrigatório")
    @Size(max = 18, message = "CNPJ deve ter no máximo 18 caracteres")
    @Schema(description = "CNPJ da cooperativa", example = "12.345.678/0001-90", required = true)
    private String cnpj;

    @Size(max = 300, message = "Endereço deve ter no máximo 300 caracteres")
    @Schema(description = "Endereço da cooperativa", example = "Rua das Flores, 123")
    private String endereco;

    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    @Schema(description = "Cidade da cooperativa", example = "São Paulo")
    private String cidade;

    @Size(max = 2, message = "UF deve ter 2 caracteres")
    @Schema(description = "UF da cooperativa", example = "SP")
    private String uf;

    @Size(max = 9, message = "CEP deve ter no máximo 9 caracteres")
    @Schema(description = "CEP da cooperativa", example = "12345-678")
    private String cep;

    @NotNull(message = "Status ativo é obrigatório")
    @Schema(description = "Indica se a cooperativa está ativa", example = "true", required = true)
    private Boolean ativa;

    @Schema(description = "Data de cadastro da cooperativa", example = "2025-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataCadastro;

    @Schema(description = "Data da última atualização", example = "2025-01-20T14:45:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
