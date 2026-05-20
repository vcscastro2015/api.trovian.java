package com.trovian.repository;

import com.trovian.entity.Telemetria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TelemetriaRepository extends JpaRepository<Telemetria, Integer> {

    // -------------------------------------------------------------------------
    // 1. KPIs do período
    // Nota: green_driving_value é int4 no banco; AVG retorna numeric.
    // -------------------------------------------------------------------------
    @Query(value = """
            WITH agg AS (
              SELECT veiculo,
                     MAX(total_odometer) - MIN(total_odometer)                                        AS km,
                     MAX(combustivel_total_usado_pelo_motor) - MIN(combustivel_total_usado_pelo_motor) AS litros
              FROM telemetria
              WHERE data_cadastro BETWEEN :inicio AND :fim
              GROUP BY veiculo
            )
            SELECT
              COALESCE(SUM(agg.km), 0)     AS km_total,
              COALESCE(SUM(agg.litros), 0) AS litros_total,
              (SELECT COUNT(*) FROM telemetria t
                 WHERE t.data_cadastro BETWEEN :inicio AND :fim
                   AND (t.acelaracao_brusca = true OR t.freada_brusca = true OR t.curva_brusca = true))
                                            AS eventos_bruscos,
              (SELECT COALESCE(AVG(t.green_driving_value), 0) FROM telemetria t
                 WHERE t.data_cadastro BETWEEN :inicio AND :fim
                   AND t.green_driving_value IS NOT NULL)
                                            AS score_medio,
              (SELECT COUNT(DISTINCT t.veiculo) FROM telemetria t
                 WHERE t.data_cadastro BETWEEN :inicio AND :fim)
                                            AS veiculos_ativos
            FROM agg
            """, nativeQuery = true)
    Object[] findKpisPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    // -------------------------------------------------------------------------
    // 2. Heatmap hora × dia da semana
    // -------------------------------------------------------------------------
    @Query(value = """
            SELECT EXTRACT(DOW FROM data_cadastro)::int  AS dia_semana,
                   EXTRACT(HOUR FROM data_cadastro)::int AS hora,
                   COUNT(*)                              AS total
            FROM telemetria
            WHERE data_cadastro BETWEEN :inicio AND :fim
              AND (acelaracao_brusca = true OR freada_brusca = true OR curva_brusca = true)
            GROUP BY 1, 2
            ORDER BY 1, 2
            """, nativeQuery = true)
    List<Object[]> findHeatmapEventos(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    // -------------------------------------------------------------------------
    // 3. Distribuição de RPM por faixa configurada no veículo
    // Usa colunas rpm_inicio/fim_* reais da tabela veiculo.
    // -------------------------------------------------------------------------
    @Query(value = """
            SELECT
              ROUND(100.0 * SUM(CASE WHEN t.rpm BETWEEN v.rpm_inicio_marcha_lenta    AND v.rpm_fim_marcha_lenta    THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0), 2) AS pct_lenta,
              ROUND(100.0 * SUM(CASE WHEN t.rpm BETWEEN v.rpm_inicio_faixa_economica AND v.rpm_fim_faixa_economica THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0), 2) AS pct_eco,
              ROUND(100.0 * SUM(CASE WHEN t.rpm BETWEEN v.rpm_inicio_faixa_verde     AND v.rpm_fim_faixa_verde     THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0), 2) AS pct_verde,
              ROUND(100.0 * SUM(CASE WHEN t.rpm BETWEEN v.rpm_inicio_faixa_amarela   AND v.rpm_fim_faixa_amarela   THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0), 2) AS pct_amarela,
              ROUND(100.0 * SUM(CASE WHEN t.rpm BETWEEN v.rpm_inicio_faixa_azul      AND v.rpm_fim_faixa_azul      THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0), 2) AS pct_azul
            FROM telemetria t
            JOIN veiculo v ON v.id = t.veiculo
            WHERE t.data_cadastro BETWEEN :inicio AND :fim
              AND t.rpm IS NOT NULL
            """, nativeQuery = true)
    Object[] findRpmDistribuicao(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    // -------------------------------------------------------------------------
    // 4. Top veículos por eventos / 100 km
    // -------------------------------------------------------------------------
    @Query(value = """
            select
            	v.placa,
            	COUNT(*) filter (
            where
            	t.acelaracao_brusca
            	or t.freada_brusca
            	or t.curva_brusca
                ) as eventos,
            	(MAX(t.total_odometer) - MIN(t.total_odometer)) as km,
            	ROUND(
                    (
                        COUNT(*) filter (
                            where t.acelaracao_brusca
                               or t.freada_brusca
                               or t.curva_brusca
                        ) * 100.0
                        /
                        nullif(
                            (MAX(t.total_odometer) - MIN(t.total_odometer)),
                            0
                        )
                    )::numeric,
                    2
                ) as eventos_por_100km
            from telemetria t
            join veiculo v on v.id = t.veiculo
            where t.data_cadastro between :inicio and :fim
            group by v.placa
            having (MAX(t.total_odometer) - MIN(t.total_odometer)) > 10
            order by eventos_por_100km desc nulls last
            limit :limite
            """, nativeQuery = true)
    List<Object[]> findTopVeiculos(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("limite") int limite);

    // -------------------------------------------------------------------------
    // 5. Últimas posições de veículos ativos (24 h)
    // Colunas reais da tabela transmissao: latitude, longitude, velocidade,
    // ignicao_ativa, data_transmissao.
    // -------------------------------------------------------------------------
    @Query(value = """
            WITH ultima AS (
              SELECT DISTINCT ON (t.veiculo) t.veiculo, t.id_transmissao
              FROM telemetria t
              WHERE t.data_cadastro > NOW() - INTERVAL '24 hours'
              ORDER BY t.veiculo, t.data_cadastro DESC
            )
            SELECT v.id             AS veiculo_id,
                   v.placa,
                   tr.latitude,
                   tr.longitude,
                   tr.velocidade    AS velocidade_gps,
                   tr.ignicao_ativa,
                   tr.data_transmissao
            FROM ultima u
            JOIN veiculo v  ON v.id  = u.veiculo
            JOIN transmissao tr ON tr.id = u.id_transmissao
            WHERE v.status = true
            """, nativeQuery = true)
    List<Object[]> findPosicoesAtuais();

    // -------------------------------------------------------------------------
    // 6. Eventos não processados para o publisher WebSocket
    // processado é bool NULL — IS NOT TRUE captura NULL e FALSE.
    // -------------------------------------------------------------------------
    @Query(value = """
            SELECT t.id,
                   t.veiculo,
                   v.placa,
                   t.data_cadastro,
                   t.acelaracao_brusca,
                   t.freada_brusca,
                   t.curva_brusca,
                   t.velocidade_baseada_na_roda,
                   t.rpm,
                   tr.latitude,
                   tr.longitude
            FROM telemetria t
            JOIN veiculo v ON v.id = t.veiculo
            LEFT JOIN transmissao tr ON tr.id = t.id_transmissao
            WHERE (t.acelaracao_brusca = true OR t.freada_brusca = true OR t.curva_brusca = true)
            ORDER BY t.data_cadastro ASC
            LIMIT 100
            """, nativeQuery = true)
    List<Object[]> findEventosNaoProcessados();

    // -------------------------------------------------------------------------
    // Marca lote como processados (id é Integer/int4)
    // -------------------------------------------------------------------------
    @Modifying
    @Query("UPDATE Telemetria t SET t.processado = true WHERE t.id IN :ids")
    void marcarComoProcessados(@Param("ids") List<Integer> ids);
}
