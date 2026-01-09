package com.trovian.repository;

import com.trovian.entity.ChecklistRealizado;
import com.trovian.enums.StatusChecklist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChecklistRealizadoRepository extends JpaRepository<ChecklistRealizado, UUID> {

    Page<ChecklistRealizado> findByVeiculoId(Long veiculoId, Pageable pageable);

    Page<ChecklistRealizado> findByMotoristaId(Long motoristaId, Pageable pageable);

    List<ChecklistRealizado> findByViagemId(Long viagemId);

    @Query("SELECT c FROM ChecklistRealizado c WHERE c.veiculo.id = :veiculoId " +
           "AND c.status = :status ORDER BY c.dataHoraInicio DESC")
    Optional<ChecklistRealizado> findUltimoChecklistPorVeiculoEStatus(
        @Param("veiculoId") Long veiculoId,
        @Param("status") StatusChecklist status
    );

    @Query("SELECT c FROM ChecklistRealizado c WHERE c.veiculo.id = :veiculoId " +
           "AND c.dataHoraInicio >= :dataInicio ORDER BY c.dataHoraInicio DESC")
    List<ChecklistRealizado> findChecklistsRecentesPorVeiculo(
        @Param("veiculoId") Long veiculoId,
        @Param("dataInicio") LocalDateTime dataInicio
    );

    Page<ChecklistRealizado> findByStatus(StatusChecklist status, Pageable pageable);

    Long countByStatus(StatusChecklist status);
}
