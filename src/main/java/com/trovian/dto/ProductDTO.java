package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de transferência de dados de Produto")
public class ProductDTO {

    @Schema(description = "ID único do produto", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Nome do produto é obrigatório")
    @Schema(description = "Nome do produto", example = "Notebook Dell Inspiron 15", required = true)
    private String name;

    @Schema(description = "Descrição detalhada do produto", example = "Notebook Dell Inspiron 15 com 16GB RAM e SSD 512GB")
    private String description;

    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser positivo")
    @Schema(description = "Preço do produto em reais", example = "3500.00", required = true)
    private BigDecimal price;

    @NotNull(message = "Quantidade é obrigatória")
    @Schema(description = "Quantidade disponível em estoque", example = "10", required = true)
    private Integer quantity;
}
