package com.trovian.repository;

import com.trovian.entity.TransmissaoBasica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransmissaoBasicaRepository extends JpaRepository<TransmissaoBasica, Long> {

    Page<TransmissaoBasica> findByVeiculoId(Long veiculoId, Pageable pageable);

    Optional<TransmissaoBasica> findTopByVeiculoIdOrderByDataTransmissaoDesc(Long veiculoId);

    Page<TransmissaoBasica> findByVeiculoIdAndDataTransmissaoBetween(
            Long veiculoId, Date dataInicial, Date dataFinal, Pageable pageable);

    Page<TransmissaoBasica> findByVeiculoIdInAndDataTransmissaoBetween(
            List<Long> veiculoIds, Date dataInicial, Date dataFinal, Pageable pageable);

    @Query("SELECT t FROM TransmissaoBasica t WHERE t.veiculo.id IN :veiculoIds AND t.dataTransmissao = " +
           "(SELECT MAX(t2.dataTransmissao) FROM TransmissaoBasica t2 WHERE t2.veiculo.id = t.veiculo.id)")
    List<TransmissaoBasica> findUltimasByVeiculoIds(@Param("veiculoIds") List<Long> veiculoIds);
}
