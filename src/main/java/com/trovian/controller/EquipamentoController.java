package com.trovian.controller;

import com.trovian.dto.EquipamentoDTO;
import com.trovian.service.EquipamentoService;
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
@RequestMapping("/equipamento")
@RequiredArgsConstructor
@Tag(name = "Equipamentos", description = "API para gerenciamento de Equipamentos")
public class EquipamentoController {

    private final EquipamentoService equipamentoService;

    @Operation(summary = "Lista todos os equipamentos com paginação",
               description = "Retorna uma página de equipamentos com suporte a paginação e ordenação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de equipamentos retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<EquipamentoDTO>> getAllEquipamentos(
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "ASC")
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<EquipamentoDTO> equipamentos = equipamentoService.findAll(pageable);
        return ResponseEntity.ok(equipamentos);
    }

    @Operation(summary = "Busca equipamento por ID", description = "Retorna um equipamento específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipamento encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EquipamentoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Equipamento não encontrado")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EquipamentoDTO> getEquipamentoById(
            @Parameter(description = "ID do equipamento", required = true)
            @PathVariable Long id) {
        EquipamentoDTO equipamento = equipamentoService.findById(id);
        return ResponseEntity.ok(equipamento);
    }

    @Operation(summary = "Cria um novo equipamento", description = "Cadastra um novo equipamento no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Equipamento criado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EquipamentoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EquipamentoDTO> createEquipamento(
            @Parameter(description = "Dados do equipamento a ser criado", required = true)
            @Valid @RequestBody EquipamentoDTO equipamentoDTO) {
        EquipamentoDTO createdEquipamento = equipamentoService.create(equipamentoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEquipamento);
    }

    @Operation(summary = "Atualiza um equipamento", description = "Atualiza os dados de um equipamento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipamento atualizado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EquipamentoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Equipamento não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EquipamentoDTO> updateEquipamento(
            @Parameter(description = "ID do equipamento a ser atualizado", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados do equipamento", required = true)
            @Valid @RequestBody EquipamentoDTO equipamentoDTO) {
        EquipamentoDTO updatedEquipamento = equipamentoService.update(id, equipamentoDTO);
        return ResponseEntity.ok(updatedEquipamento);
    }

    @Operation(summary = "Deleta um equipamento", description = "Remove um equipamento do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Equipamento deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Equipamento não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipamento(
            @Parameter(description = "ID do equipamento a ser deletado", required = true)
            @PathVariable Long id) {
        equipamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
