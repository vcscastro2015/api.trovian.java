package com.trovian.dto;

import com.trovian.enums.TipoFormaPagamento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para Forma de Pagamento")
public class FormaPagamentoDTO {

    @Schema(description = "ID da forma de pagamento", example = "1")
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Schema(description = "Nome", example = "Boleto Bancário")
    private String nome;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Schema(description = "Descrição")
    private String descricao;

    @Schema(description = "Tipo", example = "BOLETO")
    private TipoFormaPagamento tipo;

    @Schema(description = "Prazo médio em dias", example = "30")
    private Integer prazoMedioDias;

    @Schema(description = "Permite parcelamento", example = "true")
    private Boolean permiteParcelamento;

    @Schema(description = "Status ativo/inativo", example = "true")
    private Boolean status;
}
