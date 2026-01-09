package com.trovian.service;

import com.trovian.dto.*;
import com.trovian.entity.RefreshToken;
import com.trovian.entity.Role;
import com.trovian.entity.Usuario;
import com.trovian.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // Valida credenciais
       // Authentication authentication = authenticationManager.authenticate(
        //        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
       // );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Verifica login único por dispositivo
        /*
        if (request.getTokenDispositivo() != null) {
            if (usuario.getTokenDispositivo() != null &&
                    !usuario.getTokenDispositivo().equals(request.getTokenDispositivo())) {
                throw new RuntimeException("Este usuário já está logado em outro dispositivo");
            }
            usuario.setTokenDispositivo(request.getTokenDispositivo());
            usuarioRepository.invalidarTokenDispositivoOutrosUsuarios(
                    request.getTokenDispositivo(), usuario.getId()
            );
        }*/

        // Atualiza último login
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        // Gera tokens
        String accessToken = jwtService.generateToken(usuario);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuario);

        return new LoginResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRoles().stream().map(Enum::name).collect(Collectors.toSet())
        );
    }

    @Transactional
    public MessageResponse registrar(RegistroRequest request) {
        // Verifica se email já existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        // Cria novo usuário
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setTelefone(request.getTelefone());
        usuario.setAtivo(true);
        usuario.setRoles(Set.of(Role.USER)); // Role padrão

        usuarioRepository.save(usuario);

        return new MessageResponse("Usuário registrado com sucesso");
    }

    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token não encontrado"));

        refreshToken = refreshTokenService.verifyExpiration(refreshToken);
        Usuario usuario = refreshToken.getUsuario();

        String newAccessToken = jwtService.generateToken(usuario);

        return new LoginResponse(
                newAccessToken,
                refreshToken.getToken(),
                "Bearer",
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRoles().stream().map(Enum::name).collect(Collectors.toSet())
        );
    }

    @Transactional
    public MessageResponse logout(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Remove token do dispositivo
        usuario.setTokenDispositivo(null);
        usuarioRepository.save(usuario);

        // Revoga refresh tokens
        refreshTokenService.deleteByUsuario(usuario);

        return new MessageResponse("Logout realizado com sucesso");
    }

    @Transactional
    public MessageResponse solicitarRecuperacaoSenha(RecuperarSenhaRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email não encontrado"));

        // Gera token de recuperação
        String token = UUID.randomUUID().toString();
        usuario.setTokenRecuperacaoSenha(token);
        usuario.setTokenExpiracao(LocalDateTime.now().plusHours(1));
        usuarioRepository.save(usuario);

        // Envia email
        emailService.enviarEmailRecuperacaoSenha(usuario.getEmail(), token);

        return new MessageResponse("Email de recuperação enviado");
    }

    @Transactional
    public MessageResponse redefinirSenha(RedefinirSenhaRequest request) {
        Usuario usuario = usuarioRepository.findByTokenRecuperacaoSenha(request.getToken())
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (usuario.getTokenExpiracao().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        usuario.setTokenRecuperacaoSenha(null);
        usuario.setTokenExpiracao(null);
        usuarioRepository.save(usuario);

        return new MessageResponse("Senha redefinida com sucesso");
    }

    public UsuarioResponse getUsuarioLogado(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Set<String> funcionalidades = usuario.getFuncionalidades()
                .stream()
                .map(f -> f.getCodigo())
                .collect(Collectors.toSet());

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .telefone(usuario.getTelefone())
                .ativo(usuario.getAtivo())
                .roles(usuario.getRoles())
                .funcionalidades(funcionalidades)
                .ultimoLogin(usuario.getUltimoLogin())
                .criadoEm(usuario.getCriadoEm())
                .atualizadoEm(usuario.getAtualizadoEm())
                .build();
    }
}
