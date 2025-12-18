package com.trovian.controller;

import com.trovian.dto.ModeloChecklistDTO;
import com.trovian.service.ModeloChecklistService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/modelo-checklist")
@RequiredArgsConstructor
@Tag(name = "Modelos de Checklist", description = "API para gerenciamento de Modelos/Templates de Checklist")
public class ModeloChecklistController {

    private final ModeloChecklistService modeloChecklistService;

    @Operation(summary = "Lista todos os modelos de checklist com paginação",
               description = "Retorna uma página de modelos de checklist com suporte a paginação e ordenação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de modelos retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ModeloChecklistDTO>> getAllModelos(
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação", example = "nome")
            @RequestParam(defaultValue = "nome") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "ASC")
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<ModeloChecklistDTO> modelos = modeloChecklistService.findAll(pageable);
        return ResponseEntity.ok(modelos);
    }

    @Operation(summary = "Busca modelo de checklist por ID", description = "Retorna um modelo de checklist específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modelo encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ModeloChecklistDTO.class))),
            @ApiResponse(responseCode = "404", description = "Modelo não encontrado")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ModeloChecklistDTO> getModeloById(
            @Parameter(description = "ID do modelo de checklist", required = true)
            @PathVariable UUID id) {
        ModeloChecklistDTO modelo = modeloChecklistService.findById(id);
        return ResponseEntity.ok(modelo);
    }

    @Operation(summary = "Busca modelos de checklist por cliente com paginação",
               description = "Retorna uma página de modelos de checklist de um cliente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de modelos retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping(value = "/cliente/{clienteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ModeloChecklistDTO>> getModelosByCliente(
            @Parameter(description = "ID do cliente", required = true)
            @PathVariable Long clienteId,
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação", example = "nome")
            @RequestParam(defaultValue = "nome") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "ASC")
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<ModeloChecklistDTO> modelos = modeloChecklistService.findByClienteId(clienteId, pageable);
        return ResponseEntity.ok(modelos);
    }

    @Operation(summary = "Busca modelos de checklist ativos por cliente",
               description = "Retorna uma lista de modelos de checklist ativos de um cliente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de modelos ativos retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping(value = "/cliente/{clienteId}/ativos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ModeloChecklistDTO>> getModelosAtivosByCliente(
            @Parameter(description = "ID do cliente", required = true)
            @PathVariable Long clienteId) {
        List<ModeloChecklistDTO> modelos = modeloChecklistService.findAtivos(clienteId);
        return ResponseEntity.ok(modelos);
    }

    @Operation(summary = "Cria um novo modelo de checklist", description = "Cadastra um novo modelo de checklist no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Modelo criado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ModeloChecklistDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ModeloChecklistDTO> createModelo(
            @Parameter(description = "Dados do modelo de checklist a ser criado", required = true)
            @Valid @RequestBody ModeloChecklistDTO modeloDTO) {
        ModeloChecklistDTO createdModelo = modeloChecklistService.create(modeloDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdModelo);
    }

    @Operation(summary = "Atualiza um modelo de checklist", description = "Atualiza os dados de um modelo de checklist existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modelo atualizado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ModeloChecklistDTO.class))),
            @ApiResponse(responseCode = "404", description = "Modelo não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ModeloChecklistDTO> updateModelo(
            @Parameter(description = "ID do modelo de checklist a ser atualizado", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Novos dados do modelo de checklist", required = true)
            @Valid @RequestBody ModeloChecklistDTO modeloDTO) {
        ModeloChecklistDTO updatedModelo = modeloChecklistService.update(id, modeloDTO);
        return ResponseEntity.ok(updatedModelo);
    }

    @Operation(summary = "Deleta um modelo de checklist", description = "Remove um modelo de checklist do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Modelo deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Modelo não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModelo(
            @Parameter(description = "ID do modelo de checklist a ser deletado", required = true)
            @PathVariable UUID id) {
        modeloChecklistService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
