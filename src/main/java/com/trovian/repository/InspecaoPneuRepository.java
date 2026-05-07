package com.trovian.repository;

import com.trovian.entity.InspecaoPneu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InspecaoPneuRepository extends JpaRepository<InspecaoPneu, Long> {

    List<InspecaoPneu> findByPneuIdAndDataInspecaoBetween(Long pneuId, LocalDateTime inicio, LocalDateTime fim);

    Page<InspecaoPneu> findByPneuIdOrderByDataInspecaoDesc(Long pneuId, Pageable pageable);

    Optional<InspecaoPneu> findFirstByAlocacaoIdOrderByDataInspecaoDesc(Long alocacaoId);

    List<InspecaoPneu> findTop10ByVeiculoIdOrderByDataInspecaoDesc(Long veiculoId);

    @Query("SELECT i FROM InspecaoPneu i WHERE i.pneu.cliente.id = :clienteId")
    Page<InspecaoPneu> findByClienteId(@Param("clienteId") Long clienteId, Pageable pageable);
}
