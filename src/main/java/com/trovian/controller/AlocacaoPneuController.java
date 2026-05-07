package com.trovian.controller;

import com.trovian.dto.AlocacaoPneuDTO;
import com.trovian.dto.MapaVeiculoPneusDTO;
import com.trovian.dto.RodizioDTO;
import com.trovian.service.AlocacaoPneuService;
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

@RestController
@RequestMapping("/pneu/alocacao")
@RequiredArgsConstructor
@Tag(name = "Alocação de Pneu", description = "Posicionamento e rodízio de pneus no veículo")
    public class AlocacaoPneuController {

    private final AlocacaoPneuService alocacaoPneuService;

    @PostMapping("/montar")
    @Operation(summary = "Montar pneu em uma posição do veículo")
    @ApiResponse(responseCode = "201", description = "Pneu montado com sucesso")
    @ApiResponse(responseCode = "400", description = "Posição ocupada ou pneu indisponível")
    public ResponseEntity<AlocacaoPneuDTO> montar(@Valid @RequestBody AlocacaoPneuDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alocacaoPneuService.montar(dto.getPneuId(), dto.getVeiculoId(), dto.getPosicao(), dto.getKmMontagem(), dto.getResponsavel()));
    }

    @PostMapping("/{id}/desmontar")
    @Operation(summary = "Desmontar pneu de uma posição")
    @ApiResponse(responseCode = "200", description = "Pneu desmontado com sucesso")
    @ApiResponse(responseCode = "400", description = "Alocação já encerrada")
    public ResponseEntity<AlocacaoPneuDTO> desmontar(
            @Parameter(description = "ID da alocação") @PathVariable Long id,
            @RequestBody AlocacaoPneuDTO dto) {
        return ResponseEntity.ok(alocacaoPneuService.desmontar(id, dto.getKmRemocao(), dto.getMotivoRemocao()));
    }

    @PostMapping("/rodizio")
    @Operation(summary = "Realizar rodízio de pneus (transacional)")
    @ApiResponse(responseCode = "204", description = "Rodízio realizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Alguma posição inválida — rodízio revertido")
    public ResponseEntity<Void> rodizio(@Valid @RequestBody RodizioDTO dto) {
        alocacaoPneuService.realizarRodizio(dto.getVeiculoId(), dto.getPosicoes(), dto.getKm(), dto.getResponsavel());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mapa/{veiculoId}")
    @Operation(summary = "Mapa visual de posições de pneus do veículo")
    @ApiResponse(responseCode = "200", description = "Mapa retornado com sucesso")
    public ResponseEntity<MapaVeiculoPneusDTO> mapaVeiculo(@PathVariable Long veiculoId) {
        return ResponseEntity.ok(alocacaoPneuService.mapaVeiculo(veiculoId));
    }

    @GetMapping("/historico/{pneuId}")
    @Operation(summary = "Histórico de alocações de um pneu")
    @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso")
    public ResponseEntity<Page<AlocacaoPneuDTO>> historico(
            @PathVariable Long pneuId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AlocacaoPneuDTO> result = alocacaoPneuService.historicoByPneu(pneuId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataMontagem")));
        return ResponseEntity.ok(result);
    }
}
