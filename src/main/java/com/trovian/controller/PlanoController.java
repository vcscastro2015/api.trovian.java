package com.trovian.controller;

import com.trovian.dto.PlanoDTO;
import com.trovian.service.PlanoService;
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
@RequestMapping("/plano")
@RequiredArgsConstructor
@Tag(name = "Planos", description = "API para gerenciamento de Planos SaaS")
public class PlanoController {

    private final PlanoService planoService;

    @Operation(summary = "Lista todos os planos com paginação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de planos retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<PlanoDTO>> getAllPlanos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(planoService.findAll(pageable));
    }

    @Operation(summary = "Busca plano por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plano encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PlanoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Plano não encontrado")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PlanoDTO> getPlanoById(
            @Parameter(description = "ID do plano", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(planoService.findById(id));
    }

    @Operation(summary = "Lista planos ativos com paginação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Planos ativos retornados com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping(value = "/ativos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<PlanoDTO>> getPlanosAtivos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(planoService.findAtivos(pageable));
    }

    @Operation(summary = "Cria um novo plano")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plano criado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PlanoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PlanoDTO> createPlano(
            @Valid @RequestBody PlanoDTO planoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planoService.create(planoDTO));
    }

    @Operation(summary = "Atualiza um plano existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plano atualizado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PlanoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Plano não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PlanoDTO> updatePlano(
            @Parameter(description = "ID do plano", required = true) @PathVariable Long id,
            @Valid @RequestBody PlanoDTO planoDTO) {
        return ResponseEntity.ok(planoService.update(id, planoDTO));
    }

    @Operation(summary = "Deleta um plano")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Plano deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Plano não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlano(
            @Parameter(description = "ID do plano", required = true) @PathVariable Long id) {
        planoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
