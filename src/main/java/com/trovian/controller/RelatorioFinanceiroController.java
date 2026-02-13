package com.trovian.controller;

import com.trovian.dto.relatorio.*;
import com.trovian.enums.StatusConta;
import com.trovian.service.RelatorioFinanceiroService;
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
@RequestMapping("/relatorios/financeiro")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Relatórios Financeiros", description = "Relatórios financeiros e gerenciais")
public class RelatorioFinanceiroController {

    private final RelatorioFinanceiroService relatorioService;

    @GetMapping("/dre")
    @Operation(summary = "Gerar DRE - Demonstrativo de Resultado do Exercício",
               description = "Apresenta receitas, despesas e resultado do período com detalhamento por categoria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "DRE gerado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    public ResponseEntity<DreDTO> gerarDRE(
        @Parameter(description = "Data inicial do período", required = true, example = "2025-01-01")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

        @Parameter(description = "Data final do período", required = true, example = "2025-01-31")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,

        @Parameter(description = "ID do cliente (opcional - filtrar por cliente específico)")
        @RequestParam(required = false) Long clienteId,

        @Parameter(description = "ID do centro de custo (opcional)")
        @RequestParam(required = false) Long centroCustoId,

        @Parameter(description = "Comparar com período anterior?", example = "false")
        @RequestParam(defaultValue = "false") Boolean compararPeriodoAnterior
    ) {
        log.info("Request DRE: {} a {} - Cliente: {} - Comparar: {}",
                 dataInicio, dataFim, clienteId, compararPeriodoAnterior);

        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Data fim deve ser posterior à data início");
        }

        DreDTO dre = relatorioService.gerarDRE(dataInicio, dataFim, clienteId,
                                                centroCustoId, compararPeriodoAnterior);
        return ResponseEntity.ok(dre);
    }

