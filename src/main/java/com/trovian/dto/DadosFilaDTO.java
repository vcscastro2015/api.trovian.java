package com.trovian.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para dados da fila que originol do WhastApp")
public class DadosFilaDTO {
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private String id = UUID.randomUUID().toString();

    @JsonProperty("nomeDoArquivo")
    private String nomeDoArquivo;

    @JsonProperty("base64")
    private String base64;

    @JsonProperty("numeroTelefone")
    private String numeroTelefone;

    @JsonProperty("motoristaId")
    private Long motoristaId;

    @JsonProperty("motoristaNome")
    private String motoristaNome;

    @JsonProperty("placa")
    private String placa;

    @JsonProperty("tipoDocumento")
    private String tipoDocumento;

    @JsonProperty("hodometro")
    private String hodometro;

    @JsonProperty("dataEnvio")
    private LocalDateTime dataEnvio = LocalDateTime.now();

    @JsonProperty("documentoCTE")
    private DocumentoCteDTO documentoCte;

    @JsonProperty("abastecimento")
    private AbastecimentoWhatAppDTO abastecimento;

}
