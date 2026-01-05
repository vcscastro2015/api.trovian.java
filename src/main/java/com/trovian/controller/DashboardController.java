package com.trovian.controller;

import com.trovian.dto.dashboard.*;
import com.trovian.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "APIs do Dashboard de GestÃ£o")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/completo")
    @Operation(summary = "Dashboard Completo", description = "Retorna todos os indicadores do dashboard")
    public ResponseEntity<DashboardCompletoDTO> getDashboardCompleto(
            @RequestParam Long clienteId,
            @RequestParam(defaultValue = "30") Integer diasAnalise
    ) {
        log.info("GET /api/dashboard/completo - Cliente: {}, Dias: {}", clienteId, diasAnalise);
        DashboardCompletoDTO dashboard = dashboardService.getDashboardCompleto(clienteId, diasAnalise);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/resumo")
    @Operation(summary = "Resumo Geral", description = "Indicadores principais do dashboard")
    public ResponseEntity<DashboardResumoDTO> getResumo(
            @RequestParam Long clienteId,
            @RequestParam(defaultValue = "30") Integer diasAnalise
    ) {
        LocalDate dataInicio = LocalDate.now().minusDays(diasAnalise);
        DashboardResumoDTO resumo = dashboardService.getResumo(clienteId, dataInicio);
        return ResponseEntity.ok(resumo);
    }

    @GetMapping("/lucratividade-rotas")
    @Operation(summary = "Lucratividade por Rota", description = "AnÃ¡lise de lucratividade de cada rota")
    public ResponseEntity<List<LucratividadeRotaDTO>> getLucratividadeRotas(
            @RequestParam Long clienteId,
            @RequestParam(defaultValue = "30") Integer diasAnalise
    ) {
        LocalDate dataInicio = LocalDate.now().minusDays(diasAnalise);
        List<LucratividadeRotaDTO> lucratividade = dashboardService.getLucratividadeRotas(clienteId, dataInicio);
        return ResponseEntity.ok(lucratividade);
    }

    @GetMapping("/comparacao-rotas")
    @Operation(summary = "ComparaÃ§Ã£o de Rotas", description = "Compara rotas e identifica as mais lucrativas")
    public ResponseEntity<ComparacaoRotasDTO> getComparacaoRotas(
            @RequestParam Long clienteId,
            @RequestParam(defaultValue = "30") Integer diasAnalise
    ) {
        LocalDate dataInicio = LocalDate.now().minusDays(diasAnalise);
        ComparacaoRotasDTO comparacao = dashboardService.getComparacaoRotas(clienteId, dataInicio);
        return ResponseEntity.ok(comparacao);
    }

    @GetMapping("/alertas-baixa-margem")
    @Operation(summary = "Alertas de Baixa Margem", description = "Viagens com margem de lucro abaixo do esperado")
    public ResponseEntity<List<AlertaBaixaMargemDTO>> getAlertasBaixaMargem(
            @RequestParam Long clienteId,
            @RequestParam(defaultValue = "30") Integer diasAnalise
    ) {
        LocalDate dataInicio = LocalDate.now().minusDays(diasAnalise);
        List<AlertaBaixaMargemDTO> alertas = dashboardService.getAlertasBaixaMargem(clienteId, dataInicio);
        return ResponseEntity.ok(alertas);
    }

    @GetMapping("/eficiencia-veiculos")
    @Operation(summary = "EficiÃªncia de VeÃ­culos", description = "Performance e eficiÃªncia de cada veÃ­culo")
    public ResponseEntity<List<EficienciaVeiculoDTO>> getEficienciaVeiculos(
            @RequestParam Long clienteId,
            @RequestParam(defaultValue = "30") Integer diasAnalise
    ) {
        LocalDate dataInicio = LocalDate.now().minusDays(diasAnalise);
        List<EficienciaVeiculoDTO> eficiencia = dashboardService.getEficienciaVeiculos(clienteId, dataInicio);
        return ResponseEntity.ok(eficiencia);
    }

    @GetMapping("/performance-motoristas")
    @Operation(summary = "Performance de Motoristas", description = "AnÃ¡lise de performance dos motoristas")
    public ResponseEntity<List<PerformanceMotoristaDTO>> getPerformanceMotoristas(
            @RequestParam Long clienteId,
            @RequestParam(defaultValue = "30") Integer diasAnalise
    ) {
        LocalDate dataInicio = LocalDate.now().minusDays(diasAnalise);
        List<PerformanceMotoristaDTO> performance = dashboardService.getPerformanceMotoristas(clienteId, dataInicio);
        return ResponseEntity.ok(performance);
    }

    @GetMapping("/tendencias")
    @Operation(summary = "TendÃªncias Mensais", description = "EvoluÃ§Ã£o de receita e lucro ao longo do tempo")
    public ResponseEntity<List<TendenciaLucratividadeDTO>> getTendencias(
            @RequestParam Long clienteId,
            @RequestParam(defaultValue = "12") Integer meses
    ) {
        List<TendenciaLucratividadeDTO> tendencias = dashboardService.getTendenciasMensais(clienteId, meses);
        return ResponseEntity.ok(tendencias);
    }

    @GetMapping("/custos-operacionais")
    @Operation(summary = "Custos Operacionais", description = "Breakdown detalhado dos custos")
    public ResponseEntity<CustoOperacionalDTO> getCustosOperacionais(
            @RequestParam Long clienteId,
            @RequestParam(defaultValue = "30") Integer diasAnalise
    ) {
        LocalDate dataInicio = LocalDate.now().minusDays(diasAnalise);
        CustoOperacionalDTO custos = dashboardService.getCustosOperacionais(clienteId, dataInicio);
        return ResponseEntity.ok(custos);
    }

    @GetMapping("/indicadores-rotas")
    @Operation(summary = "Indicadores de Rotas", description = "Taxa de ocupaÃ§Ã£o, km produtivo, etc")
    public ResponseEntity<List<IndicadorRotaDTO>> getIndicadoresRotas(
            @RequestParam Long clienteId,
            @RequestParam(defaultValue = "30") Integer diasAnalise
    ) {
        LocalDate dataInicio = LocalDate.now().minusDays(diasAnalise);
        List<IndicadorRotaDTO> indicadores = dashboardService.getIndicadoresRotas(clienteId, dataInicio);
        return ResponseEntity.ok(indicadores);
    }

    @GetMapping("/mapa-calor")
    @Operation(summary = "Mapa de Calor de Rotas", description = "VisualizaÃ§Ã£o geogrÃ¡fica de lucratividade")
    public ResponseEntity<List<MapaCalorRotaDTO>> getMapaCalor(
            @RequestParam Long clienteId,
            @RequestParam(defaultValue = "30") Integer diasAnalise
    ) {
        LocalDate dataInicio = LocalDate.now().minusDays(diasAnalise);
        List<MapaCalorRotaDTO> mapaCalor = dashboardService.getMapaCalorRotas(clienteId, dataInicio);
        return ResponseEntity.ok(mapaCalor);
    }

    @GetMapping("/previsao-meta")
    @Operation(summary = "PrevisÃ£o e Metas", description = "Acompanhamento de metas e projeÃ§Ãµes")
    public ResponseEntity<PrevisaoMetaDTO> getPrevisaoMeta(
            @RequestParam Long clienteId
    ) {
        PrevisaoMetaDTO previsao = dashboardService.getPrevisaoMeta(clienteId);
        return ResponseEntity.ok(previsao);
    }
}
