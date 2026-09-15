package com.trovian.repository;

import com.trovian.entity.relatorios.RelatorioGerado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface RelatorioGeradoRepository extends JpaRepository<RelatorioGerado, Long> {
    List<RelatorioGerado> findByUsuarioIdOrderByGeneratedAtDesc(Long usuarioId);
    List<RelatorioGerado> findByGeneratedAtBetween(OffsetDateTime inicio, OffsetDateTime fim);
}
