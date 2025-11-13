package com.trovian.repository;

import com.trovian.entity.Rota;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para operações de persistência da entidade Rota
 */
@Repository
public interface RotaRepository extends JpaRepository<Rota, Long> {

    /**
     * Busca rota por ID com fetch de relacionamentos
     */
    @Query("SELECT r FROM Rota r " +
           "LEFT JOIN FETCH r.pontos " +
           "LEFT JOIN FETCH r.estatisticas " +
           "LEFT JOIN FETCH r.segmentos " +
           "LEFT JOIN FETCH r.pedagios " +
           "WHERE r.id = :id")
    Optional<Rota> findByIdWithDetails(@Param("id") Long id);

    /**
     * Busca rotas por nome (parcial, case-insensitive)
     */
    Page<Rota> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    /**
     * Busca rotas por cliente
     */
    @Query("SELECT r FROM Rota r WHERE r.cliente.id = :clienteId")
    Page<Rota> findByClienteId(@Param("clienteId") Long clienteId, Pageable pageable);

    /**
     * Busca rotas por veículo
     */
    @Query("SELECT r FROM Rota r WHERE r.veiculo.id = :veiculoId")
    Page<Rota> findByVeiculoId(@Param("veiculoId") Long veiculoId, Pageable pageable);

    /**
     * Busca rotas por cooperativa
     */
    @Query("SELECT r FROM Rota r WHERE r.cooperativa.id = :cooperativaId")
    Page<Rota> findByCooperativaId(@Param("cooperativaId") Long cooperativaId, Pageable pageable);

    /**
     * Busca rotas por status ativo
     */
    Page<Rota> findByAtiva(Boolean ativa, Pageable pageable);

    /**
     * Busca rotas por faixa de distância
     */
    @Query("SELECT r FROM Rota r WHERE r.distanciaTotal BETWEEN :min AND :max")
    Page<Rota> findByDistanciaTotalBetween(@Param("min") Double distanciaMinima,
                                            @Param("max") Double distanciaMaxima,
                                            Pageable pageable);

    /**
     * Busca rotas por dificuldade
     */
    @Query("SELECT r FROM Rota r JOIN r.estatisticas e WHERE e.dificuldade = :dificuldade")
    Page<Rota> findByDificuldade(@Param("dificuldade") String dificuldade, Pageable pageable);

    /**
     * Busca rotas com filtros múltiplos (query dinâmica)
     */
    @Query("SELECT DISTINCT r FROM Rota r " +
           "LEFT JOIN r.estatisticas e " +
           "WHERE (:nome IS NULL OR LOWER(r.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
           "AND (:clienteId IS NULL OR r.cliente.id = :clienteId) " +
           "AND (:veiculoId IS NULL OR r.veiculo.id = :veiculoId) " +
           "AND (:cooperativaId IS NULL OR r.cooperativa.id = :cooperativaId) " +
           "AND (:ativa IS NULL OR r.ativa = :ativa) " +
           "AND (:distanciaMinima IS NULL OR r.distanciaTotal >= :distanciaMinima) " +
           "AND (:distanciaMaxima IS NULL OR r.distanciaTotal <= :distanciaMaxima) " +
           "AND (:dificuldade IS NULL OR e.dificuldade = :dificuldade)")
    Page<Rota> findByFiltros(@Param("nome") String nome,
                              @Param("clienteId") Long clienteId,
                              @Param("veiculoId") Long veiculoId,
                              @Param("cooperativaId") Long cooperativaId,
                              @Param("ativa") Boolean ativa,
                              @Param("distanciaMinima") Double distanciaMinima,
                              @Param("distanciaMaxima") Double distanciaMaxima,
                              @Param("dificuldade") String dificuldade,
                              Pageable pageable);
}
