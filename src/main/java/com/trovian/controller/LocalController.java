package com.trovian.controller;

import com.trovian.dto.LocalDTO;
import com.trovian.service.LocalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/local")
@RequiredArgsConstructor
@Tag(name = "Locais", description = "API para gerenciamento de Locais")
public class LocalController {

    private final LocalService localService;

    @Operation(summary = "Lista todos os locais com paginação",
               description = "Retorna uma página de locais com suporte a paginação e ordenação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de locais retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<LocalDTO>> getAllLocais(
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação", example = "nome")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "ASC")
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<LocalDTO> locais = localService.findAll(pageable);
        return ResponseEntity.ok(locais);
    }

    @Operation(summary = "Busca local por ID", description = "Retorna um local específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Local encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LocalDTO.class))),
            @ApiResponse(responseCode = "404", description = "Local não encontrado")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LocalDTO> getLocalById(
            @Parameter(description = "ID do local", required = true)
            @PathVariable Long id) {
        LocalDTO local = localService.findById(id);
        return ResponseEntity.ok(local);
    }

    @Operation(summary = "Busca locais por cliente com paginação",
               description = "Retorna uma página de locais de um cliente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de locais retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping(value = "/cliente/{clienteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<LocalDTO>> getLocaisByCliente(
            @Parameter(description = "ID do cliente", required = true)
            @PathVariable Long clienteId,
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação", example = "nome")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "ASC")
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<LocalDTO> locais = localService.findByClienteId(clienteId, pageable);
        return ResponseEntity.ok(locais);
    }

    @Operation(summary = "Cria um novo local", description = "Cadastra um novo local no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Local criado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LocalDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LocalDTO> createLocal(
            @Parameter(description = "Dados do local a ser criado", required = true)
            @Valid @RequestBody LocalDTO localDTO) {
        LocalDTO createdLocal = localService.create(localDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLocal);
    }

    @Operation(summary = "Atualiza um local", description = "Atualiza os dados de um local existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Local atualizado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LocalDTO.class))),
            @ApiResponse(responseCode = "404", description = "Local não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LocalDTO> updateLocal(
            @Parameter(description = "ID do local a ser atualizado", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados do local", required = true)
            @Valid @RequestBody LocalDTO localDTO) {
        LocalDTO updatedLocal = localService.update(id, localDTO);
        return ResponseEntity.ok(updatedLocal);
    }

    @Operation(summary = "Deleta um local", description = "Remove um local do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Local deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Local não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocal(
            @Parameter(description = "ID do local a ser deletado", required = true)
            @PathVariable Long id) {
        localService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
