package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DTO para Rota
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar uma rota completa")
public class RotaDTO {

    @Schema(description = "ID da rota (somente leitura)", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    @Schema(description = "Nome da rota", example = "Rota São Paulo - Rio de Janeiro", required = true)
    private String nome;

    @Schema(description = "Descrição da rota", example = "Rota via Dutra com paradas em Guarulhos e Taubaté")
    private String descricao;

    @NotNull(message = "Status ativo é obrigatório")
    @Schema(description = "Status da rota", example = "true", required = true)
    private Boolean ativa = true;

    @Schema(description = "Data de cadastro (somente leitura)", accessMode = Schema.AccessMode.READ_ONLY)
    private Date dataCadastro;

    @Schema(description = "Data de atualização (somente leitura)", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataAtualizacao;

    @NotNull(message = "Distância total é obrigatória")
    @Schema(description = "Distância total da rota em metros", example = "450000.0", required = true)
    private Double distanciaTotal;

    // Relacionamentos opcionais (IDs)
    @Schema(description = "ID do cliente associado", example = "1")
    private Long clienteId;

    @Schema(description = "ID do veículo associado", example = "1")
    private Long veiculoId;

    @Schema(description = "ID da cooperativa associada", example = "1")
    private Long cooperativaId;

    // Campos de relacionamento (somente leitura)
    @Schema(description = "Nome do cliente (somente leitura)", accessMode = Schema.AccessMode.READ_ONLY)
    private String clienteNome;

    @Schema(description = "Placa do veículo (somente leitura)", accessMode = Schema.AccessMode.READ_ONLY)
    private String veiculoPlaca;

    @Schema(description = "Nome da cooperativa (somente leitura)", accessMode = Schema.AccessMode.READ_ONLY)
    private String cooperativaNome;

    // GeoJSON
    @Schema(description = "Representação GeoJSON da rota")
    private String geoJson;

    // Listas de objetos relacionados
    @Valid
    @Schema(description = "Lista de pontos da rota")
    private List<PontoRotaDTO> pontos = new ArrayList<>();

    @Valid
    @Schema(description = "Estatísticas de elevação da rota")
    private RotaEstatisticasDTO estatisticas;

    @Valid
    @Schema(description = "Lista de segmentos da rota")
    private List<RotaSegmentoDTO> segmentos = new ArrayList<>();

    @Valid
    @Schema(description = "Lista de pedágios encontrados na rota")
    private List<RotaPedagioDTO> pedagios = new ArrayList<>();
}
