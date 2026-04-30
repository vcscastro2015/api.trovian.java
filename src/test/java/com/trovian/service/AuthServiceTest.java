package com.trovian.service;

import com.trovian.dto.*;
import com.trovian.entity.Cliente;
import com.trovian.entity.RefreshToken;
import com.trovian.entity.Usuario;
import com.trovian.repository.UsuarioRepository;
import com.trovian.util.builders.ClienteBuilder;
import com.trovian.util.builders.UsuarioBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para AuthService.
 * Cobre login, registro, refresh token, logout e recuperação de senha.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private EmailService emailService;

    // ========== TESTES: login ==========

    @Test
    @DisplayName("login deve retornar LoginResponse com tokens quando usuário existe")
    void login_comUsuarioExistente_deveRetornarLoginResponse() {
        Cliente cliente = ClienteBuilder.umCliente().build();
        Usuario usuario = UsuarioBuilder.umUsuario()
                .comEmail("login@teste.com")
                .comCliente(cliente)
                .build();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token-value");
        refreshToken.setUsuario(usuario);

        given(usuarioRepository.findByEmail("login@teste.com")).willReturn(Optional.of(usuario));
        given(jwtService.generateToken(usuario)).willReturn("access-token-value");
        given(refreshTokenService.createRefreshToken(usuario)).willReturn(refreshToken);
        given(usuarioRepository.save(any())).willReturn(usuario);

        LoginRequest request = new LoginRequest();
        request.setEmail("login@teste.com");
        request.setSenha("senha123");

        LoginResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token-value");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token-value");
        assertThat(response.getEmail()).isEqualTo("login@teste.com");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("login deve lançar exceção quando usuário não encontrado")
    void login_quandoUsuarioNaoExiste_deveLancarExcecao() {
        given(usuarioRepository.findByEmail("naoexiste@test.com")).willReturn(Optional.empty());

        LoginRequest request = new LoginRequest();
        request.setEmail("naoexiste@test.com");
        request.setSenha("qualquerSenha");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuário não encontrado");
    }

    // ========== TESTES: registrar ==========

    @Test
    @DisplayName("registrar deve salvar usuário e retornar mensagem de sucesso")
    void registrar_comEmailNovoCadastro_deveSalvarERetornarMensagem() {
        given(usuarioRepository.existsByEmail("novo@test.com")).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("$hash$");
        given(usuarioRepository.save(any())).willReturn(new Usuario());

        RegistroRequest request = new RegistroRequest();
        request.setEmail("novo@test.com");
        request.setSenha("senha123");
        request.setNome("Novo Usuário");

        MessageResponse response = authService.registrar(request);

        assertThat(response.getMessage()).contains("sucesso");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("registrar deve lançar exceção quando email já está cadastrado")
    void registrar_comEmailJaExistente_deveLancarExcecao() {
        given(usuarioRepository.existsByEmail("existente@test.com")).willReturn(true);

        RegistroRequest request = new RegistroRequest();
        request.setEmail("existente@test.com");
        request.setSenha("senha123");
        request.setNome("Usuário");

        assertThatThrownBy(() -> authService.registrar(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email já cadastrado");

        verify(usuarioRepository, never()).save(any());
    }

    // ========== TESTES: logout ==========

    @Test
    @DisplayName("logout deve limpar token do dispositivo e revogar refresh tokens")
    void logout_comEmailValido_deveLimparTokenERetornarMensagem() {
        Usuario usuario = UsuarioBuilder.umUsuario().comEmail("logout@test.com").build();
        given(usuarioRepository.findByEmail("logout@test.com")).willReturn(Optional.of(usuario));
        given(usuarioRepository.save(any())).willReturn(usuario);

        MessageResponse response = authService.logout("logout@test.com");

        assertThat(response.getMessage()).contains("sucesso");
        verify(refreshTokenService).deleteByUsuario(usuario);
    }

    @Test
    @DisplayName("logout deve lançar exceção quando usuário não encontrado")
    void logout_comEmailInexistente_deveLancarExcecao() {
        given(usuarioRepository.findByEmail("naoexiste@test.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.logout("naoexiste@test.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuário não encontrado");
    }

    // ========== TESTES: refreshToken ==========

    @Test
    @DisplayName("refreshToken deve retornar novo access token com refresh token válido")
    void refreshToken_comRefreshTokenValido_deveRetornarNovoAccessToken() {
        Cliente cliente = ClienteBuilder.umCliente().build();
        Usuario usuario = UsuarioBuilder.umUsuario().comCliente(cliente).build();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("valid-refresh");
        refreshToken.setUsuario(usuario);

        given(refreshTokenService.findByToken("valid-refresh")).willReturn(Optional.of(refreshToken));
        given(refreshTokenService.verifyExpiration(refreshToken)).willReturn(refreshToken);
        given(jwtService.generateToken(usuario)).willReturn("new-access-token");

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("valid-refresh");

        LoginResponse response = authService.refreshToken(request);

        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
    }

    @Test
    @DisplayName("refreshToken deve lançar exceção quando refresh token não encontrado")
    void refreshToken_comRefreshTokenInexistente_deveLancarExcecao() {
        given(refreshTokenService.findByToken("invalid")).willReturn(Optional.empty());

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("invalid");

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Refresh token não encontrado");
    }

    // ========== TESTES: recuperação de senha ==========

    @Test
    @DisplayName("solicitarRecuperacaoSenha deve gerar token e enviar email")
    void solicitarRecuperacaoSenha_comEmailValido_deveEnviarEmail() {
        Usuario usuario = UsuarioBuilder.umUsuario().comEmail("reset@test.com").build();
        given(usuarioRepository.findByEmail("reset@test.com")).willReturn(Optional.of(usuario));
        given(usuarioRepository.save(any())).willReturn(usuario);

        RecuperarSenhaRequest request = new RecuperarSenhaRequest();
        request.setEmail("reset@test.com");

        MessageResponse response = authService.solicitarRecuperacaoSenha(request);

        assertThat(response.getMessage()).containsIgnoringCase("email");
        verify(emailService).enviarEmailRecuperacaoSenha(eq("reset@test.com"), anyString());
    }

    @Test
    @DisplayName("redefinirSenha deve atualizar senha quando token é válido")
    void redefinirSenha_comTokenValido_deveAtualizarSenha() {
        Usuario usuario = UsuarioBuilder.umUsuario().build();
        usuario.setTokenRecuperacaoSenha("valid-token");
        usuario.setTokenExpiracao(LocalDateTime.now().plusHours(1)); // ainda válido

        given(usuarioRepository.findByTokenRecuperacaoSenha("valid-token")).willReturn(Optional.of(usuario));
        given(passwordEncoder.encode("novaSenha")).willReturn("$hash$");
        given(usuarioRepository.save(any())).willReturn(usuario);

        RedefinirSenhaRequest request = new RedefinirSenhaRequest();
        request.setToken("valid-token");
        request.setNovaSenha("novaSenha");

        MessageResponse response = authService.redefinirSenha(request);

        assertThat(response.getMessage()).contains("Senha");
        verify(passwordEncoder).encode("novaSenha");
    }

    @Test
    @DisplayName("redefinirSenha deve lançar exceção quando token expirado")
    void redefinirSenha_comTokenExpirado_deveLancarExcecao() {
        Usuario usuario = UsuarioBuilder.umUsuario().build();
        usuario.setTokenRecuperacaoSenha("expired-token");
        usuario.setTokenExpiracao(LocalDateTime.now().minusHours(2)); // já expirado

        given(usuarioRepository.findByTokenRecuperacaoSenha("expired-token")).willReturn(Optional.of(usuario));

        RedefinirSenhaRequest request = new RedefinirSenhaRequest();
        request.setToken("expired-token");
        request.setNovaSenha("novaSenha");

        assertThatThrownBy(() -> authService.redefinirSenha(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Token expirado");
    }
}
