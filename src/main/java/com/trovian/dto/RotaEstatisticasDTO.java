package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para RotaEstatisticas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para estatísticas de elevação da rota")
public class RotaEstatisticasDTO {

    @Schema(description = "ID das estatísticas (somente leitura)", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Ganho total de elevação em metros", example = "500.0")
    private Double ganhoTotalElevacao;

    @Schema(description = "Perda total de elevação em metros", example = "450.0")
    private Double perdaTotalElevacao;

    @Schema(description = "Elevação máxima em metros", example = "850.0")
    private Double elevacaoMaxima;

    @Schema(description = "Elevação mínima em metros", example = "50.0")
    private Double elevacaoMinima;

    @Schema(description = "Elevação média em metros", example = "350.0")
    private Double elevacaoMedia;

    @Schema(description = "Dificuldade da rota", example = "Moderada", allowableValues = {"Fácil", "Moderada", "Difícil", "Muito Difícil"})
    private String dificuldade;

    @Schema(description = "Distância em subida em metros", example = "15000.0")
    private Double distanciaSubida;

    @Schema(description = "Distância em descida em metros", example = "12000.0")
    private Double distanciaDescida;

    @Schema(description = "Distância em plano em metros", example = "73000.0")
    private Double distanciaPlano;

    @Schema(description = "Ponto de elevação máxima")
    private PontoElevacaoDTO pontoElevacaoMaxima;

    @Schema(description = "Ponto de elevação mínima")
    private PontoElevacaoDTO pontoElevacaoMinima;

    /**
     * Classe interna para representar um ponto de elevação
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Ponto com coordenadas e elevação")
    public static class PontoElevacaoDTO {
        @Schema(description = "Latitude", example = "-23.5505")
        private Double latitude;

        @Schema(description = "Longitude", example = "-46.6333")
        private Double longitude;

        @Schema(description = "Elevação em metros", example = "850.0")
        private Double elevacao;
    }
}
