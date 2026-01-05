package com.trovian.repository;

import com.trovian.entity.Manutencao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ManutencaoRepository extends JpaRepository<Manutencao, Long> {
    
    @Query("SELECT COALESCE(SUM(m.valorTotal), 0) FROM Manutencao m " +
           "WHERE m.veiculo.id = :veiculoId " +
           "AND m.dataManutencao >= :dataInicio")
    Optional<BigDecimal> getCustoTotalPorVeiculo(
        @Param("veiculoId") Long veiculoId, 
        @Param("dataInicio") LocalDate dataInicio
    );
    
    @Query("SELECT COALESCE(SUM(m.valorTotal), 0) FROM Manutencao m " +
           "WHERE m.veiculo.cliente.id = :clienteId " +
           "AND m.dataManutencao >= :dataInicio")
    Optional<BigDecimal> getCustoTotalPorCliente(
        @Param("clienteId") Long clienteId, 
        @Param("dataInicio") LocalDate dataInicio
    );
    
    @Query("SELECT CASE " +
           "WHEN COUNT(m) > 0 AND MAX(m.dataManutencao) >= :dataLimite THEN 'Em dia' " +
           "WHEN COUNT(m) > 0 THEN 'Próxima' " +
           "ELSE 'Atrasada' END " +
           "FROM Manutencao m " +
           "WHERE m.veiculo.id = :veiculoId")
    Optional<String> getStatusManutencao(@Param("dataLimite") LocalDate dataLimite, @Param("veiculoId") Long veiculoId);
}

