package com.trovian.controller;

import com.trovian.dto.AbastecimentoDTO;
import com.trovian.service.AbastecimentoService;
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

@RestController
@RequestMapping("/abastecimento")
@RequiredArgsConstructor
@Tag(name = "Abastecimento", description = "API para gerenciamento de abastecimentos de veículos")
public class AbastecimentoController {

    private final AbastecimentoService abastecimentoService;

    @GetMapping
    @Operation(summary = "Lista todos os abastecimentos com paginação",
            description = "Retorna uma lista paginada de todos os abastecimentos cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<AbastecimentoDTO>> findAll(
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

        Page<AbastecimentoDTO> abastecimentos = abastecimentoService.findAll(pageable);
        return ResponseEntity.ok(abastecimentos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca abastecimento por ID", description = "Retorna um abastecimento específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Abastecimento encontrado"),
            @ApiResponse(responseCode = "404", description = "Abastecimento não encontrado")
    })
    public ResponseEntity<AbastecimentoDTO> findById(
            @Parameter(description = "ID do abastecimento", example = "1")
            @PathVariable Long id) {
        AbastecimentoDTO abastecimento = abastecimentoService.findById(id);
        return ResponseEntity.ok(abastecimento);
    }

    @GetMapping("/veiculo/{veiculoId}")
    @Operation(summary = "Busca abastecimentos por veículo",
            description = "Retorna uma lista paginada de abastecimentos de um veículo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<AbastecimentoDTO>> findByVeiculo(
            @Parameter(description = "ID do veículo", example = "1")
            @PathVariable Long veiculoId,
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação", example = "dataHora")
            @RequestParam(defaultValue = "dataHora") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "DESC")
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<AbastecimentoDTO> abastecimentos = abastecimentoService.findByVeiculo(veiculoId, pageable);
        return ResponseEntity.ok(abastecimentos);
    }

    @GetMapping("/motorista/{motoristaId}")
    @Operation(summary = "Busca abastecimentos por motorista",
            description = "Retorna uma lista paginada de abastecimentos realizados por um motorista específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<AbastecimentoDTO>> findByMotorista(
            @Parameter(description = "ID do motorista", example = "1")
            @PathVariable Long motoristaId,
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação", example = "dataHora")
            @RequestParam(defaultValue = "dataHora") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "DESC")
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<AbastecimentoDTO> abastecimentos = abastecimentoService.findByMotorista(motoristaId, pageable);
        return ResponseEntity.ok(abastecimentos);
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Busca abastecimentos por cliente",
            description = "Retorna uma lista paginada de abastecimentos de um cliente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<AbastecimentoDTO>> findByCliente(
            @Parameter(description = "ID do cliente", example = "1")
            @PathVariable Long clienteId,
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação", example = "dataHora")
            @RequestParam(defaultValue = "dataHora") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "DESC")
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<AbastecimentoDTO> abastecimentos = abastecimentoService.findByCliente(clienteId, pageable);
        return ResponseEntity.ok(abastecimentos);
    }

    @GetMapping("/rota/{rotaId}")
    @Operation(summary = "Busca abastecimentos por rota",
            description = "Retorna uma lista paginada de abastecimentos de uma rota específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<AbastecimentoDTO>> findByRota(
            @Parameter(description = "ID da rota", example = "1")
            @PathVariable Long rotaId,
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação", example = "dataHora")
            @RequestParam(defaultValue = "dataHora") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "DESC")
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<AbastecimentoDTO> abastecimentos = abastecimentoService.findByRota(rotaId, pageable);
        return ResponseEntity.ok(abastecimentos);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Busca abastecimentos por status",
            description = "Retorna uma lista paginada de abastecimentos filtrados por status (ativo/inativo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<AbastecimentoDTO>> findByStatus(
            @Parameter(description = "Status do abastecimento (true/false)", example = "true")
            @PathVariable Boolean status,
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação", example = "dataHora")
            @RequestParam(defaultValue = "dataHora") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "DESC")
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<AbastecimentoDTO> abastecimentos = abastecimentoService.findByStatus(status, pageable);
        return ResponseEntity.ok(abastecimentos);
    }

    @PostMapping
    @Operation(summary = "Cria um novo abastecimento", description = "Cria um novo registro de abastecimento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Abastecimento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<AbastecimentoDTO> create(
            @Parameter(description = "Dados do abastecimento")
            @Valid @RequestBody AbastecimentoDTO dto) {
        AbastecimentoDTO created = abastecimentoService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um abastecimento", description = "Atualiza os dados de um abastecimento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Abastecimento atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Abastecimento não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<AbastecimentoDTO> update(
            @Parameter(description = "ID do abastecimento", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Dados atualizados do abastecimento")
            @Valid @RequestBody AbastecimentoDTO dto) {
        AbastecimentoDTO updated = abastecimentoService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um abastecimento (soft delete)",
            description = "Realiza soft delete de um abastecimento (altera status para false)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Abastecimento deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Abastecimento não encontrado")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do abastecimento", example = "1")
            @PathVariable Long id) {
        abastecimentoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
