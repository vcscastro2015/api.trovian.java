package com.trovian.service;

import com.trovian.dto.EquipamentoDTO;
import com.trovian.entity.Equipamento;
import com.trovian.entity.Modelo;
import com.trovian.repository.EquipamentoRepository;
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
public class EquipamentoService {

    private final EquipamentoRepository equipamentoRepository;
    private final ModeloRepository modeloRepository;

    private static final String TIPO_PROPRIETARIO = "PR";
    private static final String TIPO_PARTICULAR = "PA";

    @Transactional(readOnly = true)
    public Page<EquipamentoDTO> findAll(Pageable pageable) {
        log.info("Buscando todos os equipamentos com paginação - Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<Equipamento> equipamentos = equipamentoRepository.findAll(pageable);
        return equipamentos.map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public EquipamentoDTO findById(Long id) {
        log.info("Buscando equipamento com ID: {}", id);
        Equipamento equipamento = equipamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado com ID: " + id));
        return toDTO(equipamento);
    }

    @Transactional
    public EquipamentoDTO create(EquipamentoDTO equipamentoDTO) {
        log.info("Criando novo equipamento com IMEI: {}", equipamentoDTO.getImei());

        validateTipoEquipamento(equipamentoDTO.getTipoEquipamento());
        validateTipoChip(equipamentoDTO.getTipoChip());

        Modelo modelo = modeloRepository.findById(equipamentoDTO.getModeloId())
                .orElseThrow(() -> new RuntimeException("Modelo não encontrado com ID: " + equipamentoDTO.getModeloId()));

        Equipamento equipamento = toEntity(equipamentoDTO);
        equipamento.setModelo(modelo);

        Equipamento savedEquipamento = equipamentoRepository.save(equipamento);
        log.info("Equipamento criado com sucesso. ID: {}", savedEquipamento.getId());
        return toDTO(savedEquipamento);
    }

    @Transactional
    public EquipamentoDTO update(Long id, EquipamentoDTO equipamentoDTO) {
        log.info("Atualizando equipamento com ID: {}", id);

        Equipamento equipamento = equipamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado com ID: " + id));

        validateTipoEquipamento(equipamentoDTO.getTipoEquipamento());
        validateTipoChip(equipamentoDTO.getTipoChip());

        Modelo modelo = modeloRepository.findById(equipamentoDTO.getModeloId())
                .orElseThrow(() -> new RuntimeException("Modelo não encontrado com ID: " + equipamentoDTO.getModeloId()));

        // Atualiza os campos
        equipamento.setImei(equipamentoDTO.getImei());
        equipamento.setNumeroCelular(equipamentoDTO.getNumeroCelular());
        equipamento.setNumeroSerial(equipamentoDTO.getNumeroSerial());
        equipamento.setObservacao(equipamentoDTO.getObservacao());
        equipamento.setOperadora(equipamentoDTO.getOperadora());
        equipamento.setStatus(equipamentoDTO.getStatus());
        equipamento.setTipoEquipamento(equipamentoDTO.getTipoEquipamento());
        equipamento.setTipoChip(equipamentoDTO.getTipoChip());
        equipamento.setModelo(modelo);
        equipamento.setEquipamentoAlocado(equipamentoDTO.getEquipamentoAlocado());

        Equipamento updatedEquipamento = equipamentoRepository.save(equipamento);
        log.info("Equipamento atualizado com sucesso. ID: {}", id);
        return toDTO(updatedEquipamento);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deletando equipamento com ID: {}", id);

        if (!equipamentoRepository.existsById(id)) {
            throw new RuntimeException("Equipamento não encontrado com ID: " + id);
        }

        equipamentoRepository.deleteById(id);
        log.info("Equipamento deletado com sucesso. ID: {}", id);
    }

    private void validateTipoEquipamento(String tipoEquipamento) {
        if (tipoEquipamento != null && !tipoEquipamento.trim().isEmpty()) {
            if (!TIPO_PROPRIETARIO.equalsIgnoreCase(tipoEquipamento) &&
                !TIPO_PARTICULAR.equalsIgnoreCase(tipoEquipamento)) {
                throw new RuntimeException("Tipo de equipamento inválido. Valores aceitos: 'PR' (Proprietário) ou 'PA' (Particular)");
            }
        }
    }

    private void validateTipoChip(String tipoChip) {
        if (tipoChip != null && !tipoChip.trim().isEmpty()) {
            if (!TIPO_PROPRIETARIO.equalsIgnoreCase(tipoChip) &&
                !TIPO_PARTICULAR.equalsIgnoreCase(tipoChip)) {
                throw new RuntimeException("Tipo de chip inválido. Valores aceitos: 'PR' (Proprietário) ou 'PA' (Particular)");
            }
        }
    }

    private EquipamentoDTO toDTO(Equipamento equipamento) {
        EquipamentoDTO dto = new EquipamentoDTO();
        dto.setId(equipamento.getId());
        dto.setDataCadastro(equipamento.getDataCadastro());
        dto.setImei(equipamento.getImei());
        dto.setNumeroCelular(equipamento.getNumeroCelular());
        dto.setNumeroSerial(equipamento.getNumeroSerial());
        dto.setObservacao(equipamento.getObservacao());
        dto.setOperadora(equipamento.getOperadora());
        dto.setStatus(equipamento.getStatus());
        dto.setTipoEquipamento(equipamento.getTipoEquipamento());
        dto.setTipoChip(equipamento.getTipoChip());
        dto.setEquipamentoAlocado(equipamento.getEquipamentoAlocado());
        dto.setUpdatedAt(equipamento.getUpdatedAt());

        // Inclui informações do modelo
        if (equipamento.getModelo() != null) {
            dto.setModeloId(equipamento.getModelo().getId());
            dto.setModeloMarca(equipamento.getModelo().getMarca());
            dto.setModeloFabricante(equipamento.getModelo().getFabricante());
        }

        return dto;
    }

    private Equipamento toEntity(EquipamentoDTO dto) {
        Equipamento equipamento = new Equipamento();
        equipamento.setImei(dto.getImei());
        equipamento.setNumeroCelular(dto.getNumeroCelular());
        equipamento.setNumeroSerial(dto.getNumeroSerial());
        equipamento.setObservacao(dto.getObservacao());
        equipamento.setOperadora(dto.getOperadora());
        equipamento.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
        equipamento.setTipoEquipamento(dto.getTipoEquipamento());
        equipamento.setTipoChip(dto.getTipoChip());
        equipamento.setEquipamentoAlocado(dto.getEquipamentoAlocado() != null ? dto.getEquipamentoAlocado() : false);
        return equipamento;
    }
}
