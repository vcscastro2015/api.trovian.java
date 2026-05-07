package com.trovian.dto;

import com.trovian.enums.TipoRecapagem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para envio de pneu para recapagem")
public class RecapagemPneuDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "Pneu é obrigatório")
    @Schema(required = true, example = "1", description = "ID do pneu a recapar")
    private Long pneuId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Número DOT do pneu")
    private String pneuDot;

    @Schema(example = "5", description = "ID do fornecedor recapador")
    private Long fornecedorId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Nome do fornecedor")
    private String fornecedorNome;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Número sequencial desta recapagem")
    private Integer numeroRecapagem;

    @NotNull(message = "Data de envio é obrigatória")
    @Schema(required = true, example = "2024-01-15", description = "Data de envio para recapadora")
    private LocalDate dataEnvio;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Data de retorno (null = em processo)")
    private LocalDate dataRetorno;

    @Schema(example = "45000", description = "KM do pneu no envio")
    private Integer kmEnvio;

    @Schema(description = "Tipo de recapagem: FRIO, QUENTE, PRE_MOLDADO")
    private TipoRecapagem tipoRecapagem;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataCadastro;
}
