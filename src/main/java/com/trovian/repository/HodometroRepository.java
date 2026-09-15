package com.trovian.repository;

import com.trovian.entity.Hodometro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HodometroRepository extends JpaRepository<Hodometro, Long> {

    Page<Hodometro> findByVeiculoId(Long veiculoId, Pageable pageable);

    List<Hodometro> findByVeiculoIdAndDataCadastroBetweenOrderByDataCadastroDesc(
            Long veiculoId, OffsetDateTime dataInicial, OffsetDateTime dataFinal);

    Optional<Hodometro> findFirstByVeiculoIdOrderByDataCadastroDesc(Long veiculoId);
}
