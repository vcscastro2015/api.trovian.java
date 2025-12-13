package com.trovian.repository;

import com.trovian.entity.MovimentacaoEstoque;
import com.trovian.enums.TipoMovimentacaoEstoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    Page<MovimentacaoEstoque> findByPecaId(Long pecaId, Pageable pageable);

    Page<MovimentacaoEstoque> findByOrdemServicoId(Long ordemServicoId, Pageable pageable);

    Page<MovimentacaoEstoque> findByTipoMovimentacao(TipoMovimentacaoEstoque tipoMovimentacao, Pageable pageable);

    @Query("SELECT m FROM MovimentacaoEstoque m WHERE m.dataMovimentacao BETWEEN :inicio AND :fim")
    Page<MovimentacaoEstoque> findByDataMovimentacaoBetween(
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim,
        Pageable pageable
    );

    @Query("SELECT m FROM MovimentacaoEstoque m WHERE m.peca.id = :pecaId ORDER BY m.dataMovimentacao DESC")
    List<MovimentacaoEstoque> findHistoricoByPecaId(@Param("pecaId") Long pecaId);

    @Query("SELECT m FROM MovimentacaoEstoque m WHERE m.usuario = :usuario")
    Page<MovimentacaoEstoque> findByUsuario(@Param("usuario") String usuario, Pageable pageable);
}
