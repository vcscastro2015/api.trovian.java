package com.trovian.controller;

import com.trovian.dto.ContaPagarDTO;
import com.trovian.service.ContaPagarService;
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
@RequestMapping("/conta-pagar")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Contas a Pagar", description = "Gerenciamento de contas a pagar")
public class ContaPagarController {

    private final ContaPagarService contaPagarService;

    @PostMapping
    @Operation(summary = "Criar nova conta a pagar")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Conta criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ContaPagarDTO> create(@Valid @RequestBody ContaPagarDTO dto) {
        log.info("Request para criar conta a pagar: {}", dto.getDescricao());
        ContaPagarDTO created = contaPagarService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar todas as contas a pagar (paginado)")
    public ResponseEntity<Page<ContaPagarDTO>> findAll(
        @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size,
        @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "dataVencimento") String sortBy,
        @Parameter(description = "Direção da ordenação") @RequestParam(defaultValue = "ASC") String direction
    ) {
        log.info("Request para listar contas a pagar - página: {}, tamanho: {}", page, size);
        Page<ContaPagarDTO> contas = contaPagarService.findAll(page, size, sortBy, direction);
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar conta a pagar por ID")
    public ResponseEntity<ContaPagarDTO> findById(
        @Parameter(description = "ID da conta") @PathVariable Long id
    ) {
        log.info("Request para buscar conta a pagar ID: {}", id);
        ContaPagarDTO conta = contaPagarService.findById(id);
        return ResponseEntity.ok(conta);
    }

    @GetMapping("/fornecedor/{fornecedorId}")
    @Operation(summary = "Buscar contas por fornecedor")
    public ResponseEntity<Page<ContaPagarDTO>> findByFornecedor(
        @PathVariable Long fornecedorId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "dataVencimento") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ContaPagarDTO> contas = contaPagarService.findByFornecedor(fornecedorId, pageable);
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/veiculo/{veiculoId}")
    @Operation(summary = "Buscar contas por veículo")
    public ResponseEntity<Page<ContaPagarDTO>> findByVeiculo(
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

        Page<ContaPagarDTO> contas = contaPagarService.findByVeiculo(veiculoId, pageable);
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/vencidas")
    @Operation(summary = "Buscar contas vencidas")
    public ResponseEntity<Page<ContaPagarDTO>> findVencidas(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "dataVencimento") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ContaPagarDTO> contas = contaPagarService.findVencidas(pageable);
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/a-vencer")
    @Operation(summary = "Buscar contas a vencer nos próximos X dias")
    public ResponseEntity<Page<ContaPagarDTO>> findAVencer(
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

        Page<ContaPagarDTO> contas = contaPagarService.findAVencer(dias, pageable);
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/periodo")
    @Operation(summary = "Buscar contas por período de vencimento")
    public ResponseEntity<Page<ContaPagarDTO>> findByPeriodo(
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

        Page<ContaPagarDTO> contas = contaPagarService.findByPeriodo(dataInicio, dataFim, pageable);
        return ResponseEntity.ok(contas);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar conta a pagar")
    public ResponseEntity<ContaPagarDTO> update(
        @PathVariable Long id,
        @Valid @RequestBody ContaPagarDTO dto
    ) {
        log.info("Request para atualizar conta a pagar ID: {}", id);
        ContaPagarDTO updated = contaPagarService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/pagar")
    @Operation(summary = "Registrar pagamento de conta")
    public ResponseEntity<ContaPagarDTO> registrarPagamento(
        @PathVariable Long id,
        @Parameter(description = "Valor pago") @RequestParam BigDecimal valorPago,
        @Parameter(description = "Data do pagamento")
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPagamento,
        @Parameter(description = "Usuário responsável") @RequestParam String usuario
    ) {
        log.info("Registrando pagamento da conta ID: {} - Valor: {}", id, valorPago);
        ContaPagarDTO updated = contaPagarService.registrarPagamento(id, valorPago, dataPagamento, usuario);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar conta a pagar")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Request para deletar conta a pagar ID: {}", id);
        contaPagarService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/total/pendente")
    @Operation(summary = "Total de contas pendentes")
    public ResponseEntity<BigDecimal> totalPendente() {
        BigDecimal total = contaPagarService.getTotalPendente();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/saldo/a-pagar")
    @Operation(summary = "Saldo total a pagar")
    public ResponseEntity<BigDecimal> saldoAPagar() {
        BigDecimal saldo = contaPagarService.getSaldoAPagar();
        return ResponseEntity.ok(saldo);
    }
}
