package com.trovian.dto;

import com.trovian.enums.TipoCombustivel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para transferência de dados de Abastecimento")
public class AbastecimentoDTO {

    @Schema(description = "ID do abastecimento", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "ID do veículo é obrigatório")
    @Schema(description = "ID do veículo abastecido", example = "1", required = true)
    private Long veiculoId;

    @Schema(description = "Placa do veículo", example = "ABC-1234", accessMode = Schema.AccessMode.READ_ONLY)
    private String veiculoPlaca;

    @NotNull(message = "ID do motorista é obrigatório")
    @Schema(description = "ID do motorista que realizou o abastecimento", example = "1", required = true)
    private Long motoristaId;

    @Schema(description = "Nome do motorista", example = "João da Silva", accessMode = Schema.AccessMode.READ_ONLY)
    private String motoristaNome;

    @Schema(description = "ID da rota (opcional)", example = "1")
    private Long rotaId;

    @Schema(description = "Descrição da rota", example = "SP-RJ", accessMode = Schema.AccessMode.READ_ONLY)
    private String rotaDescricao;

    @NotNull(message = "Data e hora são obrigatórios")
    @Schema(description = "Data e hora do abastecimento", example = "2025-11-11T10:30:00", required = true)
    private Date dataHora;

    @NotNull(message = "KM do odômetro é obrigatório")
    @Positive(message = "KM do odômetro deve ser positivo")
    @Schema(description = "Leitura do odômetro no momento do abastecimento", example = "150000", required = true)
    private Integer kmOdometro;

    @NotNull(message = "Litros abastecidos é obrigatório")
    @Positive(message = "Litros abastecidos deve ser positivo")
    @Schema(description = "Volume abastecido em litros", example = "120.50", required = true)
    private BigDecimal litrosAbastecidos;

    @NotNull(message = "Valor total é obrigatório")
    @Positive(message = "Valor total deve ser positivo")
    @Schema(description = "Valor total gasto no abastecimento", example = "650.75", required = true)
    private BigDecimal valorTotal;

    @NotNull(message = "Preço por litro é obrigatório")
    @Positive(message = "Preço por litro deve ser positivo")
    @Schema(description = "Preço por litro do combustível", example = "5.40", required = true)
    private BigDecimal precoLitro;

    @Schema(description = "ID do local onde ocorreu o abastecimento (opcional)", example = "1")
    private Long localId;

    @Schema(description = "Nome do local", example = "Posto Shell BR-101", accessMode = Schema.AccessMode.READ_ONLY)
    private String localNome;

    @NotNull(message = "Tipo de combustível é obrigatório")
    @Schema(description = "Tipo de combustível", example = "DIESEL", required = true,
            allowableValues = {"DIESEL", "GASOLINA", "ETANOL", "GNV", "ARLA32"})
    private TipoCombustivel combustivelTipo;

    @Schema(description = "Indica se o tanque foi completamente abastecido", example = "true")
    private Boolean tanqueCheio;

    @Schema(description = "Observações sobre o abastecimento", example = "Abastecimento completo, sem problemas")
    private String observacoes;

    @NotNull(message = "ID do cliente é obrigatório")
    @Schema(description = "ID do cliente proprietário", example = "1", required = true)
    private Long clienteId;

    @Schema(description = "Nome do cliente", example = "Transportadora XYZ", accessMode = Schema.AccessMode.READ_ONLY)
    private String clienteNome;

    @Schema(description = "Data de criação do registro", example = "2025-11-11T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private Date criadoEm;

    @Schema(description = "Data de última atualização", example = "2025-11-11T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime atualizadoEm;

    @Schema(description = "Status do registro (ativo/inativo)", example = "true")
    private Boolean status;
}
