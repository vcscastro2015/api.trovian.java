package com.trovian.controller;

import com.trovian.dto.*;
import com.trovian.service.UsuarioService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Listar todos os usuários com paginação
     * GET /api/usuarios?pagina=0&tamanho=10&ordenarPor=nome&direcao=asc
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<UsuarioPageResponse> listarTodos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(defaultValue = "nome") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direcao) {

        UsuarioPageResponse response = usuarioService.listarTodos(pagina, tamanho, ordenarPor, direcao);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cliente/{clienteId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    public ResponseEntity<UsuarioPageResponse> findByCliente(
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        UsuarioPageResponse usuarios = usuarioService.findByCliente(clienteId, pageable);
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Listar usuários com filtros
     * POST /api/usuarios/filtrar?pagina=0&tamanho=10&ordenarPor=nome&direcao=asc
     */
    @PostMapping("/cliente/{clienteId}/filtrar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<UsuarioPageResponse> listarComFiltros(
            @PathVariable Long clienteId,
            @RequestBody UsuarioFiltroRequest filtro,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(defaultValue = "nome") String ordenarPor,
            @RequestParam(defaultValue = "asc") String direcao) {

        UsuarioPageResponse response = usuarioService.listarComFiltros(clienteId, filtro, pagina, tamanho, ordenarPor, direcao);
        return ResponseEntity.ok(response);
    }

    /**
     * Buscar usuário por ID
     * GET /api/usuarios/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        UsuarioResponse response = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Criar novo usuário
     * POST /api/usuarios
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioCriarRequest request) {
        UsuarioResponse response = usuarioService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Atualizar usuário
     * PUT /api/usuarios/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<UsuarioResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioAtualizarRequest request) {

        UsuarioResponse response = usuarioService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletar usuário (soft delete)
     * DELETE /api/usuarios/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MessageResponse> deletar(@PathVariable Long id) {
        MessageResponse response = usuarioService.deletar(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletar usuário permanentemente
     * DELETE /api/usuarios/{id}/permanente
     */
    @DeleteMapping("/{id}/permanente")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MessageResponse> deletarPermanente(@PathVariable Long id) {
        MessageResponse response = usuarioService.deletarPermanente(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Ativar usuário
     * PATCH /api/usuarios/{id}/ativar
     */
    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MessageResponse> ativar(@PathVariable Long id) {
        MessageResponse response = usuarioService.ativar(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Desativar usuário
     * PATCH /api/usuarios/{id}/desativar
     */
    @PatchMapping("/{id}/desativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<MessageResponse> desativar(@PathVariable Long id) {
        MessageResponse response = usuarioService.desativar(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualizar roles do usuário
     * PATCH /api/usuarios/{id}/roles
     */
    @PatchMapping("/{id}/roles")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<UsuarioResponse> atualizarRoles(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRolesRequest request) {

        UsuarioResponse response = usuarioService.atualizarRoles(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Trocar senha (pelo próprio usuário)
     * PATCH /api/usuarios/{id}/senha
     */
    @PatchMapping("/{id}/senha")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<MessageResponse> trocarSenha(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioTrocarSenhaRequest request) {

        MessageResponse response = usuarioService.trocarSenha(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Trocar senha (por admin - não requer senha atual)
     * PATCH /api/usuarios/{id}/senha-admin
     */
    @PatchMapping("/{id}/senha-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> trocarSenhaAdmin(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioTrocarSenhaAdminRequest request) {

        MessageResponse response = usuarioService.trocarSenhaAdmin(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Buscar usuários por termo (autocomplete)
     * GET /api/usuarios/buscar?termo=joao
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<UsuarioResponse>> buscarPorTermo(@RequestParam String termo) {
        List<UsuarioResponse> response = usuarioService.buscarPorTermo(termo);
        return ResponseEntity.ok(response);
    }

    /**
     * Obter estatísticas de usuários
     * GET /api/usuarios/estatisticas
     */
    @GetMapping("estatisticas")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<UsuarioEstatisticasResponse> obterEstatisticasAdm() {
        UsuarioEstatisticasResponse response = usuarioService.obterEstatisticasAdm();
        return ResponseEntity.ok(response);
    }

    @GetMapping("cliente/{clienteId}/estatisticas")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<UsuarioEstatisticasResponse> obterEstatisticas(@PathVariable Long clienteId) {
        UsuarioEstatisticasResponse response = usuarioService.obterEstatisticas(clienteId);
        return ResponseEntity.ok(response);
    }
}
