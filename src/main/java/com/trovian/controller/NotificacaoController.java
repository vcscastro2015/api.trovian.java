package com.trovian.controller;

import com.trovian.dto.NotificacaoDTO;
import com.trovian.dto.NotificacaoUpdateDTO;
import com.trovian.dto.dashboard.NotificacaoDashboardDTO;
import com.trovian.service.NotificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
@Tag(name = "Notificações", description = "Gerenciamento e consulta de notificações do sistema")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    // -------------------------------------------------------------------------
    // Leitura geral (admin/manager)
    // -------------------------------------------------------------------------

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Listar todas as notificações (admin/manager)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    public ResponseEntity<Page<NotificacaoDTO>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataCriacao") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(notificacaoService.findAll(pageable));
    }

    // -------------------------------------------------------------------------
    // Notificações do usuário logado
    // -------------------------------------------------------------------------

    @GetMapping("/me")
    @Operation(summary = "Listar notificações do usuário logado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificações retornadas com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Page<NotificacaoDTO>> findByUsuarioLogado(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataCriacao") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(notificacaoService.findByUsuarioLogado(authentication.getName(), pageable));
    }

    // -------------------------------------------------------------------------
    // Notificações dos motoristas do cliente do usuário logado
    // -------------------------------------------------------------------------

    @GetMapping("/me/motoristas")
    @Operation(summary = "Listar notificações dos motoristas pertencentes ao cliente do usuário logado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificações retornadas com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<Page<NotificacaoDTO>> findByMotoristasDoUsuario(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataCriacao") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(notificacaoService.findByMotoristasDoUsuario(authentication.getName(), pageable));
    }

    // -------------------------------------------------------------------------
    // Busca por ID
    // -------------------------------------------------------------------------

    @GetMapping("/{id}")
    @Operation(summary = "Buscar notificação por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificação encontrada"),
            @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
    })
    public ResponseEntity<NotificacaoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(notificacaoService.findById(id));
    }

    // -------------------------------------------------------------------------
    // Atualização parcial (marcar como lida, registrar resposta, etc.)
    // -------------------------------------------------------------------------

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar status ou resposta de uma notificação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificação atualizada"),
            @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
    })
    public ResponseEntity<NotificacaoDTO> atualizar(
            @PathVariable Long id,
            @RequestBody NotificacaoUpdateDTO dto) {
        return ResponseEntity.ok(notificacaoService.atualizar(id, dto));
    }

    // -------------------------------------------------------------------------
    // Dashboard
    // -------------------------------------------------------------------------

    @GetMapping("/dashboard")
    @Operation(summary = "Dashboard de notificações do usuário logado",
               description = "Retorna totais por status, tipo, categoria e referenciaTipo, " +
                             "além das 10 notificações mais recentes e tendência diária")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<NotificacaoDashboardDTO> getDashboard(
            Authentication authentication,
            @RequestParam(defaultValue = "7") int dias) {
        return ResponseEntity.ok(notificacaoService.getDashboard(authentication.getName(), dias));
    }
}
