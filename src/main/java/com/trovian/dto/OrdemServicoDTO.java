package com.trovian.dto;

import com.trovian.enums.StatusOrdemServico;
import com.trovian.enums.TipoManutencao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para Ordem de Serviço")
public class OrdemServicoDTO {

    @Schema(description = "ID da ordem de serviço", example = "1")
    private Long id;

    @NotBlank(message = "Número da OS é obrigatório")
    @Size(max = 50, message = "Número da OS deve ter no máximo 50 caracteres")
    @Schema(description = "Número da ordem de serviço", example = "OS-2024-001")
    private String numeroOs;

    @NotNull(message = "Veículo é obrigatório")
    @Schema(description = "ID do veículo", example = "1")
    private Long veiculoId;

    @Schema(description = "Placa do veículo")
    private String veiculoPlaca;

    @Schema(description = "ID do motorista que detectou o problema", example = "1")
    private Long motoristaId;

    @Schema(description = "Nome do motorista")
    private String motoristaNome;

    @NotNull(message = "Tipo de manutenção é obrigatório")
    @Schema(description = "Tipo de manutenção", example = "PREVENTIVA")
    private TipoManutencao tipoManutencao;

    @Schema(description = "Quilometragem do veículo no momento", example = "50000")
    private Integer kmVeiculo;

    @NotNull(message = "Data de abertura é obrigatória")
    @Schema(description = "Data de abertura da OS", example = "2024-01-15")
    private LocalDate dataAbertura;

    @Schema(description = "Data prevista para conclusão", example = "2024-01-20")
    private LocalDate dataPrevista;

    @Schema(description = "Data de conclusão", example = "2024-01-18")
    private LocalDate dataConclusao;

    @NotNull(message = "Status é obrigatório")
    @Schema(description = "Status da ordem de serviço", example = "ABERTA")
    private StatusOrdemServico status;

    @Size(max = 5000, message = "Descrição do problema deve ter no máximo 5000 caracteres")
    @Schema(description = "Descrição do problema")
    private String descricaoProblema;

    @Size(max = 5000, message = "Diagnóstico deve ter no máximo 5000 caracteres")
    @Schema(description = "Diagnóstico técnico")
    private String diagnostico;

    @Schema(description = "Valor total da manutenção", example = "1500.00")
    private BigDecimal valorTotal;

    @Size(max = 5000, message = "Observações devem ter no máximo 5000 caracteres")
    @Schema(description = "Observações gerais")
    private String observacoes;

    @Schema(description = "Lista de itens da manutenção")
    private List<ItemManutencaoDTO> itens = new ArrayList<>();
}
