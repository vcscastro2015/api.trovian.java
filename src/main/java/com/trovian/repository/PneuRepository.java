package com.trovian.repository;

import com.trovian.entity.Pneu;
import com.trovian.enums.StatusPneu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PneuRepository extends JpaRepository<Pneu, Long> {

    Page<Pneu> findByClienteId(Long clienteId, Pageable pageable);

    Page<Pneu> findByClienteIdAndStatus(Long clienteId, StatusPneu status, Pageable pageable);

    Optional<Pneu> findByNumeroDotAndClienteId(String numeroDot, Long clienteId);

    @Query("SELECT p FROM Pneu p JOIN AlocacaoPneu a ON a.pneu = p WHERE a.veiculo.id = :veiculoId AND a.dataRemocao IS NULL")
    List<Pneu> findPneuAtualByVeiculo(@Param("veiculoId") Long veiculoId);
}
