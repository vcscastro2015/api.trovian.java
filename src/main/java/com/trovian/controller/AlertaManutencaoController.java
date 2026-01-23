package com.trovian.controller;

import com.trovian.dto.AlertaManutencaoDTO;
import com.trovian.service.AlertaManutencaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerta-manutencao")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Alertas de Manutenção", description = "Gerenciamento de alertas e notificações de manutenção")
public class AlertaManutencaoController {

    private final AlertaManutencaoService alertaManutencaoService;

    @PostMapping
    @Operation(summary = "Criar novo alerta de manutenção")
    public ResponseEntity<AlertaManutencaoDTO> create(@Valid @RequestBody AlertaManutencaoDTO dto) {
        AlertaManutencaoDTO created = alertaManutencaoService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar todos os alertas (paginado)")
    public ResponseEntity<Page<AlertaManutencaoDTO>> findAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "dataGeracao") String sortBy,
        @RequestParam(defaultValue = "DESC") String direction
    ) {
        Page<AlertaManutencaoDTO> alertas = alertaManutencaoService.findAll(page, size, sortBy, direction);
        return ResponseEntity.ok(alertas);
    }

    @GetMapping("/cliente/{clienteId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<Page<AlertaManutencaoDTO>> findByCliente(
            @Parameter(description = "ID do cliente", example = "1")
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataHora") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<AlertaManutencaoDTO> alertas = alertaManutencaoService.findByCliente(clienteId, pageable);
        return ResponseEntity.ok(alertas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar alerta por ID")
    public ResponseEntity<AlertaManutencaoDTO> findById(@PathVariable Long id) {
        AlertaManutencaoDTO alerta = alertaManutencaoService.findById(id);
        return ResponseEntity.ok(alerta);
    }

    @GetMapping("/cliente/{clienteId}/nao-lidos")
    @Operation(summary = "Listar alertas não lidos e não resolvidos")
    public ResponseEntity<List<AlertaManutencaoDTO>> findAlertasNaoLidos(@PathVariable Long clienteId) {
        List<AlertaManutencaoDTO> alertas = alertaManutencaoService.findAlertasNaoLidosNaoResolvidos(clienteId);
        return ResponseEntity.ok(alertas);
    }

    @GetMapping("/veiculo/{veiculoId}/nao-resolvidos")
    @Operation(summary = "Listar alertas não resolvidos de um veículo")
    public ResponseEntity<List<AlertaManutencaoDTO>> findAlertasNaoResolvidosPorVeiculo(
        @Parameter(description = "ID do veículo", required = true)
        @PathVariable Long veiculoId
    ) {
        List<AlertaManutencaoDTO> alertas = alertaManutencaoService.findAlertasNaoResolvidosPorVeiculo(veiculoId);
        return ResponseEntity.ok(alertas);
    }

    @GetMapping("/cliente/{clienteId}/criticos")
    @Operation(summary = "Listar alertas críticos não resolvidos")
    public ResponseEntity<List<AlertaManutencaoDTO>> findAlertasCriticos(@PathVariable Long clienteId) {
        List<AlertaManutencaoDTO> alertas = alertaManutencaoService.findAlertasCriticosNaoResolvidos(clienteId);
        return ResponseEntity.ok(alertas);
    }

    @PatchMapping("/{id}/marcar-lido")
    @Operation(summary = "Marcar alerta como lido")
    public ResponseEntity<AlertaManutencaoDTO> marcarComoLido(@PathVariable Long id) {
        AlertaManutencaoDTO alerta = alertaManutencaoService.marcarComoLido(id);
        return ResponseEntity.ok(alerta);
    }

    @PatchMapping("/{id}/marcar-resolvido")
    @Operation(summary = "Marcar alerta como resolvido")
    public ResponseEntity<AlertaManutencaoDTO> marcarComoResolvido(
        @PathVariable Long id,
        @RequestParam(required = false) String observacao
    ) {
        AlertaManutencaoDTO alerta = alertaManutencaoService.marcarComoResolvido(id, observacao);
        return ResponseEntity.ok(alerta);
    }

    @PostMapping("/verificar-abastecimento")
    @Operation(summary = "Verificar necessidade de manutenção ao abastecer")
    public ResponseEntity<Void> verificarManutencaoAbastecimento(
        @Parameter(description = "ID do veículo", required = true)
        @RequestParam Long veiculoId,
        @Parameter(description = "KM atual do veículo", required = true)
        @RequestParam Integer kmAtual
    ) {
        alertaManutencaoService.verificarManutencaoPreventivaAbastecimento(veiculoId, kmAtual);
        alertaManutencaoService.verificarTrocaPneus(veiculoId, kmAtual);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cliente/{clienteId}/verificar-estoque")
    @Operation(summary = "Verificar peças com estoque baixo")
    public ResponseEntity<Void> verificarPecasEstoqueBaixo(@PathVariable Long clienteId) {
        alertaManutencaoService.verificarPecasEstoqueBaixo(clienteId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validar-veiculo-viagem/{veiculoId}")
    @Operation(summary = "Validar se veículo pode realizar viagem")
    public ResponseEntity<Boolean> validarVeiculoParaViagem(
        @Parameter(description = "ID do veículo", required = true)
        @PathVariable Long veiculoId
    ) {
        boolean podeViajar = alertaManutencaoService.validarVeiculoParaViagem(veiculoId);
        return ResponseEntity.ok(podeViajar);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar alerta")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        alertaManutencaoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
