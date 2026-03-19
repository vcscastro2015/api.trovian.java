package com.trovian.service;

import com.trovian.dto.ItemModeloChecklistDTO;
import com.trovian.dto.ModeloChecklistDTO;
import com.trovian.entity.Cliente;
import com.trovian.entity.ItemModeloChecklist;
import com.trovian.entity.ModeloChecklist;
import com.trovian.entity.Veiculo;
import com.trovian.repository.ClienteRepository;
import com.trovian.repository.ItemModeloChecklistRepository;
import com.trovian.repository.ModeloChecklistRepository;
import com.trovian.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModeloChecklistService {

    private final ModeloChecklistRepository modeloChecklistRepository;
    private final ItemModeloChecklistRepository itemModeloChecklistRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;

    @Transactional(readOnly = true)
    public Page<ModeloChecklistDTO> findAll(Pageable pageable) {
        log.info("Buscando todos os modelos de checklist com paginação");
        Page<ModeloChecklist> modelos = modeloChecklistRepository.findAll(pageable);
        return modelos.map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public ModeloChecklistDTO findById(UUID id) {
        log.info("Buscando modelo de checklist com ID: {}", id);
        ModeloChecklist modelo = modeloChecklistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modelo de checklist não encontrado com ID: " + id));
        return toDTO(modelo);
    }

    @Transactional(readOnly = true)
    public Page<ModeloChecklistDTO> findByClienteId(Long clienteId, Pageable pageable) {
        log.info("Buscando modelos de checklist do cliente com ID: {}", clienteId);
        Page<ModeloChecklist> modelos = modeloChecklistRepository.findByClienteId(clienteId, pageable);
        return modelos.map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public List<ModeloChecklistDTO> findAtivos(Long clienteId) {
        log.info("Buscando modelos de checklist ativos do cliente com ID: {}", clienteId);
        List<ModeloChecklist> modelos = modeloChecklistRepository.findByClienteIdAndAtivoTrue(clienteId);
        return modelos.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public ModeloChecklistDTO create(ModeloChecklistDTO dto) {
        log.info("Criando novo modelo de checklist: {}", dto.getNome());

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + dto.getClienteId()));

        ModeloChecklist modelo = toEntity(dto);
        modelo.setCliente(cliente);

        if (dto.getVeiculoIds() != null && !dto.getVeiculoIds().isEmpty()) {
            List<Veiculo> veiculos = veiculoRepository.findAllById(dto.getVeiculoIds());
            modelo.setVeiculos(veiculos);
        }

        // Salva o modelo primeiro
        ModeloChecklist savedModelo = modeloChecklistRepository.save(modelo);

        // Agora salva os itens associados
        if (dto.getItens() != null && !dto.getItens().isEmpty()) {
            for (ItemModeloChecklistDTO itemDTO : dto.getItens()) {
                ItemModeloChecklist item = toItemEntity(itemDTO);
                item.setModeloChecklist(savedModelo);
                savedModelo.getItens().add(item);
            }
            savedModelo = modeloChecklistRepository.save(savedModelo);
        }

        log.info("Modelo de checklist criado com sucesso. ID: {}", savedModelo.getId());
        return toDTO(savedModelo);
    }

    @Transactional
    public ModeloChecklistDTO update(UUID id, ModeloChecklistDTO dto) {
        log.info("Atualizando modelo de checklist com ID: {}", id);

        ModeloChecklist modelo = modeloChecklistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modelo de checklist não encontrado com ID: " + id));

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + dto.getClienteId()));

        // Atualiza os campos básicos
        modelo.setNome(dto.getNome());
        modelo.setDescricao(dto.getDescricao());
        modelo.setTipo(dto.getTipo());
        modelo.setAtivo(dto.getAtivo());
        modelo.setObrigatorio(dto.getObrigatorio());
        modelo.setPeriodicidade(dto.getPeriodicidade());
        modelo.setCliente(cliente);

        // Atualiza os itens (remove todos e adiciona novamente)
        if (dto.getItens() != null) {
            modelo.getItens().clear();
            for (ItemModeloChecklistDTO itemDTO : dto.getItens()) {
                ItemModeloChecklist item = toItemEntity(itemDTO);
                item.setModeloChecklist(modelo);
                modelo.getItens().add(item);
            }
        }

        // Atualiza a lista de veículos
        if (dto.getVeiculoIds() != null) {
            List<Veiculo> veiculos = veiculoRepository.findAllById(dto.getVeiculoIds());
            modelo.setVeiculos(veiculos);
        }

        ModeloChecklist updatedModelo = modeloChecklistRepository.save(modelo);
        log.info("Modelo de checklist atualizado com sucesso. ID: {}", id);
        return toDTO(updatedModelo);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Deletando modelo de checklist com ID: {}", id);

        if (!modeloChecklistRepository.existsById(id)) {
            throw new RuntimeException("Modelo de checklist não encontrado com ID: " + id);
        }

        modeloChecklistRepository.deleteById(id);
        log.info("Modelo de checklist deletado com sucesso. ID: {}", id);
    }

    private ModeloChecklistDTO toDTO(ModeloChecklist modelo) {
        ModeloChecklistDTO dto = new ModeloChecklistDTO();
        dto.setId(modelo.getId());
        dto.setNome(modelo.getNome());
        dto.setDescricao(modelo.getDescricao());
        dto.setTipo(modelo.getTipo());
        dto.setAtivo(modelo.getAtivo());
        dto.setObrigatorio(modelo.getObrigatorio());
        dto.setPeriodicidade(modelo.getPeriodicidade());
        dto.setCriadoEm(modelo.getCriadoEm());
        dto.setAtualizadoEm(modelo.getAtualizadoEm());

        if (modelo.getCliente() != null) {
            dto.setClienteId(modelo.getCliente().getId());
            dto.setClienteNome(modelo.getCliente().getNome());
        }

        if (modelo.getItens() != null && !modelo.getItens().isEmpty()) {
            List<ItemModeloChecklistDTO> itensDTO = modelo.getItens().stream()
                    .map(this::toItemDTO)
                    .collect(Collectors.toList());
            dto.setItens(itensDTO);
        }

        if (modelo.getVeiculos() != null && !modelo.getVeiculos().isEmpty()) {
            dto.setVeiculoIds(modelo.getVeiculos().stream()
                    .map(Veiculo::getId)
                    .collect(Collectors.toList()));
            dto.setVeiculoPlacas(modelo.getVeiculos().stream()
                    .map(Veiculo::getPlaca)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private ModeloChecklist toEntity(ModeloChecklistDTO dto) {
        ModeloChecklist modelo = new ModeloChecklist();
        modelo.setNome(dto.getNome());
        modelo.setDescricao(dto.getDescricao());
        modelo.setTipo(dto.getTipo());
        modelo.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        modelo.setObrigatorio(dto.getObrigatorio() != null ? dto.getObrigatorio() : false);
        modelo.setPeriodicidade(dto.getPeriodicidade());
        return modelo;
    }

    private ItemModeloChecklistDTO toItemDTO(ItemModeloChecklist item) {
        ItemModeloChecklistDTO dto = new ItemModeloChecklistDTO();
        dto.setId(item.getId());
        dto.setModeloChecklistId(item.getModeloChecklist().getId());
        dto.setDescricao(item.getDescricao());
        dto.setOrdem(item.getOrdem());
        dto.setObrigatorio(item.getObrigatorio());
        dto.setPermiteFoto(item.getPermiteFoto());
        dto.setPermiteObservacao(item.getPermiteObservacao());
        dto.setCategoria(item.getCategoria());
        dto.setTipoResposta(item.getTipoResposta());
        return dto;
    }

    private ItemModeloChecklist toItemEntity(ItemModeloChecklistDTO dto) {
        ItemModeloChecklist item = new ItemModeloChecklist();
        item.setDescricao(dto.getDescricao());
        item.setOrdem(dto.getOrdem());
        item.setObrigatorio(dto.getObrigatorio() != null ? dto.getObrigatorio() : true);
        item.setPermiteFoto(dto.getPermiteFoto() != null ? dto.getPermiteFoto() : false);
        item.setPermiteObservacao(dto.getPermiteObservacao() != null ? dto.getPermiteObservacao() : false);
        item.setCategoria(dto.getCategoria());
        item.setTipoResposta(dto.getTipoResposta());
        return item;
    }
}
