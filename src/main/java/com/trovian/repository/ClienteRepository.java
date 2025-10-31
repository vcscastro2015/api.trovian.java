package com.trovian.repository;

import com.trovian.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Page<Cliente> findByCooperativaId(Long cooperativaId, Pageable pageable);
    Optional<Cliente> findByUuid(UUID uuid);
    Optional<Cliente> findByCnpjCpf(String cnpjCpf);
}
