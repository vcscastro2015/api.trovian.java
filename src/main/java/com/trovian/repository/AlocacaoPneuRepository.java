package com.trovian.repository;

import com.trovian.entity.AlocacaoPneu;
import com.trovian.enums.PosicaoPneu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlocacaoPneuRepository extends JpaRepository<AlocacaoPneu, Long> {

    @Query("SELECT a FROM AlocacaoPneu a JOIN FETCH a.pneu WHERE a.veiculo.id = :veiculoId AND a.dataRemocao IS NULL")
    List<AlocacaoPneu> findAlocacoesAtivasByVeiculoId(@Param("veiculoId") Long veiculoId);

    Page<AlocacaoPneu> findByPneuIdOrderByDataMontagem(Long pneuId, Pageable pageable);

    @Query("SELECT COUNT(a) > 0 FROM AlocacaoPneu a WHERE a.veiculo.id = :veiculoId AND a.posicao = :posicao AND a.dataRemocao IS NULL")
    boolean existsAlocacaoAtivaByVeiculoIdAndPosicao(@Param("veiculoId") Long veiculoId, @Param("posicao") PosicaoPneu posicao);

    @Query("SELECT a FROM AlocacaoPneu a WHERE a.pneu.id = :pneuId AND a.dataRemocao IS NULL")
    Optional<AlocacaoPneu> findAlocacaoAtivaByPneuId(@Param("pneuId") Long pneuId);
}
