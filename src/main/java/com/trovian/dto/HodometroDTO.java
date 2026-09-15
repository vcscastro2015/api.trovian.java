package com.trovian.dto;

import com.trovian.enums.TipoHodometro;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representação de Hodômetro")
public class HodometroDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "Hodômetro é obrigatório")
    @Schema(required = true, example = "125000.5", description = "Leitura do hodômetro em km")
    private Double hodometro;

    @Schema(example = "124998.3", description = "Leitura do hodômetro via rastreador")
    private Double hodometroRastreador;

    @NotNull(message = "Veículo é obrigatório")
    @Schema(required = true, example = "1", description = "ID do veículo")
    private Long veiculoId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Placa do veículo")
    private String veiculoPlaca;

    @Schema(description = "Tipo de registro: MANUAL ou AUTOMATICO")
    private TipoHodometro tipo;

    @Schema(example = "42", description = "ID da transmissão associada")
    private Long idTransmissao;

    @Schema(example = "7", description = "ID do motorista")
    private Long idMotorista;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private OffsetDateTime dataCadastro;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private OffsetDateTime dataAtualizacao;
}
