package com.trovian.repository;

import com.trovian.entity.ItemManutencao;
import com.trovian.enums.TipoItemManutencao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemManutencaoRepository extends JpaRepository<ItemManutencao, Long> {

    List<ItemManutencao> findByOrdemServicoId(Long ordemServicoId);

    Page<ItemManutencao> findByTipo(TipoItemManutencao tipo, Pageable pageable);

    Page<ItemManutencao> findByFornecedorId(Long fornecedorId, Pageable pageable);

    @Query("SELECT im FROM ItemManutencao im WHERE im.ordemServico.veiculo.id = :veiculoId")
    List<ItemManutencao> findByVeiculoId(@Param("veiculoId") Long veiculoId);

    @Query("SELECT im FROM ItemManutencao im WHERE im.descricao LIKE %:descricao%")
    Page<ItemManutencao> findByDescricaoContaining(@Param("descricao") String descricao, Pageable pageable);
}
