package com.trovian.repository;

import com.trovian.entity.Motorista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para operações de persistência de Motorista
 */
@Repository
public interface MotoristaRepository extends JpaRepository<Motorista, Long> {

    Page<Motorista> findByClienteId(Long clienteId, Pageable pageable);
    Integer countByClienteIdAndStatusTrue(Long clienteId);
    Optional<Motorista> findByTelefone(String telefone);
}
