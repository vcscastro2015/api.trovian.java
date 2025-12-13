package com.trovian.controller;

import com.trovian.dto.ItemManutencaoDTO;
import com.trovian.service.ItemManutencaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/item-manutencao")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Itens de Manutenção", description = "Gerenciamento de itens de manutenção (serviços e peças)")
public class ItemManutencaoController {

    private final ItemManutencaoService itemManutencaoService;

    @PostMapping
    @Operation(summary = "Criar novo item de manutenção")
    public ResponseEntity<ItemManutencaoDTO> create(@Valid @RequestBody ItemManutencaoDTO dto) {
        ItemManutencaoDTO created = itemManutencaoService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar todos os itens de manutenção (paginado)")
    public ResponseEntity<Page<ItemManutencaoDTO>> findAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "DESC") String direction
    ) {
        Page<ItemManutencaoDTO> itens = itemManutencaoService.findAll(page, size, sortBy, direction);
        return ResponseEntity.ok(itens);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar item de manutenção por ID")
    public ResponseEntity<ItemManutencaoDTO> findById(@PathVariable Long id) {
        ItemManutencaoDTO item = itemManutencaoService.findById(id);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/ordem-servico/{ordemServicoId}")
    @Operation(summary = "Buscar itens de manutenção por ordem de serviço")
    public ResponseEntity<List<ItemManutencaoDTO>> findByOrdemServicoId(
        @Parameter(description = "ID da ordem de serviço", required = true)
        @PathVariable Long ordemServicoId
    ) {
        List<ItemManutencaoDTO> itens = itemManutencaoService.findByOrdemServicoId(ordemServicoId);
        return ResponseEntity.ok(itens);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar item de manutenção")
    public ResponseEntity<ItemManutencaoDTO> update(
        @PathVariable Long id,
        @Valid @RequestBody ItemManutencaoDTO dto
    ) {
        ItemManutencaoDTO updated = itemManutencaoService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar item de manutenção")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        itemManutencaoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
