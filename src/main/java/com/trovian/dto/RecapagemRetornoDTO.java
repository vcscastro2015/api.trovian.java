package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para registrar retorno de pneu da recapagem")
public class RecapagemRetornoDTO {

    @NotNull(message = "Data de retorno é obrigatória")
    @Schema(required = true, example = "2024-02-01", description = "Data de retorno da recapadora")
    private LocalDate dataRetorno;

    @NotNull(message = "Resultado da aprovação é obrigatório")
    @Schema(required = true, example = "true", description = "Se o pneu passou no controle de qualidade")
    private Boolean aprovado;

    @Schema(example = "16.0", description = "Profundidade do sulco após recapagem em mm")
    private BigDecimal profundidadeAposRecapagem;

    @Schema(example = "350.00", description = "Valor cobrado pela recapagem em R$")
    private BigDecimal valorRecapagem;

    @Schema(example = "60000", description = "KM garantidos pela recapadora")
    private Integer garantiaKm;

    @Schema(example = "Carcaça comprometida", description = "Motivo de rejeição (quando aprovado = false)")
    private String motivoRejeicao;
}
