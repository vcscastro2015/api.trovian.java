package com.trovian.controller;

import com.trovian.dto.InspecaoPneuDTO;
import com.trovian.service.InspecaoPneuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pneu/inspecao")
@RequiredArgsConstructor
@Tag(name = "Inspeção de Pneu", description = "Registros periódicos de inspeção de pneus")
public class InspecaoPneuController {

    private final InspecaoPneuService inspecaoPneuService;

    @GetMapping
    @Operation(summary = "Listar inspeções por cliente")
    @ApiResponse(responseCode = "200", description = "Inspeções retornadas com sucesso")
    public ResponseEntity<Page<InspecaoPneuDTO>> listarPorCliente(
            @Parameter(description = "ID do cliente") @RequestParam Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<InspecaoPneuDTO> result = inspecaoPneuService.listarPorCliente(clienteId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataInspecao")));
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Operation(summary = "Registrar inspeção de pneu")
    @ApiResponse(responseCode = "201", description = "Inspeção registrada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<InspecaoPneuDTO> registrar(@Valid @RequestBody InspecaoPneuDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inspecaoPneuService.registrarInspecao(dto));
    }

    @GetMapping("/pneu/{pneuId}")
    @Operation(summary = "Histórico de inspeções por pneu")
    @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso")
    public ResponseEntity<Page<InspecaoPneuDTO>> historicoPorPneu(
            @Parameter(description = "ID do pneu") @PathVariable Long pneuId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<InspecaoPneuDTO> result = inspecaoPneuService.historicoByPneu(pneuId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataInspecao")));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/veiculo/{veiculoId}")
    @Operation(summary = "Última inspeção por posição do veículo")
    @ApiResponse(responseCode = "200", description = "Inspeções retornadas com sucesso")
    public ResponseEntity<List<InspecaoPneuDTO>> ultimaInspecaoPorVeiculo(@PathVariable Long veiculoId) {
        return ResponseEntity.ok(inspecaoPneuService.ultimaInspecaoByVeiculo(veiculoId));
    }
}
