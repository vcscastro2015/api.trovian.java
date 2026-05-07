package com.trovian.dto;

import com.trovian.enums.MotivoDesmontagem;
import com.trovian.enums.PosicaoPneu;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representação de Alocação de Pneu")
public class AlocacaoPneuDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "Pneu é obrigatório")
    @Schema(required = true, example = "1", description = "ID do pneu")
    private Long pneuId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Número DOT do pneu")
    private String pneuDot;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Marca e modelo do pneu")
    private String pneuDescricao;

    @NotNull(message = "Veículo é obrigatório")
    @Schema(required = true, example = "1", description = "ID do veículo")
    private Long veiculoId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Placa do veículo")
    private String veiculoPlaca;

    @NotNull(message = "Posição é obrigatória")
    @Schema(required = true, description = "Posição física no veículo")
    private PosicaoPneu posicao;

    @Schema(example = "2024-01-15T08:00:00", description = "Data de montagem")
    private LocalDateTime dataMontagem;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Data de remoção (null = montado)")
    private LocalDateTime dataRemocao;

    @Schema(example = "45000", description = "Odômetro na montagem")
    private Integer kmMontagem;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Odômetro na remoção")
    private Integer kmRemocao;

    @Schema(description = "Motivo da desmontagem")
    private MotivoDesmontagem motivoRemocao;

    @Schema(example = "João Técnico", description = "Responsável pela operação")
    private String responsavel;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataCadastro;
}
