package com.trovian.controller;

import com.trovian.dto.MovimentacaoEstoqueDTO;
import com.trovian.enums.TipoMovimentacaoEstoque;
import com.trovian.service.MovimentacaoEstoqueService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/movimentacao-estoque")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Movimentação de Estoque", description = "Gerenciamento de movimentações de estoque")
public class MovimentacaoEstoqueController {

    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    @PostMapping
    @Operation(summary = "Criar nova movimentação de estoque")
    public ResponseEntity<MovimentacaoEstoqueDTO> create(@Valid @RequestBody MovimentacaoEstoqueDTO dto) {
        MovimentacaoEstoqueDTO created = movimentacaoEstoqueService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar todas as movimentações de estoque (paginado)")
    public ResponseEntity<Page<MovimentacaoEstoqueDTO>> findAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "dataMovimentacao") String sortBy,
        @RequestParam(defaultValue = "DESC") String direction
    ) {
        Page<MovimentacaoEstoqueDTO> movimentacoes = movimentacaoEstoqueService.findAll(page, size, sortBy, direction);
        return ResponseEntity.ok(movimentacoes);
    }

    @GetMapping("/cliente/{clienteId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<MovimentacaoEstoqueDTO>> findByCliente(
            @Parameter(description = "ID do cliente", example = "1")
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dataMovimentacao") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<MovimentacaoEstoqueDTO> movimentacoes = movimentacaoEstoqueService.findByCliente(clienteId, pageable);
        return ResponseEntity.ok(movimentacoes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar movimentação de estoque por ID")
    public ResponseEntity<MovimentacaoEstoqueDTO> findById(@PathVariable Long id) {
        MovimentacaoEstoqueDTO movimentacao = movimentacaoEstoqueService.findById(id);
        return ResponseEntity.ok(movimentacao);
    }

    @GetMapping("/peca/{pecaId}")
    @Operation(summary = "Buscar movimentações por peça")
    public ResponseEntity<Page<MovimentacaoEstoqueDTO>> findByPecaId(
        @Parameter(description = "ID da peça", required = true)
        @PathVariable Long pecaId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MovimentacaoEstoqueDTO> movimentacoes = movimentacaoEstoqueService.findByPecaId(pecaId, pageable);
        return ResponseEntity.ok(movimentacoes);
    }

    @GetMapping("/peca/{pecaId}/historico")
    @Operation(summary = "Buscar histórico completo de movimentações de uma peça")
    public ResponseEntity<List<MovimentacaoEstoqueDTO>> findHistoricoByPecaId(
        @Parameter(description = "ID da peça", required = true)
        @PathVariable Long pecaId
    ) {
        List<MovimentacaoEstoqueDTO> historico = movimentacaoEstoqueService.findHistoricoByPecaId(pecaId);
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/ordem-servico/{ordemServicoId}")
    @Operation(summary = "Buscar movimentações por ordem de serviço")
    public ResponseEntity<Page<MovimentacaoEstoqueDTO>> findByOrdemServicoId(
        @Parameter(description = "ID da ordem de serviço", required = true)
        @PathVariable Long ordemServicoId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MovimentacaoEstoqueDTO> movimentacoes = movimentacaoEstoqueService.findByOrdemServicoId(ordemServicoId, pageable);
        return ResponseEntity.ok(movimentacoes);
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Buscar movimentações por tipo")
    public ResponseEntity<Page<MovimentacaoEstoqueDTO>> findByTipo(
        @Parameter(description = "Tipo de movimentação (ENTRADA, SAIDA, AJUSTE)", required = true)
        @PathVariable String tipo,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        TipoMovimentacaoEstoque tipoEnum = TipoMovimentacaoEstoque.valueOf(tipo.toUpperCase());
        Page<MovimentacaoEstoqueDTO> movimentacoes = movimentacaoEstoqueService.findByTipoMovimentacao(tipoEnum, pageable);
        return ResponseEntity.ok(movimentacoes);
    }

    @GetMapping("/periodo")
    @Operation(summary = "Buscar movimentações por período")
    public ResponseEntity<Page<MovimentacaoEstoqueDTO>> findByPeriodo(
        @Parameter(description = "Data inicial", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
        @Parameter(description = "Data final", required = true)
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MovimentacaoEstoqueDTO> movimentacoes = movimentacaoEstoqueService.findByPeriodo(inicio, fim, pageable);
        return ResponseEntity.ok(movimentacoes);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar movimentação de estoque (reverte a operação)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        movimentacaoEstoqueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
