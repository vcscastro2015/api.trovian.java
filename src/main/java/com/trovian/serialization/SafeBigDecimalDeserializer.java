package com.trovian.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.math.BigDecimal;

public class SafeBigDecimalDeserializer extends JsonDeserializer<BigDecimal> {

    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) {

        try {
            String value = p.getValueAsString();

            if (value == null || value.isBlank()) {
                return null;
            }

            // limpa lixo comum de OCR
            value = value.replaceAll("[^0-9,.-]", "")
                    .replace(",", ".");

            return new BigDecimal(value);

        } catch (Exception e) {
            return null;
        }
    }
}