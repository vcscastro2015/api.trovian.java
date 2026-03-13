package com.trovian.repository;

import com.trovian.entity.HistoricoPagamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HistoricoPagamentoRepository extends JpaRepository<HistoricoPagamento, Long> {

    Page<HistoricoPagamento> findByClientePlanoId(Long clientePlanoId, Pageable pageable);

    Optional<HistoricoPagamento> findByUuid(UUID uuid);
}
