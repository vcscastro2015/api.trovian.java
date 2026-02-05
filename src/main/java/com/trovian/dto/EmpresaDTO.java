package com.trovian.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.trovian.serialization.EnderecoDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para dados de empresa no CT-e")
public class EmpresaDTO {

    @Schema(description = "Razão social", example = "TRANSCOOPA - COOP DE TRANSP ROD DE CARG")
    private String razaoSocial;

    @Schema(description = "CNPJ", example = "21647397000192")
    private String cnpj;

    @Schema(description = "Inscrição estadual", example = "7010073042")
    private String ie;

    @JsonDeserialize(using = EnderecoDeserializer.class)
    private EnderecoDTO endereco;
}
