package com.trovian.controller;

import com.trovian.dto.CooperativaDTO;
import com.trovian.service.CooperativaService;
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
@RequestMapping("/cooperativa")
@RequiredArgsConstructor
@Tag(name = "Cooperativas", description = "API para gerenciamento de Cooperativas")
public class CooperativaController {

    private final CooperativaService cooperativaService;

    @Operation(summary = "Lista todas as cooperativas", description = "Retorna uma lista com todas as cooperativas cadastradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cooperativas retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CooperativaDTO.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CooperativaDTO>> getAllCooperativas() {
        List<CooperativaDTO> cooperativas = cooperativaService.findAll();
        return ResponseEntity.ok(cooperativas);
    }

    @Operation(summary = "Busca cooperativa por ID", description = "Retorna uma cooperativa específica pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cooperativa encontrada",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CooperativaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cooperativa não encontrada")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CooperativaDTO> getCooperativaById(
            @Parameter(description = "ID da cooperativa", required = true)
            @PathVariable Long id) {
        CooperativaDTO cooperativa = cooperativaService.findById(id);
        return ResponseEntity.ok(cooperativa);
    }

    @Operation(summary = "Cria uma nova cooperativa", description = "Cadastra uma nova cooperativa no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cooperativa criada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CooperativaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CooperativaDTO> createCooperativa(
            @Parameter(description = "Dados da cooperativa a ser criada", required = true)
            @Valid @RequestBody CooperativaDTO cooperativaDTO) {
        CooperativaDTO createdCooperativa = cooperativaService.create(cooperativaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCooperativa);
    }

    @Operation(summary = "Atualiza uma cooperativa", description = "Atualiza os dados de uma cooperativa existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cooperativa atualizada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CooperativaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cooperativa não encontrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CooperativaDTO> updateCooperativa(
            @Parameter(description = "ID da cooperativa a ser atualizada", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados da cooperativa", required = true)
            @Valid @RequestBody CooperativaDTO cooperativaDTO) {
        CooperativaDTO updatedCooperativa = cooperativaService.update(id, cooperativaDTO);
        return ResponseEntity.ok(updatedCooperativa);
    }

    @Operation(summary = "Deleta uma cooperativa", description = "Remove uma cooperativa do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cooperativa deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cooperativa não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCooperativa(
            @Parameter(description = "ID da cooperativa a ser deletada", required = true)
            @PathVariable Long id) {
        cooperativaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca cooperativas por nome", description = "Retorna cooperativas cujo nome contenha o texto informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cooperativas retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CooperativaDTO.class)))
    })
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CooperativaDTO>> searchCooperativasByNome(
            @Parameter(description = "Nome (ou parte do nome) da cooperativa", required = true)
            @RequestParam String nome) {
        List<CooperativaDTO> cooperativas = cooperativaService.searchByNome(nome);
        return ResponseEntity.ok(cooperativas);
    }

    @Operation(summary = "Busca cooperativa por CNPJ", description = "Retorna uma cooperativa específica pelo seu CNPJ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cooperativa encontrada",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CooperativaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cooperativa não encontrada")
    })
    @GetMapping(value = "/cnpj/{cnpj}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CooperativaDTO> getCooperativaByCnpj(
            @Parameter(description = "CNPJ da cooperativa", required = true)
            @PathVariable String cnpj) {
        CooperativaDTO cooperativa = cooperativaService.findByCnpj(cnpj);
        return ResponseEntity.ok(cooperativa);
    }

    @Operation(summary = "Busca cooperativas por cidade", description = "Retorna todas as cooperativas de uma cidade específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cooperativas retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CooperativaDTO.class)))
    })
    @GetMapping(value = "/cidade/{cidade}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CooperativaDTO>> getCooperativasByCidade(
            @Parameter(description = "Nome da cidade", required = true)
            @PathVariable String cidade) {
        List<CooperativaDTO> cooperativas = cooperativaService.findByCidade(cidade);
        return ResponseEntity.ok(cooperativas);
    }

    @Operation(summary = "Busca cooperativas por UF", description = "Retorna todas as cooperativas de um estado específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cooperativas retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CooperativaDTO.class)))
    })
    @GetMapping(value = "/uf/{uf}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CooperativaDTO>> getCooperativasByUf(
            @Parameter(description = "Sigla do estado (UF)", required = true)
            @PathVariable String uf) {
        List<CooperativaDTO> cooperativas = cooperativaService.findByUf(uf);
        return ResponseEntity.ok(cooperativas);
    }

    @Operation(summary = "Busca cooperativas por status", description = "Retorna cooperativas ativas ou inativas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cooperativas retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CooperativaDTO.class)))
    })
    @GetMapping(value = "/ativa/{ativa}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CooperativaDTO>> getCooperativasByAtiva(
            @Parameter(description = "Status da cooperativa (true = ativa, false = inativa)", required = true)
            @PathVariable Boolean ativa) {
        List<CooperativaDTO> cooperativas = cooperativaService.findByAtiva(ativa);
        return ResponseEntity.ok(cooperativas);
    }

    @Operation(summary = "Busca cooperativas por cidade e UF", description = "Retorna cooperativas de uma cidade e estado específicos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cooperativas retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CooperativaDTO.class)))
    })
    @GetMapping(value = "/cidade/{cidade}/uf/{uf}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CooperativaDTO>> getCooperativasByCidadeAndUf(
            @Parameter(description = "Nome da cidade", required = true)
            @PathVariable String cidade,
            @Parameter(description = "Sigla do estado (UF)", required = true)
            @PathVariable String uf) {
        List<CooperativaDTO> cooperativas = cooperativaService.findByCidadeAndUf(cidade, uf);
        return ResponseEntity.ok(cooperativas);
    }
}
