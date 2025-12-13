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

    Page<Peca> findByCategoria(String categoria, Pageable pageable);

    Page<Peca> findByStatus(Boolean status, Pageable pageable);

    @Query("SELECT p FROM Peca p WHERE p.descricao LIKE %:descricao%")
    Page<Peca> findByDescricaoContaining(@Param("descricao") String descricao, Pageable pageable);

    @Query("SELECT p FROM Peca p WHERE p.estoqueAtual <= p.estoqueMinimo")
    List<Peca> findPecasEstoqueBaixo();

    @Query("SELECT DISTINCT p.categoria FROM Peca p WHERE p.status = true ORDER BY p.categoria")
    List<String> findAllCategorias();

    @Query("SELECT p FROM Peca p WHERE p.codigo LIKE %:codigo%")
    Page<Peca> findByCodigoContaining(@Param("codigo") String codigo, Pageable pageable);
}
