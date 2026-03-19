package com.trovian.controller;

import com.trovian.dto.PecaDTO;
import com.trovian.service.PecaService;
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

import java.util.List;

@RestController
@RequestMapping("/peca")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Peças", description = "Gerenciamento de peças e estoque")
public class PecaController {

    private final PecaService pecaService;

    @PostMapping
    @Operation(summary = "Criar nova peça")
    public ResponseEntity<PecaDTO> create(@Valid @RequestBody PecaDTO dto) {
        PecaDTO created = pecaService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar todas as peças (paginado)")
    public ResponseEntity<Page<PecaDTO>> findAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "descricao") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        Page<PecaDTO> pecas = pecaService.findAll(page, size, sortBy, direction);
        return ResponseEntity.ok(pecas);
    }

    @GetMapping("/cliente/{clienteId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<PecaDTO>> findByCliente(
            @Parameter(description = "ID do cliente", example = "1")
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "descricao") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<PecaDTO> fornecedores = pecaService.findByCliente(clienteId, pageable);
        return ResponseEntity.ok(fornecedores);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar peça por ID")
    public ResponseEntity<PecaDTO> findById(@PathVariable Long id) {
        PecaDTO peca = pecaService.findById(id);
        return ResponseEntity.ok(peca);
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Buscar peça por código")
    public ResponseEntity<PecaDTO> findByCodigo(
        @Parameter(description = "Código da peça", required = true)
        @PathVariable String codigo
    ) {
        PecaDTO peca = pecaService.findByCodigo(codigo);
        return ResponseEntity.ok(peca);
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Buscar peças por categoria")
    public ResponseEntity<Page<PecaDTO>> findByCategoria(
        @Parameter(description = "Categoria da peça", required = true)
        @PathVariable String categoria,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PecaDTO> pecas = pecaService.findByCategoria(categoria, pageable);
        return ResponseEntity.ok(pecas);
    }

    @GetMapping("/cliente/{clienteId}/estoque-baixo")
    @Operation(summary = "Listar peças com estoque baixo")
    public ResponseEntity<List<PecaDTO>> findPecasEstoqueBaixo(@PathVariable Long clienteId) {
        List<PecaDTO> pecas = pecaService.findPecasEstoqueBaixo(clienteId);
        return ResponseEntity.ok(pecas);
    }

    @GetMapping("/categorias/{clienteId}")
    @Operation(summary = "Listar todas as categorias de peças")
    public ResponseEntity<List<String>> findAllCategorias(@PathVariable Long clienteId) {
        List<String> categorias = pecaService.findAllCategorias(clienteId);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/buscar/{clienteId}")
    @Operation(summary = "Buscar peças por descrição")
    public ResponseEntity<Page<PecaDTO>> findByDescricao(@PathVariable Long clienteId,
        @RequestParam String descricao,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PecaDTO> pecas = pecaService.findByDescricaoContaining(clienteId, descricao, pageable);
        return ResponseEntity.ok(pecas);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar peça")
    public ResponseEntity<PecaDTO> update(
        @PathVariable Long id,
        @Valid @RequestBody PecaDTO dto
    ) {
        PecaDTO updated = pecaService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar peça")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pecaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
