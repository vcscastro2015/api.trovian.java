package com.trovian.repository;

import com.trovian.entity.ComissaoMotorista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComissaoMotoristaRepository extends JpaRepository<ComissaoMotorista, Long> {

    Page<ComissaoMotorista> findAll(Pageable pageable);

    List<ComissaoMotorista> findByClienteId(Long clienteId);

    Page<ComissaoMotorista> findByClienteId(Long clienteId, Pageable pageable);
}
