package com.trovian.controller;

import com.trovian.dto.VeiculoDTO;
import com.trovian.service.VeiculoService;
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
@RequestMapping("/veiculo")
@RequiredArgsConstructor
@Tag(name = "Veículos", description = "API para gerenciamento de Veículos")
public class VeiculoController {

    private final VeiculoService veiculoService;

    @Operation(summary = "Lista todos os veículos com paginação",
               description = "Retorna uma página de veículos com suporte a paginação e ordenação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de veículos retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<VeiculoDTO>> getAllVeiculos(
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação", example = "placa")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "ASC")
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<VeiculoDTO> veiculos = veiculoService.findAll(pageable);
        return ResponseEntity.ok(veiculos);
    }

    @Operation(summary = "Busca veículo por ID", description = "Retorna um veículo específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Veículo encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = VeiculoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VeiculoDTO> getVeiculoById(
            @Parameter(description = "ID do veículo", required = true)
            @PathVariable Long id) {
        VeiculoDTO veiculo = veiculoService.findById(id);
        return ResponseEntity.ok(veiculo);
    }

    @Operation(summary = "Busca veículos por cliente com paginação",
               description = "Retorna uma página de veículos de um cliente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de veículos retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping(value = "/cliente/{clienteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<VeiculoDTO>> getVeiculosByCliente(
            @Parameter(description = "ID do cliente", required = true)
            @PathVariable Long clienteId,
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação", example = "placa")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "ASC")
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<VeiculoDTO> veiculos = veiculoService.findByClienteId(clienteId, pageable);
        return ResponseEntity.ok(veiculos);
    }

    @Operation(summary = "Cria um novo veículo", description = "Cadastra um novo veículo no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Veículo criado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = VeiculoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VeiculoDTO> createVeiculo(
            @Parameter(description = "Dados do veículo a ser criado", required = true)
            @Valid @RequestBody VeiculoDTO veiculoDTO) {
        VeiculoDTO createdVeiculo = veiculoService.create(veiculoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVeiculo);
    }

    @Operation(summary = "Atualiza um veículo", description = "Atualiza os dados de um veículo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Veículo atualizado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = VeiculoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VeiculoDTO> updateVeiculo(
            @Parameter(description = "ID do veículo a ser atualizado", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados do veículo", required = true)
            @Valid @RequestBody VeiculoDTO veiculoDTO) {
        VeiculoDTO updatedVeiculo = veiculoService.update(id, veiculoDTO);
        return ResponseEntity.ok(updatedVeiculo);
    }

    @Operation(summary = "Deleta um veículo", description = "Remove um veículo do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Veículo deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVeiculo(
            @Parameter(description = "ID do veículo a ser deletado", required = true)
            @PathVariable Long id) {
        veiculoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