    @GetMapping("/contas-pagar/consolidado")
    @Operation(summary = "Relatório consolidado de contas a pagar",
               description = "Visão completa das obrigações financeiras com análises e agrupamentos")
    public ResponseEntity<ContasPagarConsolidadoDTO> gerarContasPagarConsolidado(
        @Parameter(description = "Data inicial", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

        @Parameter(description = "Data final", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,

        @Parameter(description = "ID do cliente")
        @RequestParam(required = false)  Long clienteId,

        @Parameter(description = "Filtrar por status")
        @RequestParam(required = false) StatusConta status,

        @Parameter(description = "Filtrar por fornecedor")
        @RequestParam(required = false) Long fornecedorId,

        @Parameter(description = "Filtrar por categoria")
        @RequestParam(required = false) Long categoriaId
    ) {
        log.info("Request Contas a Pagar Consolidado: {} a {}", dataInicio, dataFim);

        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Data fim deve ser posterior à data início");
        }

        ContasPagarConsolidadoDTO relatorio = relatorioService.gerarContasPagarConsolidado(
            dataInicio, dataFim, status, fornecedorId, categoriaId,clienteId
        );
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/contas-receber/consolidado")
    @Operation(summary = "Relatório consolidado de contas a receber",
               description = "Visão completa das receitas com aging e análise de inadimplência")
    public ResponseEntity<ContasReceberConsolidadoDTO> gerarContasReceberConsolidado(
        @Parameter(description = "Data inicial", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

        @Parameter(description = "Data final", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,

        @Parameter(description = "Filtrar por cliente")
        @RequestParam(required = false) Long clienteId,

        @Parameter(description = "Filtrar por status")
        @RequestParam(required = false) StatusConta status,

        @Parameter(description = "Filtrar por fornecedor")
        @RequestParam(required = false) Long fornecedorId,

        @Parameter(description = "Filtrar por categoria")
        @RequestParam(required = false) Long categoriaId
    ) {
        log.info("Request Contas a Receber Consolidado: {} a {}", dataInicio, dataFim);

        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Data fim deve ser posterior à data início");
        }

        ContasReceberConsolidadoDTO relatorio = relatorioService.gerarContasReceberConsolidado(
            dataInicio, dataFim, status, clienteId, fornecedorId, categoriaId
        );
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/fluxo-caixa/realizado")
    @Operation(summary = "Fluxo de Caixa Realizado",
               description = "Movimentações financeiras efetivamente realizadas (pagas e recebidas)")
    public ResponseEntity<FluxoCaixaRealizadoDTO> gerarFluxoCaixaRealizado(
        @Parameter(description = "Data inicial", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

        @Parameter(description = "Data final", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,

        @Parameter(description = "Filtrar por cliente")
        @RequestParam(required = false) Long clienteId,

        @Parameter(description = "Filtrar por centro de custo")
        @RequestParam(required = false) Long centroCustoId,

        @Parameter(description = "Incluir evolução mensal (últimos 12 meses)?", example = "false")
        @RequestParam(defaultValue = "false") Boolean incluirEvolucaoMensal,

        @Parameter(description = "Comparar com período anterior?", example = "false")
        @RequestParam(defaultValue = "false") Boolean compararPeriodoAnterior
    ) {
        log.info("Request Fluxo de Caixa: {} a {}", dataInicio, dataFim);

        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Data fim deve ser posterior à data início");
        }

        FluxoCaixaRealizadoDTO relatorio = relatorioService.gerarFluxoCaixaRealizado(
            dataInicio, dataFim, clienteId, centroCustoId, incluirEvolucaoMensal, compararPeriodoAnterior
        );
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/despesas/por-categoria")
    @Operation(summary = "Análise de despesas por categoria",
               description = "Ranking de categorias, evolução temporal e comparativos de despesas")
    public ResponseEntity<DespesasPorCategoriaDTO> gerarDespesasPorCategoria(
        @Parameter(description = "Data inicial", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

        @Parameter(description = "Data final", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,

        @Parameter(description = "Filtrar por cliente")
        @RequestParam(required = false) Long clienteId,

        @Parameter(description = "Incluir evolução mensal?", example = "false")
        @RequestParam(defaultValue = "false") Boolean incluirEvolucaoMensal,

        @Parameter(description = "Comparar com período anterior?", example = "false")
        @RequestParam(defaultValue = "false") Boolean compararPeriodoAnterior
    ) {
        log.info("Request Despesas por Categoria: {} a {}", dataInicio, dataFim);

        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Data fim deve ser posterior à data início");
        }

        DespesasPorCategoriaDTO relatorio = relatorioService.gerarDespesasPorCategoria(
            dataInicio, dataFim, clienteId, incluirEvolucaoMensal, compararPeriodoAnterior
        );
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/receitas/por-cliente")
    @Operation(summary = "Análise de receitas por cliente",
               description = "Ranking de clientes, rentabilidade, ticket médio e comportamento de pagamento")
    public ResponseEntity<ReceitasPorClienteDTO> gerarReceitasPorCliente(
        @Parameter(description = "Data inicial", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

        @Parameter(description = "Data final", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,

        @Parameter(description = "Filtrar por cliente")
        @RequestParam(required = false) Long clienteId,

        @Parameter(description = "Incluir evolução mensal?", example = "false")
        @RequestParam(defaultValue = "false") Boolean incluirEvolucaoMensal,

        @Parameter(description = "Comparar com período anterior?", example = "false")
        @RequestParam(defaultValue = "false") Boolean compararPeriodoAnterior
    ) {
        log.info("Request Receitas por Cliente: {} a {}", dataInicio, dataFim);

        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Data fim deve ser posterior à data início");
        }

        ReceitasPorClienteDTO relatorio = relatorioService.gerarReceitasPorCliente(
            dataInicio, dataFim, clienteId, incluirEvolucaoMensal, compararPeriodoAnterior
        );
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/inadimplencia/detalhada")
    @Operation(summary = "Relatório detalhado de inadimplência",
               description = "Controle e análise de contas vencidas com aging, inadimplentes e ações recomendadas")
    public ResponseEntity<InadimplenciaDetalhadaDTO> gerarInadimplenciaDetalhada(
        @Parameter(description = "Tipo de análise: RECEBER, PAGAR ou AMBOS", required = true)
        @RequestParam InadimplenciaDetalhadaDTO.TipoAnalise tipo,

        @Parameter(description = "ID da entidade específica (cliente ou fornecedor)")
        @RequestParam(required = false) Long entidadeId
    ) {
        log.info("Request Inadimplência Detalhada - Tipo: {}", tipo);

        InadimplenciaDetalhadaDTO relatorio = relatorioService.gerarInadimplenciaDetalhada(
            tipo, entidadeId
        );
        return ResponseEntity.ok(relatorio);
    }
}
