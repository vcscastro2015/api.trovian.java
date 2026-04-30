package com.trovian.service;

import com.trovian.entity.Usuario;
import com.trovian.enums.Role;
import com.trovian.util.builders.UsuarioBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários para JwtService.
 * Usa ReflectionTestUtils para injetar @Value sem contexto Spring.
 */
class JwtServiceTest {

    // Segredo base64 válido (mínimo 256 bits / 32 bytes após decode)
    // Este valor é o mesmo configurado em application.yml para consistência
    private static final String JWT_SECRET =
            "TrovianSecretKeyMuitoSeguraComMinimo256BitsParaHS256AlgorithmQueDeveSerTrocadaEmProducao";
    private static final long JWT_EXPIRATION = 900_000L; // 15 minutos

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", JWT_SECRET);
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", JWT_EXPIRATION);
    }

    // ========== TESTES: geração de token ==========

    @Test
    @DisplayName("generateToken deve retornar token não-nulo para usuário válido")
    void generateToken_comUsuarioValido_deveRetornarTokenNaoNulo() {
        Usuario usuario = UsuarioBuilder.umUsuario().build();

        String token = jwtService.generateToken(usuario);

        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("generateToken deve incluir email do usuário no subject do token")
    void generateToken_comUsuarioValido_deveIncluirEmailNoToken() {
        Usuario usuario = UsuarioBuilder.umUsuario()
                .comEmail("teste@trovian.com")
                .build();

        String token = jwtService.generateToken(usuario);
        String emailExtraido = jwtService.extractUsername(token);

        assertThat(emailExtraido).isEqualTo("teste@trovian.com");
    }

    // ========== TESTES: validação de token ==========

    @Test
    @DisplayName("validateToken deve retornar true para token válido e usuario correto")
    void validateToken_comTokenValidoEUsuarioCorreto_deveRetornarTrue() {
        Usuario usuario = UsuarioBuilder.umUsuario()
                .comEmail("usuario@teste.com")
                .comSenha("senhaHash")
                .comRoles(Set.of(Role.USER))
                .build();

        String token = jwtService.generateToken(usuario);

        // UserDetails equivalente ao que CustomUserDetailsService retornaria
        UserDetails userDetails = User.withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .roles("USER")
                .build();

        assertThat(jwtService.validateToken(token, userDetails)).isTrue();
    }

    @Test
    @DisplayName("validateToken deve retornar false para token com email diferente")
    void validateToken_comEmailDiferente_deveRetornarFalse() {
        Usuario usuario = UsuarioBuilder.umUsuario()
                .comEmail("original@teste.com")
                .build();

        String token = jwtService.generateToken(usuario);

        UserDetails outroUsuario = User.withUsername("outro@teste.com")
                .password("hash")
                .roles("USER")
                .build();

        assertThat(jwtService.validateToken(token, outroUsuario)).isFalse();
    }

    @Test
    @DisplayName("validateToken deve lançar exceção para token expirado")
    void validateToken_comTokenExpirado_deveLancarExcecao() {
        // Configura expiração negativa para simular token já expirado
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", -1000L);

        Usuario usuario = UsuarioBuilder.umUsuario().build();
        String token = jwtService.generateToken(usuario);

        UserDetails userDetails = User.withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .roles("USER")
                .build();

        // O parser JWT lança ExpiredJwtException antes de retornar false
        assertThatThrownBy(() -> jwtService.validateToken(token, userDetails))
                .isInstanceOf(Exception.class);
    }

    // ========== TESTES: extração de claims ==========

    @Test
    @DisplayName("extractUsername deve retornar email do subject do token")
    void extractUsername_comTokenValido_deveRetornarEmail() {
        Usuario usuario = UsuarioBuilder.umUsuario()
                .comEmail("claims@test.com")
                .build();

        String token = jwtService.generateToken(usuario);

        assertThat(jwtService.extractUsername(token)).isEqualTo("claims@test.com");
    }

    @Test
    @DisplayName("extractExpiration deve retornar data de expiração do token")
    void extractExpiration_comTokenValido_deveRetornarDataValida() {
        Usuario usuario = UsuarioBuilder.umUsuario().build();
        String token = jwtService.generateToken(usuario);

        var expiration = jwtService.extractExpiration(token);

        assertThat(expiration).isInTheFuture();
    }

    // ========== TESTES: tokens de usuários diferentes ==========

    @Test
    @DisplayName("dois usuários diferentes devem ter tokens distintos")
    void generateToken_paraDoisUsuarios_deveGerarTokensDistintos() {
        Usuario usuario1 = UsuarioBuilder.umUsuario().comEmail("user1@test.com").build();
        Usuario usuario2 = UsuarioBuilder.umUsuario().comEmail("user2@test.com").build();

        String token1 = jwtService.generateToken(usuario1);
        String token2 = jwtService.generateToken(usuario2);

        assertThat(token1).isNotEqualTo(token2);
    }
}
