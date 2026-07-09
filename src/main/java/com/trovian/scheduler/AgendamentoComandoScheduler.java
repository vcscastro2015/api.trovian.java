package com.trovian.scheduler;

import com.trovian.entity.AgendamentoComandoVeiculo;
import com.trovian.enums.StatusComando;
import com.trovian.repository.AgendamentoComandoVeiculoRepository;
import com.trovian.service.ComandoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AgendamentoComandoScheduler {

    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    private static final long JANELA_MINUTOS = 3;
    private static final long INTERVALO_MINUTOS = 6;

    private final AgendamentoComandoVeiculoRepository repository;
    private final ComandoService comandoService;

    @Scheduled(fixedDelay = 300_000)
    public void processarAgendamentos() {
        LocalDateTime agora = LocalDateTime.now(ZONA);
        List<AgendamentoComandoVeiculo> agendamentos = repository.findByAtivoTrue();
        log.info("Scheduler de agendamentos iniciado. Total ativo: {}", agendamentos.size());
        for (AgendamentoComandoVeiculo agendamento : agendamentos) {
            try {
                if (!dentroJanelaDeHorario(agendamento.getHorario(), agora.toLocalTime())) continue;
                if (jaExecutadoRecentemente(agendamento.getUltimaExecucao(), agora)) continue;
                if (!recorrenciaAtiva(agendamento, agora)) continue;

                Integer idComando = agendamento.getTipoComando() == StatusComando.BLOQUEAR ? 1 : 2;
                Long idVeiculo = agendamento.getVeiculo().getId();

                log.info("Disparando agendamento id={} | veiculo={} | comando={}",
                        agendamento.getId(), idVeiculo, agendamento.getTipoComando());

                comandoService.inserirComando(idComando, idVeiculo);
                agendamento.setUltimaExecucao(agora);
                repository.save(agendamento);
            } catch (Exception e) {
                log.error("Erro ao processar agendamento id={}", agendamento.getId(), e);
            }
        }
    }

    private boolean dentroJanelaDeHorario(LocalTime horarioAgendado, LocalTime agora) {
        long diffMinutos = Math.abs(Duration.between(horarioAgendado, agora).toMinutes());
        return diffMinutos <= JANELA_MINUTOS;
    }

    private boolean jaExecutadoRecentemente(LocalDateTime ultimaExecucao, LocalDateTime agora) {
        if (ultimaExecucao == null) return false;
        return Duration.between(ultimaExecucao, agora).toMinutes() < INTERVALO_MINUTOS;
    }

    private boolean recorrenciaAtiva(AgendamentoComandoVeiculo agendamento, LocalDateTime agora) {
        return switch (agendamento.getTipoRecorrencia()) {
            case DIARIO -> true;
            case MENSAL -> agendamento.getDiaDomes() != null
                    && agendamento.getDiaDomes() == agora.getDayOfMonth();
            case DIA_ESPECIFICO -> agendamento.getDataEspecifica() != null
                    && agendamento.getDataEspecifica().equals(agora.toLocalDate());
        };
    }
}
