package com.trovian.repository;

import com.trovian.entity.relatorios.CategoriaRelatorio;
import com.trovian.entity.relatorios.RelatorioTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelatorioTemplateRepository extends JpaRepository<RelatorioTemplate, Long> {
    List<RelatorioTemplate> findByAtivoTrue();
    List<RelatorioTemplate> findByCategoriaAndAtivoTrue(CategoriaRelatorio categoria);
    List<RelatorioTemplate> findBySistemaTemplateTrue();
}
