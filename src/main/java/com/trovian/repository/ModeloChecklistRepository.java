package com.trovian.repository;

import com.trovian.entity.ModeloChecklist;
import com.trovian.enums.TipoChecklist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModeloChecklistRepository extends JpaRepository<ModeloChecklist, UUID> {

    Page<ModeloChecklist> findByClienteId(Long clienteId, Pageable pageable);

    List<ModeloChecklist> findByClienteIdAndAtivoTrue(Long clienteId);

    List<ModeloChecklist> findByClienteIdAndTipo(Long clienteId, TipoChecklist tipo);

    List<ModeloChecklist> findByClienteIdAndTipoAndAtivoTrue(Long clienteId, TipoChecklist tipo);
}
