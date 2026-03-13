package com.trovian.repository;

import com.trovian.entity.ClientePlano;
import com.trovian.enums.StatusClientePlano;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientePlanoRepository extends JpaRepository<ClientePlano, Long> {

    Page<ClientePlano> findByClienteId(Long clienteId, Pageable pageable);

    Page<ClientePlano> findByStatus(StatusClientePlano status, Pageable pageable);

    Optional<ClientePlano> findByUuid(UUID uuid);
}
