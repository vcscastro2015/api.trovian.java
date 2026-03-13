package com.trovian.dto;

import com.trovian.enums.Periodicidade;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para transferência de dados de Plano")
public class PlanoDTO {

    @Schema(description = "ID do plano", accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;

    @Schema(description = "UUID único do plano", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID uuid;

    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Nome do plano", required = true, example = "Plano Básico")
    private String nome;

    @Schema(description = "Descrição do plano", example = "Plano para pequenas empresas")
    private String descricao;

    @Schema(description = "Valor do plano", example = "99.90")
    private BigDecimal valor;

    @Schema(description = "Periodicidade do plano", example = "MENSAL")
    private Periodicidade periodicidade;

    @Schema(description = "Limite de usuários", example = "5")
    private Integer limiteUsuarios;

    @Schema(description = "Limite de veículos", example = "10")
    private Integer limiteVeiculos;

    @Schema(description = "Limite de checklists", example = "100")
    private Integer limiteChecklists;

    @Schema(description = "Indica se o plano está ativo", example = "true")
    private Boolean ativo = true;

    @Schema(description = "Data de criação", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataCriacao;

    @Schema(description = "Data da última atualização", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
