package com.trovian.controller;

import com.trovian.dto.OrdemServicoDTO;
import com.trovian.enums.StatusOrdemServico;
import com.trovian.service.OrdemServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordem-servico")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Ordens de Serviço", description = "Gerenciamento de ordens de serviço de manutenção")
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;

    @PostMapping
    @Operation(summary = "Criar nova ordem de serviço")
    public ResponseEntity<OrdemServicoDTO> create(@Valid @RequestBody OrdemServicoDTO dto) {
        OrdemServicoDTO created = ordemServicoService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar todas as ordens de serviço (paginado)")
    public ResponseEntity<Page<OrdemServicoDTO>> findAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "DESC") String direction
    ) {
        Page<OrdemServicoDTO> ordens = ordemServicoService.findAll(page, size, sortBy, direction);
        return ResponseEntity.ok(ordens);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ordem de serviço por ID")
    public ResponseEntity<OrdemServicoDTO> findById(@PathVariable Long id) {
        OrdemServicoDTO ordem = ordemServicoService.findById(id);
        return ResponseEntity.ok(ordem);
    }

    @GetMapping("/numero/{numeroOs}")
    @Operation(summary = "Buscar ordem de serviço por número")
    public ResponseEntity<OrdemServicoDTO> findByNumeroOs(
        @Parameter(description = "Número da OS", required = true)
        @PathVariable String numeroOs
    ) {
        OrdemServicoDTO ordem = ordemServicoService.findByNumeroOs(numeroOs);
        return ResponseEntity.ok(ordem);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Buscar ordens de serviço por status")
    public ResponseEntity<Page<OrdemServicoDTO>> findByStatus(
        @Parameter(description = "Status da OS", required = true)
        @PathVariable String status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        StatusOrdemServico statusEnum = StatusOrdemServico.valueOf(status.toUpperCase());
        Page<OrdemServicoDTO> ordens = ordemServicoService.findByStatus(statusEnum, pageable);
        return ResponseEntity.ok(ordens);
    }

    @GetMapping("/veiculo/{veiculoId}")
    @Operation(summary = "Buscar ordens de serviço por veículo")
    public ResponseEntity<Page<OrdemServicoDTO>> findByVeiculoId(
        @Parameter(description = "ID do veículo", required = true)
        @PathVariable Long veiculoId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrdemServicoDTO> ordens = ordemServicoService.findByVeiculoId(veiculoId, pageable);
        return ResponseEntity.ok(ordens);
    }

    @GetMapping("/atrasadas")
    @Operation(summary = "Listar ordens de serviço atrasadas")
    public ResponseEntity<List<OrdemServicoDTO>> findOrdensAtrasadas() {
        List<OrdemServicoDTO> ordens = ordemServicoService.findOrdensAtrasadas();
        return ResponseEntity.ok(ordens);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar ordem de serviço")
    public ResponseEntity<OrdemServicoDTO> update(
        @PathVariable Long id,
        @Valid @RequestBody OrdemServicoDTO dto
    ) {
        OrdemServicoDTO updated = ordemServicoService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/concluir")
    @Operation(summary = "Concluir ordem de serviço")
    public ResponseEntity<OrdemServicoDTO> concluir(
        @PathVariable Long id,
        @RequestParam(required = false) String diagnostico
    ) {
        OrdemServicoDTO updated = ordemServicoService.concluirOrdemServico(id, diagnostico);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar ordem de serviço")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ordemServicoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
