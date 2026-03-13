package com.trovian.controller;

import com.trovian.dto.ClientePlanoDTO;
import com.trovian.dto.HistoricoPagamentoDTO;
import com.trovian.enums.StatusClientePlano;
import com.trovian.service.ClientePlanoService;
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
@RequestMapping("/cliente-plano")
@RequiredArgsConstructor
@Tag(name = "Cliente-Plano", description = "API para gerenciamento de contratos de planos de clientes")
public class ClientePlanoController {

    private final ClientePlanoService clientePlanoService;

    @Operation(summary = "Lista todos os cliente-planos com paginação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ClientePlanoDTO>> getAllClientePlanos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(clientePlanoService.findAll(pageable));
    }

    @Operation(summary = "Busca cliente-plano por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ClientePlano encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ClientePlanoDTO.class))),
            @ApiResponse(responseCode = "404", description = "ClientePlano não encontrado")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientePlanoDTO> getClientePlanoById(
            @Parameter(description = "ID do cliente-plano", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(clientePlanoService.findById(id));
    }

    @Operation(summary = "Lista cliente-planos por cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping(value = "/cliente/{clienteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ClientePlanoDTO>> getByCliente(
            @Parameter(description = "ID do cliente", required = true) @PathVariable Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(clientePlanoService.findByCliente(clienteId, pageable));
    }

    @Operation(summary = "Lista cliente-planos por status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping(value = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ClientePlanoDTO>> getByStatus(
            @Parameter(description = "Status do contrato", required = true) @PathVariable StatusClientePlano status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(clientePlanoService.findByStatus(status, pageable));
    }

    @Operation(summary = "Cria um novo contrato cliente-plano")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "ClientePlano criado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ClientePlanoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientePlanoDTO> createClientePlano(
            @Valid @RequestBody ClientePlanoDTO clientePlanoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientePlanoService.create(clientePlanoDTO));
    }

    @Operation(summary = "Atualiza um contrato cliente-plano")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ClientePlano atualizado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ClientePlanoDTO.class))),
            @ApiResponse(responseCode = "404", description = "ClientePlano não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientePlanoDTO> updateClientePlano(
            @Parameter(description = "ID do cliente-plano", required = true) @PathVariable Long id,
            @Valid @RequestBody ClientePlanoDTO clientePlanoDTO) {
        return ResponseEntity.ok(clientePlanoService.update(id, clientePlanoDTO));
    }

    @Operation(summary = "Cancela um contrato cliente-plano")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ClientePlano cancelado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ClientePlanoDTO.class))),
            @ApiResponse(responseCode = "404", description = "ClientePlano não encontrado")
    })
    @PostMapping(value = "/{id}/cancelar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClientePlanoDTO> cancelarClientePlano(
            @Parameter(description = "ID do cliente-plano", required = true) @PathVariable Long id,
            @Parameter(description = "Motivo do cancelamento") @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(clientePlanoService.cancelar(id, motivo));
    }

    @Operation(summary = "Registra um pagamento para o contrato")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pagamento registrado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HistoricoPagamentoDTO.class))),
            @ApiResponse(responseCode = "404", description = "ClientePlano não encontrado")
    })
    @PostMapping(value = "/{id}/pagamento", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HistoricoPagamentoDTO> registrarPagamento(
            @Parameter(description = "ID do cliente-plano", required = true) @PathVariable Long id,
            @RequestBody HistoricoPagamentoDTO pagamentoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientePlanoService.registrarPagamento(id, pagamentoDTO));
    }
}
