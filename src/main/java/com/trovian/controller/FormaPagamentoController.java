package com.trovian.controller;

import com.trovian.dto.FormaPagamentoDTO;
import com.trovian.service.FormaPagamentoService;
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
@RequestMapping("/forma-pagamento")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Formas de Pagamento", description = "Gerenciamento de formas de pagamento")
public class FormaPagamentoController {

    private final FormaPagamentoService formaPagamentoService;

    @PostMapping
    @Operation(summary = "Criar nova forma de pagamento")
    public ResponseEntity<FormaPagamentoDTO> create(@Valid @RequestBody FormaPagamentoDTO dto) {
        FormaPagamentoDTO created = formaPagamentoService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar todas as formas de pagamento (paginado)")
    public ResponseEntity<Page<FormaPagamentoDTO>> findAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "nome") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        Page<FormaPagamentoDTO> formas = formaPagamentoService.findAll(page, size, sortBy, direction);
        return ResponseEntity.ok(formas);
    }

    @GetMapping("/cliente/{clienteId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<FormaPagamentoDTO>> findByCliente(
            @Parameter(description = "ID do cliente", example = "1")
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataHora") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<FormaPagamentoDTO> formas = formaPagamentoService.findByCliente(clienteId, pageable);
        return ResponseEntity.ok(formas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar forma de pagamento por ID")
    public ResponseEntity<FormaPagamentoDTO> findById(@PathVariable Long id) {
        FormaPagamentoDTO forma = formaPagamentoService.findById(id);
        return ResponseEntity.ok(forma);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar forma de pagamento")
    public ResponseEntity<FormaPagamentoDTO> update(
        @PathVariable Long id,
        @Valid @RequestBody FormaPagamentoDTO dto
    ) {
        FormaPagamentoDTO updated = formaPagamentoService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar forma de pagamento")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        formaPagamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
