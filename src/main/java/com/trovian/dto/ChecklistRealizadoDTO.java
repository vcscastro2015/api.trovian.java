package com.trovian.dto;

import com.trovian.enums.StatusChecklist;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representação de Checklist Realizado")
public class ChecklistRealizadoDTO {

    @Schema(description = "ID do checklist realizado", example = "550e8400-e29b-41d4-a716-446655440000", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @Schema(description = "Número do checklist", example = "CK-2024-00001", accessMode = Schema.AccessMode.READ_ONLY)
    private String numeroChecklist;

    @NotNull(message = "Modelo de checklist é obrigatório")
    @Schema(description = "ID do modelo de checklist", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    private UUID modeloChecklistId;

    @Schema(description = "Nome do modelo de checklist (somente leitura)", example = "Checklist Pré-Viagem", accessMode = Schema.AccessMode.READ_ONLY)
    private String modeloChecklistNome;

    @NotNull(message = "Veículo é obrigatório")
    @Schema(description = "ID do veículo", example = "1", required = true)
    private Long veiculoId;

    @Schema(description = "Placa do veículo (somente leitura)", example = "ABC-1234", accessMode = Schema.AccessMode.READ_ONLY)
    private String veiculoPlaca;

    @NotNull(message = "Motorista é obrigatório")
    @Schema(description = "ID do motorista", example = "1", required = true)
    private Long motoristaId;

    @Schema(description = "Nome do motorista (somente leitura)", example = "João da Silva", accessMode = Schema.AccessMode.READ_ONLY)
    private String motoristaNome;

    @Schema(description = "ID da viagem (opcional)", example = "1")
    private Long viagemId;

    @NotNull(message = "Data/hora de início é obrigatória")
    @Schema(description = "Data e hora de início do checklist", example = "2025-01-15T08:00:00")
    private LocalDateTime dataHoraInicio;

    @Schema(description = "Data e hora de conclusão do checklist", example = "2025-01-15T08:30:00")
    private LocalDateTime dataHoraConclusao;

    @Schema(description = "Quilometragem do veículo no momento do checklist", example = "15000")
    private Integer kmVeiculo;

    @NotNull(message = "Status é obrigatório")
    @Schema(description = "Status do checklist", example = "EM_ANDAMENTO", required = true,
            allowableValues = {"EM_ANDAMENTO", "APROVADO", "REPROVADO"})
    private StatusChecklist status;

    @Schema(description = "Observações gerais sobre o checklist", example = "Checklist realizado normalmente")
    private String observacoesGerais;

    @Schema(description = "Localização GPS (lat,lng)", example = "-23.550520,-46.633308")
    private String localizacao;

    @Schema(description = "Lista de respostas dos itens do checklist")
    private List<RespostaItemChecklistDTO> respostas = new ArrayList<>();

    @NotNull(message = "ID do cliente é obrigatório")
    @Schema(description = "ID do cliente proprietário", example = "1", required = true)
    private Long clienteId;
}
