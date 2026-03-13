package com.trovian.service;

import com.trovian.dto.HistoricoPagamentoDTO;
import com.trovian.entity.ClientePlano;
import com.trovian.entity.HistoricoPagamento;
import com.trovian.repository.ClientePlanoRepository;
import com.trovian.repository.HistoricoPagamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoricoPagamentoService {

    private final HistoricoPagamentoRepository historicoPagamentoRepository;
    private final ClientePlanoRepository clientePlanoRepository;

    @Transactional(readOnly = true)
    public Page<HistoricoPagamentoDTO> findByClientePlano(Long clientePlanoId, Pageable pageable) {
        log.info("Buscando histórico de pagamentos para clientePlano ID: {}", clientePlanoId);
        return historicoPagamentoRepository.findByClientePlanoId(clientePlanoId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public HistoricoPagamentoDTO findById(Long id) {
        log.info("Buscando histórico de pagamento com ID: {}", id);
        HistoricoPagamento historico = historicoPagamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Histórico de pagamento não encontrado com ID: " + id));
        return toDTO(historico);
    }

    @Transactional
    public HistoricoPagamentoDTO create(HistoricoPagamentoDTO dto) {
        log.info("Registrando pagamento para clientePlano ID: {}", dto.getClientePlanoId());
        ClientePlano clientePlano = clientePlanoRepository.findById(dto.getClientePlanoId())
                .orElseThrow(() -> new RuntimeException("ClientePlano não encontrado com ID: " + dto.getClientePlanoId()));

        HistoricoPagamento historico = new HistoricoPagamento();
        historico.setClientePlano(clientePlano);
        historico.setValorPago(dto.getValorPago());
        historico.setDataPagamento(dto.getDataPagamento());
        historico.setDataReferencia(dto.getDataReferencia());
        historico.setFormaPagamento(dto.getFormaPagamento());
        historico.setObservacao(dto.getObservacao());

        HistoricoPagamento saved = historicoPagamentoRepository.save(historico);
        log.info("Histórico de pagamento registrado. ID: {}", saved.getId());
        return toDTO(saved);
    }

    public HistoricoPagamentoDTO toDTO(HistoricoPagamento historico) {
        HistoricoPagamentoDTO dto = new HistoricoPagamentoDTO();
        dto.setId(historico.getId());
        dto.setUuid(historico.getUuid());
        dto.setClientePlanoId(historico.getClientePlano().getId());
        dto.setValorPago(historico.getValorPago());
        dto.setDataPagamento(historico.getDataPagamento());
        dto.setDataReferencia(historico.getDataReferencia());
        dto.setFormaPagamento(historico.getFormaPagamento());
        dto.setObservacao(historico.getObservacao());
        dto.setDataCriacao(historico.getDataCriacao());
        return dto;
    }
}
