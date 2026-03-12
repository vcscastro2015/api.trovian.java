package com.trovian.controller;

import com.trovian.dto.TransmissaoBasicaDTO;
import com.trovian.service.TransmissaoBasicaService;
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
@RequestMapping("/transmissao-basica")
@RequiredArgsConstructor
@Tag(name = "TransmissaoBasica", description = "API para consulta de transmissões básicas de telemetria de veículos")
public class TransmissaoBasicaController {

    private final TransmissaoBasicaService transmissaoBasicaService;

    @GetMapping("/veiculo/{veiculoId}")
    @Operation(summary = "Lista transmissões básicas por veículo", description = "Retorna lista paginada de transmissões básicas de um veículo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de transmissões básicas retornada com sucesso")
    })
    public ResponseEntity<Page<TransmissaoBasicaDTO>> findByVeiculo(
            @Parameter(description = "ID do veículo") @PathVariable Long veiculoId,
            @Parameter(description = "Número da página (inicia em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "dataTransmissao") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(transmissaoBasicaService.findByVeiculo(veiculoId, pageable));
    }

    @GetMapping("/veiculo/{veiculoId}/ultima")
    @Operation(summary = "Última transmissão básica do veículo", description = "Retorna a transmissão básica mais recente de um veículo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transmissão básica encontrada"),
            @ApiResponse(responseCode = "404", description = "Nenhuma transmissão básica encontrada")
    })
    public ResponseEntity<TransmissaoBasicaDTO> findUltimaByVeiculo(
            @Parameter(description = "ID do veículo") @PathVariable Long veiculoId) {
        return ResponseEntity.ok(transmissaoBasicaService.findUltimaByVeiculo(veiculoId));
    }

    @GetMapping("/veiculo/{veiculoId}/periodo")
    @Operation(summary = "Transmissões básicas por veículo e período", description = "Retorna lista paginada de transmissões básicas de um veículo em um período")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de transmissões básicas retornada com sucesso")
    })
    public ResponseEntity<Page<TransmissaoBasicaDTO>> findByVeiculoEPeriodo(
            @Parameter(description = "ID do veículo") @PathVariable Long veiculoId,
            @Parameter(description = "Data inicial (ISO 8601)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date dataInicial,
            @Parameter(description = "Data final (ISO 8601)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date dataFinal,
            @Parameter(description = "Número da página (inicia em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "dataTransmissao") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(transmissaoBasicaService.findByVeiculoEPeriodo(veiculoId, dataInicial, dataFinal, pageable));
    }

    @PostMapping("/ultima/veiculos")
    @Operation(summary = "Última transmissão básica por lista de veículos", description = "Retorna a transmissão básica mais recente de cada veículo informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de últimas transmissões básicas retornada com sucesso")
    })
    public ResponseEntity<List<TransmissaoBasicaDTO>> findUltimasByVeiculos(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Lista de IDs dos veículos")
            @RequestBody List<Long> veiculoIds) {
        return ResponseEntity.ok(transmissaoBasicaService.findUltimasByVeiculos(veiculoIds));
    }

    @GetMapping("/periodo")
    @Operation(summary = "Transmissões básicas por múltiplos veículos e período", description = "Retorna lista paginada de transmissões básicas de múltiplos veículos em um período")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de transmissões básicas retornada com sucesso")
    })
    public ResponseEntity<Page<TransmissaoBasicaDTO>> findByVeiculosEPeriodo(
            @Parameter(description = "IDs dos veículos separados por vírgula") @RequestParam List<Long> veiculoIds,
            @Parameter(description = "Data inicial (ISO 8601)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date dataInicial,
            @Parameter(description = "Data final (ISO 8601)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date dataFinal,
            @Parameter(description = "Número da página (inicia em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "dataTransmissao") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(transmissaoBasicaService.findByVeiculosEPeriodo(veiculoIds, dataInicial, dataFinal, pageable));
    }
}
