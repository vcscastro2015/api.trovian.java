package com.trovian.controller;

import com.trovian.dto.ComandoDTO;
import com.trovian.service.ComandoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comando")
@RequiredArgsConstructor
@Tag(name = "Comandos", description = "API para envio e consulta de comandos aos rastreadores")
public class ComandoController {

    private final ComandoService comandoService;

    @Operation(summary = "Envia um comando ao veículo",
               description = "Envia um comando ao rastreador instalado no veículo. 1 = Bloquear, 2 = Desbloquear")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comando enviado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    })
    @PostMapping("/{idVeiculo}/{idComando}")
    public ResponseEntity<Void> inserirComando(
            @Parameter(description = "ID do veículo", required = true)
            @PathVariable Long idVeiculo,
            @Parameter(description = "ID do comando (1 = Bloquear, 2 = Desbloquear)", required = true)
            @PathVariable Integer idComando) {
        comandoService.inserirComando(idComando, idVeiculo);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Busca o último comando enviado ao veículo",
               description = "Retorna o comando mais recente registrado para o veículo informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Último comando encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ComandoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Nenhum comando encontrado para o veículo")
    })
    @GetMapping(value = "/ultimo/{idVeiculo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComandoDTO> buscarUltimoComando(
            @Parameter(description = "ID do veículo", required = true)
            @PathVariable Long idVeiculo) {
        ComandoDTO comando = comandoService.buscarUltimoComando(idVeiculo);
        if (comando == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(comando);
    }
}