package com.trovian.dto;

import com.trovian.enums.StatusViagem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO para transferência de dados de Viagem
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de Viagem com cálculos de custos e receitas")
public class ViagemDTO {

    @Schema(description = "ID da viagem", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    // ==================== RELACIONAMENTOS ====================

    @Schema(description = "ID do veículo", example = "1", required = true)
    @NotNull(message = "Veículo é obrigatório")
    private Long veiculoId;

    @Schema(description = "Placa do veículo", example = "ABC-1234", accessMode = Schema.AccessMode.READ_ONLY)
    private String veiculoPlaca;

    @Schema(description = "ID do motorista", example = "1", required = true)
    @NotNull(message = "Motorista é obrigatório")
    private Long motoristaId;

    @Schema(description = "Nome do motorista", example = "João da Silva", accessMode = Schema.AccessMode.READ_ONLY)
    private String motoristaNome;

    @Schema(description = "ID da rota de ida", example = "1", required = true)
    @NotNull(message = "Rota de ida é obrigatória")
    private Long rotaIdaId;

    @Schema(description = "Nome da rota de ida", example = "São Paulo - Rio de Janeiro", accessMode = Schema.AccessMode.READ_ONLY)
    private String rotaIdaNome;

    @Schema(description = "Distância da rota de ida em KM", example = "450.5", accessMode = Schema.AccessMode.READ_ONLY)
    private Double rotaIdaDistanciaKm;

    @Schema(description = "ID da rota de volta", example = "2")
    private Long rotaVoltaId;

    @Schema(description = "Nome da rota de volta", example = "Rio de Janeiro - São Paulo", accessMode = Schema.AccessMode.READ_ONLY)
    private String rotaVoltaNome;

    @Schema(description = "Distância da rota de volta em KM", example = "450.5", accessMode = Schema.AccessMode.READ_ONLY)
    private Double rotaVoltaDistanciaKm;

    @Schema(description = "ID do abastecimento de referência", example = "1")
    private Long abastecimentoId;

    @Schema(description = "Preço do combustível do último abastecimento", example = "5.50", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal precoLitroCombustivel;

    @Schema(description = "ID do cliente", example = "1", required = true)
    @NotNull(message = "Cliente é obrigatório")
    private Long clienteId;

    @Schema(description = "Nome do cliente", example = "Transportadora ABC", accessMode = Schema.AccessMode.READ_ONLY)
    private String clienteNome;

    // ==================== DADOS BÁSICOS ====================

    @Schema(description = "Data da viagem", example = "2025-11-12", required = true)
    @NotNull(message = "Data da viagem é obrigatória")
    private Date dataViagem;

    @Schema(description = "Status da viagem", example = "true", required = true)
    @NotNull(message = "Status é obrigatório")
    private Boolean status = true;

    @Schema(description = "Status da viagem (ABERTA, ANALISE, FECHADA)", example = "ABERTA", required = true)
    @NotNull(message = "Status da viagem é obrigatório")
    private StatusViagem statusViagem = StatusViagem.ABERTA;

    // ==================== DADOS DA IDA ====================

    @Schema(description = "Quantidade de toneladas da ida", example = "25.5", required = true)
    @NotNull(message = "Quantidade de toneladas da ida é obrigatória")
    @Positive(message = "Quantidade de toneladas deve ser positiva")
    private Double quantidadeToneladasIda;

    @Schema(description = "Material transportado na ida", example = "Minério de ferro")
    private String materialIda;

    @Schema(description = "Valor da tonelada bruta da ida", example = "150.00", required = true)
    @NotNull(message = "Valor tonelada bruta da ida é obrigatório")
    @Positive(message = "Valor tonelada bruta deve ser positivo")
    private BigDecimal valorToneladaBrutaIda;

    @Schema(description = "Imposto base da ida", example = "500.00", required = true)
    @NotNull(message = "Imposto base da ida é obrigatório")
    private BigDecimal impostoBaseIda;

    @Schema(description = "Quantidade de pedágios na ida", example = "5", required = true)
    @NotNull(message = "Quantidade de pedágios da ida é obrigatória")
    private Integer quantidadePedagiosIda;

    @Schema(description = "Valor do pedágio por eixo na ida", example = "12.50", required = true)
    @NotNull(message = "Valor pedágio por eixo da ida é obrigatório")
    @Positive(message = "Valor pedágio deve ser positivo")
    private BigDecimal valorPedagioPorEixoIda;

    @Schema(description = "Valor total bruto da ida (calculado)", example = "4325.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal valorTotalBrutoIda;

    // ==================== DADOS DA VOLTA ====================

    @Schema(description = "Quantidade de toneladas da volta", example = "20.0")
    private Double quantidadeToneladasVolta;

    @Schema(description = "Material transportado na volta", example = "Soja")
    private String materialVolta;

    @Schema(description = "Valor da tonelada bruta da volta", example = "120.00")
    private BigDecimal valorToneladaBrutaVolta;

    @Schema(description = "Imposto base da volta", example = "400.00")
    private BigDecimal impostoBaseVolta;

    @Schema(description = "Quantidade de pedágios na volta", example = "5")
    private Integer quantidadePedagiosVolta;

    @Schema(description = "Valor do pedágio por eixo na volta", example = "12.50")
    private BigDecimal valorPedagioPorEixoVolta;

    @Schema(description = "Valor total bruto da volta (calculado)", example = "2800.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal valorTotalBrutoVolta;

    // ==================== COMBUSTÍVEL ====================

    @Schema(description = "Média do veículo em KM/L", example = "2.5", required = true)
    @NotNull(message = "Média do veículo é obrigatória")
    @Positive(message = "Média do veículo deve ser positiva")
    private Double mediaVeiculoKmL;

    @Schema(description = "Fator de carga (calculado)", example = "0.85", accessMode = Schema.AccessMode.READ_ONLY)
    private Double fatorCarga;

    @Schema(description = "Consumo estimado em litros (calculado)", example = "380.5", accessMode = Schema.AccessMode.READ_ONLY)
    private Double consumoEstimadoLitros;

    @Schema(description = "Valor total de combustível (calculado)", example = "2092.75", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal valorTotalCombustivel;

    // ==================== DADOS DO VEÍCULO (para cálculo) ====================

    @Schema(description = "Tara do veículo em toneladas", example = "10.5", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal veiculoTara;

    @Schema(description = "Carga máxima do veículo em toneladas", example = "50.0", accessMode = Schema.AccessMode.READ_ONLY)
    private Double veiculoCargaMaxima;

    @Schema(description = "Número de eixos do veículo", example = "5", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer veiculoNumeroEixos;

    @Schema(description = "Comissão do motorista (%)", example = "5.0", accessMode = Schema.AccessMode.READ_ONLY)
    private Double motoristaComissao;

    // ==================== RESULTADO FINAL ====================

    @Schema(description = "Valor total bruto da viagem (calculado)", example = "7125.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal valorTotalBrutoViagem;

    @Schema(description = "Valor total de pedágios (calculado)", example = "625.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal valorTotalPedagios;

    @Schema(description = "Comissão do motorista (calculado)", example = "356.25", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal comissaoMotorista;

    @Schema(description = "Valor total de custos (calculado)", example = "3074.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal valorTotalCustos;

    @Schema(description = "Valor total líquido (calculado)", example = "4051.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal valorTotalLiquido;

    @Schema(description = "Margem percentual (calculado)", example = "56.85", accessMode = Schema.AccessMode.READ_ONLY)
    private Double margemPercentual;

    // ==================== AUDITORIA ====================

    @Schema(description = "Data de cadastro", example = "2025-11-12T10:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private Date dataCadastro;

    @Schema(description = "Data de última atualização", example = "2025-11-12T15:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private String updatedAt;

    // ==================== DADOS DO FATOR DE TERRENO ====================
    @Schema(description = "Consumo detalhado Rota de Ida", accessMode = Schema.AccessMode.READ_ONLY)
    public ConsumoDetalhadoDTO consumoDetalhadoIda;
    @Schema(description = "Consumo detalhado Rota de Volta", accessMode = Schema.AccessMode.READ_ONLY)
    public ConsumoDetalhadoDTO consumoDetalhadoVolta;


}
