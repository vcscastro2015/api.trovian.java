package com.trovian.repository;

import com.trovian.entity.Role;
import com.trovian.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Boolean existsByEmail(String email);

    Optional<Usuario> findByTokenRecuperacaoSenha(String token);

    @Modifying
    @Query("UPDATE Usuario u SET u.tokenDispositivo = null WHERE u.tokenDispositivo = :tokenDispositivo AND u.id != :usuarioId")
    void invalidarTokenDispositivoOutrosUsuarios(String tokenDispositivo, Long usuarioId);

    // Queries customizadas para CRUD
    Page<Usuario> findByAtivoTrue(Pageable pageable);

    Page<Usuario> findByAtivoFalse(Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE " +
           "(:nome IS NULL OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
           "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:ativo IS NULL OR u.ativo = :ativo)")
    Page<Usuario> findByFiltros(
            @Param("nome") String nome,
            @Param("email") String email,
            @Param("ativo") Boolean ativo,
            Pageable pageable
    );

    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE r = :role")
    Page<Usuario> findByRole(@Param("role") Role role, Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE u.cliente.id = :cliente AND " +
           "(:nome IS NULL OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
           "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:ativo IS NULL OR u.ativo = :ativo) AND " +
           "(:role IS NULL OR :role MEMBER OF u.roles)")
    Page<Usuario> findByFiltrosCompletos(
            @Param("cliente") Long clienteId,
            @Param("nome") String nome,
            @Param("email") String email,
            @Param("ativo") Boolean ativo,
            @Param("role") Role role,
            Pageable pageable
    );

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.ativo = true AND u.cliente.id = :cliente")
    long countUsuariosAtivos(@Param("cliente") Long clienteId);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.ativo = false AND u.cliente.id = :cliente")
    long countUsuariosInativos(@Param("cliente") Long clienteId);

    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :termo, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<Usuario> buscarPorTermo(@Param("termo") String termo);

    Long countByClienteId(Long clienteId);

    Page<Usuario> findByClienteId(Long clienteId, Pageable pageable);
}
