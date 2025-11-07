package com.trovian.controller;

import com.trovian.dto.MotoristaDTO;
import com.trovian.service.MotoristaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para gerenciamento de Motoristas
 */
@RestController
@RequestMapping("/motorista")
@RequiredArgsConstructor
@Tag(name = "Motorista", description = "API para gerenciamento de motoristas")
public class MotoristaController {

    private final MotoristaService motoristaService;

    /**
     * Lista todos os motoristas com paginação
     */
    @GetMapping
    @Operation(summary = "Lista todos os motoristas", description = "Retorna uma lista paginada de todos os motoristas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de motoristas retornada com sucesso")
    })
    public ResponseEntity<Page<MotoristaDTO>> findAll(
            @Parameter(description = "Número da página (inicia em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<MotoristaDTO> motoristas = motoristaService.findAll(pageable);
        return ResponseEntity.ok(motoristas);
    }

    /**
     * Busca motorista por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Busca motorista por ID", description = "Retorna um motorista específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Motorista encontrado"),
            @ApiResponse(responseCode = "404", description = "Motorista não encontrado")
    })
    public ResponseEntity<MotoristaDTO> findById(
            @Parameter(description = "ID do motorista") @PathVariable Long id) {
        MotoristaDTO motorista = motoristaService.findById(id);
        return ResponseEntity.ok(motorista);
    }

    /**
     * Busca motoristas por cliente com paginação
     */
    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Busca motoristas por cliente", description = "Retorna uma lista paginada de motoristas de um cliente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de motoristas do cliente retornada com sucesso")
    })
    public ResponseEntity<Page<MotoristaDTO>> findByCliente(
            @Parameter(description = "ID do cliente") @PathVariable Long clienteId,
            @Parameter(description = "Número da página (inicia em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<MotoristaDTO> motoristas = motoristaService.findByCliente(clienteId, pageable);
        return ResponseEntity.ok(motoristas);
    }

    /**
     * Cria novo motorista
     */
    @PostMapping
    @Operation(summary = "Cria novo motorista", description = "Cria um novo motorista no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Motorista criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<MotoristaDTO> create(
            @Parameter(description = "Dados do motorista a ser criado") @Valid @RequestBody MotoristaDTO motoristaDTO) {
        MotoristaDTO createdMotorista = motoristaService.create(motoristaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMotorista);
    }

    /**
     * Atualiza motorista existente
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza motorista", description = "Atualiza os dados de um motorista existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Motorista atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Motorista não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<MotoristaDTO> update(
            @Parameter(description = "ID do motorista") @PathVariable Long id,
            @Parameter(description = "Dados atualizados do motorista") @Valid @RequestBody MotoristaDTO motoristaDTO) {
        MotoristaDTO updatedMotorista = motoristaService.update(id, motoristaDTO);
        return ResponseEntity.ok(updatedMotorista);
    }

    /**
     * Deleta motorista
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta motorista", description = "Remove um motorista do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Motorista deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Motorista não encontrado")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do motorista") @PathVariable Long id) {
        motoristaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
