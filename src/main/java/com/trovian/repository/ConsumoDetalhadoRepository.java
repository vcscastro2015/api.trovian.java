package com.trovian.repository;

import com.trovian.entity.ConsumoDetalhado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para ConsumoDetalhado
 */
@Repository
public interface ConsumoDetalhadoRepository extends JpaRepository<ConsumoDetalhado, Long> {
}
