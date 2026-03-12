package com.trovian.controller;

import com.trovian.dto.TransmissaoDTO;
import com.trovian.service.TransmissaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/transmissao")
@RequiredArgsConstructor
@Tag(name = "Transmissao", description = "API para consulta de transmissões de telemetria de veículos")
public class TransmissaoController {

    private final TransmissaoService transmissaoService;

    @GetMapping("/veiculo/{veiculoId}")
    @Operation(summary = "Lista transmissões por veículo", description = "Retorna lista paginada de transmissões de um veículo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de transmissões retornada com sucesso")
    })
    public ResponseEntity<Page<TransmissaoDTO>> findByVeiculo(
            @Parameter(description = "ID do veículo") @PathVariable Long veiculoId,
            @Parameter(description = "Número da página (inicia em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "dataTransmissao") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(transmissaoService.findByVeiculo(veiculoId, pageable));
    }

    @GetMapping("/veiculo/{veiculoId}/ultima")
    @Operation(summary = "Última transmissão do veículo", description = "Retorna a transmissão mais recente de um veículo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transmissão encontrada"),
            @ApiResponse(responseCode = "404", description = "Nenhuma transmissão encontrada")
    })
    public ResponseEntity<TransmissaoDTO> findUltimaByVeiculo(
            @Parameter(description = "ID do veículo") @PathVariable Long veiculoId) {
        return ResponseEntity.ok(transmissaoService.findUltimaByVeiculo(veiculoId));
    }

    @GetMapping("/veiculo/{veiculoId}/periodo")
    @Operation(summary = "Transmissões por veículo e período", description = "Retorna lista paginada de transmissões de um veículo em um período")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de transmissões retornada com sucesso")
    })
    public ResponseEntity<Page<TransmissaoDTO>> findByVeiculoEPeriodo(
            @Parameter(description = "ID do veículo") @PathVariable Long veiculoId,
            @Parameter(description = "Data inicial (ISO 8601)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date dataInicial,
            @Parameter(description = "Data final (ISO 8601)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date dataFinal,
            @Parameter(description = "Número da página (inicia em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "dataTransmissao") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(transmissaoService.findByVeiculoEPeriodo(veiculoId, dataInicial, dataFinal, pageable));
    }

    @GetMapping("/periodo")
    @Operation(summary = "Transmissões por múltiplos veículos e período", description = "Retorna lista paginada de transmissões de múltiplos veículos em um período")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de transmissões retornada com sucesso")
    })
    public ResponseEntity<Page<TransmissaoDTO>> findByVeiculosEPeriodo(
            @Parameter(description = "IDs dos veículos separados por vírgula") @RequestParam List<Long> veiculoIds,
            @Parameter(description = "Data inicial (ISO 8601)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date dataInicial,
            @Parameter(description = "Data final (ISO 8601)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date dataFinal,
            @Parameter(description = "Número da página (inicia em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "dataTransmissao") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(transmissaoService.findByVeiculosEPeriodo(veiculoIds, dataInicial, dataFinal, pageable));
    }
}
