package com.trovian.repository;

import com.trovian.entity.Transmissao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface TransmissaoRepository extends JpaRepository<Transmissao, Long> {

    Page<Transmissao> findByVeiculoId(Long veiculoId, Pageable pageable);

    Optional<Transmissao> findTopByVeiculoIdOrderByDataTransmissaoDesc(Long veiculoId);

    Page<Transmissao> findByVeiculoIdAndDataTransmissaoBetween(
            Long veiculoId, OffsetDateTime dataInicial, OffsetDateTime dataFinal, Pageable pageable);

    Page<Transmissao> findByVeiculoIdInAndDataTransmissaoBetween(
            List<Long> veiculoIds, OffsetDateTime dataInicial, OffsetDateTime dataFinal, Pageable pageable);
}
