package com.trovian.repository;

import com.trovian.entity.Motorista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para operações de persistência de Motorista
 */
@Repository
public interface MotoristaRepository extends JpaRepository<Motorista, Long> {

    /**
     * Busca motoristas por ID do cliente com paginação
     *
     * @param clienteId ID do cliente
     * @param pageable  Configuração de paginação
     * @return Página de motoristas
     */
    Page<Motorista> findByClienteId(Long clienteId, Pageable pageable);

    /**
     * Conta motoristas ativos por cliente
     *
     * @param clienteId ID do cliente
     * @return Quantidade de motoristas ativos
     */
    Integer countByClienteIdAndStatusTrue(Long clienteId);
}
