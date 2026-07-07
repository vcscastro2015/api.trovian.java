package com.trovian.dto;

import com.trovian.entity.Notificacao;
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
@Schema(description = "Dados de uma notificação")
public class NotificacaoDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    private Long motoristaId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String motoristaNome;

    private Long usuarioId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String usuarioNome;

    private String mensagem;
    private Notificacao.TipoNotificacao tipo;
    private Notificacao.StatusNotificacao status;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataCriacao;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataEnvio;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer tentativasEnvio;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String erroEnvio;

    private String respostaMotorista;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime dataResposta;

    private String referenciaTipo;
    private Long referenciaId;
}
