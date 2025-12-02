package com.trovian.controller;

import com.trovian.dto.CentroCustoDTO;
import com.trovian.service.CentroCustoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
