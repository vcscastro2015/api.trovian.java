package com.trovian.repository;

import com.trovian.entity.Fornecedor;
import com.trovian.enums.TipoFornecedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

    Page<Fornecedor> findByStatus(Boolean status, Pageable pageable);

    Page<Fornecedor> findByTipo(TipoFornecedor tipo, Pageable pageable);

    Page<Fornecedor> findByRazaoSocialContainingIgnoreCase(String razaoSocial, Pageable pageable);

    Optional<Fornecedor> findByCnpjCpf(String cnpjCpf);

    boolean existsByCnpjCpf(String cnpjCpf);
}
