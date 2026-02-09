package com.trovian.controller;

import com.trovian.dto.ComissaoMotoristaDTO;
import com.trovian.dto.ComissaoMotoristaRelatorioDTO;
import com.trovian.entity.ComissaoMotorista.StatusComissao;
import com.trovian.service.ComissaoMotoristaService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/comissoes-motorista")
@RequiredArgsConstructor
@Tag(name = "Comissões de Motorista", description = "API de gerenciamento de comissões de motoristas")
public class ComissaoMotoristaController {

    private final ComissaoMotoristaService comissaoMotoristaService;

    @Operation(summary = "Listar todas as comissões com paginação",
               description = "Retorna uma página de comissões com suporte a paginação e ordenação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de comissões retornada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<ComissaoMotoristaDTO>> getAllComissoesPaginated(
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
        Page<ComissaoMotoristaDTO> comissoes = comissaoMotoristaService.findAllPaginated(pageable);
        return ResponseEntity.ok(comissoes);
    }

    @Operation(summary = "Buscar comissão por ID", description = "Retorna uma comissão específica pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comissão encontrada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComissaoMotoristaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Comissão não encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ComissaoMotoristaDTO> getComissaoById(
            @Parameter(description = "ID da comissão a ser buscada", required = true) @PathVariable Long id) {
        ComissaoMotoristaDTO comissao = comissaoMotoristaService.findById(id);
        return ResponseEntity.ok(comissao);
    }

    @Operation(summary = "Buscar comissões por cliente", description = "Retorna uma lista de comissões de um cliente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comissões encontradas com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComissaoMotoristaDTO.class)))
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ComissaoMotoristaDTO>> getComissoesByCliente(
            @Parameter(description = "ID do cliente", required = true) @PathVariable Long clienteId) {
        List<ComissaoMotoristaDTO> comissoes = comissaoMotoristaService.findByCliente(clienteId);
        return ResponseEntity.ok(comissoes);
    }

    @Operation(summary = "Buscar comissões por motorista", description = "Retorna uma página de comissões de um motorista específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comissões encontradas com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "404", description = "Motorista não encontrado", content = @Content)
    })
    @GetMapping("/motorista/{motoristaId}")
    public ResponseEntity<Page<ComissaoMotoristaDTO>> getComissoesByMotorista(
            @Parameter(description = "ID do motorista", required = true) @PathVariable Long motoristaId,
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
        Page<ComissaoMotoristaDTO> comissoes = comissaoMotoristaService.findByMotorista(motoristaId, pageable);
        return ResponseEntity.ok(comissoes);
    }

    @Operation(summary = "Buscar comissões por status", description = "Retorna uma página de comissões filtradas por status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comissões encontradas com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<ComissaoMotoristaDTO>> getComissoesByStatus(
            @Parameter(description = "Status da comissão (PENDENTE, CALCULADA, PAGA, CANCELADA)", required = true)
            @PathVariable StatusComissao status,
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
        Page<ComissaoMotoristaDTO> comissoes = comissaoMotoristaService.findByStatus(status, pageable);
        return ResponseEntity.ok(comissoes);
    }

    @Operation(summary = "Gerar relatório de comissões do motorista",
               description = "Retorna um relatório com lista de comissões e totais consolidados para um motorista em um período específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComissaoMotoristaRelatorioDTO.class))),
            @ApiResponse(responseCode = "404", description = "Motorista não encontrado", content = @Content)
    })
    @GetMapping("/relatorio/motorista/{motoristaId}")
    public ResponseEntity<ComissaoMotoristaRelatorioDTO> gerarRelatorio(
            @Parameter(description = "ID do motorista", required = true)
            @PathVariable Long motoristaId,
            @Parameter(description = "Data inicial do período (formato: yyyy-MM-dd'T'HH:mm:ss)", required = true, example = "2025-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicial,
            @Parameter(description = "Data final do período (formato: yyyy-MM-dd'T'HH:mm:ss)", required = true, example = "2025-01-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFinal,
            @Parameter(description = "Status da comissão para filtrar (opcional: PENDENTE, CALCULADA, PAGA, CANCELADA)")
            @RequestParam(required = false) StatusComissao status) {

        ComissaoMotoristaRelatorioDTO relatorio = comissaoMotoristaService.gerarRelatorio(
                motoristaId, dataInicial, dataFinal, status);
        return ResponseEntity.ok(relatorio);
    }

    @Operation(summary = "Criar nova comissão", description = "Cria uma nova comissão de motorista no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comissão criada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComissaoMotoristaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ComissaoMotoristaDTO> createComissao(
            @Parameter(description = "Dados da comissão a ser criada", required = true) @Valid @RequestBody ComissaoMotoristaDTO comissaoDTO) {
        ComissaoMotoristaDTO createdComissao = comissaoMotoristaService.create(comissaoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComissao);
    }

    @Operation(summary = "Atualizar comissão", description = "Atualiza os dados de uma comissão existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comissão atualizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ComissaoMotoristaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Comissão não encontrada", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ComissaoMotoristaDTO> updateComissao(
            @Parameter(description = "ID da comissão a ser atualizada", required = true) @PathVariable Long id,
            @Parameter(description = "Novos dados da comissão", required = true) @Valid @RequestBody ComissaoMotoristaDTO comissaoDTO) {
        ComissaoMotoristaDTO updatedComissao = comissaoMotoristaService.update(id, comissaoDTO);
        return ResponseEntity.ok(updatedComissao);
    }

    @Operation(summary = "Deletar comissão", description = "Remove uma comissão do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comissão deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Comissão não encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComissao(
            @Parameter(description = "ID da comissão a ser deletada", required = true) @PathVariable Long id) {
        comissaoMotoristaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
