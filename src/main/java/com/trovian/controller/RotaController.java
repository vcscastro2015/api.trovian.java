package com.trovian.controller;

import com.trovian.dto.RotaDTO;
import com.trovian.dto.RotaFiltrosDTO;
import com.trovian.service.RotaService;
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
 * Controller para gerenciamento de rotas
 */
@RestController
@RequestMapping("/rota")
@RequiredArgsConstructor
@Tag(name = "Rota", description = "Endpoints para gerenciamento de rotas")
public class RotaController {

    private final RotaService rotaService;

    /**
     * Lista todas as rotas com paginação e filtros opcionais
     */
    @GetMapping
    @Operation(summary = "Lista todas as rotas", description = "Retorna uma lista paginada de todas as rotas com filtros opcionais")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de rotas retornada com sucesso")
    })
    public ResponseEntity<Page<RotaDTO>> getAll(
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação") @RequestParam(defaultValue = "ASC") String direction,
            @Parameter(description = "Filtro por nome") @RequestParam(required = false) String nome,
            @Parameter(description = "Filtro por ID do cliente") @RequestParam(required = false) Long clienteId,
            @Parameter(description = "Filtro por ID do veículo") @RequestParam(required = false) Long veiculoId,
            @Parameter(description = "Filtro por ID da cooperativa") @RequestParam(required = false) Long cooperativaId,
            @Parameter(description = "Filtro por status ativo") @RequestParam(required = false) Boolean ativa,
            @Parameter(description = "Filtro por distância mínima") @RequestParam(required = false) Double distanciaMinima,
            @Parameter(description = "Filtro por distância máxima") @RequestParam(required = false) Double distanciaMaxima,
            @Parameter(description = "Filtro por dificuldade") @RequestParam(required = false) String dificuldade
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        // Se houver filtros, usa o método com filtros
        if (nome != null || clienteId != null || veiculoId != null || cooperativaId != null ||
            ativa != null || distanciaMinima != null || distanciaMaxima != null || dificuldade != null) {

            RotaFiltrosDTO filtros = new RotaFiltrosDTO();
            filtros.setNome(nome);
            filtros.setClienteId(clienteId);
            filtros.setVeiculoId(veiculoId);
            filtros.setCooperativaId(cooperativaId);
            filtros.setAtiva(ativa);
            filtros.setDistanciaMinima(distanciaMinima);
            filtros.setDistanciaMaxima(distanciaMaxima);
            filtros.setDificuldade(dificuldade);

            return ResponseEntity.ok(rotaService.findByFiltros(filtros, pageable));
        }

        return ResponseEntity.ok(rotaService.findAll(pageable));
    }

    /**
     * Busca rota por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Busca rota por ID", description = "Retorna uma rota específica pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rota encontrada"),
            @ApiResponse(responseCode = "404", description = "Rota não encontrada")
    })
    public ResponseEntity<RotaDTO> getById(
            @Parameter(description = "ID da rota") @PathVariable Long id
    ) {
        return ResponseEntity.ok(rotaService.findById(id));
    }

    /**
     * Cria nova rota
     */
    @PostMapping
    @Operation(summary = "Cria nova rota", description = "Cria uma nova rota com todos os detalhes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rota criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<RotaDTO> create(
            @Valid @RequestBody RotaDTO rotaDTO
    ) {
        RotaDTO createdRota = rotaService.create(rotaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRota);
    }

    /**
     * Atualiza rota existente
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza rota", description = "Atualiza uma rota existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rota atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Rota não encontrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<RotaDTO> update(
            @Parameter(description = "ID da rota") @PathVariable Long id,
            @Valid @RequestBody RotaDTO rotaDTO
    ) {
        return ResponseEntity.ok(rotaService.update(id, rotaDTO));
    }

    /**
     * Deleta rota
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta rota", description = "Remove uma rota do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rota deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Rota não encontrada")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da rota") @PathVariable Long id
    ) {
        rotaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Busca rotas por cliente
     */
    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Busca rotas por cliente", description = "Retorna todas as rotas de um cliente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de rotas retornada com sucesso")
    })
    public ResponseEntity<Page<RotaDTO>> getByCliente(
            @Parameter(description = "ID do cliente") @PathVariable Long clienteId,
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação") @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(rotaService.findByCliente(clienteId, pageable));
    }

    /**
     * Busca rotas por veículo
     */
    @GetMapping("/veiculo/{veiculoId}")
    @Operation(summary = "Busca rotas por veículo", description = "Retorna todas as rotas de um veículo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de rotas retornada com sucesso")
    })
    public ResponseEntity<Page<RotaDTO>> getByVeiculo(
            @Parameter(description = "ID do veículo") @PathVariable Long veiculoId,
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação") @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(rotaService.findByVeiculo(veiculoId, pageable));
    }

    /**
     * Busca rotas por cooperativa
     */
    @GetMapping("/cooperativa/{cooperativaId}")
    @Operation(summary = "Busca rotas por cooperativa", description = "Retorna todas as rotas de uma cooperativa específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de rotas retornada com sucesso")
    })
    public ResponseEntity<Page<RotaDTO>> getByCooperativa(
            @Parameter(description = "ID da cooperativa") @PathVariable Long cooperativaId,
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação") @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(rotaService.findByCooperativa(cooperativaId, pageable));
    }

    /**
     * Busca rotas ativas ou inativas
     */
    @GetMapping("/ativa/{ativa}")
    @Operation(summary = "Busca rotas por status", description = "Retorna rotas ativas ou inativas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de rotas retornada com sucesso")
    })
    public ResponseEntity<Page<RotaDTO>> getByAtiva(
            @Parameter(description = "Status ativo (true/false)") @PathVariable Boolean ativa,
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação") @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(rotaService.findByAtiva(ativa, pageable));
    }

    /**
     * Busca rotas por nome (parcial)
     */
    @GetMapping("/search")
    @Operation(summary = "Busca rotas por nome", description = "Retorna rotas que contenham o texto no nome")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de rotas retornada com sucesso")
    })
    public ResponseEntity<Page<RotaDTO>> searchByNome(
            @Parameter(description = "Nome ou parte do nome") @RequestParam String nome,
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação") @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(rotaService.searchByNome(nome, pageable));
    }

    /**
     * Duplica uma rota existente
     */
    @PostMapping("/{id}/duplicate")
    @Operation(summary = "Duplica rota", description = "Cria uma cópia de uma rota existente com novo nome")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rota duplicada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Rota não encontrada")
    })
    public ResponseEntity<RotaDTO> duplicate(
            @Parameter(description = "ID da rota a ser duplicada") @PathVariable Long id,
            @Parameter(description = "Nome da nova rota") @RequestParam String novoNome
    ) {
        RotaDTO duplicatedRota = rotaService.duplicate(id, novoNome);
        return ResponseEntity.status(HttpStatus.CREATED).body(duplicatedRota);
    }

    /**
     * Exporta rota em formato GeoJSON
     */
    @GetMapping("/{id}/geojson")
    @Operation(summary = "Exporta rota em GeoJSON", description = "Retorna a representação GeoJSON da rota")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "GeoJSON retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Rota não encontrada")
    })
    public ResponseEntity<String> exportGeoJson(
            @Parameter(description = "ID da rota") @PathVariable Long id
    ) {
        String geoJson = rotaService.exportGeoJson(id);
        return ResponseEntity.ok()
                .header("Content-Type", "application/geo+json")
                .body(geoJson);
    }
}
