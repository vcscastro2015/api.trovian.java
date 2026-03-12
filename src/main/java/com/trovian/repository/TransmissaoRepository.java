package com.trovian.repository;

import com.trovian.entity.Transmissao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransmissaoRepository extends JpaRepository<Transmissao, Long> {

    Page<Transmissao> findByVeiculoId(Long veiculoId, Pageable pageable);

    Optional<Transmissao> findTopByVeiculoIdOrderByDataTransmissaoDesc(Long veiculoId);

    Page<Transmissao> findByVeiculoIdAndDataTransmissaoBetween(
            Long veiculoId, Date dataInicial, Date dataFinal, Pageable pageable);

    Page<Transmissao> findByVeiculoIdInAndDataTransmissaoBetween(
            List<Long> veiculoIds, Date dataInicial, Date dataFinal, Pageable pageable);
}
