package com.trovian.controller;

import com.trovian.dto.ViagemDTO;
import com.trovian.service.ViagemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para operações de Viagem
 */
@RestController
@RequestMapping("/viagem")
@RequiredArgsConstructor
@Tag(name = "Viagem", description = "API para gerenciamento de viagens com cálculos de custos e receitas")
public class ViagemController {

    private final ViagemService viagemService;

    /**
     * Lista todas as viagens com paginação
     */
    @GetMapping
    @Operation(summary = "Lista todas as viagens", description = "Retorna uma lista paginada de todas as viagens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de viagens retornada com sucesso")
    })
    public ResponseEntity<Page<ViagemDTO>> findAll(
            @Parameter(description = "Número da página (inicia em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ViagemDTO> viagens = viagemService.findAll(pageable);
        return ResponseEntity.ok(viagens);
    }

    /**
     * Busca viagem por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Busca viagem por ID", description = "Retorna uma viagem específica pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Viagem encontrada"),
            @ApiResponse(responseCode = "404", description = "Viagem não encontrada")
    })
    public ResponseEntity<ViagemDTO> findById(@Parameter(description = "ID da viagem") @PathVariable Long id) {
        ViagemDTO viagem = viagemService.findById(id);
        return ResponseEntity.ok(viagem);
    }

    /**
     * Busca viagens por veículo
     */
    @GetMapping("/veiculo/{veiculoId}")
    @Operation(summary = "Busca viagens por veículo", description = "Retorna lista paginada de viagens de um veículo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de viagens retornada com sucesso")
    })
    public ResponseEntity<Page<ViagemDTO>> findByVeiculo(
            @Parameter(description = "ID do veículo") @PathVariable Long veiculoId,
            @Parameter(description = "Número da página (inicia em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ViagemDTO> viagens = viagemService.findByVeiculo(veiculoId, pageable);
        return ResponseEntity.ok(viagens);
    }

    /**
     * Busca viagens por motorista
     */
    @GetMapping("/motorista/{motoristaId}")
    @Operation(summary = "Busca viagens por motorista", description = "Retorna lista paginada de viagens de um motorista")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de viagens retornada com sucesso")
    })
    public ResponseEntity<Page<ViagemDTO>> findByMotorista(
            @Parameter(description = "ID do motorista") @PathVariable Long motoristaId,
            @Parameter(description = "Número da página (inicia em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ViagemDTO> viagens = viagemService.findByMotorista(motoristaId, pageable);
        return ResponseEntity.ok(viagens);
    }

    /**
     * Busca viagens por cliente
     */
    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Busca viagens por cliente", description = "Retorna lista paginada de viagens de um cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de viagens retornada com sucesso")
    })
    public ResponseEntity<Page<ViagemDTO>> findByCliente(
            @Parameter(description = "ID do cliente") @PathVariable Long clienteId,
            @Parameter(description = "Número da página (inicia em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ViagemDTO> viagens = viagemService.findByCliente(clienteId, pageable);
        return ResponseEntity.ok(viagens);
    }

    /**
     * ENDPOINT PRINCIPAL: Calcula viagem SEM salvar no banco
     * Usado quando usuário clica em "Calcular" no front
     */
    @PostMapping("/calcular")
    @Operation(
            summary = "Calcula viagem sem salvar",
            description = "Recebe dados da viagem, executa todos os cálculos (receita, combustível, pedágios, comissão, lucro) e retorna o resultado SEM salvar no banco de dados. Usado para visualização prévia dos cálculos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cálculos realizados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ViagemDTO> calcular(@Valid @RequestBody ViagemDTO dto) {
        ViagemDTO calculado = viagemService.calcular(dto);
        return ResponseEntity.ok(calculado);
    }

    /**
     * Cria nova viagem (salva no banco)
     */
    @PostMapping
    @Operation(summary = "Cria nova viagem", description = "Cria uma nova viagem e salva no banco de dados com todos os cálculos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Viagem criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ViagemDTO> create(@Valid @RequestBody ViagemDTO dto) {
        ViagemDTO created = viagemService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Atualiza viagem existente
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza viagem", description = "Atualiza uma viagem existente e recalcula todos os valores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Viagem atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Viagem não encontrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ViagemDTO> update(
            @Parameter(description = "ID da viagem") @PathVariable Long id,
            @Valid @RequestBody ViagemDTO dto) {
        ViagemDTO updated = viagemService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Recalcula viagem existente
     */
    @PutMapping("/{id}/recalcular")
    @Operation(
            summary = "Recalcula viagem existente",
            description = "Recalcula todos os valores de uma viagem existente com base nos dados atuais e salva no banco"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Viagem recalculada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Viagem não encontrada")
    })
    public ResponseEntity<ViagemDTO> recalcular(@Parameter(description = "ID da viagem") @PathVariable Long id) {
        ViagemDTO recalculado = viagemService.recalcular(id);
        return ResponseEntity.ok(recalculado);
    }

    /**
     * Deleta viagem
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta viagem", description = "Remove uma viagem do banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Viagem deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Viagem não encontrada")
    })
    public ResponseEntity<Void> delete(@Parameter(description = "ID da viagem") @PathVariable Long id) {
        viagemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
