package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para filtros de busca de rotas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para filtros de busca de rotas")
public class RotaFiltrosDTO {

    @Schema(description = "Busca por nome (parcial, case-insensitive)", example = "São Paulo")
    private String nome;

    @Schema(description = "Filtrar por ID do cliente", example = "1")
    private Long clienteId;

    @Schema(description = "Filtrar por ID do veículo", example = "1")
    private Long veiculoId;

    @Schema(description = "Filtrar por ID da cooperativa", example = "1")
    private Long cooperativaId;

    @Schema(description = "Filtrar por status ativo", example = "true")
    private Boolean ativa;

    @Schema(description = "Distância mínima em metros", example = "100000.0")
    private Double distanciaMinima;

    @Schema(description = "Distância máxima em metros", example = "500000.0")
    private Double distanciaMaxima;

    @Schema(description = "Filtrar por dificuldade", example = "Moderada", allowableValues = {"Fácil", "Moderada", "Difícil", "Muito Difícil"})
    private String dificuldade;
}
