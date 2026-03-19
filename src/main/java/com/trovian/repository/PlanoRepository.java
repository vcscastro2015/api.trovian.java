package com.trovian.repository;

import com.trovian.entity.Plano;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanoRepository extends JpaRepository<Plano, Long> {

    Page<Plano> findByAtivo(Boolean ativo, Pageable pageable);

    Optional<Plano> findByUuid(UUID uuid);
}
