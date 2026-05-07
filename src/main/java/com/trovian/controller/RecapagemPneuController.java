package com.trovian.controller;

import com.trovian.dto.RecapagemPneuDTO;
import com.trovian.dto.RecapagemRetornoDTO;
import com.trovian.service.RecapagemPneuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pneu/recapagem")
@RequiredArgsConstructor
@Tag(name = "Recapagem de Pneu", description = "Controle de envio e retorno de pneus para recapagem")
public class RecapagemPneuController {

    private final RecapagemPneuService recapagemPneuService;

    @PostMapping("/enviar")
    @Operation(summary = "Enviar pneu para recapagem")
    @ApiResponse(responseCode = "201", description = "Pneu enviado para recapagem")
    @ApiResponse(responseCode = "422", description = "Limite máximo de recapagens atingido")
    public ResponseEntity<RecapagemPneuDTO> enviar(@Valid @RequestBody RecapagemPneuDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recapagemPneuService.enviarParaRecapagem(dto));
    }

    @PutMapping("/{id}/retorno")
    @Operation(summary = "Registrar retorno do pneu da recapagem")
    @ApiResponse(responseCode = "200", description = "Retorno registrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Recapagem não encontrada")
    public ResponseEntity<RecapagemPneuDTO> retorno(
            @Parameter(description = "ID da recapagem") @PathVariable Long id,
            @Valid @RequestBody RecapagemRetornoDTO dto) {
        return ResponseEntity.ok(recapagemPneuService.registrarRetorno(id, dto));
    }

    @GetMapping("/em-processo")
    @Operation(summary = "Listar recapagens em processo")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<Page<RecapagemPneuDTO>> emProcesso(
            @Parameter(description = "ID do cliente") @RequestParam Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<RecapagemPneuDTO> result = recapagemPneuService.listarEmProcesso(clienteId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataEnvio")));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{pneuId}")
    @Operation(summary = "Histórico de recapagens de um pneu")
    @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso")
    public ResponseEntity<List<RecapagemPneuDTO>> historico(@PathVariable Long pneuId) {
        return ResponseEntity.ok(recapagemPneuService.historicoByPneu(pneuId));
    }
}
