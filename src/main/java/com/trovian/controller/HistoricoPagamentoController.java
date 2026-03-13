package com.trovian.controller;

import com.trovian.dto.HistoricoPagamentoDTO;
import com.trovian.service.HistoricoPagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/historico-pagamento")
@RequiredArgsConstructor
@Tag(name = "Histórico de Pagamentos", description = "API para consulta do histórico de pagamentos")
public class HistoricoPagamentoController {

    private final HistoricoPagamentoService historicoPagamentoService;

    @Operation(summary = "Lista o histórico de pagamentos por cliente-plano")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping(value = "/cliente-plano/{clientePlanoId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<HistoricoPagamentoDTO>> getByClientePlano(
            @Parameter(description = "ID do cliente-plano", required = true) @PathVariable Long clientePlanoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataPagamento") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(historicoPagamentoService.findByClientePlano(clientePlanoId, pageable));
    }

    @Operation(summary = "Busca histórico de pagamento por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = HistoricoPagamentoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Histórico não encontrado")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HistoricoPagamentoDTO> getById(
            @Parameter(description = "ID do histórico de pagamento", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(historicoPagamentoService.findById(id));
    }
}
