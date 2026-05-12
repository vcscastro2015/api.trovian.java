package com.trovian.service;

import com.trovian.dto.PecaDTO;
import com.trovian.entity.Cliente;
import com.trovian.entity.Peca;
import com.trovian.repository.ClienteRepository;
import com.trovian.repository.PecaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PecaService {

    private final PecaRepository pecaRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public PecaDTO create(PecaDTO dto) {
        log.info("Criando peça: {}", dto.getCodigo());

        if (pecaRepository.findByCodigoAndClienteId(dto.getCodigo(), dto.getClienteId()).isPresent()) {
            throw new RuntimeException("Já existe uma peça com o código: " + dto.getCodigo());
        }

        // Validar cliente
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + dto.getClienteId()));

        Peca peca = toEntity(dto, cliente);
        Peca saved = pecaRepository.save(peca);
        log.info("Peça criada com sucesso. ID: {}", saved.getId());
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<PecaDTO> findAll(int page, int size, String sortBy, String direction) {
        log.info("Buscando peças - página: {}, tamanho: {}", page, size);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return pecaRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<PecaDTO> findByCliente(Long clienteId, Pageable pageable) {
        return pecaRepository.findByClienteId(clienteId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public PecaDTO findById(Long id) {
        log.info("Buscando peça por ID: {}", id);
        Peca peca = pecaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Peça não encontrada com ID: " + id));
        return toDTO(peca);
    }

    @Transactional(readOnly = true)
    public PecaDTO findByCodigo(String codigo) {
        log.info("Buscando peça por código: {}", codigo);
        Peca peca = pecaRepository.findByCodigo(codigo)
            .orElseThrow(() -> new RuntimeException("Peça não encontrada com código: " + codigo));
        return toDTO(peca);
    }

    @Transactional(readOnly = true)
    public Page<PecaDTO> findByCategoria(String categoria, Pageable pageable) {
        return pecaRepository.findByCategoria(categoria, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public List<PecaDTO> findPecasEstoqueBaixo(Long clienteId) {
        log.info("Buscando peças com estoque baixo");
        return pecaRepository.findPecasEstoqueBaixo(clienteId)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> findAllCategorias(Long clienteId) {
        log.info("Buscando todas as categorias");
        return pecaRepository.findAllCategorias(clienteId);
    }

    @Transactional(readOnly = true)
    public Page<PecaDTO> findByDescricaoContaining(Long clienteId, String descricao, Pageable pageable) {
        return pecaRepository.findByDescricaoContaining(clienteId, descricao, pageable).map(this::toDTO);
    }

    @Transactional
    public PecaDTO update(Long id, PecaDTO dto) {
        log.info("Atualizando peça ID: {}", id);
        Peca peca = pecaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Peça não encontrada com ID: " + id));

        // Validar cliente
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + dto.getClienteId()));

        updateEntityFromDTO(peca, dto);
        peca.setCliente(cliente);
        Peca updated = pecaRepository.save(peca);
        log.info("Peça atualizada com sucesso. ID: {}", id);
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deletando peça ID: {}", id);
        if (!pecaRepository.existsById(id)) {
            throw new RuntimeException("Peça não encontrada com ID: " + id);
        }
        pecaRepository.deleteById(id);
        log.info("Peça deletada com sucesso. ID: {}", id);
    }

    private PecaDTO toDTO(Peca entity) {
        PecaDTO dto = new PecaDTO();
        dto.setId(entity.getId());
        dto.setCodigo(entity.getCodigo());
        dto.setDescricao(entity.getDescricao());
        dto.setCategoria(entity.getCategoria());
        dto.setEstoqueMinimo(entity.getEstoqueMinimo());
        dto.setEstoqueAtual(entity.getEstoqueAtual());
        dto.setValorMedio(entity.getValorMedio());
        dto.setAplicacaoVeiculos(entity.getAplicacaoVeiculos());
        dto.setStatus(entity.getStatus());
        dto.setObservacoes(entity.getObservacoes());
        dto.setEstoqueBaixo(entity.verificarEstoqueBaixo());
        dto.setClienteId(entity.getCliente().getId());
        return dto;
    }

    private Peca toEntity(PecaDTO dto, Cliente cliente) {
        Peca entity = new Peca();
        entity.setCodigo(dto.getCodigo());
        entity.setDescricao(dto.getDescricao());
        entity.setCategoria(dto.getCategoria());
        entity.setEstoqueMinimo(dto.getEstoqueMinimo());
        entity.setEstoqueAtual(dto.getEstoqueAtual() != null ? dto.getEstoqueAtual() : 0);
        entity.setValorMedio(dto.getValorMedio());
        entity.setAplicacaoVeiculos(dto.getAplicacaoVeiculos());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
        entity.setObservacoes(dto.getObservacoes());
        entity.setCliente(cliente);
        return entity;
    }

    private void updateEntityFromDTO(Peca entity, PecaDTO dto) {
        entity.setCodigo(dto.getCodigo());
        entity.setDescricao(dto.getDescricao());
        entity.setCategoria(dto.getCategoria());
        entity.setEstoqueMinimo(dto.getEstoqueMinimo());
        entity.setValorMedio(dto.getValorMedio());
        entity.setAplicacaoVeiculos(dto.getAplicacaoVeiculos());
        entity.setStatus(dto.getStatus());
        entity.setObservacoes(dto.getObservacoes());
    }
}
