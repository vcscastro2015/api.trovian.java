package com.trovian.controller;

import com.trovian.dto.dashboard.*;
import com.trovian.service.TelemetriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dashboard/telemetria")
@RequiredArgsConstructor
@Tag(name = "Dashboard Telemetria", description = "APIs de telemetria em tempo real para gestão de frota")
@CrossOrigin(origins = "*")
public class DashboardTelemetriaController {

    private final TelemetriaService telemetriaService;

    @GetMapping("/kpis")
    @Operation(summary = "KPIs do período", description = "Retorna km total, litros consumidos, km/l, eventos bruscos, score médio e veículos ativos")
    public ResponseEntity<KpiDashboardDTO> getKpis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim
    ) {
        log.info("GET /api/dashboard/telemetria/kpis - inicio={}, fim={}", inicio, fim);
        return ResponseEntity.ok(telemetriaService.getKpis(inicio, fim));
    }

    @GetMapping("/rpm-distribuicao")
    @Operation(summary = "Distribuição de RPM", description = "Percentual do tempo em cada faixa de RPM configurada no veículo")
    public ResponseEntity<RpmDistribuicaoDTO> getRpmDistribuicao(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim
    ) {
        log.info("GET /api/dashboard/telemetria/rpm-distribuicao - inicio={}, fim={}", inicio, fim);
        return ResponseEntity.ok(telemetriaService.getRpmDistribuicao(inicio, fim));
    }

    @GetMapping("/heatmap-eventos")
    @Operation(summary = "Heatmap de eventos bruscos", description = "Contagem de eventos por hora do dia × dia da semana")
    public ResponseEntity<List<HeatmapPontoDTO>> getHeatmapEventos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim
    ) {
        log.info("GET /api/dashboard/telemetria/heatmap-eventos - inicio={}, fim={}", inicio, fim);
        return ResponseEntity.ok(telemetriaService.getHeatmapEventos(inicio, fim));
    }

    @GetMapping("/top-veiculos")
    @Operation(summary = "Top veículos por eventos / 100 km", description = "Ranking de veículos com mais eventos bruscos normalizados por 100 km rodados")
    public ResponseEntity<List<TopVeiculoDTO>> getTopVeiculos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @RequestParam(defaultValue = "10") int limite
    ) {
        log.info("GET /api/dashboard/telemetria/top-veiculos - inicio={}, fim={}, limite={}", inicio, fim, limite);
        return ResponseEntity.ok(telemetriaService.getTopVeiculos(inicio, fim, limite));
    }

    @GetMapping("/posicoes-atuais")
    @Operation(summary = "Posições atuais dos veículos", description = "Última posição GPS conhecida de cada veículo ativo nas últimas 24 horas")
    public ResponseEntity<List<PosicaoAtualDTO>> getPosicoesAtuais() {
        log.info("GET /api/dashboard/telemetria/posicoes-atuais");
        return ResponseEntity.ok(telemetriaService.getPosicoesAtuais());
    }
}
