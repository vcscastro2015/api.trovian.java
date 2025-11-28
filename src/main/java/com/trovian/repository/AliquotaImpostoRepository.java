package com.trovian.repository;

import com.trovian.entity.AliquotaImposto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AliquotaImpostoRepository extends JpaRepository<AliquotaImposto, Long> {
}
