package com.trovian.repository;

import com.trovian.entity.Funcionalidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FuncionalidadeRepository extends JpaRepository<Funcionalidade, Long> {

    Optional<Funcionalidade> findByCodigo(String codigo);
}
