package com.trovian.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoVeiculoDTO {

    private Long id;
    private String nomeArquivo;
    private String mimeType;
    private LocalDateTime dataEnvio;
    private String numeroTelefone;
    private String motoristaNome;
    private String veiculoPlaca;
}
