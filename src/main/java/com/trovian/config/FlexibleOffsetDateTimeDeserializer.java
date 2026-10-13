package com.trovian.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FlexibleOffsetDateTimeDeserializer extends StdDeserializer<OffsetDateTime> {

    public FlexibleOffsetDateTimeDeserializer() {
        super(OffsetDateTime.class);
    }

    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().trim();
        try {
            return OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException e1) {
            try {
                LocalDateTime ldt = LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                return ldt.atOffset(ZoneOffset.UTC);
            } catch (DateTimeParseException e2) {
                LocalDate ld = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
                return ld.atStartOfDay().atOffset(ZoneOffset.UTC);
            }
        }
    }
}
