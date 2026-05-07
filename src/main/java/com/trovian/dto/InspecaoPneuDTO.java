package com.trovian.dto;

import com.trovian.enums.CondicaoVisual;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para registro de inspeção de pneu")
public class InspecaoPneuDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(example = "1", description = "ID do pneu (obrigatório se alocacaoId não for informado)")
    private Long pneuId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Número DOT do pneu")
    private String pneuDot;

    @Schema(example = "1", description = "ID do veículo (obrigatório se alocacaoId não for informado)")
    private Long veiculoId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Placa do veículo")
    private String veiculoPlaca;

    @Schema(example = "1", description = "ID do motorista que realizou a inspeção")
    private Long motoristaId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Nome do motorista")
    private String motoristaNome;

    @Schema(example = "1", description = "ID da alocação ativa no momento")
    private Long alocacaoId;

    @Schema(example = "2024-01-15T08:00:00", description = "Data e hora da inspeção")
    private LocalDateTime dataInspecao;

    @Schema(example = "45000", description = "Odômetro do veículo no momento")
    private Integer kmVeiculo;

    @Schema(example = "120.5", description = "Pressão medida em PSI")
    private BigDecimal pressaoMedida;

    @Schema(example = "120.0", description = "Pressão recomendada em PSI")
    private BigDecimal pressaoRecomendada;

    @Schema(example = "5.2", description = "Profundidade do sulco medida em mm")
    private BigDecimal profundidadeSulco;

    @Schema(description = "Condição visual: BOA, ATENCAO, CRITICA")
    private CondicaoVisual condicaoVisual;

    @Schema(example = "false", description = "Indica deformação (bolha, corte, desgaste irregular)")
    private Boolean temDeformacao;

    @Schema(example = "Pneu com desgaste uniforme", description = "Observações")
    private String observacoes;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Se gerou alerta automaticamente")
    private Boolean geraAlerta;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataCadastro;
}
