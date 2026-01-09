package com.trovian.service;

import com.trovian.entity.RefreshToken;
import com.trovian.entity.Usuario;
import com.trovian.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenDurationMs;

    @Transactional
    public RefreshToken createRefreshToken(Usuario usuario) {
        // Remove tokens antigos do usuário
        refreshTokenRepository.deleteByUsuario(usuario);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(usuario);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setDataCriacao(Instant.now());
        refreshToken.setDataExpiracao(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setRevogado(false);

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getDataExpiracao().compareTo(Instant.now()) < 0 || token.isRevogado()) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expirado ou revogado. Faça login novamente.");
        }
        return token;
    }

    @Transactional
    public void deleteByUsuario(Usuario usuario) {
        refreshTokenRepository.deleteByUsuario(usuario);
    }

    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshToken -> {
            refreshToken.setRevogado(true);
            refreshTokenRepository.save(refreshToken);
        });
    }
}
