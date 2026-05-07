package com.trovian.controller;

import com.trovian.dto.CpkPneuDTO;
import com.trovian.dto.PneuDTO;
import com.trovian.dto.ResumoFrotaPneusDTO;
import com.trovian.enums.StatusPneu;
import com.trovian.service.PneuService;
import com.trovian.service.RelatorioPneuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pneu")
@RequiredArgsConstructor
@Tag(name = "Pneu", description = "Gestão do ciclo de vida de pneus da frota")
public class PneuController {

    private final PneuService pneuService;
    private final RelatorioPneuService relatorioPneuService;

    @PostMapping
    @Operation(summary = "Cadastrar pneu")
    @ApiResponse(responseCode = "201", description = "Pneu cadastrado com sucesso")
    @ApiResponse(responseCode = "400", description = "DOT duplicado ou dados inválidos")
    public ResponseEntity<PneuDTO> cadastrar(@Valid @RequestBody PneuDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pneuService.cadastrar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar pneus do cliente")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<Page<PneuDTO>> listar(
            @Parameter(description = "ID do cliente") @RequestParam Long clienteId,
            @Parameter(description = "Filtrar por status") @RequestParam(required = false) StatusPneu status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<PneuDTO> result = pneuService.listar(clienteId, status,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro")));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pneu por ID")
    @ApiResponse(responseCode = "200", description = "Pneu encontrado")
    @ApiResponse(responseCode = "404", description = "Pneu não encontrado")
    public ResponseEntity<PneuDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pneuService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pneu")
    @ApiResponse(responseCode = "200", description = "Pneu atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Pneu não encontrado")
    public ResponseEntity<PneuDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PneuDTO dto) {
        return ResponseEntity.ok(pneuService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Descartar pneu (soft delete via status DESCARTADO)")
    @ApiResponse(responseCode = "204", description = "Pneu descartado com sucesso")
    @ApiResponse(responseCode = "400", description = "Pneu em uso — desmonte antes de descartar")
    public ResponseEntity<Void> descartar(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo) {
        pneuService.descartar(id, motivo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/cpk")
    @Operation(summary = "Calcular CPK do pneu")
    @ApiResponse(responseCode = "200", description = "CPK calculado com sucesso")
    public ResponseEntity<CpkPneuDTO> calcularCpk(@PathVariable Long id) {
        return ResponseEntity.ok(pneuService.calcularCpk(id));
    }

    @GetMapping("/relatorio/proximos-troca")
    @Operation(summary = "Pneus próximos do limite de troca")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<CpkPneuDTO>> proximosTroca(
            @Parameter(description = "ID do cliente") @RequestParam Long clienteId) {
        return ResponseEntity.ok(relatorioPneuService.getPneusProximosTroca(clienteId));
    }

    @GetMapping("/relatorio/cpk-por-marca")
    @Operation(summary = "CPK comparativo por marca")
    @ApiResponse(responseCode = "200", description = "Comparativo retornado com sucesso")
    public ResponseEntity<Map<String, BigDecimal>> cpkPorMarca(
            @Parameter(description = "ID do cliente") @RequestParam Long clienteId) {
        return ResponseEntity.ok(relatorioPneuService.getCpkPorMarca(clienteId));
    }

    @GetMapping("/relatorio/custo-frota")
    @Operation(summary = "Custo total da frota de pneus no período")
    @ApiResponse(responseCode = "200", description = "Custo calculado com sucesso")
    public ResponseEntity<BigDecimal> custoFrota(
            @Parameter(description = "ID do cliente") @RequestParam Long clienteId,
            @Parameter(description = "Data inicial (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde) {
        return ResponseEntity.ok(relatorioPneuService.getCustoTotalFrota(clienteId, desde));
    }

    @GetMapping("/relatorio/resumo")
    @Operation(summary = "Resumo da frota de pneus para dashboard")
    @ApiResponse(responseCode = "200", description = "Resumo retornado com sucesso")
    public ResponseEntity<ResumoFrotaPneusDTO> resumoFrota(
            @Parameter(description = "ID do cliente") @RequestParam Long clienteId) {
        return ResponseEntity.ok(pneuService.getResumoFrota(clienteId));
    }
}
