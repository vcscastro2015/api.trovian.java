package com.trovian.controller;

import com.trovian.dto.FuncionalidadeDTO;
import com.trovian.service.FuncionalidadeService;
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

@RestController
@RequestMapping("/funcionalidade")
@RequiredArgsConstructor
@Tag(name = "Funcionalidades", description = "API para gerenciamento de Funcionalidades do sistema")
@CrossOrigin(origins = "http://localhost:4200")
public class FuncionalidadeController {

    private final FuncionalidadeService funcionalidadeService;

    @Operation(summary = "Lista todas as funcionalidades com paginação",
               description = "Retorna uma página de funcionalidades com suporte a paginação e ordenação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de funcionalidades retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<FuncionalidadeDTO>> getAllFuncionalidades(
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação", example = "nome")
            @RequestParam(defaultValue = "categoria") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "ASC")
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<FuncionalidadeDTO> funcionalidades = funcionalidadeService.findAllPaginated(pageable);
        return ResponseEntity.ok(funcionalidades);
    }

    @Operation(summary = "Lista todas as funcionalidades sem paginação",
               description = "Retorna todas as funcionalidades do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de funcionalidades retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FuncionalidadeDTO.class)))
    })
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FuncionalidadeDTO>> getAllFuncionalidadesWithoutPagination() {
        List<FuncionalidadeDTO> funcionalidades = funcionalidadeService.findAll();
        return ResponseEntity.ok(funcionalidades);
    }

    @Operation(summary = "Busca funcionalidade por ID", description = "Retorna uma funcionalidade específica pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionalidade encontrada",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FuncionalidadeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Funcionalidade não encontrada")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FuncionalidadeDTO> getFuncionalidadeById(
            @Parameter(description = "ID da funcionalidade", required = true)
            @PathVariable Long id) {
        FuncionalidadeDTO funcionalidade = funcionalidadeService.findById(id);
        return ResponseEntity.ok(funcionalidade);
    }

    @Operation(summary = "Busca funcionalidade por código", description = "Retorna uma funcionalidade específica pelo seu código único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionalidade encontrada",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FuncionalidadeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Funcionalidade não encontrada")
    })
    @GetMapping(value = "/codigo/{codigo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FuncionalidadeDTO> getFuncionalidadeByCodigo(
            @Parameter(description = "Código da funcionalidade", required = true, example = "MAPA")
            @PathVariable String codigo) {
        FuncionalidadeDTO funcionalidade = funcionalidadeService.findByCodigo(codigo);
        return ResponseEntity.ok(funcionalidade);
    }

    @Operation(summary = "Cria uma nova funcionalidade", description = "Cadastra uma nova funcionalidade no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Funcionalidade criada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FuncionalidadeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FuncionalidadeDTO> createFuncionalidade(
            @Parameter(description = "Dados da funcionalidade a ser criada", required = true)
            @Valid @RequestBody FuncionalidadeDTO funcionalidadeDTO) {
        FuncionalidadeDTO createdFuncionalidade = funcionalidadeService.create(funcionalidadeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFuncionalidade);
    }

    @Operation(summary = "Atualiza uma funcionalidade", description = "Atualiza os dados de uma funcionalidade existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionalidade atualizada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FuncionalidadeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Funcionalidade não encontrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FuncionalidadeDTO> updateFuncionalidade(
            @Parameter(description = "ID da funcionalidade a ser atualizada", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados da funcionalidade", required = true)
            @Valid @RequestBody FuncionalidadeDTO funcionalidadeDTO) {
        FuncionalidadeDTO updatedFuncionalidade = funcionalidadeService.update(id, funcionalidadeDTO);
        return ResponseEntity.ok(updatedFuncionalidade);
    }

    @Operation(summary = "Deleta uma funcionalidade", description = "Remove uma funcionalidade do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Funcionalidade deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Funcionalidade não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFuncionalidade(
            @Parameter(description = "ID da funcionalidade a ser deletada", required = true)
            @PathVariable Long id) {
        funcionalidadeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
