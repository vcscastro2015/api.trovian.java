package com.trovian.repository;

import com.trovian.entity.CategoriaConta;
import com.trovian.entity.Cliente;
import com.trovian.enums.TipoConta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaContaRepository extends JpaRepository<CategoriaConta, Long> {

    Page<CategoriaConta> findByStatus(Boolean status, Pageable pageable);

    Page<CategoriaConta> findByTipo(TipoConta tipo, Pageable pageable);

    List<CategoriaConta> findByTipo(TipoConta tipo);

    Page<CategoriaConta> findByTipoAndClienteId(TipoConta tipo, Long clienteId, Pageable pageable);

    Page<CategoriaConta> findByCategoriaPaiId(Long categoriaPaiId, Pageable pageable);

    Page<CategoriaConta> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Page<CategoriaConta> findByClienteId(Long clienteId, Pageable pageable);

    Optional<CategoriaConta> findByClienteAndCodigo(Cliente cliente, String codigo);

    Optional<CategoriaConta> findByCodigo(String codigo);
}
