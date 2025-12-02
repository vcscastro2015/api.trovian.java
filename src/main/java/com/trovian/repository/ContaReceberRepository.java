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

@Repository
public interface ContaReceberRepository extends JpaRepository<ContaReceber, Long> {

    Page<ContaReceber> findByStatus(StatusConta status, Pageable pageable);

    Page<ContaReceber> findByClienteId(Long clienteId, Pageable pageable);

    Page<ContaReceber> findByCategoriaId(Long categoriaId, Pageable pageable);

    Page<ContaReceber> findByCentroCustoId(Long centroCustoId, Pageable pageable);

    Page<ContaReceber> findByVeiculoId(Long veiculoId, Pageable pageable);

    Page<ContaReceber> findByMotoristaId(Long motoristaId, Pageable pageable);

    Page<ContaReceber> findByDataVencimentoBetween(
        LocalDate dataInicio,
        LocalDate dataFim,
        Pageable pageable
    );

    Page<ContaReceber> findByDataEmissaoBetween(
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

    Page<ContaReceber> findByDescricaoContainingIgnoreCase(String descricao, Pageable pageable);

    Page<ContaReceber> findByOrigemFreteContainingIgnoreCaseOrDestinoFreteContainingIgnoreCase(
        String origem,
        String destino,
        Pageable pageable
    );

    @Query("SELECT SUM(c.valorTotal) FROM ContaReceber c WHERE c.status = :status")
    BigDecimal sumTotalByStatus(@Param("status") StatusConta status);

    @Query("SELECT SUM(c.valorTotal - c.valorRecebido) FROM ContaReceber c WHERE c.status IN ('PENDENTE', 'PARCIAL')")
    BigDecimal sumSaldoAReceber();

    @Query("SELECT SUM(c.valorTotal) FROM ContaReceber c WHERE c.dataVencimento BETWEEN :dataInicio AND :dataFim")
    BigDecimal sumTotalPorPeriodo(
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );
}
