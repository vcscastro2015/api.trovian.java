package com.trovian.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.trovian.serialization.SafeBigDecimalDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * Classe para acomodar o abastecimento que vem do serviço de WhatsApp
 */
public class AbastecimentoWhatAppDTO {
    @JsonDeserialize(using = SafeBigDecimalDeserializer.class)
    private BigDecimal totalAPagar;
    @JsonDeserialize(using = SafeBigDecimalDeserializer.class)
    private BigDecimal litros;
    @JsonDeserialize(using = SafeBigDecimalDeserializer.class)
    private BigDecimal precoPorLitro;
}
