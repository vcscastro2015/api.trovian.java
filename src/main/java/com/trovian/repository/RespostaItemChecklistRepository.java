package com.trovian.repository;

import com.trovian.entity.RespostaItemChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RespostaItemChecklistRepository extends JpaRepository<RespostaItemChecklist, UUID> {

    List<RespostaItemChecklist> findByChecklistRealizadoId(UUID checklistRealizadoId);

    List<RespostaItemChecklist> findByChecklistRealizadoIdAndRequerAtencaoTrue(UUID checklistRealizadoId);
}
