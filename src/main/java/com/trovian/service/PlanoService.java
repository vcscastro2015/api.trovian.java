package com.trovian.service;

import com.trovian.dto.PlanoDTO;
import com.trovian.entity.Plano;
import com.trovian.repository.PlanoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanoService {

    private final PlanoRepository planoRepository;

    @Transactional(readOnly = true)
    public Page<PlanoDTO> findAll(Pageable pageable) {
        log.info("Buscando todos os planos - Página: {}, Tamanho: {}", pageable.getPageNumber(), pageable.getPageSize());
        return planoRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public PlanoDTO findById(Long id) {
        log.info("Buscando plano com ID: {}", id);
        Plano plano = planoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano não encontrado com ID: " + id));
        return toDTO(plano);
    }

    @Transactional(readOnly = true)
    public Page<PlanoDTO> findAtivos(Pageable pageable) {
        log.info("Buscando planos ativos");
        return planoRepository.findByAtivo(true, pageable).map(this::toDTO);
    }

    @Transactional
    public PlanoDTO create(PlanoDTO dto) {
        log.info("Criando novo plano: {}", dto.getNome());
        Plano plano = toEntity(dto);
        Plano saved = planoRepository.save(plano);
        log.info("Plano criado com sucesso. ID: {}, UUID: {}", saved.getId(), saved.getUuid());
        return toDTO(saved);
    }

    @Transactional
    public PlanoDTO update(Long id, PlanoDTO dto) {
        log.info("Atualizando plano com ID: {}", id);
        Plano plano = planoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano não encontrado com ID: " + id));

        plano.setNome(dto.getNome());
        plano.setDescricao(dto.getDescricao());
        plano.setValor(dto.getValor());
        plano.setPeriodicidade(dto.getPeriodicidade());
        plano.setLimiteUsuarios(dto.getLimiteUsuarios());
        plano.setLimiteVeiculos(dto.getLimiteVeiculos());
        plano.setLimiteChecklists(dto.getLimiteChecklists());
        plano.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : plano.getAtivo());

        Plano updated = planoRepository.save(plano);
        log.info("Plano atualizado com sucesso. ID: {}", id);
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deletando plano com ID: {}", id);
        if (!planoRepository.existsById(id)) {
            throw new RuntimeException("Plano não encontrado com ID: " + id);
        }
        planoRepository.deleteById(id);
        log.info("Plano deletado com sucesso. ID: {}", id);
    }

    private PlanoDTO toDTO(Plano plano) {
        PlanoDTO dto = new PlanoDTO();
        dto.setId(plano.getId());
        dto.setUuid(plano.getUuid());
        dto.setNome(plano.getNome());
        dto.setDescricao(plano.getDescricao());
        dto.setValor(plano.getValor());
        dto.setPeriodicidade(plano.getPeriodicidade());
        dto.setLimiteUsuarios(plano.getLimiteUsuarios());
        dto.setLimiteVeiculos(plano.getLimiteVeiculos());
        dto.setLimiteChecklists(plano.getLimiteChecklists());
        dto.setAtivo(plano.getAtivo());
        dto.setDataCriacao(plano.getDataCriacao());
        dto.setUpdatedAt(plano.getUpdatedAt());
        return dto;
    }

    private Plano toEntity(PlanoDTO dto) {
        Plano plano = new Plano();
        plano.setNome(dto.getNome());
        plano.setDescricao(dto.getDescricao());
        plano.setValor(dto.getValor());
        plano.setPeriodicidade(dto.getPeriodicidade());
        plano.setLimiteUsuarios(dto.getLimiteUsuarios());
        plano.setLimiteVeiculos(dto.getLimiteVeiculos());
        plano.setLimiteChecklists(dto.getLimiteChecklists());
        plano.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        return plano;
    }
}
