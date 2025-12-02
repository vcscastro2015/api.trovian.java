package com.trovian.dto;

import com.trovian.enums.StatusConta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para Conta a Pagar")
public class ContaPagarDTO {

    @Schema(description = "ID da conta", example = "1")
    private Long id;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    @Schema(description = "Descrição", example = "Manutenção preventiva veículo ABC-1234")
    private String descricao;

    @Schema(description = "Número do documento")
    private String numeroDocumento;

    @Schema(description = "Número da nota fiscal")
    private String numeroNotaFiscal;

    @Schema(description = "Número de controle interno")
    private String numeroControle;

    @NotNull(message = "Fornecedor é obrigatório")
    @Schema(description = "ID do fornecedor", example = "1")
    private Long fornecedorId;

    @Schema(description = "Nome do fornecedor")
    private String fornecedorNome;

    @NotNull(message = "Categoria é obrigatória")
    @Schema(description = "ID da categoria", example = "1")
    private Long categoriaId;

    @Schema(description = "Nome da categoria")
    private String categoriaNome;

    @Schema(description = "ID do centro de custo")
    private Long centroCustoId;

    @Schema(description = "Nome do centro de custo")
    private String centroCustoNome;

    @Schema(description = "ID da forma de pagamento")
    private Long formaPagamentoId;

    @Schema(description = "Nome da forma de pagamento")
    private String formaPagamentoNome;

    @Schema(description = "ID do veículo relacionado")
    private Long veiculoId;

    @Schema(description = "Placa do veículo")
    private String veiculoPlaca;

    @Schema(description = "ID do motorista relacionado")
    private Long motoristaId;

    @Schema(description = "Nome do motorista")
    private String motoristaNome;

    @Schema(description = "ID do cliente")
    private Long clienteId;

    @Schema(description = "Nome do cliente")
    private String clienteNome;

    @NotNull(message = "Valor original é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor original deve ser maior que zero")
    @Schema(description = "Valor original", example = "1500.00")
    private BigDecimal valorOriginal;

    @DecimalMin(value = "0.00", message = "Valor de desconto não pode ser negativo")
    @Schema(description = "Valor de desconto", example = "50.00")
    private BigDecimal valorDesconto;

    @DecimalMin(value = "0.00", message = "Valor de juros não pode ser negativo")
    @Schema(description = "Valor de juros", example = "0.00")
    private BigDecimal valorJuros;

    @DecimalMin(value = "0.00", message = "Valor de multa não pode ser negativo")
    @Schema(description = "Valor de multa", example = "0.00")
    private BigDecimal valorMulta;

    @NotNull(message = "Valor total é obrigatório")
    @Schema(description = "Valor total", example = "1450.00")
    private BigDecimal valorTotal;

    @Schema(description = "Valor já pago", example = "0.00")
    private BigDecimal valorPago;

    @Schema(description = "Saldo a pagar", example = "1450.00")
    private BigDecimal saldo;

    @NotNull(message = "Data de emissão é obrigatória")
    @Schema(description = "Data de emissão", example = "2025-11-01")
    private LocalDate dataEmissao;

    @NotNull(message = "Data de vencimento é obrigatória")
    @Schema(description = "Data de vencimento", example = "2025-12-01")
    private LocalDate dataVencimento;

    @Schema(description = "Data de pagamento", example = "2025-11-28")
    private LocalDate dataPagamento;

    @Schema(description = "Data de competência (mês/ano)", example = "2025-11-01")
    private LocalDate dataCompetencia;

    @NotNull(message = "Status é obrigatório")
    @Schema(description = "Status da conta", example = "PENDENTE")
    private StatusConta status;

    @Schema(description = "Número da parcela", example = "1")
    private Integer numeroParcela;

    @Schema(description = "Total de parcelas", example = "3")
    private Integer totalParcelas;

    @Schema(description = "É recorrente?", example = "false")
    private Boolean recorrente;

    @Schema(description = "Periodicidade", example = "MENSAL")
    private String periodicidade;

    @Schema(description = "Observações")
    private String observacao;

    @Schema(description = "Anexos (JSON)")
    private String anexos;

    @Schema(description = "Usuário que cadastrou")
    private String usuarioCadastro;

    @Schema(description = "Usuário que realizou o pagamento")
    private String usuarioPagamento;

    @Schema(description = "Conta está vencida?", example = "false")
    private Boolean vencida;
}
