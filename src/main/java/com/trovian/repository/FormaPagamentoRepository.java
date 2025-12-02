package com.trovian.repository;

import com.trovian.entity.FormaPagamento;
import com.trovian.enums.TipoFormaPagamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormaPagamentoRepository extends JpaRepository<FormaPagamento, Long> {

    Page<FormaPagamento> findByStatus(Boolean status, Pageable pageable);

    Page<FormaPagamento> findByTipo(TipoFormaPagamento tipo, Pageable pageable);

    Page<FormaPagamento> findByPermiteParcelamento(Boolean permiteParcelamento, Pageable pageable);
}
