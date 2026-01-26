package com.trovian.controller;

import com.trovian.dto.ChecklistRealizadoDTO;
import com.trovian.dto.EstatisticasChecklistDTO;
import com.trovian.enums.StatusChecklist;
import com.trovian.service.ChecklistRealizadoService;
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

import java.util.UUID;

@RestController
@RequestMapping("/checklist-realizado")
@RequiredArgsConstructor
@Tag(name = "Checklists Realizados", description = "API para gerenciamento de Checklists Executados/Realizados")
public class ChecklistRealizadoController {

    private final ChecklistRealizadoService checklistRealizadoService;

    @Operation(summary = "Lista todos os checklists realizados com paginação",
               description = "Retorna uma página de checklists realizados com suporte a paginação e ordenação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de checklists retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ChecklistRealizadoDTO>> getAllChecklists(
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação", example = "dataHoraInicio")
            @RequestParam(defaultValue = "dataHoraInicio") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "DESC")
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<ChecklistRealizadoDTO> checklists = checklistRealizadoService.findAll(pageable);
        return ResponseEntity.ok(checklists);
    }

    @GetMapping("/cliente/{clienteId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<ChecklistRealizadoDTO>> findByCliente(
            @Parameter(description = "ID do cliente", example = "1")
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataHoraInicio") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ChecklistRealizadoDTO> lista = checklistRealizadoService.findByCliente(clienteId, pageable);
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Busca checklist realizado por ID", description = "Retorna um checklist realizado específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checklist encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChecklistRealizadoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Checklist não encontrado")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChecklistRealizadoDTO> getChecklistById(
            @Parameter(description = "ID do checklist realizado", required = true)
            @PathVariable UUID id) {
        ChecklistRealizadoDTO checklist = checklistRealizadoService.findById(id);
        return ResponseEntity.ok(checklist);
    }

    @Operation(summary = "Busca checklists realizados por veículo com paginação",
               description = "Retorna uma página de checklists realizados de um veículo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de checklists retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping(value = "/veiculo/{veiculoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ChecklistRealizadoDTO>> getChecklistsByVeiculo(
            @Parameter(description = "ID do veículo", required = true)
            @PathVariable Long veiculoId,
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação", example = "dataHoraInicio")
            @RequestParam(defaultValue = "dataHoraInicio") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "DESC")
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<ChecklistRealizadoDTO> checklists = checklistRealizadoService.findByVeiculoId(veiculoId, pageable);
        return ResponseEntity.ok(checklists);
    }

    @Operation(summary = "Busca checklists realizados por motorista com paginação",
               description = "Retorna uma página de checklists realizados de um motorista específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de checklists retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping(value = "/motorista/{motoristaId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ChecklistRealizadoDTO>> getChecklistsByMotorista(
            @Parameter(description = "ID do motorista", required = true)
            @PathVariable Long motoristaId,
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação", example = "dataHoraInicio")
            @RequestParam(defaultValue = "dataHoraInicio") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "DESC")
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<ChecklistRealizadoDTO> checklists = checklistRealizadoService.findByMotoristaId(motoristaId, pageable);
        return ResponseEntity.ok(checklists);
    }

    @Operation(summary = "Busca estatísticas gerais de checklists",
               description = "Retorna estatísticas consolidadas incluindo total, aprovados, reprovados, em andamento e conformidade geral")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EstatisticasChecklistDTO.class)))
    })
    @GetMapping(value = "/cliente/{clienteId}/estatisticas", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EstatisticasChecklistDTO> getEstatisticas(@PathVariable Long clienteId) {
        EstatisticasChecklistDTO estatisticas = checklistRealizadoService.getEstatisticas(clienteId);
        return ResponseEntity.ok(estatisticas);
    }

    @Operation(summary = "Busca checklists por status com paginação",
               description = "Retorna uma página de checklists filtrados por status (EM_ANDAMENTO, APROVADO ou REPROVADO)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de checklists retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping(value = "/cliente/{clienteId}/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ChecklistRealizadoDTO>> getChecklistsByStatus(
            @PathVariable Long clienteId,
            @Parameter(description = "Status do checklist (EM_ANDAMENTO, APROVADO ou REPROVADO)", required = true)
            @PathVariable StatusChecklist status,
            @Parameter(description = "Número da página (inicia em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página", example = "5")
            @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "Campo para ordenação", example = "dataHoraInicio")
            @RequestParam(defaultValue = "dataHoraInicio") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "DESC")
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<ChecklistRealizadoDTO> checklists = checklistRealizadoService.findByStatus(clienteId, status, pageable);
        return ResponseEntity.ok(checklists);
    }

    @Operation(summary = "Busca último checklist aprovado de um veículo",
               description = "Retorna o último checklist aprovado de um veículo (usado para validação pré-viagem)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checklist encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChecklistRealizadoDTO.class))),
            @ApiResponse(responseCode = "204", description = "Nenhum checklist aprovado encontrado")
    })
    @GetMapping(value = "/veiculo/{veiculoId}/ultimo-aprovado", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChecklistRealizadoDTO> getUltimoChecklistAprovado(
            @Parameter(description = "ID do veículo", required = true)
            @PathVariable Long veiculoId) {
        ChecklistRealizadoDTO checklist = checklistRealizadoService.getUltimoChecklistAprovado(veiculoId);
        if (checklist == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(checklist);
    }

    @Operation(summary = "Verifica se veículo possui checklist obrigatório aprovado",
               description = "Verifica se o veículo possui checklist obrigatório aprovado no dia atual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso")
    })
    @GetMapping(value = "/veiculo/{veiculoId}/verificar-obrigatorio", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> verificarChecklistObrigatorio(
            @Parameter(description = "ID do veículo", required = true)
            @PathVariable Long veiculoId) {
        boolean temChecklistAprovado = checklistRealizadoService.verificarChecklistObrigatorio(veiculoId);
        return ResponseEntity.ok(temChecklistAprovado);
    }

    @Operation(summary = "Cria um novo checklist realizado", description = "Inicia a execução de um novo checklist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Checklist criado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChecklistRealizadoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChecklistRealizadoDTO> createChecklist(
            @Parameter(description = "Dados do checklist a ser criado", required = true)
            @Valid @RequestBody ChecklistRealizadoDTO checklistDTO) {
        ChecklistRealizadoDTO createdChecklist = checklistRealizadoService.create(checklistDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChecklist);
    }

    @Operation(summary = "Atualiza um checklist realizado", description = "Atualiza os dados de um checklist em execução")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checklist atualizado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChecklistRealizadoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Checklist não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChecklistRealizadoDTO> updateChecklist(
            @Parameter(description = "ID do checklist a ser atualizado", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Novos dados do checklist", required = true)
            @Valid @RequestBody ChecklistRealizadoDTO checklistDTO) {
        ChecklistRealizadoDTO updatedChecklist = checklistRealizadoService.update(id, checklistDTO);
        return ResponseEntity.ok(updatedChecklist);
    }

    @Operation(summary = "Finaliza um checklist", description = "Finaliza a execução de um checklist e define o status (APROVADO/REPROVADO)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checklist finalizado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChecklistRealizadoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Checklist não encontrado")
    })
    @PostMapping(value = "/{id}/finalizar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChecklistRealizadoDTO> finalizarChecklist(
            @Parameter(description = "ID do checklist a ser finalizado", required = true)
            @PathVariable UUID id) {
        ChecklistRealizadoDTO finalized = checklistRealizadoService.finalizar(id);
        return ResponseEntity.ok(finalized);
    }

    @Operation(summary = "Deleta um checklist realizado", description = "Remove um checklist realizado do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Checklist deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Checklist não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChecklist(
            @Parameter(description = "ID do checklist a ser deletado", required = true)
            @PathVariable UUID id) {
        checklistRealizadoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
