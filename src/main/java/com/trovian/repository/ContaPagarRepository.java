package com.trovian.repository;

import com.trovian.entity.ContaPagar;
import com.trovian.enums.StatusConta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface ContaPagarRepository extends JpaRepository<ContaPagar, Long> {

    Page<ContaPagar> findByStatus(StatusConta status, Pageable pageable);

    Page<ContaPagar> findByFornecedorId(Long fornecedorId, Pageable pageable);

    Page<ContaPagar> findByCategoriaId(Long categoriaId, Pageable pageable);

    Page<ContaPagar> findByCentroCustoId(Long centroCustoId, Pageable pageable);

    Page<ContaPagar> findByClienteId(Long clienteId, Pageable pageable);

    Page<ContaPagar> findByVeiculoId(Long veiculoId, Pageable pageable);

    Page<ContaPagar> findByMotoristaId(Long motoristaId, Pageable pageable);

    Page<ContaPagar> findByDataVencimentoBetween(
        LocalDate dataInicio,
        LocalDate dataFim,
        Pageable pageable
    );

    Page<ContaPagar> findByDataEmissaoBetween(
        LocalDate dataInicio,
        LocalDate dataFim,
        Pageable pageable
    );

    @Query("SELECT c FROM ContaPagar c WHERE c.status IN ('PENDENTE', 'PARCIAL') AND c.dataVencimento < :hoje")
    Page<ContaPagar> findVencidas(@Param("hoje") LocalDate hoje, Pageable pageable);

    @Query("SELECT c FROM ContaPagar c WHERE c.status IN ('PENDENTE', 'PARCIAL') AND c.dataVencimento BETWEEN :hoje AND :dataFutura")
    Page<ContaPagar> findAVencer(
        @Param("hoje") LocalDate hoje,
        @Param("dataFutura") LocalDate dataFutura,
        Pageable pageable
    );

    Page<ContaPagar> findByDescricaoContainingIgnoreCase(String descricao, Pageable pageable);

    @Query("SELECT SUM(c.valorTotal) FROM ContaPagar c WHERE c.status = :status AND c.cliente.id = :clienteId")
    BigDecimal sumTotalByStatus(@Param("status") StatusConta status, @Param("clienteId") Long clientId);

    @Query("SELECT SUM(c.valorTotal - c.valorPago) FROM ContaPagar c WHERE c.status IN ('PENDENTE', 'PARCIAL') AND c.cliente.id = :clienteId")
    BigDecimal sumSaldoAPagar(@Param("clienteId") Long clientId);

    @Query("SELECT SUM(c.valorTotal) FROM ContaPagar c WHERE c.dataVencimento BETWEEN :dataInicio AND :dataFim")
    BigDecimal sumTotalPorPeriodo(
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );

    @Query(value = "SELECT nextval('seq_contas_pagar_diaria')", nativeQuery = true)
    Long nextNumeroControle();
}
