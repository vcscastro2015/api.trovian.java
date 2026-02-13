package com.trovian.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import com.trovian.repository.ViagemRepositoryCustom;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class ViagemRepositoryImpl implements ViagemRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> getResumoGeral(Long clienteId, LocalDate dataInicio) {
        String sql = """
            SELECT 
                COUNT(*) as total_viagens,
                COALESCE(SUM(CASE WHEN v.status_viagem = 'ABERTA' THEN 1 ELSE 0 END), 0) as viagens_abertas,
                COALESCE(SUM(CASE WHEN v.status_viagem = 'FINALIZADA' THEN 1 ELSE 0 END), 0) as viagens_finalizadas,
                COALESCE(SUM(v.valor_total_bruto_viagem), 0) as receita_total,
                COALESCE(SUM(v.valor_total_liquido), 0) as lucro_total,
                COALESCE(AVG(v.margem_percentual), 0) as margem_media,
                COALESCE(SUM(ri.distancia_total + COALESCE(rv.distancia_total, 0)), 0) / 1000 as km_total,
                COALESCE(SUM(v.valor_total_combustivel), 0) as custo_combustivel,
                COALESCE(SUM(v.valor_total_pedagios), 0) as custo_pedagios
            FROM viagem v
            INNER JOIN rota ri ON v.rota_ida_id = ri.id
            LEFT JOIN rota rv ON v.rota_volta_id = rv.id
            WHERE v.cliente_id = :clienteId
                AND v.status = true
                AND v.data_viagem >= :dataInicio
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("clienteId", clienteId);
        query.setParameter("dataInicio", dataInicio);
        
        return query.getResultList();
    }

    @Override
    public List<Object[]> getLucratividadePorRota(Long clienteId, LocalDate dataInicio) {
        String sql = """
            SELECT 
                r.id as rota_id,
                r.nome as rota_nome,
                COUNT(v.id) as total_viagens,
                COALESCE(SUM(v.valor_total_bruto_viagem), 0) as receita_total,
                COALESCE(SUM(v.valor_total_custos), 0) as custo_total,
                COALESCE(SUM(v.valor_total_liquido), 0) as lucro_total,
                COALESCE(AVG(v.margem_percentual), 0) as margem_percentual,
                AVG(r.distancia_total) / 1000 as distancia_media_km
            FROM viagem v
            INNER JOIN rota r ON v.rota_ida_id = r.id
            WHERE v.cliente_id = :clienteId
                AND v.status = true
                AND v.data_viagem >= :dataInicio
            GROUP BY r.id, r.nome
            HAVING COUNT(v.id) > 0
            ORDER BY lucro_total DESC
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("clienteId", clienteId);
        query.setParameter("dataInicio", dataInicio);
        
        return query.getResultList();
    }

    @Override
    public List<Object[]> getViagensBaixaMargem(Long clienteId, LocalDate dataInicio, Double margemMaxima) {
        String sql = """
            SELECT 
                v.id as viagem_id,
                r.nome as rota_nome,
                m.nome as motorista_nome,
                ve.placa as veiculo_placa,
                COALESCE(v.margem_percentual, 0) as margem,
                v.valor_total_liquido as lucro,
                v.valor_total_bruto_viagem as receita,
                v.data_viagem
            FROM viagem v
            INNER JOIN rota r ON v.rota_ida_id = r.id
            INNER JOIN motorista m ON v.motorista_id = m.id
            INNER JOIN veiculo ve ON v.veiculo_id = ve.id
            WHERE v.cliente_id = :clienteId
                AND v.status = true
                AND v.data_viagem >= :dataInicio
                AND COALESCE(v.margem_percentual, 0) <= :margemMaxima
            ORDER BY v.margem_percentual ASC
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("clienteId", clienteId);
        query.setParameter("dataInicio", dataInicio);
        query.setParameter("margemMaxima", margemMaxima);
        
        return query.getResultList();
    }

    @Override
    public List<Object[]> getEficienciaVeiculos(Long clienteId, LocalDate dataInicio) {
        String sql = """
            SELECT 
                ve.id as veiculo_id,
                ve.placa,
                ve.modelo,
                COUNT(v.id) as total_viagens,
                COALESCE(SUM(v.valor_total_liquido), 0) as lucro_total,
                COALESCE(SUM(v.valor_total_bruto_viagem), 0) as receita_total,
                COALESCE(SUM((ri.distancia_total + COALESCE(rv.distancia_total, 0)) / 1000), 0) as km_total,
                AVG(v.media_veiculo_km_l) as media_consumo
            FROM viagem v
            INNER JOIN veiculo ve ON v.veiculo_id = ve.id
            INNER JOIN rota ri ON v.rota_ida_id = ri.id
            LEFT JOIN rota rv ON v.rota_volta_id = rv.id
            WHERE v.cliente_id = :clienteId
                AND v.status = true
                AND v.data_viagem >= :dataInicio
            GROUP BY ve.id, ve.placa, ve.modelo
            HAVING COUNT(v.id) > 0
            ORDER BY lucro_total DESC
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("clienteId", clienteId);
        query.setParameter("dataInicio", dataInicio);
        
        return query.getResultList();
    }

    @Override
    public List<Object[]> getPerformanceMotoristas(Long clienteId, LocalDate dataInicio) {
        String sql = """
            SELECT 
                m.id as motorista_id,
                m.nome,
                COUNT(v.id) as total_viagens,
                AVG(v.media_veiculo_km_l) as media_consumo,
                COALESCE(SUM(v.comissao_motorista), 0) as comissao_total,
                COUNT(CASE WHEN v.status_viagem = 'FINALIZADA' THEN 1 END) as viagens_no_prazo,
                COALESCE(SUM((ri.distancia_total + COALESCE(rv.distancia_total, 0)) / 1000), 0) as km_total
            FROM viagem v
            INNER JOIN motorista m ON v.motorista_id = m.id
            INNER JOIN rota ri ON v.rota_ida_id = ri.id
            LEFT JOIN rota rv ON v.rota_volta_id = rv.id
            WHERE v.cliente_id = :clienteId
                AND v.status = true
                AND v.data_viagem >= :dataInicio
            GROUP BY m.id, m.nome
            HAVING COUNT(v.id) > 0
            ORDER BY total_viagens DESC
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("clienteId", clienteId);
        query.setParameter("dataInicio", dataInicio);
        
        return query.getResultList();
    }

    @Override
    public List<Object[]> getTendenciasMensais(Long clienteId, LocalDate dataInicio) {
        String sql = """
            SELECT 
                CAST(DATE_PART('year', v.data_viagem) AS INTEGER) as ano,
                CAST(DATE_PART('month', v.data_viagem)AS INTEGER) as mes,
                COALESCE(SUM(v.valor_total_bruto_viagem), 0) as receita,
                COALESCE(SUM(v.valor_total_custos), 0) as custo,
                COALESCE(SUM(v.valor_total_liquido), 0) as lucro,
                COUNT(v.id) as total_viagens
            FROM viagem v
            WHERE v.cliente_id = :clienteId
                AND v.status = true
                AND v.data_viagem >= :dataInicio
            GROUP BY DATE_PART('year', v.data_viagem), DATE_PART('month', v.data_viagem)
            ORDER BY ano, mes
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("clienteId", clienteId);
        query.setParameter("dataInicio", dataInicio);
        
        return query.getResultList();
    }

    @Override
    public List<Object[]> getCustosOperacionais(Long clienteId, LocalDate dataInicio) {
        String sql = """
            SELECT 
                COALESCE(SUM(v.valor_total_combustivel), 0) as combustivel,
                COALESCE(SUM(v.valor_total_pedagios), 0) as pedagios,
                COALESCE(SUM(v.comissao_motorista), 0) as comissoes
            FROM viagem v
            WHERE v.cliente_id = :clienteId
                AND v.status = true
                AND v.data_viagem >= :dataInicio
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("clienteId", clienteId);
        query.setParameter("dataInicio", dataInicio);
        
        return query.getResultList();
    }

    @Override
    public List<Object[]> getIndicadoresRotas(Long clienteId, LocalDate dataInicio) {
        String sql = """
            SELECT 
                r.id as rota_id,
                r.nome as rota_nome,
                AVG(
                    (v.quantidade_toneladas_ida + COALESCE(v.quantidade_toneladas_volta, 0)) / 
                    (ve.carga_maxima)
                ) * 100 as taxa_ocupacao,
                SUM(ri.distancia_total / 1000) as km_total,
                SUM(CASE WHEN v.quantidade_toneladas_ida > 0 THEN ri.distancia_total / 1000 ELSE 0 END) as km_com_carga,
                AVG(v.valor_total_bruto_viagem / (ri.distancia_total / 1000)) as receita_por_km,
                AVG(CAST(EXTRACT(EPOCH FROM (v.updated_at - v.data_cadastro)) / 3600 AS NUMERIC)) as tempo_medio_horas
            FROM viagem v
            INNER JOIN rota r ON v.rota_ida_id = r.id
            INNER JOIN rota ri ON v.rota_ida_id = ri.id
            INNER JOIN veiculo ve ON v.veiculo_id = ve.id
            WHERE v.cliente_id = :clienteId
                AND v.status = true
                AND v.data_viagem >= :dataInicio
            GROUP BY r.id, r.nome
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("clienteId", clienteId);
        query.setParameter("dataInicio", dataInicio);
        
        return query.getResultList();
    }

    @Override
    public List<Object[]> getMapaCalorRotas(Long clienteId, LocalDate dataInicio) {
        String sql = """
            SELECT 
                r.id as rota_id,
                r.nome as rota_nome,
                po.latitude as lat_origem,
                po.longitude as lng_origem,
                pd.latitude as lat_destino,
                pd.longitude as lng_destino,
                COALESCE(SUM(v.valor_total_liquido), 0) as lucratividade,
                COUNT(v.id) as frequencia
            FROM viagem v
            INNER JOIN rota r ON v.rota_ida_id = r.id
            INNER JOIN ponto_rota po ON po.rota_id = r.id AND po.tipo = 'ORIGEM'
            INNER JOIN ponto_rota pd ON pd.rota_id = r.id AND pd.tipo = 'DESTINO'
            WHERE v.cliente_id = :clienteId
                AND v.status = true
                AND v.data_viagem >= :dataInicio
            GROUP BY r.id, r.nome, po.latitude, po.longitude, pd.latitude, pd.longitude
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("clienteId", clienteId);
        query.setParameter("dataInicio", dataInicio);
        
        return query.getResultList();
    }

    @Override
    public Optional<BigDecimal> getImpostosTotal(Long clienteId, LocalDate dataInicio) {
        String sql = """
            SELECT COALESCE(SUM(v.imposto_base_ida + COALESCE(v.imposto_base_volta, 0)), 0)
            FROM viagem v
            WHERE v.cliente_id = :clienteId
                AND v.status = true
                AND v.data_viagem >= :dataInicio
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("clienteId", clienteId);
        query.setParameter("dataInicio", dataInicio);
        
        Object result = query.getSingleResult();
        return Optional.ofNullable(result != null ? (BigDecimal) result : BigDecimal.ZERO);
    }

    @Override
    public Optional<Double> getTaxaOcupacaoVeiculo(Long veiculoId, LocalDate dataInicio) {
        String sql = """
            SELECT AVG(
                (v.quantidade_toneladas_ida + COALESCE(v.quantidade_toneladas_volta, 0)) /
                (ve.carga_maxima)
            ) * 100
            FROM viagem v
            INNER JOIN veiculo ve ON v.veiculo_id = ve.id
            WHERE v.veiculo_id = :veiculoId
                AND v.status = true
                AND v.data_viagem >= :dataInicio
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("veiculoId", veiculoId);
        query.setParameter("dataInicio", dataInicio);

        Object result = query.getSingleResult();
        return Optional.ofNullable(result != null ? ((Number) result).doubleValue() : 0.0);
    }

    // =========================================================================
    // Métodos com range de data (dataInicio + dataFim) para relatórios
    // =========================================================================

    @Override
    public List<Object[]> getResumoGeralPeriodo(Long clienteId, LocalDate dataInicio, LocalDate dataFim) {
        String sql = """
            SELECT
                COUNT(*) as total_viagens,
                COALESCE(SUM(CASE WHEN v.status_viagem = 'ABERTA' THEN 1 ELSE 0 END), 0) as viagens_abertas,
                COALESCE(SUM(CASE WHEN v.status_viagem = 'ANALISE' THEN 1 ELSE 0 END), 0) as viagens_analise,
                COALESCE(SUM(CASE WHEN v.status_viagem = 'FECHADA' THEN 1 ELSE 0 END), 0) as viagens_fechadas,
                COALESCE(SUM(v.valor_total_bruto_viagem), 0) as receita_total,
                COALESCE(SUM(v.valor_total_custos), 0) as custo_total,
                COALESCE(SUM(v.valor_total_liquido), 0) as lucro_total,
                COALESCE(AVG(v.margem_percentual), 0) as margem_media,
                COALESCE(SUM(ri.distancia_total + COALESCE(rv.distancia_total, 0)), 0) / 1000 as km_total,
                COALESCE(SUM(v.valor_total_combustivel), 0) as custo_combustivel,
                COALESCE(SUM(v.valor_total_pedagios), 0) as custo_pedagios,
                COALESCE(SUM(v.comissao_motorista), 0) as custo_comissoes
            FROM viagem v
            INNER JOIN rota ri ON v.rota_ida_id = ri.id
            LEFT JOIN rota rv ON v.rota_volta_id = rv.id
            WHERE v.status = true
                AND v.data_viagem >= :dataInicio
                AND v.data_viagem <= :dataFim
            """ + (clienteId != null ? " AND v.cliente_id = :clienteId" : "");

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("dataInicio", dataInicio);
        query.setParameter("dataFim", dataFim);
        if (clienteId != null) {
            query.setParameter("clienteId", clienteId);
        }

        return query.getResultList();
    }

    @Override
    public List<Object[]> getLucratividadePorRotaPeriodo(Long clienteId, LocalDate dataInicio, LocalDate dataFim) {
        String sql = """
            SELECT
                r.id as rota_id,
                r.nome as rota_nome,
                COUNT(v.id) as total_viagens,
                COALESCE(SUM(v.valor_total_bruto_viagem), 0) as receita_total,
                COALESCE(SUM(v.valor_total_custos), 0) as custo_total,
                COALESCE(SUM(v.valor_total_liquido), 0) as lucro_total,
                COALESCE(AVG(v.margem_percentual), 0) as margem_percentual,
                AVG(r.distancia_total) / 1000 as distancia_media_km,
                COALESCE(AVG(v.valor_total_pedagios), 0) as pedagio_medio,
                CASE WHEN AVG(r.distancia_total) > 0
                    THEN COALESCE(SUM(v.valor_total_bruto_viagem), 0) / (SUM(r.distancia_total) / 1000)
                    ELSE 0 END as receita_por_km
            FROM viagem v
            INNER JOIN rota r ON v.rota_ida_id = r.id
            WHERE v.status = true
                AND v.data_viagem >= :dataInicio
                AND v.data_viagem <= :dataFim
            """ + (clienteId != null ? " AND v.cliente_id = :clienteId" : "") + """

            GROUP BY r.id, r.nome
            HAVING COUNT(v.id) > 0
            ORDER BY lucro_total DESC
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("dataInicio", dataInicio);
        query.setParameter("dataFim", dataFim);
        if (clienteId != null) {
            query.setParameter("clienteId", clienteId);
        }

        return query.getResultList();
    }

    @Override
    public List<Object[]> getEficienciaVeiculosPeriodo(Long clienteId, LocalDate dataInicio, LocalDate dataFim) {
        String sql = """
            SELECT
                ve.id as veiculo_id,
                ve.placa,
                COUNT(v.id) as total_viagens,
                COALESCE(SUM(v.valor_total_bruto_viagem), 0) as receita_total,
                COALESCE(SUM(v.valor_total_custos), 0) as custo_total,
                COALESCE(SUM(v.valor_total_liquido), 0) as lucro_total,
                COALESCE(AVG(v.margem_percentual), 0) as margem_media,
                COALESCE(SUM((ri.distancia_total + COALESCE(rv.distancia_total, 0)) / 1000), 0) as km_total,
                AVG(v.media_veiculo_km_l) as media_consumo
            FROM viagem v
            INNER JOIN veiculo ve ON v.veiculo_id = ve.id
            INNER JOIN rota ri ON v.rota_ida_id = ri.id
            LEFT JOIN rota rv ON v.rota_volta_id = rv.id
            WHERE v.status = true
                AND v.data_viagem >= :dataInicio
                AND v.data_viagem <= :dataFim
            """ + (clienteId != null ? " AND v.cliente_id = :clienteId" : "") + """

            GROUP BY ve.id, ve.placa
            HAVING COUNT(v.id) > 0
            ORDER BY lucro_total DESC
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("dataInicio", dataInicio);
        query.setParameter("dataFim", dataFim);
        if (clienteId != null) {
            query.setParameter("clienteId", clienteId);
        }

        return query.getResultList();
    }

    @Override
    public List<Object[]> getPerformanceMotoristasPeriodo(Long clienteId, LocalDate dataInicio, LocalDate dataFim) {
        String sql = """
            SELECT
                m.id as motorista_id,
                m.nome,
                COUNT(v.id) as total_viagens,
                COALESCE(SUM(v.valor_total_bruto_viagem), 0) as receita_total,
                COALESCE(AVG(v.margem_percentual), 0) as margem_media,
                COALESCE(SUM(v.comissao_motorista), 0) as comissao_total,
                AVG(v.media_veiculo_km_l) as media_consumo,
                COALESCE(SUM((ri.distancia_total + COALESCE(rv.distancia_total, 0)) / 1000), 0) as km_total
            FROM viagem v
            INNER JOIN motorista m ON v.motorista_id = m.id
            INNER JOIN rota ri ON v.rota_ida_id = ri.id
            LEFT JOIN rota rv ON v.rota_volta_id = rv.id
            WHERE v.status = true
                AND v.data_viagem >= :dataInicio
                AND v.data_viagem <= :dataFim
            """ + (clienteId != null ? " AND v.cliente_id = :clienteId" : "") + """

            GROUP BY m.id, m.nome
            HAVING COUNT(v.id) > 0
            ORDER BY total_viagens DESC
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("dataInicio", dataInicio);
        query.setParameter("dataFim", dataFim);
        if (clienteId != null) {
            query.setParameter("clienteId", clienteId);
        }

        return query.getResultList();
    }

    @Override
    public List<Object[]> getTendenciasMensaisPeriodo(Long clienteId, LocalDate dataInicio, LocalDate dataFim) {
        String sql = """
            SELECT
                CAST(DATE_PART('year', v.data_viagem) AS INTEGER) as ano,
                CAST(DATE_PART('month', v.data_viagem) AS INTEGER) as mes,
                COALESCE(SUM(v.valor_total_bruto_viagem), 0) as receita,
                COALESCE(SUM(v.valor_total_custos), 0) as custo,
                COALESCE(SUM(v.valor_total_liquido), 0) as lucro,
                COUNT(v.id) as total_viagens
            FROM viagem v
            WHERE v.status = true
                AND v.data_viagem >= :dataInicio
                AND v.data_viagem <= :dataFim
            """ + (clienteId != null ? " AND v.cliente_id = :clienteId" : "") + """

            GROUP BY DATE_PART('year', v.data_viagem), DATE_PART('month', v.data_viagem)
            ORDER BY ano, mes
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("dataInicio", dataInicio);
        query.setParameter("dataFim", dataFim);
        if (clienteId != null) {
            query.setParameter("clienteId", clienteId);
        }

        return query.getResultList();
    }

    @Override
    public List<Object[]> getCustosOperacionaisPeriodo(Long clienteId, LocalDate dataInicio, LocalDate dataFim) {
        String sql = """
            SELECT
                COALESCE(SUM(v.valor_total_combustivel), 0) as combustivel,
                COALESCE(SUM(v.valor_total_pedagios), 0) as pedagios,
                COALESCE(SUM(v.comissao_motorista), 0) as comissoes
            FROM viagem v
            WHERE v.status = true
                AND v.data_viagem >= :dataInicio
                AND v.data_viagem <= :dataFim
            """ + (clienteId != null ? " AND v.cliente_id = :clienteId" : "");

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("dataInicio", dataInicio);
        query.setParameter("dataFim", dataFim);
        if (clienteId != null) {
            query.setParameter("clienteId", clienteId);
        }

        return query.getResultList();
    }

    @Override
    public List<Object[]> getViagensBaixaMargemPeriodo(Long clienteId, LocalDate dataInicio, LocalDate dataFim, Double margemMaxima) {
        String sql = """
            SELECT
                v.id as viagem_id,
                r.nome as rota_nome,
                m.nome as motorista_nome,
                ve.placa as veiculo_placa,
                COALESCE(v.margem_percentual, 0) as margem,
                v.valor_total_liquido as lucro,
                v.valor_total_bruto_viagem as receita,
                v.data_viagem
            FROM viagem v
            INNER JOIN rota r ON v.rota_ida_id = r.id
            INNER JOIN motorista m ON v.motorista_id = m.id
            INNER JOIN veiculo ve ON v.veiculo_id = ve.id
            WHERE v.status = true
                AND v.data_viagem >= :dataInicio
                AND v.data_viagem <= :dataFim
                AND COALESCE(v.margem_percentual, 0) <= :margemMaxima
            """ + (clienteId != null ? " AND v.cliente_id = :clienteId" : "") + """

            ORDER BY v.margem_percentual ASC
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("dataInicio", dataInicio);
        query.setParameter("dataFim", dataFim);
        query.setParameter("margemMaxima", margemMaxima);
        if (clienteId != null) {
            query.setParameter("clienteId", clienteId);
        }

        return query.getResultList();
    }

    @Override
    public List<Object[]> getReceitaPorClientePeriodo(LocalDate dataInicio, LocalDate dataFim) {
        String sql = """
            SELECT
                c.id as cliente_id,
                c.nome as cliente_nome,
                COUNT(v.id) as total_viagens,
                COALESCE(SUM(v.valor_total_bruto_viagem), 0) as receita_bruta,
                COALESCE(SUM(v.valor_total_liquido), 0) as lucro_liquido,
                COALESCE(AVG(v.margem_percentual), 0) as margem_media,
                COALESCE(SUM(v.quantidade_toneladas_ida + COALESCE(v.quantidade_toneladas_volta, 0)), 0) as toneladas,
                CASE WHEN COUNT(v.id) > 0
                    THEN COALESCE(SUM(v.valor_total_bruto_viagem), 0) / COUNT(v.id)
                    ELSE 0 END as ticket_medio,
                STRING_AGG(DISTINCT v.material_ida, ', ') as materiais
            FROM viagem v
            INNER JOIN cliente c ON v.cliente_id = c.id
            WHERE v.status = true
                AND v.data_viagem >= :dataInicio
                AND v.data_viagem <= :dataFim
            GROUP BY c.id, c.nome
            HAVING COUNT(v.id) > 0
            ORDER BY receita_bruta DESC
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("dataInicio", dataInicio);
        query.setParameter("dataFim", dataFim);

        return query.getResultList();
    }

    @Override
    public List<Object[]> getAnaliseCombustivelPorVeiculoPeriodo(Long clienteId, LocalDate dataInicio, LocalDate dataFim) {
        String sql = """
            SELECT
                ve.id as veiculo_id,
                ve.placa,
                COALESCE(SUM(v.consumo_estimado_litros), 0) as consumo_litros,
                COALESCE(SUM(v.valor_total_combustivel), 0) as custo_combustivel,
                AVG(v.media_veiculo_km_l) as media_km_l,
                COALESCE(SUM((ri.distancia_total + COALESCE(rv.distancia_total, 0)) / 1000), 0) as km_rodados
            FROM viagem v
            INNER JOIN veiculo ve ON v.veiculo_id = ve.id
            INNER JOIN rota ri ON v.rota_ida_id = ri.id
            LEFT JOIN rota rv ON v.rota_volta_id = rv.id
            WHERE v.status = true
                AND v.data_viagem >= :dataInicio
                AND v.data_viagem <= :dataFim
            """ + (clienteId != null ? " AND v.cliente_id = :clienteId" : "") + """

            GROUP BY ve.id, ve.placa
            HAVING COUNT(v.id) > 0
            ORDER BY custo_combustivel DESC
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("dataInicio", dataInicio);
        query.setParameter("dataFim", dataFim);
        if (clienteId != null) {
            query.setParameter("clienteId", clienteId);
        }

        return query.getResultList();
    }

    @Override
    public List<Object[]> getAnaliseCombustivelPorRotaPeriodo(Long clienteId, LocalDate dataInicio, LocalDate dataFim) {
        String sql = """
            SELECT
                r.id as rota_id,
                r.nome as rota_nome,
                AVG(v.consumo_estimado_litros) as consumo_medio_litros,
                COALESCE(MAX(cd.classificacao), 'N/A') as classificacao_terreno,
                COALESCE(AVG(v.fator_carga), 0) as fator_terreno_medio,
                AVG(r.distancia_total) / 1000 as distancia_km
            FROM viagem v
            INNER JOIN rota r ON v.rota_ida_id = r.id
            LEFT JOIN consumo_detalhado cd ON v.consumo_detalhado_ida_id = cd.id
            WHERE v.status = true
                AND v.data_viagem >= :dataInicio
                AND v.data_viagem <= :dataFim
            """ + (clienteId != null ? " AND v.cliente_id = :clienteId" : "") + """

            GROUP BY r.id, r.nome
            HAVING COUNT(v.id) > 0
            ORDER BY consumo_medio_litros DESC
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("dataInicio", dataInicio);
        query.setParameter("dataFim", dataFim);
        if (clienteId != null) {
            query.setParameter("clienteId", clienteId);
        }

        return query.getResultList();
    }
}
