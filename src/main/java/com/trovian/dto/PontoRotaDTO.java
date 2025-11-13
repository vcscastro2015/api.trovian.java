package com.trovian.dto;

import com.trovian.enums.TipoPonto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para PontoRota
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar um ponto da rota (origem, destino ou waypoint)")
public class PontoRotaDTO {

    @Schema(description = "ID do ponto (somente leitura)", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "Sequência é obrigatória")
    @Schema(description = "Ordem do ponto na rota", example = "1", required = true)
    private Integer sequencia;

    @NotNull(message = "Tipo do ponto é obrigatório")
    @Schema(description = "Tipo do ponto", example = "ORIGEM", allowableValues = {"ORIGEM", "DESTINO", "WAYPOINT"}, required = true)
    private TipoPonto tipo;

    @NotNull(message = "Latitude é obrigatória")
    @Schema(description = "Latitude do ponto", example = "-23.5505", required = true)
    private Double latitude;

    @NotNull(message = "Longitude é obrigatória")
    @Schema(description = "Longitude do ponto", example = "-46.6333", required = true)
    private Double longitude;

    @Schema(description = "Endereço do ponto", example = "Av. Paulista, 1000 - São Paulo, SP")
    private String endereco;

    @Schema(description = "Nome do ponto", example = "Terminal Rodoviário")
    private String nome;
}
