package com.trovian.repository;

import com.trovian.entity.Cliente;
import com.trovian.entity.Fornecedor;
import com.trovian.enums.TipoFornecedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

    Page<Fornecedor> findByStatus(Boolean status, Pageable pageable);

    Page<Fornecedor> findByTipo(TipoFornecedor tipo, Pageable pageable);

    Page<Fornecedor> findByRazaoSocialContainingIgnoreCase(String razaoSocial, Pageable pageable);

    Optional<Fornecedor> findByCnpjCpf(String cnpjCpf);

    Optional<Fornecedor> findByCnpjCpfAndCliente(String cnpjCpf, Cliente cliente);

    boolean existsByCnpjCpf(String cnpjCpf);

    boolean existsByCnpjCpfAndClienteId(String cnpjCpf, Long clienteId);

    Page<Fornecedor> findByClienteId(Long clienteId, Pageable pageable);

    @Query("SELECT f FROM Fornecedor f WHERE f.razaoSocial = 'FORNECEDOR PENDENTE (SISTEMA)'")
    Optional<Fornecedor> buscarFornecedorPendente();

    @Query("SELECT f FROM Fornecedor f WHERE f.razaoSocial = 'Cliente Trovian' AND f.cliente.id = 1")
    Optional<Fornecedor> buscarFornecedorTrovian();

}
