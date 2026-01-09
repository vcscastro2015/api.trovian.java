package com.trovian.repository;

import com.trovian.entity.RefreshToken;
import com.trovian.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    void deleteByUsuario(Usuario usuario);

    @Modifying
    void deleteByToken(String token);
}
