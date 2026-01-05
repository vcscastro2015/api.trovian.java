package com.trovian.repository;

import com.trovian.entity.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    Page<Veiculo> findByClienteId(Long clienteId, Pageable pageable);

    Integer countByClienteIdAndStatusTrue(Long clienteId);
}
