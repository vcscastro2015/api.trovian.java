package com.trovian.service;

import com.trovian.dto.FuncionalidadeDTO;
import com.trovian.entity.Funcionalidade;
import com.trovian.repository.FuncionalidadeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FuncionalidadeService {

    private final FuncionalidadeRepository funcionalidadeRepository;

    /**
     * Busca todas as funcionalidades com paginação
     */
    @Transactional(readOnly = true)
    public Page<FuncionalidadeDTO> findAllPaginated(Pageable pageable) {
        log.info("Buscando todas as funcionalidades com paginação - Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<Funcionalidade> funcionalidades = funcionalidadeRepository.findAll(pageable);
        return funcionalidades.map(this::toDTO);
    }

    /**
     * Busca todas as funcionalidades sem paginação
     */
    @Transactional(readOnly = true)
    public List<FuncionalidadeDTO> findAll() {
        log.info("Buscando todas as funcionalidades");
        List<Funcionalidade> funcionalidades = funcionalidadeRepository.findAll();
        return funcionalidades.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca funcionalidade por ID
     */
    @Transactional(readOnly = true)
    public FuncionalidadeDTO findById(Long id) {
        log.info("Buscando funcionalidade com ID: {}", id);
        Funcionalidade funcionalidade = funcionalidadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionalidade não encontrada com ID: " + id));
        return toDTO(funcionalidade);
    }

    /**
     * Busca funcionalidade por código
     */
    @Transactional(readOnly = true)
    public FuncionalidadeDTO findByCodigo(String codigo) {
        log.info("Buscando funcionalidade com código: {}", codigo);
        Funcionalidade funcionalidade = funcionalidadeRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Funcionalidade não encontrada com código: " + codigo));
        return toDTO(funcionalidade);
    }

    /**
     * Cria uma nova funcionalidade
     */
    @Transactional
    public FuncionalidadeDTO create(FuncionalidadeDTO funcionalidadeDTO) {
        log.info("Criando nova funcionalidade: {}", funcionalidadeDTO.getNome());

        // Valida se já existe código cadastrado
        funcionalidadeRepository.findByCodigo(funcionalidadeDTO.getCodigo())
                .ifPresent(f -> {
                    throw new RuntimeException("Já existe uma funcionalidade cadastrada com o código: " + funcionalidadeDTO.getCodigo());
                });

        Funcionalidade funcionalidade = toEntity(funcionalidadeDTO);
        Funcionalidade savedFuncionalidade = funcionalidadeRepository.save(funcionalidade);
        log.info("Funcionalidade criada com sucesso. ID: {}", savedFuncionalidade.getId());
        return toDTO(savedFuncionalidade);
    }

    /**
     * Atualiza uma funcionalidade existente
     */
    @Transactional
    public FuncionalidadeDTO update(Long id, FuncionalidadeDTO funcionalidadeDTO) {
        log.info("Atualizando funcionalidade com ID: {}", id);

        Funcionalidade funcionalidade = funcionalidadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionalidade não encontrada com ID: " + id));

        // Valida se o código já existe em outra funcionalidade
        funcionalidadeRepository.findByCodigo(funcionalidadeDTO.getCodigo())
                .ifPresent(f -> {
                    if (!f.getId().equals(id)) {
                        throw new RuntimeException("Já existe outra funcionalidade cadastrada com o código: " + funcionalidadeDTO.getCodigo());
                    }
                });

        // Atualiza os campos
        funcionalidade.setCodigo(funcionalidadeDTO.getCodigo());
        funcionalidade.setNome(funcionalidadeDTO.getNome());
        funcionalidade.setCategoria(funcionalidadeDTO.getCategoria());
        funcionalidade.setOrdemMenu(funcionalidadeDTO.getOrdemMenu());

        Funcionalidade updatedFuncionalidade = funcionalidadeRepository.save(funcionalidade);
        log.info("Funcionalidade atualizada com sucesso. ID: {}", id);
        return toDTO(updatedFuncionalidade);
    }

    /**
     * Deleta uma funcionalidade
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deletando funcionalidade com ID: {}", id);

        if (!funcionalidadeRepository.existsById(id)) {
            throw new RuntimeException("Funcionalidade não encontrada com ID: " + id);
        }

        funcionalidadeRepository.deleteById(id);
        log.info("Funcionalidade deletada com sucesso. ID: {}", id);
    }

    /**
     * Converte Entity para DTO
     */
    private FuncionalidadeDTO toDTO(Funcionalidade funcionalidade) {
        FuncionalidadeDTO dto = new FuncionalidadeDTO();
        dto.setId(funcionalidade.getId());
        dto.setCodigo(funcionalidade.getCodigo());
        dto.setNome(funcionalidade.getNome());
        dto.setCategoria(funcionalidade.getCategoria());
        dto.setOrdemMenu(funcionalidade.getOrdemMenu());
        return dto;
    }

    /**
     * Converte DTO para Entity
     */
    private Funcionalidade toEntity(FuncionalidadeDTO dto) {
        Funcionalidade funcionalidade = new Funcionalidade();
        funcionalidade.setCodigo(dto.getCodigo());
        funcionalidade.setNome(dto.getNome());
        funcionalidade.setCategoria(dto.getCategoria());
        funcionalidade.setOrdemMenu(dto.getOrdemMenu());
        return funcionalidade;
    }
}
