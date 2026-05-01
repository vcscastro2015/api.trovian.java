package com.trovian.repository;

import com.trovian.entity.Hodometro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HodometroRepository extends JpaRepository<Hodometro, Integer> {

    Page<Hodometro> findByVeiculoId(Long veiculoId, Pageable pageable);

    List<Hodometro> findByVeiculoIdAndDataCadastroBetweenOrderByDataCadastroDesc(
            Long veiculoId, Date dataInicial, Date dataFinal);
}
