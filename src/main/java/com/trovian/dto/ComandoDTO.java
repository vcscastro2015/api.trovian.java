package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representação do comando enviado ao veículo")
public class ComandoDTO {

    @Schema(description = "ID do comando", example = "1")
    private Integer id;

    @Schema(description = "Texto do comando enviado ao equipamento", example = "CMD,123456789012345,RELAY,1#")
    private String comando;

    @Schema(description = "Sigla identificadora do comando", example = "BLQ")
    private String sigla;

    @Schema(description = "Valor retornado pelo aparelho após execução", example = "OK")
    private String valorComando;

    @Schema(description = "Indica se o comando foi enviado ao equipamento", example = "true")
    private Boolean comandoEnviado;

    @Schema(description = "Indica se o retorno do aparelho foi recebido", example = "false")
    private Boolean retornoRecebido;

    @Schema(description = "Indica se o comando está em processamento", example = "false")
    private Boolean comandoEmProcesso;

    @Schema(description = "Indica se houve erro no processamento do comando", example = "false")
    private Boolean comandoComErro;

    @Schema(description = "Retorno bruto recebido do aparelho", example = "+RESP:GTOUT,...")
    private String retornoAparelho;

    @Schema(description = "Indica se este comando depende de outro para ser executado", example = "false")
    private Boolean dependeOutroComando;

    @Schema(description = "ID do comando referenciado quando há dependência", example = "5")
    private Integer idComandoReferente;

    @Schema(description = "Indica se o retorno deve ser exibido ao usuário", example = "true")
    private Boolean mostrarRetorno;

    @Schema(description = "Indica se há mais iButtons a processar", example = "false")
    private Boolean temMaisIbutton;

    @Schema(description = "Código do último iButton inserido", example = "42")
    private Integer ultimoIbuttonInserido;

    @Schema(description = "ID do cliente associado ao comando", example = "7")
    private Integer cliente;

    @Schema(description = "Sequência do comando na fila de processamento", example = "1")
    private Integer sequencia;

    @Schema(description = "Nome da fila de processamento do comando", example = "fila-bloqueio")
    private String nomeDaFila;

    @Schema(description = "Data e hora em que o comando foi criado", example = "2025-01-15T10:30:00")
    private LocalDateTime dataCadastro;

    @Schema(description = "Data e hora em que o processamento foi finalizado", example = "2025-01-15T10:30:05")
    private LocalDateTime dataFimProcesso;

    @Schema(description = "ID do veículo ao qual o comando pertence", example = "10")
    private Long veiculoId;

    @Schema(description = "Status derivado do estado atual do comando",
            example = "CONCLUIDO",
            allowableValues = {"PENDENTE", "ENVIADO", "EM_PROCESSO", "CONCLUIDO", "ERRO"})
    private String status;
}