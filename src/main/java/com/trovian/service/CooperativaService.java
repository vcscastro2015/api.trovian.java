package com.trovian.service;

import com.trovian.dto.CooperativaDTO;
import com.trovian.entity.Cooperativa;
import com.trovian.repository.CooperativaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CooperativaService {

    private final CooperativaRepository cooperativaRepository;

    /**
     * Busca todas as cooperativas
     */
    @Transactional(readOnly = true)
    public List<CooperativaDTO> findAll() {
        log.info("Buscando todas as cooperativas");
        return cooperativaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca cooperativa por ID
     */
    @Transactional(readOnly = true)
    public CooperativaDTO findById(Long id) {
        log.info("Buscando cooperativa com ID: {}", id);
        Cooperativa cooperativa = cooperativaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cooperativa não encontrada com ID: " + id));
        return toDTO(cooperativa);
    }

    /**
     * Cria uma nova cooperativa
     */
    @Transactional
    public CooperativaDTO create(CooperativaDTO cooperativaDTO) {
        log.info("Criando nova cooperativa: {}", cooperativaDTO.getNome());

        // Valida se já existe CNPJ cadastrado
        cooperativaRepository.findByCnpj(cooperativaDTO.getCnpj())
                .ifPresent(c -> {
                    throw new RuntimeException("Já existe uma cooperativa cadastrada com o CNPJ: " + cooperativaDTO.getCnpj());
                });

        Cooperativa cooperativa = toEntity(cooperativaDTO);
        Cooperativa savedCooperativa = cooperativaRepository.save(cooperativa);
        log.info("Cooperativa criada com sucesso. ID: {}", savedCooperativa.getId());
        return toDTO(savedCooperativa);
    }

    /**
     * Atualiza uma cooperativa existente
     */
    @Transactional
    public CooperativaDTO update(Long id, CooperativaDTO cooperativaDTO) {
        log.info("Atualizando cooperativa com ID: {}", id);

        Cooperativa cooperativa = cooperativaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cooperativa não encontrada com ID: " + id));

        // Valida se o CNPJ já existe em outra cooperativa
        cooperativaRepository.findByCnpj(cooperativaDTO.getCnpj())
                .ifPresent(c -> {
                    if (!c.getId().equals(id)) {
                        throw new RuntimeException("Já existe outra cooperativa cadastrada com o CNPJ: " + cooperativaDTO.getCnpj());
                    }
                });

        cooperativa.setNome(cooperativaDTO.getNome());
        cooperativa.setCnpj(cooperativaDTO.getCnpj());
        cooperativa.setEndereco(cooperativaDTO.getEndereco());
        cooperativa.setCidade(cooperativaDTO.getCidade());
        cooperativa.setUf(cooperativaDTO.getUf());
        cooperativa.setCep(cooperativaDTO.getCep());
        cooperativa.setAtiva(cooperativaDTO.getAtiva());

        Cooperativa updatedCooperativa = cooperativaRepository.save(cooperativa);
        log.info("Cooperativa atualizada com sucesso. ID: {}", id);
        return toDTO(updatedCooperativa);
    }

    /**
     * Deleta uma cooperativa
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deletando cooperativa com ID: {}", id);

        if (!cooperativaRepository.existsById(id)) {
            throw new RuntimeException("Cooperativa não encontrada com ID: " + id);
        }

        cooperativaRepository.deleteById(id);
        log.info("Cooperativa deletada com sucesso. ID: {}", id);
    }

    /**
     * Busca cooperativas por nome
     */
    @Transactional(readOnly = true)
    public List<CooperativaDTO> searchByNome(String nome) {
        log.info("Buscando cooperativas com nome contendo: {}", nome);
        return cooperativaRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca cooperativa por CNPJ
     */
    @Transactional(readOnly = true)
    public CooperativaDTO findByCnpj(String cnpj) {
        log.info("Buscando cooperativa com CNPJ: {}", cnpj);
        Cooperativa cooperativa = cooperativaRepository.findByCnpj(cnpj)
                .orElseThrow(() -> new RuntimeException("Cooperativa não encontrada com CNPJ: " + cnpj));
        return toDTO(cooperativa);
    }

    /**
     * Busca cooperativas por cidade
     */
    @Transactional(readOnly = true)
    public List<CooperativaDTO> findByCidade(String cidade) {
        log.info("Buscando cooperativas na cidade: {}", cidade);
        return cooperativaRepository.findByCidadeIgnoreCase(cidade)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca cooperativas por UF
     */
    @Transactional(readOnly = true)
    public List<CooperativaDTO> findByUf(String uf) {
        log.info("Buscando cooperativas na UF: {}", uf);
        return cooperativaRepository.findByUfIgnoreCase(uf)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca cooperativas ativas ou inativas
     */
    @Transactional(readOnly = true)
    public List<CooperativaDTO> findByAtiva(Boolean ativa) {
        log.info("Buscando cooperativas ativas: {}", ativa);
        return cooperativaRepository.findByAtiva(ativa)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca cooperativas por cidade e UF
     */
    @Transactional(readOnly = true)
    public List<CooperativaDTO> findByCidadeAndUf(String cidade, String uf) {
        log.info("Buscando cooperativas na cidade: {} e UF: {}", cidade, uf);
        return cooperativaRepository.findByCidadeIgnoreCaseAndUfIgnoreCase(cidade, uf)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converte Entity para DTO
     */
    private CooperativaDTO toDTO(Cooperativa cooperativa) {
        CooperativaDTO dto = new CooperativaDTO();
        dto.setId(cooperativa.getId());
        dto.setNome(cooperativa.getNome());
        dto.setCnpj(cooperativa.getCnpj());
        dto.setEndereco(cooperativa.getEndereco());
        dto.setCidade(cooperativa.getCidade());
        dto.setUf(cooperativa.getUf());
        dto.setCep(cooperativa.getCep());
        dto.setAtiva(cooperativa.getAtiva());
        dto.setDataCadastro(cooperativa.getDataCadastro());
        dto.setUpdatedAt(cooperativa.getUpdatedAt());
        return dto;
    }

    /**
     * Converte DTO para Entity
     */
    private Cooperativa toEntity(CooperativaDTO dto) {
        Cooperativa cooperativa = new Cooperativa();
        cooperativa.setNome(dto.getNome());
        cooperativa.setCnpj(dto.getCnpj());
        cooperativa.setEndereco(dto.getEndereco());
        cooperativa.setCidade(dto.getCidade());
        cooperativa.setUf(dto.getUf());
        cooperativa.setCep(dto.getCep());
        cooperativa.setAtiva(dto.getAtiva() != null ? dto.getAtiva() : true);
        return cooperativa;
    }
}
