package com.trovian.controller;

import com.trovian.dto.relatorio.*;
import com.trovian.enums.StatusViagem;
import com.trovian.service.RelatorioViagemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/relatorios/viagem")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Relatórios de Viagens", description = "Relatórios analíticos de viagens e operações de transporte")
public class RelatorioViagemController {

    private final RelatorioViagemService relatorioService;

    @GetMapping("/consolidado")
    @Operation(summary = "Relatório consolidado de viagens",
               description = "Resumo geral com receitas, custos, lucro, evolução mensal e comparativo com período anterior")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    public ResponseEntity<ViagemConsolidadoDTO> gerarConsolidado(
        @Parameter(description = "Data inicial do período", required = true, example = "2025-01-01")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

        @Parameter(description = "Data final do período", required = true, example = "2025-01-31")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,

        @Parameter(description = "ID do cliente (opcional)")
        @RequestParam(required = false) Long clienteId,

        @Parameter(description = "Status da viagem (ABERTA, ANALISE, FECHADA)")
        @RequestParam(required = false) StatusViagem statusViagem
    ) {
        log.info("Request Consolidado Viagens: {} a {} - Cliente: {}", dataInicio, dataFim, clienteId);

        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Data fim deve ser posterior à data início");
        }

        ViagemConsolidadoDTO relatorio = relatorioService.gerarConsolidado(
            dataInicio, dataFim, clienteId, statusViagem != null ? statusViagem.name() : null);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/rentabilidade-veiculo")
    @Operation(summary = "Rentabilidade por veículo",
               description = "Análise de rentabilidade com receita, custo, lucro, margem e consumo por veículo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    public ResponseEntity<RentabilidadeVeiculoDTO> gerarRentabilidadeVeiculo(
        @Parameter(description = "Data inicial do período", required = true, example = "2025-01-01")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

        @Parameter(description = "Data final do período", required = true, example = "2025-01-31")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,

        @Parameter(description = "ID do cliente (opcional)")
        @RequestParam(required = false) Long clienteId,

        @Parameter(description = "ID do veículo (opcional - filtrar veículo específico)")
        @RequestParam(required = false) Long veiculoId
    ) {
        log.info("Request Rentabilidade Veículo: {} a {}", dataInicio, dataFim);

        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Data fim deve ser posterior à data início");
        }

        RentabilidadeVeiculoDTO relatorio = relatorioService.gerarRentabilidadeVeiculo(
            dataInicio, dataFim, clienteId, veiculoId);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/desempenho-motorista")
    @Operation(summary = "Desempenho por motorista",
               description = "Análise de desempenho com viagens, receita, margem, comissões e km por motorista")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    public ResponseEntity<DesempenhoMotoristaDTO> gerarDesempenhoMotorista(
        @Parameter(description = "Data inicial do período", required = true, example = "2025-01-01")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

        @Parameter(description = "Data final do período", required = true, example = "2025-01-31")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,

        @Parameter(description = "ID do cliente (opcional)")
        @RequestParam(required = false) Long clienteId,

        @Parameter(description = "ID do motorista (opcional - filtrar motorista específico)")
        @RequestParam(required = false) Long motoristaId
    ) {
        log.info("Request Desempenho Motorista: {} a {}", dataInicio, dataFim);

        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Data fim deve ser posterior à data início");
        }

        DesempenhoMotoristaDTO relatorio = relatorioService.gerarDesempenhoMotorista(
            dataInicio, dataFim, clienteId, motoristaId);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/analise-rotas")
    @Operation(summary = "Análise de rotas",
               description = "Análise de rentabilidade, utilização e distância por rota")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    public ResponseEntity<AnaliseRotaDTO> gerarAnaliseRotas(
        @Parameter(description = "Data inicial do período", required = true, example = "2025-01-01")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

        @Parameter(description = "Data final do período", required = true, example = "2025-01-31")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,

        @Parameter(description = "ID do cliente (opcional)")
        @RequestParam(required = false) Long clienteId
    ) {
        log.info("Request Análise Rotas: {} a {}", dataInicio, dataFim);

        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Data fim deve ser posterior à data início");
        }

        AnaliseRotaDTO relatorio = relatorioService.gerarAnaliseRotas(dataInicio, dataFim, clienteId);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/analise-combustivel")
    @Operation(summary = "Análise de combustível",
               description = "Consumo e custos de combustível por veículo e rota com evolução mensal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    public ResponseEntity<AnaliseCombustivelDTO> gerarAnaliseCombustivel(
        @Parameter(description = "Data inicial do período", required = true, example = "2025-01-01")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

        @Parameter(description = "Data final do período", required = true, example = "2025-01-31")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,

        @Parameter(description = "ID do cliente (opcional)")
        @RequestParam(required = false) Long clienteId,

        @Parameter(description = "ID do veículo (opcional - filtrar veículo específico)")
        @RequestParam(required = false) Long veiculoId
    ) {
        log.info("Request Análise Combustível: {} a {}", dataInicio, dataFim);

        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Data fim deve ser posterior à data início");
        }

        AnaliseCombustivelDTO relatorio = relatorioService.gerarAnaliseCombustivel(
            dataInicio, dataFim, clienteId, veiculoId);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/analise-custos")
    @Operation(summary = "Análise de custos de viagem",
               description = "Composição dos custos operacionais, viagens com baixa margem e estatísticas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    public ResponseEntity<AnaliseCustosViagemDTO> gerarAnaliseCustos(
        @Parameter(description = "Data inicial do período", required = true, example = "2025-01-01")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

        @Parameter(description = "Data final do período", required = true, example = "2025-01-31")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,

        @Parameter(description = "ID do cliente (opcional)")
        @RequestParam(required = false) Long clienteId
    ) {
        log.info("Request Análise Custos: {} a {}", dataInicio, dataFim);

        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Data fim deve ser posterior à data início");
        }

        AnaliseCustosViagemDTO relatorio = relatorioService.gerarAnaliseCustos(
            dataInicio, dataFim, clienteId);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/receita-por-cliente")
    @Operation(summary = "Receita por cliente",
               description = "Análise de receita com ranking, margem, toneladas e materiais por cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    public ResponseEntity<ReceitaPorClienteViagemDTO> gerarReceitaPorCliente(
        @Parameter(description = "Data inicial do período", required = true, example = "2025-01-01")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

        @Parameter(description = "Data final do período", required = true, example = "2025-01-31")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,

        @Parameter(description = "ID do cliente (opcional - filtrar cliente específico)")
        @RequestParam(required = false) Long clienteId
    ) {
        log.info("Request Receita por Cliente: {} a {}", dataInicio, dataFim);

        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Data fim deve ser posterior à data início");
        }

        ReceitaPorClienteViagemDTO relatorio = relatorioService.gerarReceitaPorCliente(
            dataInicio, dataFim, clienteId);
        return ResponseEntity.ok(relatorio);
    }
}
