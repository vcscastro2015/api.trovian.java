package com.trovian.service;

import com.trovian.dto.CentroCustoDTO;
import com.trovian.entity.CentroCusto;
import com.trovian.entity.Cliente;
import com.trovian.repository.CentroCustoRepository;
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
public class CentroCustoService {

    private final CentroCustoRepository centroCustoRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public CentroCustoDTO create(CentroCustoDTO dto) {
        log.info("Criando centro de custo: {}", dto.getNome());
        CentroCusto centroCusto = toEntity(dto);
        CentroCusto saved = centroCustoRepository.save(centroCusto);
        log.info("Centro de custo criado com sucesso. ID: {}", saved.getId());
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<CentroCustoDTO> findAll(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return centroCustoRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public CentroCustoDTO findById(Long id) {
        CentroCusto centroCusto = centroCustoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Centro de custo não encontrado com ID: " + id));
        return toDTO(centroCusto);
    }

    @Transactional
    public CentroCustoDTO update(Long id, CentroCustoDTO dto) {
        CentroCusto centroCusto = centroCustoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Centro de custo não encontrado com ID: " + id));
        updateEntityFromDTO(centroCusto, dto);
        CentroCusto updated = centroCustoRepository.save(centroCusto);
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!centroCustoRepository.existsById(id)) {
            throw new RuntimeException("Centro de custo não encontrado com ID: " + id);
        }
        centroCustoRepository.deleteById(id);
    }

    private CentroCustoDTO toDTO(CentroCusto entity) {
        CentroCustoDTO dto = new CentroCustoDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setDescricao(entity.getDescricao());
        dto.setCodigo(entity.getCodigo());
        dto.setStatus(entity.getStatus());
        if (entity.getCliente() != null) {
            dto.setClienteId(entity.getCliente().getId());
            dto.setClienteNome(entity.getCliente().getNome());
        }
        return dto;
    }

    private CentroCusto toEntity(CentroCustoDTO dto) {
        CentroCusto entity = new CentroCusto();
        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        entity.setCodigo(dto.getCodigo());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
        if (dto.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(dto.getClienteId()).orElse(null);
            entity.setCliente(cliente);
        }
        return entity;
    }

    private void updateEntityFromDTO(CentroCusto entity, CentroCustoDTO dto) {
        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        entity.setCodigo(dto.getCodigo());
        entity.setStatus(dto.getStatus());
        if (dto.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(dto.getClienteId()).orElse(null);
            entity.setCliente(cliente);
        } else {
            entity.setCliente(null);
        }
    }
}
