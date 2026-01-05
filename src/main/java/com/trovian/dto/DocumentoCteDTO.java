package com.trovian.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para documento CT-e (Conhecimento de Transporte Eletrônico)")
public class DocumentoCteDTO {

    @Schema(description = "Número do CT-e", example = "000028529")
    private String numero;

    @Schema(description = "Série do documento", example = "1")
    private String serie;

    @Schema(description = "Chave de acesso do CT-e", example = "312514218980618001740579010009285291009023026")
    private String chaveAcesso;

    @Schema(description = "Data de emissão do documento")
    private LocalDateTime dataEmissao;

    @Schema(description = "Modelo do documento", example = "57")
    private String modelo;

    @Schema(description = "Tipo de documento", example = "NORMAL")
    private String tipoDocumento;

    @Schema(description = "Dados do emitente")
    private EmpresaDTO emitente;

    @Schema(description = "Dados do remetente")
    private EmpresaDTO remetente;

    @Schema(description = "Dados do destinatário")
    private EmpresaDTO destinatario;

    @Schema(description = "Dados do tomador do serviço")
    private EmpresaDTO tomador;

    @Schema(description = "Dados da carga transportada")
    private DadosCargaDTO dadosCarga;

    @Schema(description = "Valores da prestação de serviço")
    private ValoresPrestacaoDTO valoresPrestacao;

    @Schema(description = "Observações do CT-e")
    private String observacoes;
}
