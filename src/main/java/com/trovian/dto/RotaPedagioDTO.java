package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para RotaPedagio
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar um pedágio encontrado na rota")
public class RotaPedagioDTO {

    @Schema(description = "ID do pedágio (somente leitura)", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nome do pedágio", example = "Praça de Pedágio São Paulo")
    private String nome;

    @Schema(description = "Rodovia", example = "BR-116")
    private String rodovia;

    @Schema(description = "Quilômetro da rodovia", example = "450")
    private String km;

    @Schema(description = "Município", example = "São Paulo")
    private String municipio;

    @Schema(description = "UF", example = "SP")
    private String uf;

    @Schema(description = "Concessionária", example = "Arteris")
    private String concessionaria;

    @NotNull(message = "Latitude é obrigatória")
    @Schema(description = "Latitude do pedágio", example = "-23.5505", required = true)
    private Double latitude;

    @NotNull(message = "Longitude é obrigatória")
    @Schema(description = "Longitude do pedágio", example = "-46.6333", required = true)
    private Double longitude;

    @Schema(description = "Distância até a rota em metros", example = "150.0")
    private Double distanciaRota;
}
