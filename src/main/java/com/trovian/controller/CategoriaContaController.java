package com.trovian.controller;

import com.trovian.dto.CategoriaContaDTO;
import com.trovian.enums.TipoConta;
import com.trovian.service.CategoriaContaService;
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

import java.util.Objects;

@RestController
@RequestMapping("/categoria-conta")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Categorias de Conta", description = "Gerenciamento de categorias de contas")
public class CategoriaContaController {

    private final CategoriaContaService categoriaContaService;

    @PostMapping
    @Operation(summary = "Criar nova categoria")
    public ResponseEntity<CategoriaContaDTO> create(@Valid @RequestBody CategoriaContaDTO dto) {
        CategoriaContaDTO created = categoriaContaService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar todas as categorias (paginado)")
    public ResponseEntity<Page<CategoriaContaDTO>> findAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "nome") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        Page<CategoriaContaDTO> categorias = categoriaContaService.findAll(page, size, sortBy, direction);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria por ID")
    public ResponseEntity<CategoriaContaDTO> findById(@PathVariable Long id) {
        CategoriaContaDTO categoria = categoriaContaService.findById(id);
        return ResponseEntity.ok(categoria);
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Buscar categorias por tipo (PAGAR ou RECEBER)")
    public ResponseEntity<Page<CategoriaContaDTO>> findByTipo(
        @Parameter(description = "Tipo da conta: PAGAR ou RECEBER", required = true)
        @PathVariable String tipo,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "100") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoriaContaDTO> categorias = categoriaContaService.findByTipo(tipo.toUpperCase(), pageable);
        return ResponseEntity.ok(categorias);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria")
    public ResponseEntity<CategoriaContaDTO> update(
        @PathVariable Long id,
        @Valid @RequestBody CategoriaContaDTO dto
    ) {
        if(Objects.nonNull(dto.getPodeEditar()) && !dto.getPodeEditar()){
            return ResponseEntity.unprocessableEntity().build();
        }
        CategoriaContaDTO updated = categoriaContaService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar categoria")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriaContaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Busca fornecedores por cliente",
            description = "Retorna uma lista paginada de fornecedores de um cliente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<CategoriaContaDTO>> findByCliente(
            @Parameter(description = "ID do cliente", example = "1")
            @PathVariable Long clienteId,
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo para ordenação", example = "dataHora")
            @RequestParam(defaultValue = "nome") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "ASC")
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<CategoriaContaDTO> fornecedores = categoriaContaService.findByCliente(clienteId, pageable);
        return ResponseEntity.ok(fornecedores);
    }
}
