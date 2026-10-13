package com.trovian.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        SimpleModule flexibleDates = new SimpleModule();
        flexibleDates.addDeserializer(LocalDateTime.class, new FlexibleLocalDateTimeDeserializer());
        flexibleDates.addDeserializer(OffsetDateTime.class, new FlexibleOffsetDateTimeDeserializer());
        mapper.registerModule(flexibleDates);

        return mapper;
    }
}
