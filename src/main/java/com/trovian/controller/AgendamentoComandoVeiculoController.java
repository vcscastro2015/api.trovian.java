package com.trovian.controller;

import com.trovian.dto.AgendamentoComandoVeiculoDTO;
import com.trovian.service.AgendamentoComandoVeiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agendamento-comando")
@RequiredArgsConstructor
@Tag(name = "Agendamento de Comandos", description = "API para agendamento automático de comandos por veículo")
public class AgendamentoComandoVeiculoController {

    private final AgendamentoComandoVeiculoService service;

    @Operation(summary = "Cria um novo agendamento de comando",
               description = "Agenda um comando (bloquear/desbloquear) para ser disparado automaticamente conforme recorrência definida")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Agendamento criado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AgendamentoComandoVeiculoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AgendamentoComandoVeiculoDTO> criar(
            @Valid @RequestBody AgendamentoComandoVeiculoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @Operation(summary = "Edita um agendamento existente",
               description = "Atualiza os dados de um agendamento de comando")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento atualizado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AgendamentoComandoVeiculoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AgendamentoComandoVeiculoDTO> editar(
            @Parameter(description = "ID do agendamento", required = true) @PathVariable Long id,
            @Valid @RequestBody AgendamentoComandoVeiculoDTO dto) {
        return ResponseEntity.ok(service.editar(id, dto));
    }

    @Operation(summary = "Exclui um agendamento",
               description = "Remove permanentemente um agendamento de comando")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Agendamento excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @Parameter(description = "ID do agendamento", required = true) @PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca agendamento por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AgendamentoComandoVeiculoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AgendamentoComandoVeiculoDTO> buscarPorId(
            @Parameter(description = "ID do agendamento", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Lista agendamentos ativos de um veículo",
               description = "Retorna todos os agendamentos ativos vinculados ao veículo informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de agendamentos retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping(value = "/veiculo/{idVeiculo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AgendamentoComandoVeiculoDTO>> listarPorVeiculo(
            @Parameter(description = "ID do veículo", required = true) @PathVariable Long idVeiculo) {
        return ResponseEntity.ok(service.listarPorVeiculo(idVeiculo));
    }
}
