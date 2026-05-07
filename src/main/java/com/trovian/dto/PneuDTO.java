package com.trovian.dto;

import com.trovian.enums.PosicaoPneu;
import com.trovian.enums.StatusPneu;
import com.trovian.enums.TipoEixo;
import com.trovian.enums.TipoPneu;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representação de Pneu")
public class PneuDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "Número DOT é obrigatório")
    @Schema(required = true, example = "DOT HJ8X RWLX 2423", description = "Código DOT gravado no pneu")
    private String numeroDot;

    @Schema(example = "ABC123", description = "Número de fogo / série do fabricante")
    private String numeroFogo;

    @Schema(example = "Michelin", description = "Marca do pneu")
    private String marca;

    @Schema(example = "XZE2+", description = "Modelo do pneu")
    private String modelo;

    @Schema(example = "275/80R22.5", description = "Dimensão do pneu")
    private String dimensao;

    @Schema(description = "Tipo do pneu: BORRACHUDO, LISO, MISTO, DIRECIONAL")
    private TipoPneu tipoPneu;

    @Schema(description = "Tipo de eixo: DIRECIONAL, TRACAO, LIVRE, ESTEPE")
    private TipoEixo eixo;

    @Schema(example = "2024-01-01", description = "Data de fabricação extraída do DOT")
    private LocalDate dataFabricacao;

    @Schema(example = "2024-03-15", description = "Data de compra")
    private LocalDate dataCompra;

    @Schema(example = "1200.00", description = "Valor de compra em R$")
    private BigDecimal valorCompra;

    @Schema(example = "1", description = "ID do fornecedor")
    private Long fornecedorId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Razão social do fornecedor")
    private String fornecedorNome;

    @Schema(example = "18.0", description = "Profundidade inicial do sulco em mm")
    private BigDecimal profundidadeInicial;

    @Schema(example = "1.6", description = "Profundidade mínima permitida em mm")
    private BigDecimal profundidadeMinima;

    @Schema(example = "120000", description = "KM de vida útil esperada")
    private Integer kmLimite;

    @Schema(description = "Status atual do pneu")
    private StatusPneu status;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Contador de recapagens realizadas")
    private Integer numeroRecapagens;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Total de km rodados na vida do pneu")
    private Integer kmAcumulado;

    @NotNull(message = "Cliente é obrigatório")
    @Schema(required = true, example = "1", description = "ID do cliente (multi-tenancy)")
    private Long clienteId;

    @Schema(example = "Observações gerais sobre o pneu", description = "Observações")
    private String observacoes;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "ID do veículo atual (se montado)")
    private Long veiculoAtualId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Placa do veículo atual")
    private String veiculoAtualPlaca;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Posição atual no veículo")
    private PosicaoPneu posicaoAtual;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataCadastro;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
