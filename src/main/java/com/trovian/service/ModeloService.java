package com.trovian.service;

import com.trovian.dto.ModeloDTO;
import com.trovian.entity.Cliente;
import com.trovian.entity.Modelo;
import com.trovian.repository.ClienteRepository;
import com.trovian.repository.ModeloRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModeloService {

    private final ModeloRepository modeloRepository;
    private final ClienteRepository clienteRepository;

    private static final String TIPO_EQUIPAMENTO = "Equipamento";
    private static final String TIPO_VEICULO = "Veiculo";

    /**
     * Busca modelo por ID
     */
    @Transactional(readOnly = true)
    public ModeloDTO findById(Long id) {
        log.info("Buscando modelo com ID: {}", id);
        Modelo modelo = modeloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modelo não encontrado com ID: " + id));
        return toDTO(modelo);
    }

    /**
     * Busca todos os modelos do tipo Equipamento por cliente com paginação
     */
    @Transactional(readOnly = true)
    public Page<ModeloDTO> findAllEquipamentos(Long clienteId, Pageable pageable) {
        log.info("Buscando modelos do tipo Equipamento por cliente ID: {} com paginação - Página: {}, Tamanho: {}",
                clienteId, pageable.getPageNumber(), pageable.getPageSize());
        Page<Modelo> modelos = modeloRepository.findByTipoIgnoreCaseAndClienteId(TIPO_EQUIPAMENTO, clienteId, pageable);
        return modelos.map(this::toDTO);
    }

    /**
     * Busca todos os modelos do tipo Veiculo por cliente com paginação
     */
    @Transactional(readOnly = true)
    public Page<ModeloDTO> findAllVeiculos(Long clienteId, Pageable pageable) {
        log.info("Buscando modelos do tipo Veiculo por cliente ID: {} com paginação - Página: {}, Tamanho: {}",
                clienteId, pageable.getPageNumber(), pageable.getPageSize());
        Page<Modelo> modelos = modeloRepository.findByTipoIgnoreCaseAndClienteId(TIPO_VEICULO, clienteId, pageable);
        return modelos.map(this::toDTO);
    }

    /**
     * Cria um novo modelo
     */
    @Transactional
    public ModeloDTO create(ModeloDTO modeloDTO) {
        log.info("Criando novo modelo: {} - {}", modeloDTO.getFabricante(), modeloDTO.getMarca());
        validateTipo(modeloDTO.getTipo());

        Cliente cliente = clienteRepository.findById(modeloDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + modeloDTO.getClienteId()));

        Modelo modelo = toEntity(modeloDTO, cliente);
        Modelo savedModelo = modeloRepository.save(modelo);
        log.info("Modelo criado com sucesso. ID: {}", savedModelo.getId());
        return toDTO(savedModelo);
    }

    /**
     * Atualiza um modelo existente
     */
    @Transactional
    public ModeloDTO update(Long id, ModeloDTO modeloDTO) {
        log.info("Atualizando modelo com ID: {}", id);
        Modelo modelo = modeloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modelo não encontrado com ID: " + id));
        validateTipo(modeloDTO.getTipo());
        modelo.setFabricante(modeloDTO.getFabricante());
        modelo.setMarca(modeloDTO.getMarca());
        modelo.setTipo(modeloDTO.getTipo());
        modelo.setStatus(modeloDTO.getStatus());
        Modelo updatedModelo = modeloRepository.save(modelo);
        log.info("Modelo atualizado com sucesso. ID: {}", id);
        return toDTO(updatedModelo);
    }

    /**
     * Deleta um modelo
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deletando modelo com ID: {}", id);
        if (!modeloRepository.existsById(id)) {
            throw new RuntimeException("Modelo não encontrado com ID: " + id);
        }
        modeloRepository.deleteById(id);
        log.info("Modelo deletado com sucesso. ID: {}", id);
    }

    /**
     * Valida se o tipo é Equipamento ou Veiculo
     */
    private void validateTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new RuntimeException("Tipo é obrigatório");
        }

        if (!TIPO_EQUIPAMENTO.equalsIgnoreCase(tipo) && !TIPO_VEICULO.equalsIgnoreCase(tipo)) {
            throw new RuntimeException("Tipo inválido. Valores aceitos: 'Equipamento' ou 'Veiculo'");
        }
    }

    /**
     * Converte Entity para DTO
     */
    private ModeloDTO toDTO(Modelo modelo) {
        ModeloDTO dto = new ModeloDTO();
        dto.setId(modelo.getId());
        dto.setFabricante(modelo.getFabricante());
        dto.setMarca(modelo.getMarca());
        dto.setTipo(modelo.getTipo());
        dto.setStatus(modelo.getStatus());
        dto.setCreatedAt(modelo.getCreatedAt());
        dto.setUpdatedAt(modelo.getUpdatedAt());

        if (modelo.getCliente() != null) {
            dto.setClienteId(modelo.getCliente().getId());
        }

        return dto;
    }

    /**
     * Converte DTO para Entity
     */
    private Modelo toEntity(ModeloDTO dto, Cliente cliente) {
        Modelo modelo = new Modelo();
        modelo.setCliente(cliente);
        modelo.setFabricante(dto.getFabricante());
        modelo.setMarca(dto.getMarca());
        modelo.setTipo(dto.getTipo());
        modelo.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
        return modelo;
    }
}
