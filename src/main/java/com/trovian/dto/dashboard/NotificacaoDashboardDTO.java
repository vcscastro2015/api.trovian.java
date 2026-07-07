package com.trovian.dto.dashboard;

import com.trovian.dto.NotificacaoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dashboard consolidado de notificações")
public class NotificacaoDashboardDTO {

    @Schema(description = "Total de notificações do período")
    private long total;

    @Schema(description = "Total de notificações não lidas (status PENDENTE ou ENVIADA)")
    private long totalNaoLidas;

    @Schema(description = "Contagem por status: PENDENTE, ENVIADA, LIDA, RESPONDIDA, ERRO, CANCELADA")
    private Map<String, Long> porStatus;

    @Schema(description = "Contagem por tipo de notificação do enum TipoNotificacao")
    private Map<String, Long> porTipo;

    @Schema(description = "Contagem agrupada por categoria: FINANCEIRO, FROTA, OPERACIONAL")
    private Map<String, Long> porCategoria;

    @Schema(description = "Contagem por referenciaTipo (template de origem: CONTA_PAGAR_VENCIDA, MANUTENCAO_VENCIDA etc.)")
    private Map<String, Long> porReferenciaTipo;

    @Schema(description = "Últimas 10 notificações recentes")
    private List<NotificacaoDTO> recentes;

    @Schema(description = "Tendência diária de criação de notificações")
    private List<TendenciaDiariaDTO> tendencia;
}
