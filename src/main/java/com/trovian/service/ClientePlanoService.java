package com.trovian.service;

import com.trovian.dto.ClientePlanoDTO;
import com.trovian.dto.HistoricoPagamentoDTO;
import com.trovian.entity.Cliente;
import com.trovian.entity.ClientePlano;
import com.trovian.entity.HistoricoPagamento;
import com.trovian.entity.Plano;
import com.trovian.enums.StatusClientePlano;
import com.trovian.repository.ClientePlanoRepository;
import com.trovian.repository.ClienteRepository;
import com.trovian.repository.HistoricoPagamentoRepository;
import com.trovian.repository.PlanoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientePlanoService {

    private final ClientePlanoRepository clientePlanoRepository;
    private final ClienteRepository clienteRepository;
    private final PlanoRepository planoRepository;
    private final HistoricoPagamentoRepository historicoPagamentoRepository;
    private final HistoricoPagamentoService historicoPagamentoService;
    private final ContaReceberService contaReceberService;

    @Transactional(readOnly = true)
    public Page<ClientePlanoDTO> findAll(Pageable pageable) {
        log.info("Buscando todos os cliente-planos - Página: {}, Tamanho: {}", pageable.getPageNumber(), pageable.getPageSize());
        return clientePlanoRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public ClientePlanoDTO findById(Long id) {
        log.info("Buscando cliente-plano com ID: {}", id);
        ClientePlano clientePlano = clientePlanoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClientePlano não encontrado com ID: " + id));
        return toDTO(clientePlano);
    }

    @Transactional(readOnly = true)
    public Page<ClientePlanoDTO> findByCliente(Long clienteId, Pageable pageable) {
        log.info("Buscando cliente-planos do cliente ID: {}", clienteId);
        return clientePlanoRepository.findByClienteId(clienteId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ClientePlanoDTO> findByStatus(StatusClientePlano status, Pageable pageable) {
        log.info("Buscando cliente-planos com status: {}", status);
        return clientePlanoRepository.findByStatus(status, pageable).map(this::toDTO);
    }

    @Transactional
    public ClientePlanoDTO create(ClientePlanoDTO dto) {
        log.info("Criando cliente-plano para cliente ID: {} e plano ID: {}", dto.getClienteId(), dto.getPlanoId());

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + dto.getClienteId()));
        Plano plano = planoRepository.findById(dto.getPlanoId())
                .orElseThrow(() -> new RuntimeException("Plano não encontrado com ID: " + dto.getPlanoId()));

        ClientePlano clientePlano = new ClientePlano();
        clientePlano.setCliente(cliente);
        clientePlano.setPlano(plano);
        clientePlano.setValorContratado(dto.getValorContratado() != null ? dto.getValorContratado() : plano.getValor());
        clientePlano.setDesconto(dto.getDesconto() != null ? dto.getDesconto() : BigDecimal.ZERO);

        BigDecimal valorContratado = clientePlano.getValorContratado() != null ? clientePlano.getValorContratado() : BigDecimal.ZERO;
        BigDecimal desconto = clientePlano.getDesconto() != null ? clientePlano.getDesconto() : BigDecimal.ZERO;
        clientePlano.setValorFinal(valorContratado.subtract(desconto));

        clientePlano.setDataInicio(dto.getDataInicio());
        clientePlano.setDataFinal(dto.getDataFinal());
        clientePlano.setDataVencimento(dto.getDataVencimento());
        clientePlano.setDiaVencimento(dto.getDiaVencimento());
        clientePlano.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusClientePlano.ATIVO);
        clientePlano.setFormaPagamento(dto.getFormaPagamento());

        ClientePlano saved = clientePlanoRepository.save(clientePlano);
        log.info("ClientePlano criado com sucesso. ID: {}", saved.getId());

        contaReceberService.criarContaReceberDePlano(saved);

        return toDTO(saved);
    }

    @Transactional
    public ClientePlanoDTO update(Long id, ClientePlanoDTO dto) {
        log.info("Atualizando cliente-plano com ID: {}", id);

        ClientePlano clientePlano = clientePlanoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClientePlano não encontrado com ID: " + id));

        if (dto.getPlanoId() != null) {
            Plano plano = planoRepository.findById(dto.getPlanoId())
                    .orElseThrow(() -> new RuntimeException("Plano não encontrado com ID: " + dto.getPlanoId()));
            clientePlano.setPlano(plano);
        }

        if (dto.getValorContratado() != null) clientePlano.setValorContratado(dto.getValorContratado());
        if (dto.getDesconto() != null) clientePlano.setDesconto(dto.getDesconto());

        BigDecimal valorContratado = clientePlano.getValorContratado() != null ? clientePlano.getValorContratado() : BigDecimal.ZERO;
        BigDecimal desconto = clientePlano.getDesconto() != null ? clientePlano.getDesconto() : BigDecimal.ZERO;
        clientePlano.setValorFinal(valorContratado.subtract(desconto));

        if (dto.getDataInicio() != null) clientePlano.setDataInicio(dto.getDataInicio());
        if (dto.getDataFinal() != null) clientePlano.setDataFinal(dto.getDataFinal());
        if (dto.getDataVencimento() != null) clientePlano.setDataVencimento(dto.getDataVencimento());
        if (dto.getDiaVencimento() != null) clientePlano.setDiaVencimento(dto.getDiaVencimento());
        if (dto.getStatus() != null) clientePlano.setStatus(dto.getStatus());
        if (dto.getFormaPagamento() != null) clientePlano.setFormaPagamento(dto.getFormaPagamento());

        ClientePlano updated = clientePlanoRepository.save(clientePlano);
        log.info("ClientePlano atualizado com sucesso. ID: {}", id);
        return toDTO(updated);
    }

    @Transactional
    public ClientePlanoDTO cancelar(Long id, String motivo) {
        log.info("Cancelando cliente-plano com ID: {}", id);

        ClientePlano clientePlano = clientePlanoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClientePlano não encontrado com ID: " + id));

        clientePlano.setStatus(StatusClientePlano.CANCELADO);
        clientePlano.setDataCancelamento(LocalDate.now());
        clientePlano.setMotivoCancelamento(motivo);

        ClientePlano updated = clientePlanoRepository.save(clientePlano);
        log.info("ClientePlano cancelado. ID: {}", id);
        return toDTO(updated);
    }

    @Transactional
    public HistoricoPagamentoDTO registrarPagamento(Long id, HistoricoPagamentoDTO pagamentoDTO) {
        log.info("Registrando pagamento para cliente-plano ID: {}", id);

        ClientePlano clientePlano = clientePlanoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ClientePlano não encontrado com ID: " + id));

        pagamentoDTO.setClientePlanoId(id);

        // Cria o registro de pagamento
        HistoricoPagamento historico = new HistoricoPagamento();
        historico.setClientePlano(clientePlano);
        historico.setValorPago(pagamentoDTO.getValorPago());
        historico.setDataPagamento(pagamentoDTO.getDataPagamento());
        historico.setDataReferencia(pagamentoDTO.getDataReferencia());
        historico.setFormaPagamento(pagamentoDTO.getFormaPagamento());
        historico.setObservacao(pagamentoDTO.getObservacao());
        historicoPagamentoRepository.save(historico);

        // Atualiza a data de vencimento para o próximo ciclo
        if (clientePlano.getDataVencimento() != null && clientePlano.getPlano() != null
                && clientePlano.getPlano().getPeriodicidade() != null) {
            LocalDate novoVencimento;
            switch (clientePlano.getPlano().getPeriodicidade()) {
                case MENSAL -> novoVencimento = clientePlano.getDataVencimento().plusMonths(1);
                case ANUAL -> novoVencimento = clientePlano.getDataVencimento().plusYears(1);
                case SEMANAL -> novoVencimento = clientePlano.getDataVencimento().plusWeeks(1);
                default -> novoVencimento = clientePlano.getDataVencimento().plusMonths(1);
            }
            clientePlano.setDataVencimento(novoVencimento);
            clientePlanoRepository.save(clientePlano);
        }

        log.info("Pagamento registrado com sucesso para cliente-plano ID: {}", id);
        return historicoPagamentoService.toDTO(historico);
    }

    private ClientePlanoDTO toDTO(ClientePlano clientePlano) {
        ClientePlanoDTO dto = new ClientePlanoDTO();
        dto.setId(clientePlano.getId());
        dto.setUuid(clientePlano.getUuid());

        if (clientePlano.getCliente() != null) {
            dto.setClienteId(clientePlano.getCliente().getId());
            dto.setClienteNome(clientePlano.getCliente().getNome());
        }

        if (clientePlano.getPlano() != null) {
            dto.setPlanoId(clientePlano.getPlano().getId());
            dto.setPlanoNome(clientePlano.getPlano().getNome());
        }

        dto.setValorContratado(clientePlano.getValorContratado());
        dto.setDesconto(clientePlano.getDesconto());
        dto.setValorFinal(clientePlano.getValorFinal());
        dto.setDataInicio(clientePlano.getDataInicio());
        dto.setDataFinal(clientePlano.getDataFinal());
        dto.setDataVencimento(clientePlano.getDataVencimento());
        dto.setDiaVencimento(clientePlano.getDiaVencimento());
        dto.setStatus(clientePlano.getStatus());
        dto.setFormaPagamento(clientePlano.getFormaPagamento());
        dto.setDataCancelamento(clientePlano.getDataCancelamento());
        dto.setMotivoCancelamento(clientePlano.getMotivoCancelamento());
        dto.setDataCriacao(clientePlano.getDataCriacao());
        dto.setUpdatedAt(clientePlano.getUpdatedAt());
        return dto;
    }
}
