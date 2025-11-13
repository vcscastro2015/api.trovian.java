package com.trovian.repository;

import com.trovian.entity.Local;
import com.trovian.enums.TipoLocal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalRepository extends JpaRepository<Local, Long> {

    /**
     * Busca locais por cliente com paginação
     */
    Page<Local> findByClienteId(Long clienteId, Pageable pageable);

    /**
     * Busca locais por tipo com paginação
     */
    Page<Local> findByTipo(TipoLocal tipo, Pageable pageable);
}
