package com.trovian.service;

import com.trovian.dto.MovimentacaoEstoqueDTO;
import com.trovian.entity.MovimentacaoEstoque;
import com.trovian.entity.OrdemServico;
import com.trovian.entity.Peca;
import com.trovian.enums.TipoMovimentacaoEstoque;
import com.trovian.repository.MovimentacaoEstoqueRepository;
import com.trovian.repository.OrdemServicoRepository;
import com.trovian.repository.PecaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final PecaRepository pecaRepository;
    private final OrdemServicoRepository ordemServicoRepository;

    @Transactional
    public MovimentacaoEstoqueDTO create(MovimentacaoEstoqueDTO dto) {
        log.info("Criando movimentação de estoque para peça ID: {}", dto.getPecaId());

        Peca peca = pecaRepository.findById(dto.getPecaId())
            .orElseThrow(() -> new RuntimeException("Peça não encontrada com ID: " + dto.getPecaId()));

        MovimentacaoEstoque movimentacao = toEntity(dto);
        movimentacao.setPeca(peca);

        processarMovimentacao(peca, dto.getTipoMovimentacao(), dto.getQuantidade(), dto.getValorUnitario());

        MovimentacaoEstoque saved = movimentacaoEstoqueRepository.save(movimentacao);
        pecaRepository.save(peca);

        log.info("Movimentação de estoque criada com sucesso. ID: {}", saved.getId());
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<MovimentacaoEstoqueDTO> findAll(int page, int size, String sortBy, String direction) {
        log.info("Buscando movimentações de estoque - página: {}, tamanho: {}", page, size);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return movimentacaoEstoqueRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public MovimentacaoEstoqueDTO findById(Long id) {
        log.info("Buscando movimentação de estoque por ID: {}", id);
        MovimentacaoEstoque movimentacao = movimentacaoEstoqueRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Movimentação de estoque não encontrada com ID: " + id));
        return toDTO(movimentacao);
    }

    @Transactional(readOnly = true)
    public Page<MovimentacaoEstoqueDTO> findByPecaId(Long pecaId, Pageable pageable) {
        return movimentacaoEstoqueRepository.findByPecaId(pecaId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoEstoqueDTO> findHistoricoByPecaId(Long pecaId) {
        log.info("Buscando histórico de movimentações da peça ID: {}", pecaId);
        return movimentacaoEstoqueRepository.findHistoricoByPecaId(pecaId)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<MovimentacaoEstoqueDTO> findByOrdemServicoId(Long ordemServicoId, Pageable pageable) {
        return movimentacaoEstoqueRepository.findByOrdemServicoId(ordemServicoId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<MovimentacaoEstoqueDTO> findByTipoMovimentacao(TipoMovimentacaoEstoque tipo, Pageable pageable) {
        return movimentacaoEstoqueRepository.findByTipoMovimentacao(tipo, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<MovimentacaoEstoqueDTO> findByPeriodo(LocalDateTime inicio, LocalDateTime fim, Pageable pageable) {
        return movimentacaoEstoqueRepository.findByDataMovimentacaoBetween(inicio, fim, pageable).map(this::toDTO);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deletando movimentação de estoque ID: {}", id);

        MovimentacaoEstoque movimentacao = movimentacaoEstoqueRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Movimentação de estoque não encontrada com ID: " + id));

        Peca peca = movimentacao.getPeca();
        reverterMovimentacao(peca, movimentacao.getTipoMovimentacao(), movimentacao.getQuantidade());

        movimentacaoEstoqueRepository.deleteById(id);
        pecaRepository.save(peca);

        log.info("Movimentação de estoque deletada com sucesso. ID: {}", id);
    }

    private void processarMovimentacao(Peca peca, TipoMovimentacaoEstoque tipo, Integer quantidade, java.math.BigDecimal valorUnitario) {
        switch (tipo) {
            case ENTRADA:
                peca.adicionarEstoque(quantidade);
                if (valorUnitario != null) {
                    peca.atualizarValorMedio(valorUnitario, quantidade);
                }
                break;
            case SAIDA:
                peca.removerEstoque(quantidade);
                break;
            case AJUSTE:
                peca.setEstoqueAtual(quantidade);
                break;
        }
    }

    private void reverterMovimentacao(Peca peca, TipoMovimentacaoEstoque tipo, Integer quantidade) {
        switch (tipo) {
            case ENTRADA:
                peca.removerEstoque(quantidade);
                break;
            case SAIDA:
                peca.adicionarEstoque(quantidade);
                break;
            case AJUSTE:
                log.warn("Reversão de ajuste de estoque não implementada. Revisar manualmente.");
                break;
        }
    }

    private MovimentacaoEstoqueDTO toDTO(MovimentacaoEstoque entity) {
        MovimentacaoEstoqueDTO dto = new MovimentacaoEstoqueDTO();
        dto.setId(entity.getId());
        dto.setPecaId(entity.getPeca().getId());
        dto.setPecaCodigo(entity.getPeca().getCodigo());
        dto.setPecaDescricao(entity.getPeca().getDescricao());
        dto.setTipoMovimentacao(entity.getTipoMovimentacao());
        dto.setQuantidade(entity.getQuantidade());
        dto.setDataMovimentacao(entity.getDataMovimentacao());
        dto.setValorUnitario(entity.getValorUnitario());
        dto.setObservacao(entity.getObservacao());
        dto.setUsuario(entity.getUsuario());

        if (entity.getOrdemServico() != null) {
            dto.setOrdemServicoId(entity.getOrdemServico().getId());
            dto.setOrdemServicoNumero(entity.getOrdemServico().getNumeroOs());
        }

        return dto;
    }

    private MovimentacaoEstoque toEntity(MovimentacaoEstoqueDTO dto) {
        MovimentacaoEstoque entity = new MovimentacaoEstoque();
        entity.setTipoMovimentacao(dto.getTipoMovimentacao());
        entity.setQuantidade(dto.getQuantidade());
        entity.setDataMovimentacao(dto.getDataMovimentacao() != null ? dto.getDataMovimentacao() : LocalDateTime.now());
        entity.setValorUnitario(dto.getValorUnitario());
        entity.setObservacao(dto.getObservacao());
        entity.setUsuario(dto.getUsuario());

        if (dto.getOrdemServicoId() != null) {
            OrdemServico os = ordemServicoRepository.findById(dto.getOrdemServicoId())
                .orElse(null);
            entity.setOrdemServico(os);
        }

        return entity;
    }
}
