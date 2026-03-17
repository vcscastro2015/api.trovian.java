package com.trovian.controller;

import com.trovian.dto.DocumentoVeiculoDTO;
import com.trovian.service.DocumentoVeiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/documento-veiculo")
@RequiredArgsConstructor
@Tag(name = "Documento Veículo", description = "API para gerenciamento de documentos de veículos recebidos via WhatsApp")
public class DocumentoVeiculoController {

    private final DocumentoVeiculoService documentoVeiculoService;

    @GetMapping("/veiculo/{veiculoId}")
    @Operation(summary = "Lista documentos de um veículo",
            description = "Retorna os metadados dos documentos de um veículo, com filtro opcional por período")
    public ResponseEntity<List<DocumentoVeiculoDTO>> listarPorVeiculo(
            @Parameter(description = "ID do veículo", example = "1")
            @PathVariable Long veiculoId,
            @Parameter(description = "Data/hora de início do filtro (yyyy-MM-ddTHH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(description = "Data/hora de fim do filtro (yyyy-MM-ddTHH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {

        List<DocumentoVeiculoDTO> documentos = documentoVeiculoService.listarPorVeiculo(veiculoId, inicio, fim);
        return ResponseEntity.ok(documentos);
    }

    @GetMapping("/veiculo/{veiculoId}/download-zip")
    @Operation(summary = "Baixa documentos de um veículo em ZIP",
            description = "Gera e retorna um arquivo ZIP com todos os documentos do veículo, com filtro opcional por período")
    public ResponseEntity<byte[]> downloadZip(
            @Parameter(description = "ID do veículo", example = "1")
            @PathVariable Long veiculoId,
            @Parameter(description = "Data/hora de início do filtro (yyyy-MM-ddTHH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(description = "Data/hora de fim do filtro (yyyy-MM-ddTHH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {

        byte[] zip = documentoVeiculoService.gerarZip(veiculoId, inicio, fim);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"documentos_veiculo_" + veiculoId + ".zip\"")
                .body(zip);
    }
}
