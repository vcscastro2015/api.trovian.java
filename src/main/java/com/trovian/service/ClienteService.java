package com.trovian.service;

import com.trovian.dto.ClienteDTO;
import com.trovian.entity.Cliente;
import com.trovian.entity.Cooperativa;
import com.trovian.repository.ClienteRepository;
import com.trovian.repository.CooperativaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final CooperativaRepository cooperativaRepository;

    /**
     * Busca todos os clientes com paginação
     */
    @Transactional(readOnly = true)
    public Page<ClienteDTO> findAllPaginated(Pageable pageable) {
        log.info("Buscando todos os clientes com paginação - Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<Cliente> clientes = clienteRepository.findAll(pageable);
        return clientes.map(this::toDTO);
    }

    /**
     * Busca cliente por ID
     */
    @Transactional(readOnly = true)
    public ClienteDTO findById(Long id) {
        log.info("Buscando cliente com ID: {}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + id));
        return toDTO(cliente);
    }

    /**
     * Busca clientes por cooperativa com paginação
     */
    @Transactional(readOnly = true)
    public Page<ClienteDTO> findByCooperativa(Long cooperativaId, Pageable pageable) {
        log.info("Buscando clientes da cooperativa ID: {} com paginação - Página: {}, Tamanho: {}",
                cooperativaId, pageable.getPageNumber(), pageable.getPageSize());
        Page<Cliente> clientes = clienteRepository.findByCooperativaId(cooperativaId, pageable);
        return clientes.map(this::toDTO);
    }

    /**
     * Cria um novo cliente
     */
    @Transactional
    public ClienteDTO create(ClienteDTO clienteDTO) {
        log.info("Criando novo cliente: {}", clienteDTO.getNome());

        // Valida se já existe CNPJ/CPF cadastrado
        clienteRepository.findByCnpjCpf(clienteDTO.getCnpjCpf())
                .ifPresent(c -> {
                    throw new RuntimeException("Já existe um cliente cadastrado com o CNPJ/CPF: " + clienteDTO.getCnpjCpf());
                });

        // Se for cooperado, valida se cooperativa existe
        if (Boolean.TRUE.equals(clienteDTO.getCooperado()) && clienteDTO.getCooperativaId() != null) {
            cooperativaRepository.findById(clienteDTO.getCooperativaId())
                    .orElseThrow(() -> new RuntimeException("Cooperativa não encontrada com ID: " + clienteDTO.getCooperativaId()));
        }

        // Se for cooperado mas não tem cooperativa, lança erro
        if (Boolean.TRUE.equals(clienteDTO.getCooperado()) && clienteDTO.getCooperativaId() == null) {
            throw new RuntimeException("Cliente cooperado deve ter uma cooperativa associada");
        }

        Cliente cliente = toEntity(clienteDTO);
        Cliente savedCliente = clienteRepository.save(cliente);
        log.info("Cliente criado com sucesso. ID: {}, UUID: {}", savedCliente.getId(), savedCliente.getUuid());
        return toDTO(savedCliente);
    }

    /**
     * Atualiza um cliente existente
     */
    @Transactional
    public ClienteDTO update(Long id, ClienteDTO clienteDTO) {
        log.info("Atualizando cliente com ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + id));

        // Valida se o CNPJ/CPF já existe em outro cliente
        clienteRepository.findByCnpjCpf(clienteDTO.getCnpjCpf())
                .ifPresent(c -> {
                    if (!c.getId().equals(id)) {
                        throw new RuntimeException("Já existe outro cliente cadastrado com o CNPJ/CPF: " + clienteDTO.getCnpjCpf());
                    }
                });

        // Se for cooperado, valida se cooperativa existe
        if (Boolean.TRUE.equals(clienteDTO.getCooperado()) && clienteDTO.getCooperativaId() != null) {
            cooperativaRepository.findById(clienteDTO.getCooperativaId())
                    .orElseThrow(() -> new RuntimeException("Cooperativa não encontrada com ID: " + clienteDTO.getCooperativaId()));
        }

        // Se for cooperado mas não tem cooperativa, lança erro
        if (Boolean.TRUE.equals(clienteDTO.getCooperado()) && clienteDTO.getCooperativaId() == null) {
            throw new RuntimeException("Cliente cooperado deve ter uma cooperativa associada");
        }

        // Atualiza os campos
        cliente.setNome(clienteDTO.getNome());
        cliente.setCnpjCpf(clienteDTO.getCnpjCpf());
        cliente.setIe(clienteDTO.getIe());
        cliente.setEndereco(clienteDTO.getEndereco());
        cliente.setBairro(clienteDTO.getBairro());
        cliente.setComplemento(clienteDTO.getComplemento());
        cliente.setNumero(clienteDTO.getNumero());
        cliente.setCep(clienteDTO.getCep());
        cliente.setCidade(clienteDTO.getCidade());
        cliente.setUf(clienteDTO.getUf());
        cliente.setContatos(clienteDTO.getContatos());
        cliente.setTelefones(clienteDTO.getTelefones());
        cliente.setStatus(clienteDTO.getStatus());
        cliente.setCooperado(clienteDTO.getCooperado());

        // Atualiza cooperativa
        if (clienteDTO.getCooperativaId() != null) {
            Cooperativa cooperativa = cooperativaRepository.findById(clienteDTO.getCooperativaId())
                    .orElse(null);
            cliente.setCooperativa(cooperativa);
        } else {
            cliente.setCooperativa(null);
        }

        Cliente updatedCliente = clienteRepository.save(cliente);
        log.info("Cliente atualizado com sucesso. ID: {}", id);
        return toDTO(updatedCliente);
    }

    /**
     * Deleta um cliente
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deletando cliente com ID: {}", id);

        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado com ID: " + id);
        }

        clienteRepository.deleteById(id);
        log.info("Cliente deletado com sucesso. ID: {}", id);
    }

    /**
     * Converte Entity para DTO
     */
    private ClienteDTO toDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setUuid(cliente.getUuid());
        dto.setNome(cliente.getNome());
        dto.setCnpjCpf(cliente.getCnpjCpf());
        dto.setIe(cliente.getIe());
        dto.setEndereco(cliente.getEndereco());
        dto.setBairro(cliente.getBairro());
        dto.setComplemento(cliente.getComplemento());
        dto.setNumero(cliente.getNumero());
        dto.setCep(cliente.getCep());
        dto.setCidade(cliente.getCidade());
        dto.setUf(cliente.getUf());
        dto.setContatos(cliente.getContatos());
        dto.setTelefones(cliente.getTelefones());
        dto.setStatus(cliente.getStatus());
        dto.setCooperado(cliente.getCooperado());
        dto.setDataCadastro(cliente.getDataCadastro());
        dto.setUpdatedAt(cliente.getUpdatedAt());

        if (cliente.getCooperativa() != null) {
            dto.setCooperativaId(cliente.getCooperativa().getId());
            dto.setCooperativaNome(cliente.getCooperativa().getNome());
        }

        return dto;
    }

    /**
     * Converte DTO para Entity
     */
    private Cliente toEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setCnpjCpf(dto.getCnpjCpf());
        cliente.setIe(dto.getIe());
        cliente.setEndereco(dto.getEndereco());
        cliente.setBairro(dto.getBairro());
        cliente.setComplemento(dto.getComplemento());
        cliente.setNumero(dto.getNumero());
        cliente.setCep(dto.getCep());
        cliente.setCidade(dto.getCidade());
        cliente.setUf(dto.getUf());
        cliente.setContatos(dto.getContatos());
        cliente.setTelefones(dto.getTelefones());
        cliente.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
        cliente.setCooperado(dto.getCooperado() != null ? dto.getCooperado() : false);

        // Se tiver cooperativa, carrega
        if (dto.getCooperativaId() != null) {
            Cooperativa cooperativa = cooperativaRepository.findById(dto.getCooperativaId())
                    .orElse(null);
            cliente.setCooperativa(cooperativa);
        }

        return cliente;
    }
}
