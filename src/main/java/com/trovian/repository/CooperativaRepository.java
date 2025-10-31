package com.trovian.repository;

import com.trovian.entity.Cooperativa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Busca cooperativas por nome com paginação
     */
    Page<Cooperativa> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    /**
     * Busca cooperativa por CNPJ
     */
    Optional<Cooperativa> findByCnpj(String cnpj);

    /**
     * Busca cooperativas por cidade
     */
    List<Cooperativa> findByCidadeIgnoreCase(String cidade);

    /**
     * Busca cooperativas por cidade com paginação
     */
    Page<Cooperativa> findByCidadeIgnoreCase(String cidade, Pageable pageable);

    /**
     * Busca cooperativas por UF
     */
    List<Cooperativa> findByUfIgnoreCase(String uf);

    /**
     * Busca cooperativas por UF com paginação
     */
    Page<Cooperativa> findByUfIgnoreCase(String uf, Pageable pageable);

    /**
     * Busca cooperativas ativas ou inativas
     */
    List<Cooperativa> findByAtiva(Boolean ativa);

    /**
     * Busca cooperativas ativas ou inativas com paginação
     */
    Page<Cooperativa> findByAtiva(Boolean ativa, Pageable pageable);

    /**
     * Busca cooperativas por cidade e UF
     */
    List<Cooperativa> findByCidadeIgnoreCaseAndUfIgnoreCase(String cidade, String uf);

    /**
     * Busca cooperativas por cidade e UF com paginação
     */
    Page<Cooperativa> findByCidadeIgnoreCaseAndUfIgnoreCase(String cidade, String uf, Pageable pageable);
}
