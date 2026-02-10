package com.trovian.repository;

import com.trovian.entity.ComissaoMotorista;
import com.trovian.entity.ComissaoMotorista.StatusComissao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComissaoMotoristaRepository extends JpaRepository<ComissaoMotorista, Long> {

    Page<ComissaoMotorista> findAll(Pageable pageable);

    List<ComissaoMotorista> findByClienteId(Long clienteId);

    Optional<ComissaoMotorista> findByViagemId(Long viagemId);

    Page<ComissaoMotorista> findByClienteId(Long clienteId, Pageable pageable);

    Page<ComissaoMotorista> findByMotoristaId(Long motoristaId, Pageable pageable);

    Page<ComissaoMotorista> findByStatus(StatusComissao status, Pageable pageable);

    List<ComissaoMotorista> findByMotoristaIdAndCreatedAtBetween(
            Long motoristaId,
            LocalDateTime dataInicial,
            LocalDateTime dataFinal
    );

    List<ComissaoMotorista> findByMotoristaIdAndCreatedAtBetweenAndStatus(
            Long motoristaId,
            LocalDateTime dataInicial,
            LocalDateTime dataFinal,
            StatusComissao status
    );

    @Query("SELECT COALESCE(SUM(c.valorComissao), 0) FROM ComissaoMotorista c WHERE c.cliente.id = :clienteId AND c.createdAt BETWEEN :dataInicial AND :dataFinal")
    BigDecimal sumTotalComissoesPorClienteEPeriodo(
            @Param("clienteId") Long clienteId,
            @Param("dataInicial") LocalDateTime dataInicial,
            @Param("dataFinal") LocalDateTime dataFinal
    );
}
