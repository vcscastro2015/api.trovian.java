package com.trovian.repository;

import com.trovian.entity.Modelo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModeloRepository extends JpaRepository<Modelo, Long> {

    Page<Modelo> findByTipoIgnoreCase(String tipo, Pageable pageable);
}
