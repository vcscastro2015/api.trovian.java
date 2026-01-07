package com.trovian.controller;

import com.trovian.dto.*;
import com.trovian.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/registrar")
    public ResponseEntity<MessageResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        return ResponseEntity.ok(authService.registrar(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(Authentication authentication) {
        return ResponseEntity.ok(authService.logout(authentication.getName()));
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<MessageResponse> recuperarSenha(@Valid @RequestBody RecuperarSenhaRequest request) {
        return ResponseEntity.ok(authService.solicitarRecuperacaoSenha(request));
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<MessageResponse> redefinirSenha(@Valid @RequestBody RedefinirSenhaRequest request) {
        return ResponseEntity.ok(authService.redefinirSenha(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> getUsuarioLogado(Authentication authentication) {
        return ResponseEntity.ok(authService.getUsuarioLogado(authentication.getName()));
    }
}
