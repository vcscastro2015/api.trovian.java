package com.trovian.repository;

import com.trovian.entity.Cooperativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CooperativaRepository extends JpaRepository<Cooperativa, Long> {

    /**
     * Busca cooperativas por nome (case-insensitive, contém)
     */
    List<Cooperativa> findByNomeContainingIgnoreCase(String nome);

    /**
     * Busca cooperativa por CNPJ
     */
    Optional<Cooperativa> findByCnpj(String cnpj);

    /**
     * Busca cooperativas por cidade
     */
    List<Cooperativa> findByCidadeIgnoreCase(String cidade);

    /**
     * Busca cooperativas por UF
     */
    List<Cooperativa> findByUfIgnoreCase(String uf);

    /**
     * Busca cooperativas ativas ou inativas
     */
    List<Cooperativa> findByAtiva(Boolean ativa);

    /**
     * Busca cooperativas por cidade e UF
     */
    List<Cooperativa> findByCidadeIgnoreCaseAndUfIgnoreCase(String cidade, String uf);
}
