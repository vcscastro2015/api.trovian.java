package com.trovian.repository;

import com.trovian.entity.CategoriaConta;
import com.trovian.enums.TipoConta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaContaRepository extends JpaRepository<CategoriaConta, Long> {

    Page<CategoriaConta> findByStatus(Boolean status, Pageable pageable);

    Page<CategoriaConta> findByTipo(TipoConta tipo, Pageable pageable);

    Page<CategoriaConta> findByCategoriaPaiId(Long categoriaPaiId, Pageable pageable);

    Page<CategoriaConta> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
