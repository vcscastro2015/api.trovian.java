package com.trovian.dto;

import com.trovian.enums.DirecaoSegmento;
import com.trovian.enums.NivelInclinacao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para RotaSegmento
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar um segmento colorido da rota")
public class RotaSegmentoDTO {

    @Schema(description = "ID do segmento (somente leitura)", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "Sequência é obrigatória")
    @Schema(description = "Ordem do segmento na rota", example = "1", required = true)
    private Integer sequencia;

    @NotNull(message = "Latitude de início é obrigatória")
    @Schema(description = "Latitude do ponto inicial", example = "-23.5505", required = true)
    private Double latitudeInicio;

    @NotNull(message = "Longitude de início é obrigatória")
    @Schema(description = "Longitude do ponto inicial", example = "-46.6333", required = true)
    private Double longitudeInicio;

    @NotNull(message = "Elevação de início é obrigatória")
    @Schema(description = "Elevação do ponto inicial em metros", example = "750.0", required = true)
    private Double elevacaoInicio;

    @NotNull(message = "Latitude de fim é obrigatória")
    @Schema(description = "Latitude do ponto final", example = "-23.5510", required = true)
    private Double latitudeFim;

    @NotNull(message = "Longitude de fim é obrigatória")
    @Schema(description = "Longitude do ponto final", example = "-46.6340", required = true)
    private Double longitudeFim;

    @NotNull(message = "Elevação de fim é obrigatória")
    @Schema(description = "Elevação do ponto final em metros", example = "780.0", required = true)
    private Double elevacaoFim;

    @Schema(description = "Direção do segmento", example = "SUBIDA", allowableValues = {"SUBIDA", "DESCIDA", "PLANO"})
    private DirecaoSegmento direcao;

    @Schema(description = "Nível de inclinação", example = "MODERADO", allowableValues = {"PLANO", "LEVE", "MODERADO", "FORTE", "MUITO_FORTE"})
    private NivelInclinacao nivelInclinacao;

    @Schema(description = "Porcentagem de inclinação", example = "5.5")
    private Double porcentagemInclinacao;

    @Schema(description = "Variação de elevação em metros", example = "30.0")
    private Double variacaoElevacao;

    @Schema(description = "Distância do segmento em metros", example = "250.0")
    private Double distancia;

    @Schema(description = "Cor do segmento em hexadecimal", example = "#00FF00")
    private String cor;
}
