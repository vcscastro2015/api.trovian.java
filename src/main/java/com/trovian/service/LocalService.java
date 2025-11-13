package com.trovian.service;

import com.trovian.dto.CoordenadaDTO;
import com.trovian.dto.LocalDTO;
import com.trovian.dto.ParametroLocalDTO;
import com.trovian.entity.Cliente;
import com.trovian.entity.Coordenada;
import com.trovian.entity.Local;
import com.trovian.entity.ParametroLocal;
import com.trovian.repository.ClienteRepository;
import com.trovian.repository.LocalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalService {

    private final LocalRepository localRepository;
    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public Page<LocalDTO> findAll(Pageable pageable) {
        log.info("Buscando todos os locais com paginação - Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<Local> locais = localRepository.findAll(pageable);
        return locais.map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public LocalDTO findById(Long id) {
        log.info("Buscando local com ID: {}", id);
        Local local = localRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Local não encontrado com ID: " + id));
        return toDTO(local);
    }

    @Transactional(readOnly = true)
    public Page<LocalDTO> findByClienteId(Long clienteId, Pageable pageable) {
        log.info("Buscando locais do cliente com ID: {} - Página: {}, Tamanho: {}",
                clienteId, pageable.getPageNumber(), pageable.getPageSize());
        Page<Local> locais = localRepository.findByClienteId(clienteId, pageable);
        return locais.map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<LocalDTO> findByTipo(com.trovian.enums.TipoLocal tipo, Pageable pageable) {
        log.info("Buscando locais do tipo: {} - Página: {}, Tamanho: {}",
                tipo, pageable.getPageNumber(), pageable.getPageSize());
        Page<Local> locais = localRepository.findByTipo(tipo, pageable);
        return locais.map(this::toDTO);
    }

    @Transactional
    public LocalDTO create(LocalDTO localDTO) {
        log.info("Criando novo local com nome: {}", localDTO.getNome());

        Cliente cliente = clienteRepository.findById(localDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + localDTO.getClienteId()));

        Local local = toEntity(localDTO);
        local.setCliente(cliente);

        // Processar coordenadas
        if (localDTO.getListaDeCoordenadas() != null && !localDTO.getListaDeCoordenadas().isEmpty()) {
            List<Coordenada> coordenadas = localDTO.getListaDeCoordenadas().stream()
                    .map(this::coordenadaDtoToEntity)
                    .collect(Collectors.toList());

            coordenadas.forEach(local::addCoordenada);
        }

        // Processar parâmetro local
        if (localDTO.getParametroLocal() != null) {
            ParametroLocal parametroLocal = parametroLocalDtoToEntity(localDTO.getParametroLocal());
            local.setParametroLocal(parametroLocal);
        }

        Local savedLocal = localRepository.save(local);
        log.info("Local criado com sucesso. ID: {}", savedLocal.getId());
        return toDTO(savedLocal);
    }

    @Transactional
    public LocalDTO update(Long id, LocalDTO localDTO) {
        log.info("Atualizando local com ID: {}", id);

        Local local = localRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Local não encontrado com ID: " + id));

        Cliente cliente = clienteRepository.findById(localDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + localDTO.getClienteId()));

        // Atualizar campos básicos
        local.setNome(localDTO.getNome());
        local.setAtivo(localDTO.getAtivo());
        local.setCodigoUnico(localDTO.getCodigoUnico());
        local.setMostrarNoMapaPrincipal(localDTO.getMostrarNoMapaPrincipal());
        local.setMostrarNomeNoMapa(localDTO.getMostrarNomeNoMapa());
        local.setNotificaEvento(localDTO.getNotificaEvento());
        local.setFuncao(localDTO.getFuncao());
        local.setEndereco(localDTO.getEndereco());
        local.setBairro(localDTO.getBairro());
        local.setComplemento(localDTO.getComplemento());
        local.setCidade(localDTO.getCidade());
        local.setUf(localDTO.getUf());
        local.setTipo(localDTO.getTipo());
        local.setPermiteDescanso(localDTO.getPermiteDescanso());
        local.setCliente(cliente);

        // Atualizar coordenadas
        local.getListaDeCoordenadas().clear();
        if (localDTO.getListaDeCoordenadas() != null && !localDTO.getListaDeCoordenadas().isEmpty()) {
            List<Coordenada> coordenadas = localDTO.getListaDeCoordenadas().stream()
                    .map(this::coordenadaDtoToEntity)
                    .collect(Collectors.toList());

            coordenadas.forEach(local::addCoordenada);
        }

        // Atualizar parâmetro local
        if (localDTO.getParametroLocal() != null) {
            if (local.getParametroLocal() != null) {
                // Atualizar existente
                ParametroLocal parametroLocal = local.getParametroLocal();
                parametroLocal.setLimiteVeiculosMesmoLocal(localDTO.getParametroLocal().getLimiteVeiculosMesmoLocal());
                parametroLocal.setTempoMinimoDePermanencia(localDTO.getParametroLocal().getTempoMinimoDePermanencia());
                parametroLocal.setTempoMaximoDePermanencia(localDTO.getParametroLocal().getTempoMaximoDePermanencia());
            } else {
                // Criar novo
                ParametroLocal parametroLocal = parametroLocalDtoToEntity(localDTO.getParametroLocal());
                local.setParametroLocal(parametroLocal);
            }
        } else {
            local.setParametroLocal(null);
        }

        Local updatedLocal = localRepository.save(local);
        log.info("Local atualizado com sucesso. ID: {}", updatedLocal.getId());
        return toDTO(updatedLocal);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deletando local com ID: {}", id);
        Local local = localRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Local não encontrado com ID: " + id));
        localRepository.delete(local);
        log.info("Local deletado com sucesso. ID: {}", id);
    }

    // Métodos de conversão DTO <-> Entity

    private LocalDTO toDTO(Local local) {
        LocalDTO dto = new LocalDTO();
        dto.setId(local.getId());
        dto.setNome(local.getNome());
        dto.setAtivo(local.getAtivo());
        dto.setCodigoUnico(local.getCodigoUnico());
        dto.setMostrarNoMapaPrincipal(local.getMostrarNoMapaPrincipal());
        dto.setMostrarNomeNoMapa(local.getMostrarNomeNoMapa());
        dto.setNotificaEvento(local.getNotificaEvento());
        dto.setFuncao(local.getFuncao());
        dto.setEndereco(local.getEndereco());
        dto.setBairro(local.getBairro());
        dto.setComplemento(local.getComplemento());
        dto.setCidade(local.getCidade());
        dto.setUf(local.getUf());
        dto.setTipo(local.getTipo());
        dto.setPermiteDescanso(local.getPermiteDescanso());
        dto.setClienteId(local.getCliente().getId());
        dto.setClienteNome(local.getCliente().getNome());
        dto.setDataCadastro(local.getDataCadastro());
        dto.setUpdatedAt(local.getUpdatedAt());

        // Converter coordenadas
        if (local.getListaDeCoordenadas() != null) {
            List<CoordenadaDTO> coordenadasDTO = local.getListaDeCoordenadas().stream()
                    .map(this::coordenadaToDto)
                    .collect(Collectors.toList());
            dto.setListaDeCoordenadas(coordenadasDTO);
        } else {
            dto.setListaDeCoordenadas(new ArrayList<>());
        }

        // Converter parâmetro local
        if (local.getParametroLocal() != null) {
            dto.setParametroLocal(parametroLocalToDto(local.getParametroLocal()));
        }

        return dto;
    }

    private Local toEntity(LocalDTO dto) {
        Local local = new Local();
        local.setNome(dto.getNome());
        local.setAtivo(dto.getAtivo());
        local.setCodigoUnico(dto.getCodigoUnico());
        local.setMostrarNoMapaPrincipal(dto.getMostrarNoMapaPrincipal());
        local.setMostrarNomeNoMapa(dto.getMostrarNomeNoMapa());
        local.setNotificaEvento(dto.getNotificaEvento());
        local.setFuncao(dto.getFuncao());
        local.setEndereco(dto.getEndereco());
        local.setBairro(dto.getBairro());
        local.setComplemento(dto.getComplemento());
        local.setCidade(dto.getCidade());
        local.setUf(dto.getUf());
        local.setTipo(dto.getTipo());
        local.setPermiteDescanso(dto.getPermiteDescanso());
        return local;
    }

    private CoordenadaDTO coordenadaToDto(Coordenada coordenada) {
        CoordenadaDTO dto = new CoordenadaDTO();
        dto.setId(coordenada.getId());
        dto.setSequencia(coordenada.getSequencia());
        dto.setLatitude(coordenada.getLatitude());
        dto.setLongitude(coordenada.getLongitude());
        dto.setIsRaio(coordenada.getIsRaio());
        dto.setRaio(coordenada.getRaio());
        return dto;
    }

    private Coordenada coordenadaDtoToEntity(CoordenadaDTO dto) {
        Coordenada coordenada = new Coordenada();
        coordenada.setSequencia(dto.getSequencia());
        coordenada.setLatitude(dto.getLatitude());
        coordenada.setLongitude(dto.getLongitude());
        coordenada.setIsRaio(dto.getIsRaio());
        coordenada.setRaio(dto.getRaio());
        return coordenada;
    }

    private ParametroLocalDTO parametroLocalToDto(ParametroLocal parametroLocal) {
        ParametroLocalDTO dto = new ParametroLocalDTO();
        dto.setId(parametroLocal.getId());
        dto.setLimiteVeiculosMesmoLocal(parametroLocal.getLimiteVeiculosMesmoLocal());
        dto.setTempoMinimoDePermanencia(parametroLocal.getTempoMinimoDePermanencia());
        dto.setTempoMaximoDePermanencia(parametroLocal.getTempoMaximoDePermanencia());
        return dto;
    }

    private ParametroLocal parametroLocalDtoToEntity(ParametroLocalDTO dto) {
        ParametroLocal parametroLocal = new ParametroLocal();
        parametroLocal.setLimiteVeiculosMesmoLocal(dto.getLimiteVeiculosMesmoLocal());
        parametroLocal.setTempoMinimoDePermanencia(dto.getTempoMinimoDePermanencia());
        parametroLocal.setTempoMaximoDePermanencia(dto.getTempoMaximoDePermanencia());
        return parametroLocal;
    }
}
