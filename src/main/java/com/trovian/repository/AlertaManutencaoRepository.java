package com.trovian.repository;

import com.trovian.entity.AlertaManutencao;
import com.trovian.enums.PrioridadeAlerta;
import com.trovian.enums.TipoAlerta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaManutencaoRepository extends JpaRepository<AlertaManutencao, Long> {

    Page<AlertaManutencao> findByVeiculoId(Long veiculoId, Pageable pageable);

    Page<AlertaManutencao> findByLido(Boolean lido, Pageable pageable);

    Page<AlertaManutencao> findByResolvido(Boolean resolvido, Pageable pageable);

    Page<AlertaManutencao> findByTipoAlerta(TipoAlerta tipoAlerta, Pageable pageable);

    Page<AlertaManutencao> findByPrioridade(PrioridadeAlerta prioridade, Pageable pageable);

    @Query("SELECT a FROM AlertaManutencao a WHERE a.lido = false AND a.resolvido = false AND a.cliente.id = :cliente ORDER BY a.prioridade DESC, a.dataGeracao ASC")
    List<AlertaManutencao> findAlertasNaoLidosNaoResolvidos(@Param("cliente") Long clienteId);

    @Query("SELECT a FROM AlertaManutencao a WHERE a.veiculo.id = :veiculoId AND a.resolvido = false")
    List<AlertaManutencao> findAlertasNaoResolvidosPorVeiculo(@Param("veiculoId") Long veiculoId);

    @Query("SELECT a FROM AlertaManutencao a WHERE a.prioridade = 'CRITICA' AND a.resolvido = false AND a.cliente.id = :cliente")
    List<AlertaManutencao> findAlertasCriticosNaoResolvidos(@Param("cliente") Long clienteId);

    @Query("SELECT COUNT(a) FROM AlertaManutencao a WHERE a.lido = false")
    Long countAlertasNaoLidos();

    Page<AlertaManutencao> findByClienteId(Long clienteId, Pageable pageable);
}
