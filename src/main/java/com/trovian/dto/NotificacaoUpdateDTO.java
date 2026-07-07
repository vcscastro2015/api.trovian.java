package com.trovian.dto;

import com.trovian.entity.Notificacao;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para atualização de uma notificação")
public class NotificacaoUpdateDTO {

    @Schema(description = "Novo status da notificação")
    private Notificacao.StatusNotificacao status;

    @Schema(description = "Resposta do motorista (preenchida pelo sistema WhatsApp)")
    private String respostaMotorista;
}
