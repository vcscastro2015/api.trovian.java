package com.trovian.repository;

import com.trovian.entity.ContaReceber;
import com.trovian.enums.StatusConta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContaReceberRepository extends JpaRepository<ContaReceber, Long> {

    Optional<ContaReceber> findByViagemId(Long viagemId);

    List<ContaReceber> findByClientePlanoIdOrderByDataVencimentoAsc(Long clientePlanoId);

    Page<ContaReceber> findByStatus(StatusConta status, Pageable pageable);

    Page<ContaReceber> findByClienteId(Long clienteId, Pageable pageable);

    Page<ContaReceber> findByVeiculoId(Long veiculoId, Pageable pageable);

    Page<ContaReceber> findByMotoristaId(Long motoristaId, Pageable pageable);

    Page<ContaReceber> findByDataVencimentoBetweenAndClienteId(
        LocalDate dataInicio,
        LocalDate dataFim,
        Long clienteId,
        Pageable pageable
    );

    Page<ContaReceber> findByDataVencimentoBetween(
            LocalDate dataInicio,
            LocalDate dataFim,
            Pageable pageable
    );

    @Query("SELECT c FROM ContaReceber c WHERE c.status IN ('PENDENTE', 'PARCIAL') AND c.dataVencimento < :hoje")
    Page<ContaReceber> findVencidas(@Param("hoje") LocalDate hoje, Pageable pageable);

    @Query("SELECT c FROM ContaReceber c WHERE c.status IN ('PENDENTE', 'PARCIAL') AND c.dataVencimento BETWEEN :hoje AND :dataFutura")
    Page<ContaReceber> findAVencer(
        @Param("hoje") LocalDate hoje,
        @Param("dataFutura") LocalDate dataFutura,
        Pageable pageable
    );

    @Query("SELECT SUM(c.valorTotal) FROM ContaReceber c WHERE c.status = :status AND c.cliente.id = :clienteId")
    BigDecimal sumTotalByStatus(@Param("status") StatusConta status, @Param("clienteId") Long clientId);

    @Query("SELECT SUM(c.valorTotal - c.valorRecebido) FROM ContaReceber c WHERE c.status IN ('PENDENTE', 'PARCIAL') AND c.cliente.id = :clienteId")
    BigDecimal sumSaldoAReceber(@Param("clienteId") Long clientId);

    @Query("SELECT COALESCE(SUM(c.valorTotal - c.valorRecebido), 0) FROM ContaReceber c WHERE c.status IN ('PENDENTE', 'PARCIAL') AND c.cliente.id = :clienteId AND c.dataEmissao BETWEEN :dataInicio AND :dataFim")
    BigDecimal sumTotalAReceberPorClienteEPeriodo(
        @Param("clienteId") Long clienteId,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );

    @Query(value = "SELECT nextval('seq_contas_receber_diaria')", nativeQuery = true)
    Long nextNumeroControle();

    // === Métodos para Relatórios Financeiros ===

    @Query("SELECT c.categoria.nome, SUM(c.valorRecebido), COUNT(c) " +
           "FROM ContaReceber c " +
           "WHERE c.dataRecebimento BETWEEN :dataInicio AND :dataFim " +
           "AND c.status = :status " +
           "AND (:clienteId IS NULL OR c.cliente.id = :clienteId) " +
           "AND (:centroCustoId IS NULL OR c.centroCusto.id = :centroCustoId) " +
           "GROUP BY c.categoria.nome " +
           "ORDER BY SUM(c.valorRecebido) DESC")
    List<Object[]> somarReceitasPorCategoria(
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim,
        @Param("clienteId") Long clienteId,
        @Param("centroCustoId") Long centroCustoId,
        @Param("status") StatusConta status
    );


    @Query("SELECT c FROM ContaReceber c " +
           " WHERE c.dataVencimento BETWEEN :dataInicio AND :dataFim " +
           " AND c.cliente.id = :clienteId AND c.status = :status")
    Page<ContaReceber> findByDataVencimentoBetweenAndStatusAndClienteId(
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim,
        @Param("clienteId") Long clienteId,
        @Param("status") StatusConta status,
        Pageable pageable
    );

}
