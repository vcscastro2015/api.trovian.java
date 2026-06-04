package com.trovian.service;

import com.trovian.dto.dashboard.*;
import com.trovian.repository.TelemetriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TelemetriaService {

    private final TelemetriaRepository telemetriaRepository;

    public KpiDashboardDTO getKpis(LocalDateTime inicio, LocalDateTime fim) {
        List<Object[]> rows = telemetriaRepository.findKpisPeriodo(inicio, fim);
        if (rows == null || rows.isEmpty() || rows.get(0) == null || rows.get(0)[0] == null) {
            return KpiDashboardDTO.builder()
                    .kmTotal(0).litrosTotal(0).kmPorLitro(null)
                    .eventosBruscos(0).scoreMedio(0).veiculosAtivos(0)
                    .build();
        }
        Object[] row = rows.get(0);
        double kmTotal     = toDouble(row[0]);
        double litrosTotal = toDouble(row[1]);
        long eventos       = toLong(row[2]);
        double score       = toDouble(row[3]);
        long veiculos      = toLong(row[4]);
        Double kmPorLitro  = (litrosTotal > 0) ? kmTotal / litrosTotal : null;

        return KpiDashboardDTO.builder()
                .kmTotal(kmTotal)
                .litrosTotal(litrosTotal)
                .kmPorLitro(kmPorLitro)
                .eventosBruscos(eventos)
                .scoreMedio(score)
                .veiculosAtivos(veiculos)
                .build();
    }

    public RpmDistribuicaoDTO getRpmDistribuicao(LocalDateTime inicio, LocalDateTime fim) {
        List<Object[]> rows = telemetriaRepository.findRpmDistribuicao(inicio, fim);
        if (rows == null || rows.isEmpty() || rows.get(0) == null) {
            return new RpmDistribuicaoDTO(null, null, null, null, null);
        }
        Object[] row = rows.get(0);
        return RpmDistribuicaoDTO.builder()
                .pctLenta(toDoubleNullable(row[0]))
                .pctEco(toDoubleNullable(row[1]))
                .pctVerde(toDoubleNullable(row[2]))
                .pctAmarela(toDoubleNullable(row[3]))
                .pctAzul(toDoubleNullable(row[4]))
                .build();
    }

    public List<HeatmapPontoDTO> getHeatmapEventos(LocalDateTime inicio, LocalDateTime fim) {
        return telemetriaRepository.findHeatmapEventos(inicio, fim).stream()
                .map(row -> HeatmapPontoDTO.builder()
                        .diaSemana(toInt(row[0]))
                        .hora(toInt(row[1]))
                        .total(toLong(row[2]))
                        .build())
                .collect(Collectors.toList());
    }

    public List<TopVeiculoDTO> getTopVeiculos(LocalDateTime inicio, LocalDateTime fim, int limite) {
        return telemetriaRepository.findTopVeiculos(inicio, fim, limite).stream()
                .map(row -> TopVeiculoDTO.builder()
                        .placa((String) row[0])
                        .eventos(toLong(row[1]))
                        .km(toDouble(row[2]))
                        .eventosPor100km(toDoubleNullable(row[3]))
                        .build())
                .collect(Collectors.toList());
    }

    public List<PosicaoAtualDTO> getPosicoesAtuais() {
        return telemetriaRepository.findPosicoesAtuais().stream()
                .map(row -> PosicaoAtualDTO.builder()
                        .veiculoId(toLong(row[0]))
                        .placa((String) row[1])
                        .latitude(toDoubleNullable(row[2]))
                        .longitude(toDoubleNullable(row[3]))
                        .velocidadeGps(toDoubleNullable(row[4]))
                        .ignicaoAtiva(toBoolean(row[5]))
                        .dataTransmissao(toDate(row[6]))
                        .build())
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Helpers de conversão de Object[] retornados por nativeQuery
    // -------------------------------------------------------------------------

    private double toDouble(Object o) {
        if (o == null) return 0.0;
        return ((Number) o).doubleValue();
    }

    private Double toDoubleNullable(Object o) {
        if (o == null) return null;
        return ((Number) o).doubleValue();
    }

    private long toLong(Object o) {
        if (o == null) return 0L;
        return ((Number) o).longValue();
    }

    private int toInt(Object o) {
        if (o == null) return 0;
        return ((Number) o).intValue();
    }

    private Boolean toBoolean(Object o) {
        if (o == null) return null;
        if (o instanceof Boolean b) return b;
        if (o instanceof Number n) return n.intValue() != 0;
        return Boolean.parseBoolean(o.toString());
    }

    private Date toDate(Object o) {
        if (o == null) return null;
        if (o instanceof Date d) return d;
        if (o instanceof java.sql.Timestamp ts) return new Date(ts.getTime());
        return null;
    }
}
