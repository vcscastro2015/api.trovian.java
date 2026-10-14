package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representação de Equipamento")
public class EquipamentoDTO {
    @Schema(description = "ID do equipamento", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    @Schema(description = "Data de cadastro do equipamento", example = "2025-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private OffsetDateTime dataCadastro;
    @Size(max = 50, message = "IMEI deve ter no máximo 50 caracteres")
    @Schema(description = "IMEI do equipamento", example = "123456789012345")
    private String imei;
    @Size(max = 20, message = "Número do celular deve ter no máximo 20 caracteres")
    @Schema(description = "Número do celular", example = "(11) 98765-4321")
    private String numeroCelular;
    @Size(max = 100, message = "Número serial deve ter no máximo 100 caracteres")
    @Schema(description = "Número serial do equipamento", example = "SN123456789")
    private String numeroSerial;
    @Schema(description = "Observações sobre o equipamento", example = "Equipamento em bom estado")
    private String observacao;
    @NotNull(message = "Operadora é obrigatória")
    @Size(min = 1, max = 15, message = "Operadora deve ter entre 1 e 15 caracteres")
    @Schema(description = "Operadora do chip", example = "Vivo", required = true)
    private String operadora;
    @Schema(description = "Indica se o equipamento está ativo", example = "true")
    private Boolean status;
    @Size(max = 3, message = "Tipo de equipamento deve ter no máximo 3 caracteres")
    @Schema(description = "Tipo de equipamento (PR = Proprietário, PA = Particular)", example = "PR", allowableValues = {"PR", "PA"})
    private String tipoEquipamento;
    @Size(max = 3, message = "Tipo de chip deve ter no máximo 3 caracteres")
    @Schema(description = "Tipo de chip (PR = Proprietário, PA = Particular)", example = "PR", allowableValues = {"PR", "PA"})
    private String tipoChip;
    @NotNull(message = "Modelo é obrigatório")
    @Schema(description = "ID do modelo do equipamento", example = "1", required = true)
    private Long modeloId;
    @Schema(description = "Marca do modelo (somente leitura)", example = "D8T", accessMode = Schema.AccessMode.READ_ONLY)
    private String modeloMarca;
    @Schema(description = "Fabricante do modelo (somente leitura)", example = "Caterpillar", accessMode = Schema.AccessMode.READ_ONLY)
    private String modeloFabricante;
    @Schema(description = "Indica se o equipamento está alocado", example = "false")
    private Boolean equipamentoAlocado;
    @Schema(description = "Data da última atualização", example = "2025-01-20T14:45:00", accessMode = Schema.AccessMode.READ_ONLY)
    private OffsetDateTime updatedAt;
    @NotNull(message = "ID do cliente é obrigatório")
    @Schema(description = "ID do cliente proprietário", example = "1", required = true)
    private Long clienteId;

    @Schema(description = "Indica se o equipamento possui CAN Bus", example = "false")
    private Boolean temCan;

    @Schema(description = "Indica se o equipamento possui FMS", example = "false")
    private Boolean temFms;

    @Schema(description = "Indica se o equipamento possui sonda de nível", example = "false")
    private Boolean temSondaNivel;

    @Schema(description = "Indica se o equipamento possui iButton", example = "false")
    private Boolean temIbutton;

    @Size(max = 50, message = "Modelo da sonda deve ter no máximo 50 caracteres")
    @Schema(description = "Modelo da sonda instalada", example = "BCD-200")
    private String sondaModelo;

    @Schema(description = "Quantidade de sondas instaladas", example = "2")
    private Short sondaQtd;

    @Schema(description = "Data em que a sonda foi calibrada", example = "2025-06-10")
    private LocalDate sondaCalibradadEm;

    @Schema(description = "Percentual de erro admitido para a sonda", example = "3.00")
    private BigDecimal sondaErroPct;

    @Schema(description = "Percentual de erro admitido para o CAN", example = "8.00")
    private BigDecimal canErroPct;
}
