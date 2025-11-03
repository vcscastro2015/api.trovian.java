package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para coordenadas do local")
public class CoordenadaDTO {

    @Schema(description = "ID da coordenada", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Sequência da coordenada", example = "1")
    private Integer sequencia;

    @Schema(description = "Latitude", example = "-23.5505")
    private Double latitude;

    @Schema(description = "Longitude", example = "-46.6333")
    private Double longitude;

    @Schema(description = "Indica se usa raio", example = "true")
    private Boolean isRaio;

    @Schema(description = "Raio em metros", example = "500.0")
    private Double raio;
}
