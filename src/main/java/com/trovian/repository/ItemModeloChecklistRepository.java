package com.trovian.repository;

import com.trovian.entity.ItemModeloChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemModeloChecklistRepository extends JpaRepository<ItemModeloChecklist, UUID> {

    List<ItemModeloChecklist> findByModeloChecklistIdOrderByOrdemAsc(UUID modeloChecklistId);
}
