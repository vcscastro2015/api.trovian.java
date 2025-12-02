package com.trovian.controller;

import com.trovian.dto.FornecedorDTO;
import com.trovian.service.FornecedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fornecedor")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Fornecedores", description = "Gerenciamento de fornecedores")
public class FornecedorController {

    private final FornecedorService fornecedorService;

    @PostMapping
    @Operation(summary = "Criar novo fornecedor")
    public ResponseEntity<FornecedorDTO> create(@Valid @RequestBody FornecedorDTO dto) {
        log.info("Request para criar fornecedor: {}", dto.getRazaoSocial());
        FornecedorDTO created = fornecedorService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar todos os fornecedores (paginado)")
    public ResponseEntity<Page<FornecedorDTO>> findAll(
        @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size,
        @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "razaoSocial") String sortBy,
        @Parameter(description = "Direção da ordenação") @RequestParam(defaultValue = "ASC") String direction
    ) {
        Page<FornecedorDTO> fornecedores = fornecedorService.findAll(page, size, sortBy, direction);
        return ResponseEntity.ok(fornecedores);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar fornecedor por ID")
    public ResponseEntity<FornecedorDTO> findById(@PathVariable Long id) {
        FornecedorDTO fornecedor = fornecedorService.findById(id);
        return ResponseEntity.ok(fornecedor);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar fornecedor")
    public ResponseEntity<FornecedorDTO> update(
        @PathVariable Long id,
        @Valid @RequestBody FornecedorDTO dto
    ) {
        FornecedorDTO updated = fornecedorService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar fornecedor")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fornecedorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
