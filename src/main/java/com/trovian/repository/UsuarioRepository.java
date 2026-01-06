package com.trovian.repository;

import com.trovian.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Boolean existsByEmail(String email);

    Optional<Usuario> findByTokenRecuperacaoSenha(String token);

    @Modifying
    @Query("UPDATE Usuario u SET u.tokenDispositivo = null WHERE u.tokenDispositivo = :tokenDispositivo AND u.id != :usuarioId")
    void invalidarTokenDispositivoOutrosUsuarios(String tokenDispositivo, Long usuarioId);
}
