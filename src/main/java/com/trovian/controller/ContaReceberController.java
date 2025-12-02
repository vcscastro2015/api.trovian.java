package com.trovian.controller;

import com.trovian.dto.ContaReceberDTO;
import com.trovian.service.ContaReceberService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/conta-receber")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Contas a Receber", description = "Gerenciamento de contas a receber")
public class ContaReceberController {

    private final ContaReceberService contaReceberService;

    @PostMapping
    @Operation(summary = "Criar nova conta a receber")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Conta criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ContaReceberDTO> create(@Valid @RequestBody ContaReceberDTO dto) {
        log.info("Request para criar conta a receber: {}", dto.getDescricao());
        ContaReceberDTO created = contaReceberService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar todas as contas a receber (paginado)")
    public ResponseEntity<Page<ContaReceberDTO>> findAll(
        @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size,
        @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "dataVencimento") String sortBy,
        @Parameter(description = "Direção da ordenação") @RequestParam(defaultValue = "ASC") String direction
    ) {
        log.info("Request para listar contas a receber - página: {}, tamanho: {}", page, size);
        Page<ContaReceberDTO> contas = contaReceberService.findAll(page, size, sortBy, direction);
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar conta a receber por ID")
    public ResponseEntity<ContaReceberDTO> findById(
        @Parameter(description = "ID da conta") @PathVariable Long id
    ) {
        log.info("Request para buscar conta a receber ID: {}", id);
        ContaReceberDTO conta = contaReceberService.findById(id);
        return ResponseEntity.ok(conta);
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Buscar contas por cliente")
    public ResponseEntity<Page<ContaReceberDTO>> findByCliente(
        @PathVariable Long clienteId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "dataVencimento") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ContaReceberDTO> contas = contaReceberService.findByCliente(clienteId, pageable);
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/veiculo/{veiculoId}")
    @Operation(summary = "Buscar contas por veículo")
    public ResponseEntity<Page<ContaReceberDTO>> findByVeiculo(
        @PathVariable Long veiculoId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "dataVencimento") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ContaReceberDTO> contas = contaReceberService.findByVeiculo(veiculoId, pageable);
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/vencidas")
    @Operation(summary = "Buscar contas vencidas")
    public ResponseEntity<Page<ContaReceberDTO>> findVencidas(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "dataVencimento") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ContaReceberDTO> contas = contaReceberService.findVencidas(pageable);
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/a-vencer")
    @Operation(summary = "Buscar contas a vencer nos próximos X dias")
    public ResponseEntity<Page<ContaReceberDTO>> findAVencer(
        @Parameter(description = "Número de dias") @RequestParam(defaultValue = "30") int dias,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "dataVencimento") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ContaReceberDTO> contas = contaReceberService.findAVencer(dias, pageable);
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/periodo")
    @Operation(summary = "Buscar contas por período de vencimento")
    public ResponseEntity<Page<ContaReceberDTO>> findByPeriodo(
        @Parameter(description = "Data inicial")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
        @Parameter(description = "Data final")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "dataVencimento") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ContaReceberDTO> contas = contaReceberService.findByPeriodo(dataInicio, dataFim, pageable);
        return ResponseEntity.ok(contas);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar conta a receber")
    public ResponseEntity<ContaReceberDTO> update(
        @PathVariable Long id,
        @Valid @RequestBody ContaReceberDTO dto
    ) {
        log.info("Request para atualizar conta a receber ID: {}", id);
        ContaReceberDTO updated = contaReceberService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/receber")
    @Operation(summary = "Registrar recebimento de conta")
    public ResponseEntity<ContaReceberDTO> registrarRecebimento(
        @PathVariable Long id,
        @Parameter(description = "Valor recebido") @RequestParam BigDecimal valorRecebido,
        @Parameter(description = "Data do recebimento")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataRecebimento,
        @Parameter(description = "Usuário responsável") @RequestParam String usuario
    ) {
        log.info("Registrando recebimento da conta ID: {} - Valor: {}", id, valorRecebido);
        ContaReceberDTO updated = contaReceberService.registrarRecebimento(id, valorRecebido, dataRecebimento, usuario);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar conta a receber")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Request para deletar conta a receber ID: {}", id);
        contaReceberService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total/pendente")
    @Operation(summary = "Total de contas pendentes")
    public ResponseEntity<BigDecimal> totalPendente() {
        BigDecimal total = contaReceberService.getTotalPendente();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/saldo/a-receber")
    @Operation(summary = "Saldo total a receber")
    public ResponseEntity<BigDecimal> saldoAReceber() {
        BigDecimal saldo = contaReceberService.getSaldoAReceber();
        return ResponseEntity.ok(saldo);
    }
}
