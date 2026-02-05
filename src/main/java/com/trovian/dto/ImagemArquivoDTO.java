package com.trovian.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para imagens dos arquivos")
public class ImagemArquivoDTO {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private String id = UUID.randomUUID().toString();

    @JsonProperty("base64")
    private String base64;
}
