package com.trovian.service;

import com.trovian.dto.ItemManutencaoDTO;
import com.trovian.entity.Fornecedor;
import com.trovian.entity.ItemManutencao;
import com.trovian.entity.OrdemServico;
import com.trovian.repository.FornecedorRepository;
import com.trovian.repository.ItemManutencaoRepository;
import com.trovian.repository.OrdemServicoRepository;
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
public class ItemManutencaoService {

    private final ItemManutencaoRepository itemManutencaoRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final FornecedorRepository fornecedorRepository;

    @Transactional
    public ItemManutencaoDTO create(ItemManutencaoDTO dto) {
        log.info("Criando item de manutenção");
        ItemManutencao item = toEntity(dto);
        ItemManutencao saved = itemManutencaoRepository.save(item);

        OrdemServico os = saved.getOrdemServico();
        os.calcularValorTotal();
        ordemServicoRepository.save(os);

        log.info("Item de manutenção criado com sucesso. ID: {}", saved.getId());
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<ItemManutencaoDTO> findAll(int page, int size, String sortBy, String direction) {
        log.info("Buscando itens de manutenção - página: {}, tamanho: {}", page, size);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return itemManutencaoRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public ItemManutencaoDTO findById(Long id) {
        log.info("Buscando item de manutenção por ID: {}", id);
        ItemManutencao item = itemManutencaoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item de manutenção não encontrado com ID: " + id));
        return toDTO(item);
    }

    @Transactional(readOnly = true)
    public List<ItemManutencaoDTO> findByOrdemServicoId(Long ordemServicoId) {
        log.info("Buscando itens por ordem de serviço ID: {}", ordemServicoId);
        return itemManutencaoRepository.findByOrdemServicoId(ordemServicoId)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public ItemManutencaoDTO update(Long id, ItemManutencaoDTO dto) {
        log.info("Atualizando item de manutenção ID: {}", id);
        ItemManutencao item = itemManutencaoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item de manutenção não encontrado com ID: " + id));

        updateEntityFromDTO(item, dto);
        ItemManutencao updated = itemManutencaoRepository.save(item);

        OrdemServico os = updated.getOrdemServico();
        os.calcularValorTotal();
        ordemServicoRepository.save(os);

        log.info("Item de manutenção atualizado com sucesso. ID: {}", id);
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deletando item de manutenção ID: {}", id);
        ItemManutencao item = itemManutencaoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item de manutenção não encontrado com ID: " + id));

        OrdemServico os = item.getOrdemServico();
        itemManutencaoRepository.deleteById(id);

        os.calcularValorTotal();
        ordemServicoRepository.save(os);

        log.info("Item de manutenção deletado com sucesso. ID: {}", id);
    }

    private ItemManutencaoDTO toDTO(ItemManutencao entity) {
        ItemManutencaoDTO dto = new ItemManutencaoDTO();
        dto.setId(entity.getId());
        dto.setOrdemServicoId(entity.getOrdemServico().getId());
        dto.setTipo(entity.getTipo());
        dto.setDescricao(entity.getDescricao());
        dto.setQuantidade(entity.getQuantidade());
        dto.setValorUnitario(entity.getValorUnitario());
        dto.setValorTotal(entity.getValorTotal());

        if (entity.getFornecedor() != null) {
            dto.setFornecedorId(entity.getFornecedor().getId());
            dto.setFornecedorNome(entity.getFornecedor().getNomeFantasia() != null
                ? entity.getFornecedor().getNomeFantasia()
                : entity.getFornecedor().getRazaoSocial());
        }

        return dto;
    }

    private ItemManutencao toEntity(ItemManutencaoDTO dto) {
        ItemManutencao entity = new ItemManutencao();
        entity.setTipo(dto.getTipo());
        entity.setDescricao(dto.getDescricao());
        entity.setQuantidade(dto.getQuantidade());
        entity.setValorUnitario(dto.getValorUnitario());

        if (dto.getOrdemServicoId() != null) {
            OrdemServico os = ordemServicoRepository.findById(dto.getOrdemServicoId())
                .orElseThrow(() -> new RuntimeException("Ordem de serviço não encontrada com ID: " + dto.getOrdemServicoId()));
            entity.setOrdemServico(os);
        }

        if (dto.getFornecedorId() != null) {
            Fornecedor fornecedor = fornecedorRepository.findById(dto.getFornecedorId())
                .orElse(null);
            entity.setFornecedor(fornecedor);
        }

        return entity;
    }

    private void updateEntityFromDTO(ItemManutencao entity, ItemManutencaoDTO dto) {
        entity.setTipo(dto.getTipo());
        entity.setDescricao(dto.getDescricao());
        entity.setQuantidade(dto.getQuantidade());
        entity.setValorUnitario(dto.getValorUnitario());

        if (dto.getFornecedorId() != null) {
            Fornecedor fornecedor = fornecedorRepository.findById(dto.getFornecedorId())
                .orElse(null);
            entity.setFornecedor(fornecedor);
        } else {
            entity.setFornecedor(null);
        }
    }
}
