package com.trovian.controller;

import com.trovian.dto.ModeloDTO;
import com.trovian.service.ModeloService;
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
@RequestMapping("/modelo")
@RequiredArgsConstructor
@Tag(name = "Modelos", description = "API para gerenciamento de Modelos (Equipamentos e Veículos)")
public class ModeloController {

    private final ModeloService modeloService;

    @Operation(summary = "Busca modelo por ID", description = "Retorna um modelo específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modelo encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ModeloDTO.class))),
            @ApiResponse(responseCode = "404", description = "Modelo não encontrado")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ModeloDTO> getModeloById(
            @Parameter(description = "ID do modelo", required = true)
            @PathVariable Long id) {
        ModeloDTO modelo = modeloService.findById(id);
        return ResponseEntity.ok(modelo);
    }

    @Operation(summary = "Lista modelos do tipo Equipamento com paginação",
               description = "Retorna uma página de modelos do tipo Equipamento com suporte a paginação e ordenação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de modelos retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping(value = "/equipamentos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ModeloDTO>> getAllEquipamentos(
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação", example = "fabricante")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "ASC")
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<ModeloDTO> modelos = modeloService.findAllEquipamentos(pageable);
        return ResponseEntity.ok(modelos);
    }

    @Operation(summary = "Lista modelos do tipo Veículo com paginação",
               description = "Retorna uma página de modelos do tipo Veículo com suporte a paginação e ordenação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de modelos retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping(value = "/veiculos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ModeloDTO>> getAllVeiculos(
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação", example = "fabricante")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "ASC")
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<ModeloDTO> modelos = modeloService.findAllVeiculos(pageable);
        return ResponseEntity.ok(modelos);
    }

    @Operation(summary = "Cria um novo modelo", description = "Cadastra um novo modelo no sistema (Equipamento ou Veículo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Modelo criado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ModeloDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ModeloDTO> createModelo(
            @Parameter(description = "Dados do modelo a ser criado", required = true)
            @Valid @RequestBody ModeloDTO modeloDTO) {
        ModeloDTO createdModelo = modeloService.create(modeloDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdModelo);
    }

    @Operation(summary = "Atualiza um modelo", description = "Atualiza os dados de um modelo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modelo atualizado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ModeloDTO.class))),
            @ApiResponse(responseCode = "404", description = "Modelo não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ModeloDTO> updateModelo(
            @Parameter(description = "ID do modelo a ser atualizado", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados do modelo", required = true)
            @Valid @RequestBody ModeloDTO modeloDTO) {
        ModeloDTO updatedModelo = modeloService.update(id, modeloDTO);
        return ResponseEntity.ok(updatedModelo);
    }

    @Operation(summary = "Deleta um modelo", description = "Remove um modelo do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Modelo deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Modelo não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModelo(
            @Parameter(description = "ID do modelo a ser deletado", required = true)
            @PathVariable Long id) {
        modeloService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
