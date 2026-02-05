package com.trovian.service;

import com.trovian.dto.CategoriaContaDTO;
import com.trovian.entity.CategoriaConta;
import com.trovian.entity.Cliente;
import com.trovian.enums.TipoConta;
import com.trovian.repository.CategoriaContaRepository;
import com.trovian.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriaContaService {

    private final CategoriaContaRepository categoriaContaRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public CategoriaContaDTO create(CategoriaContaDTO dto) {
        log.info("Criando categoria de conta: {}", dto.getNome());

        // Validar cliente
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + dto.getClienteId()));

        CategoriaConta categoria = toEntity(dto, cliente);
        CategoriaConta saved = categoriaContaRepository.save(categoria);
        log.info("Categoria criada com sucesso. ID: {}", saved.getId());
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<CategoriaContaDTO> findAll(int page, int size, String sortBy, String direction) {
        log.info("Buscando categorias - página: {}, tamanho: {}", page, size);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return categoriaContaRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<CategoriaContaDTO> findByCliente(Long clienteId, Pageable pageable) {
        log.info("Buscando categorias por cliente ID: {}", clienteId);
        return categoriaContaRepository.findByClienteId(clienteId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public CategoriaContaDTO findById(Long id) {
        log.info("Buscando categoria por ID: {}", id);
        CategoriaConta categoria = categoriaContaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada com ID: " + id));
        return toDTO(categoria);
    }

    @Transactional
    public CategoriaContaDTO update(Long id, CategoriaContaDTO dto) {
        log.info("Atualizando categoria ID: {}", id);
        CategoriaConta categoria = categoriaContaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada com ID: " + id));

        // Validar cliente
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + dto.getClienteId()));

        updateEntityFromDTO(categoria, dto);
        categoria.setCliente(cliente);
        CategoriaConta updated = categoriaContaRepository.save(categoria);
        log.info("Categoria atualizada com sucesso. ID: {}", id);
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deletando categoria ID: {}", id);
        if (!categoriaContaRepository.existsById(id)) {
            throw new RuntimeException("Categoria não encontrada com ID: " + id);
        }
        categoriaContaRepository.deleteById(id);
        log.info("Categoria deletada com sucesso. ID: {}", id);
    }

    @Transactional(readOnly = true)
    public Page<CategoriaContaDTO> findByTipo(String tipo, Pageable pageable) {
        return categoriaContaRepository.findByTipo(TipoConta.valueOf(tipo), pageable).map(this::toDTO);
    }

    private CategoriaContaDTO toDTO(CategoriaConta entity) {
        CategoriaContaDTO dto = new CategoriaContaDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setDescricao(entity.getDescricao());
        dto.setTipo(entity.getTipo());
        dto.setCodigo(entity.getCodigo());
        dto.setStatus(entity.getStatus());
        if (entity.getCategoriaPai() != null) {
            dto.setCategoriaPaiId(entity.getCategoriaPai().getId());
            dto.setCategoriaPaiNome(entity.getCategoriaPai().getNome());
        }
        dto.setClienteId(entity.getCliente().getId());
        return dto;
    }

    private CategoriaConta toEntity(CategoriaContaDTO dto, Cliente cliente) {
        CategoriaConta entity = new CategoriaConta();
        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        entity.setTipo(dto.getTipo());
        entity.setCodigo(dto.getCodigo());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
        entity.setPodeEditar(dto.getPodeEditar() != null ? dto.getPodeEditar(): true);
        if (dto.getCategoriaPaiId() != null) {
            CategoriaConta pai = categoriaContaRepository.findById(dto.getCategoriaPaiId())
                .orElse(null);
            entity.setCategoriaPai(pai);
        }
        entity.setCliente(cliente);
        return entity;
    }

    private void updateEntityFromDTO(CategoriaConta entity, CategoriaContaDTO dto) {
        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        entity.setTipo(dto.getTipo());
        entity.setCodigo(dto.getCodigo());
        entity.setStatus(dto.getStatus());
        entity.setPodeEditar(dto.getPodeEditar());
        if (dto.getCategoriaPaiId() != null) {
            CategoriaConta pai = categoriaContaRepository.findById(dto.getCategoriaPaiId())
                .orElse(null);
            entity.setCategoriaPai(pai);
        } else {
            entity.setCategoriaPai(null);
        }
    }
}
