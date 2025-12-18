package com.trovian.dto;

import com.trovian.enums.Periodicidade;
import com.trovian.enums.TipoChecklist;
import io.swagger.v3.oas.annotations.media.Schema;
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
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representação de Modelo de Checklist")
public class ModeloChecklistDTO {

    @Schema(description = "ID do modelo de checklist", example = "550e8400-e29b-41d4-a716-446655440000", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Schema(description = "Nome do modelo de checklist", example = "Checklist Pré-Viagem", required = true)
    private String nome;

    @Schema(description = "Descrição do modelo de checklist", example = "Checklist obrigatório antes de iniciar qualquer viagem")
    private String descricao;

    @NotNull(message = "Tipo é obrigatório")
    @Schema(description = "Tipo do checklist", example = "VIAGEM", required = true,
            allowableValues = {"VIAGEM", "MANUTENCAO", "DIARIO", "SEMANAL"})
    private TipoChecklist tipo;

    @NotNull(message = "Status ativo é obrigatório")
    @Schema(description = "Indica se o modelo está ativo", example = "true")
    private Boolean ativo;

    @NotNull(message = "Campo obrigatório é obrigatório")
    @Schema(description = "Indica se o checklist é obrigatório (bloqueia viagem se não realizado)", example = "true")
    private Boolean obrigatorio;

    @NotNull(message = "Periodicidade é obrigatória")
    @Schema(description = "Periodicidade do checklist", example = "ANTES_VIAGEM", required = true,
            allowableValues = {"ANTES_VIAGEM", "DIARIA", "SEMANAL", "MENSAL"})
    private Periodicidade periodicidade;

    @Schema(description = "Lista de itens do modelo de checklist")
    private List<ItemModeloChecklistDTO> itens = new ArrayList<>();

    @NotNull(message = "Cliente é obrigatório")
    @Schema(description = "ID do cliente", example = "1", required = true)
    private Long clienteId;

    @Schema(description = "Nome do cliente (somente leitura)", example = "João da Silva", accessMode = Schema.AccessMode.READ_ONLY)
    private String clienteNome;

    @Schema(description = "Data de criação", accessMode = Schema.AccessMode.READ_ONLY)
    private Date criadoEm;

    @Schema(description = "Data da última atualização", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime atualizadoEm;
}
