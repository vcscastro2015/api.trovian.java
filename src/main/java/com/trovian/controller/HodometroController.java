package com.trovian.controller;

import com.trovian.dto.HodometroDTO;
import com.trovian.service.HodometroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/hodometro")
@RequiredArgsConstructor
@Tag(name = "Hodômetro", description = "Gerenciamento de leituras de hodômetro")
public class HodometroController {

    private final HodometroService hodometroService;

    @GetMapping
    @Operation(summary = "Listar hodômetros", description = "Retorna todos os registros de hodômetro paginados")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<Page<HodometroDTO>> findAll(
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size) {
        Page<HodometroDTO> result = hodometroService.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro")));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar hodômetro por ID")
    @ApiResponse(responseCode = "200", description = "Registro encontrado")
    @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    public ResponseEntity<HodometroDTO> findById(
            @Parameter(description = "ID do hodômetro") @PathVariable Integer id) {
        return ResponseEntity.ok(hodometroService.findById(id));
    }

    @GetMapping("/veiculo/{veiculoId}")
    @Operation(summary = "Listar hodômetros por veículo")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<Page<HodometroDTO>> findByVeiculoId(
            @Parameter(description = "ID do veículo") @PathVariable Long veiculoId,
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size) {
        Page<HodometroDTO> result = hodometroService.findByVeiculoId(veiculoId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro")));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/veiculo/{veiculoId}/ultimo")
    @Operation(summary = "Buscar último hodômetro do veículo", description = "Retorna o registro de hodômetro mais recente de um veículo")
    @ApiResponse(responseCode = "200", description = "Registro encontrado")
    @ApiResponse(responseCode = "404", description = "Nenhum hodômetro encontrado para o veículo")
    public ResponseEntity<HodometroDTO> findUltimoByVeiculoId(
            @Parameter(description = "ID do veículo") @PathVariable Long veiculoId) {
        return ResponseEntity.ok(hodometroService.findUltimoByVeiculoId(veiculoId));
    }

    @GetMapping("/veiculo/{veiculoId}/periodo")
    @Operation(summary = "Listar hodômetros por veículo e período",
            description = "Retorna todos os registros de hodômetro de um veículo dentro do intervalo de datas informado (inclusive), para uso em relatórios")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<HodometroDTO>> findByVeiculoIdAndPeriodo(
            @Parameter(description = "ID do veículo") @PathVariable Long veiculoId,
            @Parameter(description = "Data inicial (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @Parameter(description = "Data final (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {
        return ResponseEntity.ok(hodometroService.findByVeiculoIdAndPeriodo(veiculoId, dataInicial, dataFinal));
    }

    @PostMapping
    @Operation(summary = "Criar hodômetro")
    @ApiResponse(responseCode = "201", description = "Registro criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<HodometroDTO> create(
            @Valid @RequestBody HodometroDTO hodometroDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hodometroService.create(hodometroDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar hodômetro")
    @ApiResponse(responseCode = "200", description = "Registro atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    public ResponseEntity<HodometroDTO> update(
            @Parameter(description = "ID do hodômetro") @PathVariable Integer id,
            @Valid @RequestBody HodometroDTO hodometroDTO) {
        return ResponseEntity.ok(hodometroService.update(id, hodometroDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir hodômetro")
    @ApiResponse(responseCode = "204", description = "Registro excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do hodômetro") @PathVariable Integer id) {
        hodometroService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
