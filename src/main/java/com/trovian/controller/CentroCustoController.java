package com.trovian.controller;

import com.trovian.dto.CentroCustoDTO;
import com.trovian.service.CentroCustoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/centro-custo")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Centros de Custo", description = "Gerenciamento de centros de custo")
public class CentroCustoController {

    private final CentroCustoService centroCustoService;

    @PostMapping
    @Operation(summary = "Criar novo centro de custo")
    public ResponseEntity<CentroCustoDTO> create(@Valid @RequestBody CentroCustoDTO dto) {
        CentroCustoDTO created = centroCustoService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar todos os centros de custo (paginado)")
    public ResponseEntity<Page<CentroCustoDTO>> findAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "nome") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        Page<CentroCustoDTO> centros = centroCustoService.findAll(page, size, sortBy, direction);
        return ResponseEntity.ok(centros);
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Busca fornecedores por cliente",
            description = "Retorna uma lista paginada de fornecedores de um cliente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<CentroCustoDTO>> findByCliente(
            @Parameter(description = "ID do cliente", example = "1")
            @PathVariable Long clienteId,
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo para ordenação", example = "dataHora")
            @RequestParam(defaultValue = "nome") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "DESC")
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<CentroCustoDTO> fornecedores = centroCustoService.findByCliente(clienteId, pageable);
        return ResponseEntity.ok(fornecedores);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar centro de custo por ID")
    public ResponseEntity<CentroCustoDTO> findById(@PathVariable Long id) {
        CentroCustoDTO centro = centroCustoService.findById(id);
        return ResponseEntity.ok(centro);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar centro de custo")
    public ResponseEntity<CentroCustoDTO> update(
        @PathVariable Long id,
        @Valid @RequestBody CentroCustoDTO dto
    ) {
        CentroCustoDTO updated = centroCustoService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar centro de custo")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        centroCustoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
