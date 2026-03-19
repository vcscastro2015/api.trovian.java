package com.trovian.repository;

import com.trovian.entity.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    Page<Veiculo> findByClienteId(Long clienteId, Pageable pageable);

    Integer countByClienteIdAndStatusTrue(Long clienteId);

    Optional<Veiculo> findByPlacaIgnoreCase(String placa);
}
