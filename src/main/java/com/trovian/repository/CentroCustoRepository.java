package com.trovian.repository;

import com.trovian.entity.CentroCusto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CentroCustoRepository extends JpaRepository<CentroCusto, Long> {

    Page<CentroCusto> findByStatus(Boolean status, Pageable pageable);

    Page<CentroCusto> findByClienteId(Long clienteId, Pageable pageable);

    Page<CentroCusto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
