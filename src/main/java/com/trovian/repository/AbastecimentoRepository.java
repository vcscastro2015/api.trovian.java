package com.trovian.repository;

import com.trovian.entity.Abastecimento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbastecimentoRepository extends JpaRepository<Abastecimento, Long> {

    /**
     * Busca abastecimentos por veículo com paginação
     */
    Page<Abastecimento> findByVeiculoId(Long veiculoId, Pageable pageable);

    /**
     * Busca abastecimentos por motorista com paginação
     */
    Page<Abastecimento> findByMotoristaId(Long motoristaId, Pageable pageable);

    /**
     * Busca abastecimentos por cliente com paginação
     */
    Page<Abastecimento> findByClienteId(Long clienteId, Pageable pageable);

    /**
     * Busca abastecimentos por rota com paginação
     */
    Page<Abastecimento> findByRotaId(Long rotaId, Pageable pageable);

    /**
     * Busca abastecimentos por status com paginação
     */
    Page<Abastecimento> findByStatus(Boolean status, Pageable pageable);
}
