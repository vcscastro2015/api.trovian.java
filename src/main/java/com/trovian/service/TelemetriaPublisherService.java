package com.trovian.service;

import com.trovian.dto.dashboard.TelemetriaLiveDTO;
import com.trovian.repository.TelemetriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetriaPublisherService {

    private static final String TOPIC = "/topic/telemetria-live";

    private final TelemetriaRepository telemetriaRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedDelayString = "${trovian.telemetria.publisher-interval-ms:5000}")
    @Transactional
    public void publicarEventosNaoProcessados() {
        List<Object[]> rows = telemetriaRepository.findEventosNaoProcessados();
        if (rows.isEmpty()) return;

        List<Long> ids = new ArrayList<>(rows.size());

        for (Object[] row : rows) {
            // colunas: id(bigint), veiculo(bigint), placa, data_cadastro,
            //          acelaracao_brusca, freada_brusca, curva_brusca,
            //          velocidade_baseada_na_roda, rpm, latitude, longitude
            Long telemetriaId = toLong(row[0]);
            long veiculoId       = toLong(row[1]);
            String placa         = (String) row[2];
            OffsetDateTime timestamp = toOffsetDateTime(row[3]);
            boolean aceleracao   = toBoolean(row[4]);
            boolean freada       = toBoolean(row[5]);
            boolean curva        = toBoolean(row[6]);
            Integer velocidade   = toIntNullable(row[7]);
            Integer rpm          = toIntNullable(row[8]);
            Double latitude      = toDoubleNullable(row[9]);
            Double longitude     = toDoubleNullable(row[10]);

            String tipoEvento = resolverTipoEvento(aceleracao, freada, curva);

            TelemetriaLiveDTO dto = TelemetriaLiveDTO.builder()
                    .telemetriaId(telemetriaId)
                    .veiculoId(veiculoId)
                    .placa(placa)
                    .timestamp(timestamp)
                    .tipoEvento(tipoEvento)
                    .velocidade(velocidade)
                    .rpm(rpm)
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();

            messagingTemplate.convertAndSend(TOPIC, dto);
            ids.add(telemetriaId);
        }

        telemetriaRepository.marcarComoProcessados(ids);
        log.debug("Telemetria: {} evento(s) publicados e marcados como processados.", ids.size());
    }

    private String resolverTipoEvento(boolean aceleracao, boolean freada, boolean curva) {
        if (aceleracao) return "ACELARACAO_BRUSCA";
        if (freada)     return "FREADA_BRUSCA";
        if (curva)      return "CURVA_BRUSCA";
        return "DESCONHECIDO";
    }

    private long toLong(Object o) {
        if (o == null) return 0L;
        return ((Number) o).longValue();
    }

    private Integer toIntNullable(Object o) {
        if (o == null) return null;
        return ((Number) o).intValue();
    }

    private Double toDoubleNullable(Object o) {
        if (o == null) return null;
        return ((Number) o).doubleValue();
    }

    private boolean toBoolean(Object o) {
        if (o == null) return false;
        if (o instanceof Boolean b) return b;
        if (o instanceof Number n) return n.intValue() != 0;
        return Boolean.parseBoolean(o.toString());
    }

    private OffsetDateTime toOffsetDateTime(Object o) {
        if (o == null) return null;
        if (o instanceof OffsetDateTime odt) return odt;
        if (o instanceof java.sql.Timestamp ts) return ts.toInstant().atZone(ZoneId.of("America/Sao_Paulo")).toOffsetDateTime();
        return null;
    }
}
