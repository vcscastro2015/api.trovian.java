package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * DTO resumido para Rota, sem pontos, segmentos, pedágios e estatísticas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO resumido de rota, sem pontos, segmentos, pedágios e estatísticas")
public class RotaSummaryDTO {

    @Schema(description = "ID da rota", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nome da rota")
    private String nome;

    @Schema(description = "Descrição da rota")
    private String descricao;

    @Schema(description = "Status da rota")
    private Boolean ativa;

    @Schema(description = "Data de cadastro", accessMode = Schema.AccessMode.READ_ONLY)
    private Date dataCadastro;

    @Schema(description = "Data de atualização", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataAtualizacao;

    @Schema(description = "Distância total da rota em metros")
    private Double distanciaTotal;

    @Schema(description = "ID do cliente associado")
    private Long clienteId;

    @Schema(description = "Nome do cliente", accessMode = Schema.AccessMode.READ_ONLY)
    private String clienteNome;

    @Schema(description = "ID do veículo associado")
    private Long veiculoId;

    @Schema(description = "Placa do veículo", accessMode = Schema.AccessMode.READ_ONLY)
    private String veiculoPlaca;

    @Schema(description = "ID da cooperativa associada")
    private Long cooperativaId;

    @Schema(description = "Nome da cooperativa", accessMode = Schema.AccessMode.READ_ONLY)
    private String cooperativaNome;
}
