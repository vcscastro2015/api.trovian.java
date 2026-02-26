package com.trovian.service;

import com.trovian.dto.MotoristaDTO;
import com.trovian.entity.Cliente;
import com.trovian.entity.Motorista;
import com.trovian.repository.ClienteRepository;
import com.trovian.repository.MotoristaRepository;
import com.trovian.util.TelefoneUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Service para lógica de negócio de Motorista
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MotoristaService {

    private final MotoristaRepository motoristaRepository;
    private final ClienteRepository clienteRepository;
    private final NotificacaoService notificacaoService;

    /**
     * Busca todos os motoristas com paginação
     *
     * @param pageable Configuração de paginação
     * @return Página de motoristas
     */
    @Transactional(readOnly = true)
    public Page<MotoristaDTO> findAll(Pageable pageable) {
        log.info("Buscando todos os motoristas com paginação: {}", pageable);
        Page<Motorista> motoristas = motoristaRepository.findAll(pageable);
        return motoristas.map(this::toDTO);
    }

    /**
     * Busca motorista por ID
     *
     * @param id ID do motorista
     * @return MotoristaDTO
     */
    @Transactional(readOnly = true)
    public MotoristaDTO findById(Long id) {
        log.info("Buscando motorista por ID: {}", id);
        Motorista motorista = motoristaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Motorista não encontrado com ID: " + id));
        return toDTO(motorista);
    }

    /**
     * Busca motoristas por cliente com paginação
     *
     * @param clienteId ID do cliente
     * @param pageable  Configuração de paginação
     * @return Página de motoristas
     */
    @Transactional(readOnly = true)
    public Page<MotoristaDTO> findByCliente(Long clienteId, Pageable pageable) {
        log.info("Buscando motoristas do cliente ID: {} com paginação: {}", clienteId, pageable);
        Page<Motorista> motoristas = motoristaRepository.findByClienteId(clienteId, pageable);
        return motoristas.map(this::toDTO);
    }

    /**
     * Cria novo motorista
     *
     * @param dto MotoristaDTO
     * @return MotoristaDTO criado
     */
    @Transactional
    public MotoristaDTO create(MotoristaDTO dto) {
        log.info("Criando novo motorista: {}", dto.getNome());

        // Validar se cliente existe
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + dto.getClienteId()));

        Motorista motorista = toEntity(dto);
        motorista.setCliente(cliente);

        Motorista savedMotorista = motoristaRepository.save(motorista);
        log.info("Motorista criado com sucesso. ID: {}", savedMotorista.getId());

        notificacaoService.criarNotificacaoBemVindoMotorista(savedMotorista);

        return toDTO(savedMotorista);
    }

    /**
     * Atualiza motorista existente
     *
     * @param id  ID do motorista
     * @param dto MotoristaDTO
     * @return MotoristaDTO atualizado
     */
    @Transactional
    public MotoristaDTO update(Long id, MotoristaDTO dto) {
        log.info("Atualizando motorista ID: {}", id);

        Motorista motorista = motoristaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Motorista não encontrado com ID: " + id));

        // Validar se cliente existe
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + dto.getClienteId()));

        // Atualizar campos
        motorista.setNome(dto.getNome());
        motorista.setDataNascimento(dto.getDataNascimento());
        motorista.setSexo(dto.getSexo());
        motorista.setCpf(dto.getCpf());
        motorista.setNumeroCnh(dto.getNumeroCnh());
        motorista.setValidadeCnh(dto.getValidadeCnh());
        motorista.setDataAdmissao(dto.getDataAdmissao());
        motorista.setCategoriaCnh(dto.getCategoriaCnh());
        motorista.setTelefone(TelefoneUtils.salvarSemMascara(dto.getTelefone()));
        motorista.setStatus(dto.getStatus());
        motorista.setComissao(dto.getComissao());
        motorista.setLogradouro(dto.getLogradouro());
        motorista.setNumero(dto.getNumero());
        motorista.setBairro(dto.getBairro());
        motorista.setCep(dto.getCep());
        motorista.setComplemento(dto.getComplemento());
        motorista.setCidade(dto.getCidade());
        motorista.setUf(dto.getUf());
        motorista.setLiberarAbastecimentoWpp(dto.getLiberarAbastecimentoWpp());
        motorista.setLiberarCheckListWpp(dto.getLiberarCheckListWpp());
        motorista.setLiberarDocumentosWpp(dto.getLiberarDocumentosWpp());
        motorista.setCliente(cliente);

        Motorista updatedMotorista = motoristaRepository.save(motorista);
        log.info("Motorista atualizado com sucesso. ID: {}", updatedMotorista.getId());

        return toDTO(updatedMotorista);
    }

    /**
     * Deleta motorista
     *
     * @param id ID do motorista
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deletando motorista ID: {}", id);

        if (!motoristaRepository.existsById(id)) {
            throw new RuntimeException("Motorista não encontrado com ID: " + id);
        }

        motoristaRepository.deleteById(id);
        log.info("Motorista deletado com sucesso. ID: {}", id);
    }

    /**
     * Converte Entity para DTO
     *
     * @param motorista Motorista entity
     * @return MotoristaDTO
     */
    private MotoristaDTO toDTO(Motorista motorista) {
        MotoristaDTO dto = new MotoristaDTO();
        dto.setId(motorista.getId());
        dto.setNome(motorista.getNome());
        dto.setDataNascimento(motorista.getDataNascimento());
        dto.setSexo(motorista.getSexo());
        dto.setCpf(motorista.getCpf());
        dto.setNumeroCnh(motorista.getNumeroCnh());
        dto.setValidadeCnh(motorista.getValidadeCnh());
        dto.setDataAdmissao(motorista.getDataAdmissao());
        dto.setCategoriaCnh(motorista.getCategoriaCnh());
        dto.setTelefone(TelefoneUtils.aplicarMascara(motorista.getTelefone()));
        dto.setStatus(motorista.getStatus());
        dto.setComissao(motorista.getComissao());
        dto.setLogradouro(motorista.getLogradouro());
        dto.setNumero(motorista.getNumero());
        dto.setBairro(motorista.getBairro());
        dto.setCep(motorista.getCep());
        dto.setComplemento(motorista.getComplemento());
        dto.setCidade(motorista.getCidade());
        dto.setUf(motorista.getUf());
        dto.setLiberarAbastecimentoWpp(motorista.getLiberarAbastecimentoWpp());
        dto.setLiberarCheckListWpp(motorista.getLiberarCheckListWpp());
        dto.setLiberarDocumentosWpp(motorista.getLiberarDocumentosWpp());
        dto.setClienteId(motorista.getCliente().getId());
        dto.setClienteNome(motorista.getCliente().getNome());
        dto.setDataCadastro(motorista.getDataCadastro());
        dto.setUpdatedAt(motorista.getUpdatedAt() != null ? motorista.getUpdatedAt().toString() : null);
        return dto;
    }

    /**
     * Converte DTO para Entity
     *
     * @param dto MotoristaDTO
     * @return Motorista entity
     */
    private Motorista toEntity(MotoristaDTO dto) {
        Motorista motorista = new Motorista();
        motorista.setNome(dto.getNome());
        motorista.setDataNascimento(dto.getDataNascimento());
        motorista.setSexo(dto.getSexo());
        motorista.setCpf(dto.getCpf());
        motorista.setNumeroCnh(dto.getNumeroCnh());
        motorista.setValidadeCnh(dto.getValidadeCnh());
        motorista.setDataAdmissao(dto.getDataAdmissao());
        motorista.setCategoriaCnh(dto.getCategoriaCnh());
        motorista.setTelefone(TelefoneUtils.salvarSemMascara(dto.getTelefone()));
        motorista.setStatus(dto.getStatus());
        motorista.setComissao(dto.getComissao());
        motorista.setLogradouro(dto.getLogradouro());
        motorista.setNumero(dto.getNumero());
        motorista.setBairro(dto.getBairro());
        motorista.setCep(dto.getCep());
        motorista.setComplemento(dto.getComplemento());
        motorista.setCidade(dto.getCidade());
        motorista.setUf(dto.getUf());
        motorista.setLiberarAbastecimentoWpp(dto.getLiberarAbastecimentoWpp());
        motorista.setLiberarCheckListWpp(dto.getLiberarCheckListWpp());
        motorista.setLiberarDocumentosWpp(dto.getLiberarDocumentosWpp());
        return motorista;
    }
}
