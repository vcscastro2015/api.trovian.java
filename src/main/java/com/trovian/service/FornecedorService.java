package com.trovian.service;

import com.trovian.dto.FornecedorDTO;
import com.trovian.entity.Cliente;
import com.trovian.entity.Fornecedor;
import com.trovian.enums.TipoFornecedor;
import com.trovian.repository.ClienteRepository;
import com.trovian.repository.FornecedorRepository;
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
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public FornecedorDTO create(FornecedorDTO dto) {
        log.info("Criando fornecedor: {}", dto.getRazaoSocial());

        if (dto.getCnpjCpf() != null && fornecedorRepository.existsByCnpjCpf(dto.getCnpjCpf())) {
            throw new RuntimeException("CNPJ/CPF já cadastrado: " + dto.getCnpjCpf());
        }

        // Validar cliente
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + dto.getClienteId()));

        Fornecedor fornecedor = toEntity(dto, cliente);
        Fornecedor saved = fornecedorRepository.save(fornecedor);

        log.info("Fornecedor criado com sucesso. ID: {}", saved.getId());
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<FornecedorDTO> findAll(int page, int size, String sortBy, String direction) {
        log.info("Buscando fornecedores - página: {}, tamanho: {}, ordenar por: {}", page, size, sortBy);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        return fornecedorRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public FornecedorDTO findById(Long id) {
        log.info("Buscando fornecedor por ID: {}", id);

        Fornecedor fornecedor = fornecedorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado com ID: " + id));

        return toDTO(fornecedor);
    }

    @Transactional
    public FornecedorDTO update(Long id, FornecedorDTO dto) {
        log.info("Atualizando fornecedor ID: {}", id);

        Fornecedor fornecedor = fornecedorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado com ID: " + id));

        if (dto.getCnpjCpf() != null && !dto.getCnpjCpf().equals(fornecedor.getCnpjCpf())) {
            if (fornecedorRepository.existsByCnpjCpf(dto.getCnpjCpf())) {
                throw new RuntimeException("CNPJ/CPF já cadastrado: " + dto.getCnpjCpf());
            }
        }

        // Validar cliente
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + dto.getClienteId()));

        updateEntityFromDTO(fornecedor, dto);
        fornecedor.setCliente(cliente);
        Fornecedor updated = fornecedorRepository.save(fornecedor);

        log.info("Fornecedor atualizado com sucesso. ID: {}", id);
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deletando fornecedor ID: {}", id);

        if (!fornecedorRepository.existsById(id)) {
            throw new RuntimeException("Fornecedor não encontrado com ID: " + id);
        }

        fornecedorRepository.deleteById(id);
        log.info("Fornecedor deletado com sucesso. ID: {}", id);
    }

    @Transactional(readOnly = true)
    public Page<FornecedorDTO> findByStatus(Boolean status, Pageable pageable) {
        return fornecedorRepository.findByStatus(status, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<FornecedorDTO> findByTipo(String tipo, Pageable pageable) {
        return fornecedorRepository.findByTipo(
            TipoFornecedor.valueOf(tipo),
            pageable
        ).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<FornecedorDTO> findByRazaoSocial(String razaoSocial, Pageable pageable) {
        return fornecedorRepository.findByRazaoSocialContainingIgnoreCase(
            razaoSocial,
            pageable
        ).map(this::toDTO);
    }

    private FornecedorDTO toDTO(Fornecedor entity) {
        FornecedorDTO dto = new FornecedorDTO();
        dto.setId(entity.getId());
        dto.setRazaoSocial(entity.getRazaoSocial());
        dto.setNomeFantasia(entity.getNomeFantasia());
        dto.setCnpjCpf(entity.getCnpjCpf());
        dto.setInscricaoEstadual(entity.getInscricaoEstadual());
        dto.setInscricaoMunicipal(entity.getInscricaoMunicipal());
        dto.setLogradouro(entity.getLogradouro());
        dto.setNumero(entity.getNumero());
        dto.setComplemento(entity.getComplemento());
        dto.setBairro(entity.getBairro());
        dto.setCep(entity.getCep());
        dto.setCidade(entity.getCidade());
        dto.setUf(entity.getUf());
        dto.setTelefone1(entity.getTelefone1());
        dto.setTelefone2(entity.getTelefone2());
        dto.setEmail(entity.getEmail());
        dto.setSite(entity.getSite());
        dto.setContatoPrincipal(entity.getContatoPrincipal());
        dto.setBanco(entity.getBanco());
        dto.setAgencia(entity.getAgencia());
        dto.setConta(entity.getConta());
        dto.setTipoConta(entity.getTipoConta());
        dto.setChavePix(entity.getChavePix());
        dto.setTipo(entity.getTipo());
        dto.setObservacao(entity.getObservacao());
        dto.setStatus(entity.getStatus());
        dto.setClienteId(entity.getCliente().getId());
        return dto;
    }

    private Fornecedor toEntity(FornecedorDTO dto, Cliente cliente) {
        Fornecedor entity = new Fornecedor();
        entity.setRazaoSocial(dto.getRazaoSocial());
        entity.setNomeFantasia(dto.getNomeFantasia());
        entity.setCnpjCpf(dto.getCnpjCpf());
        entity.setInscricaoEstadual(dto.getInscricaoEstadual());
        entity.setInscricaoMunicipal(dto.getInscricaoMunicipal());
        entity.setLogradouro(dto.getLogradouro());
        entity.setNumero(dto.getNumero());
        entity.setComplemento(dto.getComplemento());
        entity.setBairro(dto.getBairro());
        entity.setCep(dto.getCep());
        entity.setCidade(dto.getCidade());
        entity.setUf(dto.getUf());
        entity.setTelefone1(dto.getTelefone1());
        entity.setTelefone2(dto.getTelefone2());
        entity.setEmail(dto.getEmail());
        entity.setSite(dto.getSite());
        entity.setContatoPrincipal(dto.getContatoPrincipal());
        entity.setBanco(dto.getBanco());
        entity.setAgencia(dto.getAgencia());
        entity.setConta(dto.getConta());
        entity.setTipoConta(dto.getTipoConta());
        entity.setChavePix(dto.getChavePix());
        entity.setTipo(dto.getTipo());
        entity.setObservacao(dto.getObservacao());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
        entity.setCliente(cliente);
        return entity;
    }

    private void updateEntityFromDTO(Fornecedor entity, FornecedorDTO dto) {
        entity.setRazaoSocial(dto.getRazaoSocial());
        entity.setNomeFantasia(dto.getNomeFantasia());
        entity.setCnpjCpf(dto.getCnpjCpf());
        entity.setInscricaoEstadual(dto.getInscricaoEstadual());
        entity.setInscricaoMunicipal(dto.getInscricaoMunicipal());
        entity.setLogradouro(dto.getLogradouro());
        entity.setNumero(dto.getNumero());
        entity.setComplemento(dto.getComplemento());
        entity.setBairro(dto.getBairro());
        entity.setCep(dto.getCep());
        entity.setCidade(dto.getCidade());
        entity.setUf(dto.getUf());
        entity.setTelefone1(dto.getTelefone1());
        entity.setTelefone2(dto.getTelefone2());
        entity.setEmail(dto.getEmail());
        entity.setSite(dto.getSite());
        entity.setContatoPrincipal(dto.getContatoPrincipal());
        entity.setBanco(dto.getBanco());
        entity.setAgencia(dto.getAgencia());
        entity.setConta(dto.getConta());
        entity.setTipoConta(dto.getTipoConta());
        entity.setChavePix(dto.getChavePix());
        entity.setTipo(dto.getTipo());
        entity.setObservacao(dto.getObservacao());
        entity.setStatus(dto.getStatus());
    }

    @Transactional(readOnly = true)
    public Page<FornecedorDTO> findByCliente(Long clienteId, Pageable pageable) {
        return fornecedorRepository.findByClienteId(clienteId, pageable).map(this::toDTO);
    }


}
