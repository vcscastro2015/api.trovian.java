package com.trovian.repository;

import com.trovian.entity.OrdemServico;
import com.trovian.enums.StatusOrdemServico;
import com.trovian.enums.TipoManutencao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    Optional<OrdemServico> findByNumeroOs(String numeroOs);

    Page<OrdemServico> findByStatus(StatusOrdemServico status, Pageable pageable);

    Page<OrdemServico> findByTipoManutencao(TipoManutencao tipoManutencao, Pageable pageable);

    Page<OrdemServico> findByVeiculoId(Long veiculoId, Pageable pageable);

    Page<OrdemServico> findByMotoristaId(Long motoristaId, Pageable pageable);

    @Query("SELECT os FROM OrdemServico os WHERE os.veiculo.id = :veiculoId AND os.status IN :statuses")
    List<OrdemServico> findByVeiculoIdAndStatusIn(@Param("veiculoId") Long veiculoId, @Param("statuses") List<StatusOrdemServico> statuses);

    @Query("SELECT os FROM OrdemServico os WHERE os.dataAbertura BETWEEN :inicio AND :fim")
    Page<OrdemServico> findByDataAberturaBetween(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim, Pageable pageable);

    @Query("SELECT os FROM OrdemServico os WHERE os.dataPrevista <= :data AND os.status NOT IN ('CONCLUIDA', 'CANCELADA')")
    List<OrdemServico> findOrdensAtrasadas(@Param("data") LocalDate data);
}
