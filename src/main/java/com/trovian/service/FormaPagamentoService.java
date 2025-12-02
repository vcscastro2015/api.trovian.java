package com.trovian.service;

import com.trovian.dto.FormaPagamentoDTO;
import com.trovian.entity.FormaPagamento;
import com.trovian.repository.FormaPagamentoRepository;
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
public class FormaPagamentoService {

    private final FormaPagamentoRepository formaPagamentoRepository;

    @Transactional
    public FormaPagamentoDTO create(FormaPagamentoDTO dto) {
        log.info("Criando forma de pagamento: {}", dto.getNome());
        FormaPagamento formaPagamento = toEntity(dto);
        FormaPagamento saved = formaPagamentoRepository.save(formaPagamento);
        log.info("Forma de pagamento criada com sucesso. ID: {}", saved.getId());
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<FormaPagamentoDTO> findAll(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return formaPagamentoRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public FormaPagamentoDTO findById(Long id) {
        FormaPagamento formaPagamento = formaPagamentoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Forma de pagamento não encontrada com ID: " + id));
        return toDTO(formaPagamento);
    }

    @Transactional
    public FormaPagamentoDTO update(Long id, FormaPagamentoDTO dto) {
        FormaPagamento formaPagamento = formaPagamentoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Forma de pagamento não encontrada com ID: " + id));
        updateEntityFromDTO(formaPagamento, dto);
        FormaPagamento updated = formaPagamentoRepository.save(formaPagamento);
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!formaPagamentoRepository.existsById(id)) {
            throw new RuntimeException("Forma de pagamento não encontrada com ID: " + id);
        }
        formaPagamentoRepository.deleteById(id);
    }

    private FormaPagamentoDTO toDTO(FormaPagamento entity) {
        FormaPagamentoDTO dto = new FormaPagamentoDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setDescricao(entity.getDescricao());
        dto.setTipo(entity.getTipo());
        dto.setPrazoMedioDias(entity.getPrazoMedioDias());
        dto.setPermiteParcelamento(entity.getPermiteParcelamento());
        dto.setStatus(entity.getStatus());
        return dto;
    }

    private FormaPagamento toEntity(FormaPagamentoDTO dto) {
        FormaPagamento entity = new FormaPagamento();
        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        entity.setTipo(dto.getTipo());
        entity.setPrazoMedioDias(dto.getPrazoMedioDias());
        entity.setPermiteParcelamento(dto.getPermiteParcelamento() != null ? dto.getPermiteParcelamento() : false);
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
        return entity;
    }

    private void updateEntityFromDTO(FormaPagamento entity, FormaPagamentoDTO dto) {
        entity.setNome(dto.getNome());
        entity.setDescricao(dto.getDescricao());
        entity.setTipo(dto.getTipo());
        entity.setPrazoMedioDias(dto.getPrazoMedioDias());
        entity.setPermiteParcelamento(dto.getPermiteParcelamento());
        entity.setStatus(dto.getStatus());
    }
}
