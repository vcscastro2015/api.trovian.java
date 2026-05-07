package com.trovian.repository;

import com.trovian.entity.RecapagemPneu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecapagemPneuRepository extends JpaRepository<RecapagemPneu, Long> {

    List<RecapagemPneu> findByPneuId(Long pneuId);

    @Query("SELECT r FROM RecapagemPneu r WHERE r.pneu.cliente.id = :clienteId AND r.dataRetorno IS NULL")
    Page<RecapagemPneu> findEmProcessoByClienteId(@Param("clienteId") Long clienteId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(r.valorRecapagem), 0) FROM RecapagemPneu r WHERE r.pneu.id = :pneuId AND r.aprovado = true")
    java.math.BigDecimal sumValorRecapagensByPneuId(@Param("pneuId") Long pneuId);
}
