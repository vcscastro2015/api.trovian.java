package com.trovian.service;

import com.trovian.dto.ComissaoMotoristaDTO;
import com.trovian.dto.ComissaoMotoristaRelatorioDTO;
import com.trovian.entity.Cliente;
import com.trovian.entity.ComissaoMotorista;
import com.trovian.entity.ComissaoMotorista.StatusComissao;
import com.trovian.entity.Motorista;
import com.trovian.entity.Viagem;
import com.trovian.exception.ClienteNotFoundException;
import com.trovian.exception.ComissaoNotFoundException;
import com.trovian.exception.MotoristaNotFoundException;
import com.trovian.exception.ViagemNotFoundException;
import com.trovian.repository.ClienteRepository;
import com.trovian.repository.ComissaoMotoristaRepository;
import com.trovian.repository.MotoristaRepository;
import com.trovian.repository.ViagemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComissaoMotoristaService {

    private final ComissaoMotoristaRepository comissaoMotoristaRepository;
    private final MotoristaRepository motoristaRepository;
    private final ClienteRepository clienteRepository;
    private final ViagemRepository viagemRepository;

    @Transactional(readOnly = true)
    public Page<ComissaoMotoristaDTO> findAllPaginated(Pageable pageable) {
        log.info("Buscando todas as comissões com paginação - Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<ComissaoMotorista> comissoes = comissaoMotoristaRepository.findAll(pageable);
        return comissoes.map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public ComissaoMotoristaDTO findById(Long id) {
        log.info("Buscando comissão por ID: {}", id);
        ComissaoMotorista comissao = comissaoMotoristaRepository.findById(id)
                .orElseThrow(() -> new ComissaoNotFoundException(id));
        return toDTO(comissao);
    }

    @Transactional(readOnly = true)
    public List<ComissaoMotoristaDTO> findByCliente(Long clienteId) {
        log.info("Buscando comissões por cliente ID: {}", clienteId);
        List<ComissaoMotorista> comissoes = comissaoMotoristaRepository.findByClienteId(clienteId);
        return comissoes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ComissaoMotoristaDTO> findByMotorista(Long motoristaId, Pageable pageable) {
        log.info("Buscando comissões por motorista ID: {} - Página: {}, Tamanho: {}",
                motoristaId, pageable.getPageNumber(), pageable.getPageSize());

        if (!motoristaRepository.existsById(motoristaId)) {
            throw new MotoristaNotFoundException(motoristaId);
        }

        Page<ComissaoMotorista> comissoes = comissaoMotoristaRepository.findByMotoristaId(motoristaId, pageable);
        return comissoes.map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ComissaoMotoristaDTO> findByStatus(StatusComissao status, Pageable pageable) {
        log.info("Buscando comissões por status: {} - Página: {}, Tamanho: {}",
                status, pageable.getPageNumber(), pageable.getPageSize());
        Page<ComissaoMotorista> comissoes = comissaoMotoristaRepository.findByStatus(status, pageable);
        return comissoes.map(this::toDTO);
    }

    @Transactional
    public ComissaoMotoristaDTO create(ComissaoMotoristaDTO comissaoDTO) {
        log.info("Criando nova comissão para motorista ID: {}", comissaoDTO.getMotoristaId());

        Motorista motorista = motoristaRepository.findById(comissaoDTO.getMotoristaId())
                .orElseThrow(() -> new MotoristaNotFoundException(comissaoDTO.getMotoristaId()));

        Cliente cliente = clienteRepository.findById(comissaoDTO.getClienteId())
                .orElseThrow(() -> new ClienteNotFoundException(comissaoDTO.getClienteId()));

        ComissaoMotorista comissao = toEntity(comissaoDTO);
        comissao.setMotorista(motorista);
        comissao.setCliente(cliente);

        if (comissaoDTO.getViagemId() != null) {
            Viagem viagem = viagemRepository.findById(comissaoDTO.getViagemId())
                    .orElseThrow(() -> new ViagemNotFoundException(comissaoDTO.getViagemId()));
            comissao.setViagem(viagem);
        }

        ComissaoMotorista savedComissao = comissaoMotoristaRepository.save(comissao);
        log.info("Comissão criada com ID: {}", savedComissao.getId());
        return toDTO(savedComissao);
    }

    @Transactional
    public ComissaoMotoristaDTO update(Long id, ComissaoMotoristaDTO comissaoDTO) {
        log.info("Atualizando comissão ID: {}", id);

        ComissaoMotorista comissao = comissaoMotoristaRepository.findById(id)
                .orElseThrow(() -> new ComissaoNotFoundException(id));

        if (comissaoDTO.getMotoristaId() != null) {
            Motorista motorista = motoristaRepository.findById(comissaoDTO.getMotoristaId())
                    .orElseThrow(() -> new MotoristaNotFoundException(comissaoDTO.getMotoristaId()));
            comissao.setMotorista(motorista);
        }

        if (comissaoDTO.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(comissaoDTO.getClienteId())
                    .orElseThrow(() -> new ClienteNotFoundException(comissaoDTO.getClienteId()));
            comissao.setCliente(cliente);
        }

        if (comissaoDTO.getViagemId() != null) {
            Viagem viagem = viagemRepository.findById(comissaoDTO.getViagemId())
                    .orElseThrow(() -> new ViagemNotFoundException(comissaoDTO.getViagemId()));
            comissao.setViagem(viagem);
        }


        comissao.setPercentualComissao(comissaoDTO.getPercentualComissao());
        comissao.setValorComissao(comissaoDTO.getValorComissao());
        comissao.setValorLiquidoMotorista(comissaoDTO.getValorLiquidoMotorista());
        comissao.setDescontos(comissaoDTO.getDescontos());
        comissao.setDataPagamento(comissaoDTO.getDataPagamento());
        comissao.setPeriodoReferencia(comissaoDTO.getPeriodoReferencia());
        comissao.setStatus(comissaoDTO.getStatus());
        comissao.setTipoComissao(comissaoDTO.getTipoComissao());
        comissao.setFormaPagamento(comissaoDTO.getFormaPagamento());
        comissao.setObservacoes(comissaoDTO.getObservacoes());
        comissao.setUsuarioRegistroId(comissaoDTO.getUsuarioRegistroId());
        comissao.setBonificacoes(comissaoDTO.getBonificacoes());
        comissao.setPenalidades(comissaoDTO.getPenalidades());
        comissao.setOrigem(comissaoDTO.getOrigem());
        comissao.setDestino(comissaoDTO.getDestino());
        comissao.setNumeroNotaFiscal(comissaoDTO.getNumeroNotaFiscal());

        ComissaoMotorista updatedComissao = comissaoMotoristaRepository.save(comissao);
        log.info("Comissão atualizada com ID: {}", updatedComissao.getId());
        return toDTO(updatedComissao);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deletando comissão ID: {}", id);
        if (!comissaoMotoristaRepository.existsById(id)) {
            throw new ComissaoNotFoundException(id);
        }
        comissaoMotoristaRepository.deleteById(id);
        log.info("Comissão deletada com ID: {}", id);
    }

    @Transactional(readOnly = true)
    public ComissaoMotoristaRelatorioDTO gerarRelatorio(
            Long motoristaId,
            LocalDateTime dataInicial,
            LocalDateTime dataFinal,
            StatusComissao status) {

        log.info("Gerando relatório de comissões - Motorista: {}, Período: {} a {}, Status: {}",
                motoristaId, dataInicial, dataFinal, status);

        Motorista motorista = motoristaRepository.findById(motoristaId)
                .orElseThrow(() -> new MotoristaNotFoundException(motoristaId));

        List<ComissaoMotorista> comissoes;
        if (status != null) {
            comissoes = comissaoMotoristaRepository.findByMotoristaIdAndCreatedAtBetweenAndStatus(
                    motoristaId, dataInicial, dataFinal, status);
        } else {
            comissoes = comissaoMotoristaRepository.findByMotoristaIdAndCreatedAtBetween(
                    motoristaId, dataInicial, dataFinal);
        }

        List<ComissaoMotoristaDTO> comissoesDTO = comissoes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        ComissaoMotoristaRelatorioDTO.TotaisDTO totais = calcularTotais(comissoes);

        ComissaoMotoristaRelatorioDTO relatorio = ComissaoMotoristaRelatorioDTO.builder()
                .motoristaId(motoristaId)
                .nomeMotorista(motorista.getNome())
                .periodoInicial(dataInicial)
                .periodoFinal(dataFinal)
                .statusFiltrado(status != null ? status.name() : null)
                .quantidadeComissoes(comissoes.size())
                .totais(totais)
                .comissoes(comissoesDTO)
                .build();

        log.info("Relatório gerado com {} comissões para o motorista {}",
                comissoes.size(), motorista.getNome());

        return relatorio;
    }

    private ComissaoMotoristaRelatorioDTO.TotaisDTO calcularTotais(List<ComissaoMotorista> comissoes) {
        BigDecimal totalValorComissao = comissoes.stream()
                .map(c -> c.getValorComissao() != null ? c.getValorComissao() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalValorLiquido = comissoes.stream()
                .map(c -> c.getValorLiquidoMotorista() != null ? c.getValorLiquidoMotorista() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDescontos = comissoes.stream()
                .map(c -> c.getDescontos() != null ? c.getDescontos() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalBonificacoes = comissoes.stream()
                .map(c -> c.getBonificacoes() != null ? c.getBonificacoes() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPenalidades = comissoes.stream()
                .map(c -> c.getPenalidades() != null ? c.getPenalidades() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ComissaoMotoristaRelatorioDTO.TotaisDTO.builder()
                .valorComissao(totalValorComissao)
                .valorLiquidoMotorista(totalValorLiquido)
                .descontos(totalDescontos)
                .bonificacoes(totalBonificacoes)
                .penalidades(totalPenalidades)
                .build();
    }

    private ComissaoMotoristaDTO toDTO(ComissaoMotorista comissao) {
        ComissaoMotoristaDTO dto = new ComissaoMotoristaDTO();
        dto.setId(comissao.getId());
        dto.setMotoristaId(comissao.getMotorista() != null ? comissao.getMotorista().getId() : null);
        dto.setNomeDoMotorista(comissao.getMotorista().getNome());
        dto.setViagemId(comissao.getViagem() != null ? comissao.getViagem().getId() : null);
        dto.setClienteId(comissao.getCliente() != null ? comissao.getCliente().getId() : null);
        dto.setPercentualComissao(comissao.getPercentualComissao());
        dto.setValorComissao(comissao.getValorComissao());
        dto.setValorLiquidoMotorista(comissao.getValorLiquidoMotorista());
        dto.setDescontos(comissao.getDescontos());
        dto.setDataPagamento(comissao.getDataPagamento());
        dto.setPeriodoReferencia(comissao.getPeriodoReferencia());
        dto.setStatus(comissao.getStatus());
        dto.setTipoComissao(comissao.getTipoComissao());
        dto.setFormaPagamento(comissao.getFormaPagamento());
        dto.setObservacoes(comissao.getObservacoes());
        dto.setUsuarioRegistroId(comissao.getUsuarioRegistroId());
        dto.setBonificacoes(comissao.getBonificacoes());
        dto.setPenalidades(comissao.getPenalidades());
        dto.setOrigem(comissao.getOrigem());
        dto.setDestino(comissao.getDestino());
        dto.setNumeroNotaFiscal(comissao.getNumeroNotaFiscal());
        dto.setCreatedAt(comissao.getCreatedAt());
        dto.setUpdatedAt(comissao.getUpdatedAt());
        return dto;
    }

    private ComissaoMotorista toEntity(ComissaoMotoristaDTO dto) {
        ComissaoMotorista comissao = new ComissaoMotorista();
        comissao.setPercentualComissao(dto.getPercentualComissao());
        comissao.setValorComissao(dto.getValorComissao());
        comissao.setValorLiquidoMotorista(dto.getValorLiquidoMotorista());
        comissao.setDescontos(dto.getDescontos());
        comissao.setDataPagamento(dto.getDataPagamento());
        comissao.setPeriodoReferencia(dto.getPeriodoReferencia());
        comissao.setStatus(dto.getStatus());
        comissao.setTipoComissao(dto.getTipoComissao());
        comissao.setFormaPagamento(dto.getFormaPagamento());
        comissao.setObservacoes(dto.getObservacoes());
        comissao.setUsuarioRegistroId(dto.getUsuarioRegistroId());
        comissao.setBonificacoes(dto.getBonificacoes());
        comissao.setPenalidades(dto.getPenalidades());
        comissao.setOrigem(dto.getOrigem());
        comissao.setDestino(dto.getDestino());
        comissao.setNumeroNotaFiscal(dto.getNumeroNotaFiscal());
        return comissao;
    }
}
