package com.trovian.repository;

import com.trovian.entity.Peca;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PecaRepository extends JpaRepository<Peca, Long> {

    Optional<Peca> findByCodigo(String codigo);

    Optional<Peca> findByCodigoAndClienteId(String codigo, Long clienteId);

    Page<Peca> findByCategoria(String categoria, Pageable pageable);

    Page<Peca> findByStatus(Boolean status, Pageable pageable);

    @Query("SELECT p FROM Peca p WHERE p.descricao LIKE %:descricao% AND p.cliente.id = :cliente")
    Page<Peca> findByDescricaoContaining(@Param("cliente") Long clienteId, @Param("descricao") String descricao, Pageable pageable);

    @Query("SELECT p FROM Peca p WHERE p.estoqueAtual <= p.estoqueMinimo AND p.cliente.id = :cliente")
    List<Peca> findPecasEstoqueBaixo(@Param("cliente") Long clienteId);

    @Query("SELECT DISTINCT p.categoria FROM Peca p WHERE p.status = true AND p.cliente.id = :cliente ORDER BY p.categoria")
    List<String> findAllCategorias(@Param("cliente") Long clienteId);

    @Query("SELECT p FROM Peca p WHERE p.codigo LIKE %:codigo%")
    Page<Peca> findByCodigoContaining(@Param("codigo") String codigo, Pageable pageable);

    Page<Peca> findByClienteId(Long clienteId, Pageable pageable);
}
