package com.trovian.service;

import com.trovian.dto.*;
import com.trovian.entity.*;
import com.trovian.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Service para gerenciamento de rotas
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RotaService {

    private final RotaRepository rotaRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final CooperativaRepository cooperativaRepository;

    /**
     * Lista todas as rotas com paginação
     */
    @Transactional(readOnly = true)
    public Page<RotaDTO> findAll(Pageable pageable) {
        log.info("Buscando todas as rotas com paginação: {}", pageable);
        return rotaRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * Busca rota por ID
     */
    @Transactional(readOnly = true)
    public RotaDTO findById(Long id) {
        log.info("Buscando rota por ID: {}", id);
        Rota rota = rotaRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Rota não encontrada com ID: " + id));
        return toDTO(rota);
    }

    /**
     * Cria nova rota
     */
    @Transactional
    public RotaDTO create(RotaDTO rotaDTO) {
        log.info("Criando nova rota: {}", rotaDTO.getNome());

        Rota rota = toEntity(rotaDTO);

        // Valida e define relacionamentos opcionais
        if (rotaDTO.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(rotaDTO.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + rotaDTO.getClienteId()));
            rota.setCliente(cliente);
        }

        if (rotaDTO.getVeiculoId() != null) {
            Veiculo veiculo = veiculoRepository.findById(rotaDTO.getVeiculoId())
                    .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + rotaDTO.getVeiculoId()));
            rota.setVeiculo(veiculo);
        }

        if (rotaDTO.getCooperativaId() != null) {
            Cooperativa cooperativa = cooperativaRepository.findById(rotaDTO.getCooperativaId())
                    .orElseThrow(() -> new RuntimeException("Cooperativa não encontrada com ID: " + rotaDTO.getCooperativaId()));
            rota.setCooperativa(cooperativa);
        }

        Rota savedRota = rotaRepository.save(rota);
        log.info("Rota criada com sucesso com ID: {}", savedRota.getId());

        return toDTO(savedRota);
    }

    /**
     * Atualiza rota existente
     */
    @Transactional
    public RotaDTO update(Long id, RotaDTO rotaDTO) {
        log.info("Atualizando rota ID: {}", id);

        Rota rotaExistente = rotaRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Rota não encontrada com ID: " + id));

        // Atualiza campos básicos
        rotaExistente.setNome(rotaDTO.getNome());
        rotaExistente.setDescricao(rotaDTO.getDescricao());
        rotaExistente.setAtiva(rotaDTO.getAtiva());
        rotaExistente.setDistanciaTotal(rotaDTO.getDistanciaTotal());
        rotaExistente.setGeoJson(rotaDTO.getGeoJson());

        // Atualiza relacionamentos opcionais
        if (rotaDTO.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(rotaDTO.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + rotaDTO.getClienteId()));
            rotaExistente.setCliente(cliente);
        } else {
            rotaExistente.setCliente(null);
        }

        if (rotaDTO.getVeiculoId() != null) {
            Veiculo veiculo = veiculoRepository.findById(rotaDTO.getVeiculoId())
                    .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + rotaDTO.getVeiculoId()));
            rotaExistente.setVeiculo(veiculo);
        } else {
            rotaExistente.setVeiculo(null);
        }

        if (rotaDTO.getCooperativaId() != null) {
            Cooperativa cooperativa = cooperativaRepository.findById(rotaDTO.getCooperativaId())
                    .orElseThrow(() -> new RuntimeException("Cooperativa não encontrada com ID: " + rotaDTO.getCooperativaId()));
            rotaExistente.setCooperativa(cooperativa);
        } else {
            rotaExistente.setCooperativa(null);
        }

        // Remove pontos antigos e adiciona novos
        rotaExistente.getPontos().clear();
        if (rotaDTO.getPontos() != null) {
            rotaDTO.getPontos().forEach(pontoDTO -> {
                PontoRota ponto = toPontoRotaEntity(pontoDTO);
                rotaExistente.addPonto(ponto);
            });
        }

        // Atualiza estatísticas
        if (rotaDTO.getEstatisticas() != null) {
            if (rotaExistente.getEstatisticas() == null) {
                rotaExistente.setEstatisticas(toRotaEstatisticasEntity(rotaDTO.getEstatisticas()));
            } else {
                updateEstatisticas(rotaExistente.getEstatisticas(), rotaDTO.getEstatisticas());
            }
        }

        // Remove segmentos antigos e adiciona novos
        rotaExistente.getSegmentos().clear();
        if (rotaDTO.getSegmentos() != null) {
            rotaDTO.getSegmentos().forEach(segmentoDTO -> {
                RotaSegmento segmento = toRotaSegmentoEntity(segmentoDTO);
                rotaExistente.addSegmento(segmento);
            });
        }

        // Remove pedágios antigos e adiciona novos
        rotaExistente.getPedagios().clear();
        if (rotaDTO.getPedagios() != null) {
            rotaDTO.getPedagios().forEach(pedagioDTO -> {
                RotaPedagio pedagio = toRotaPedagioEntity(pedagioDTO);
                rotaExistente.addPedagio(pedagio);
            });
        }

        Rota updatedRota = rotaRepository.save(rotaExistente);
        log.info("Rota atualizada com sucesso: {}", id);

        return toDTO(updatedRota);
    }

    /**
     * Deleta rota por ID
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deletando rota ID: {}", id);

        if (!rotaRepository.existsById(id)) {
            throw new RuntimeException("Rota não encontrada com ID: " + id);
        }

        rotaRepository.deleteById(id);
        log.info("Rota deletada com sucesso: {}", id);
    }

    /**
     * Busca rotas por cliente
     */
    @Transactional(readOnly = true)
    public Page<RotaDTO> findByCliente(Long clienteId, RotaFiltrosDTO filtros, Pageable pageable) {
        log.info("Buscando rotas do cliente ID: {}", clienteId);
        return rotaRepository.findByClienteId(clienteId, filtros.getNome(), pageable).map(this::toDTO);
    }

    /**
     * Busca rotas por veículo
     */
    @Transactional(readOnly = true)
    public Page<RotaDTO> findByVeiculo(Long veiculoId, Pageable pageable) {
        log.info("Buscando rotas do veículo ID: {}", veiculoId);
        return rotaRepository.findByVeiculoId(veiculoId, pageable).map(this::toDTO);
    }

    /**
     * Busca rotas por cooperativa
     */
    @Transactional(readOnly = true)
    public Page<RotaDTO> findByCooperativa(Long cooperativaId, Pageable pageable) {
        log.info("Buscando rotas da cooperativa ID: {}", cooperativaId);
        return rotaRepository.findByCooperativaId(cooperativaId, pageable).map(this::toDTO);
    }

    /**
     * Busca rotas ativas
     */
    @Transactional(readOnly = true)
    public Page<RotaDTO> findByAtiva(Boolean ativa, Pageable pageable) {
        log.info("Buscando rotas com status ativo: {}", ativa);
        return rotaRepository.findByAtiva(ativa, pageable).map(this::toDTO);
    }

    /**
     * Busca rotas por nome
     */
    @Transactional(readOnly = true)
    public Page<RotaDTO> searchByNome(String nome, Pageable pageable) {
        log.info("Buscando rotas por nome: {}", nome);
        return rotaRepository.findByNomeContainingIgnoreCase(nome, pageable).map(this::toDTO);
    }

    /**
     * Busca rotas com filtros múltiplos
     */
    @Transactional(readOnly = true)
    public Page<RotaDTO> findByFiltros(RotaFiltrosDTO filtros, Pageable pageable) {
        log.info("Buscando rotas com filtros: {}", filtros);
        return rotaRepository.findByFiltros(
                filtros.getNome(),
                filtros.getClienteId(),
                filtros.getVeiculoId(),
                filtros.getCooperativaId(),
                filtros.getAtiva(),
                filtros.getDistanciaMinima(),
                filtros.getDistanciaMaxima(),
                filtros.getDificuldade(),
                pageable
        ).map(this::toDTO);
    }

    /**
     * Duplica uma rota existente
     */
    @Transactional
    public RotaDTO duplicate(Long id, String novoNome) {
        log.info("Duplicando rota ID: {} com novo nome: {}", id, novoNome);

        Rota rotaOriginal = rotaRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Rota não encontrada com ID: " + id));

        RotaDTO rotaDTO = toDTO(rotaOriginal);
        rotaDTO.setId(null); // Remove ID para criar nova
        rotaDTO.setNome(novoNome);
        rotaDTO.setDataCadastro(null);
        rotaDTO.setDataAtualizacao(null);

        return create(rotaDTO);
    }

    /**
     * Exporta rota em formato GeoJSON
     */
    @Transactional(readOnly = true)
    public String exportGeoJson(Long id) {
        log.info("Exportando rota ID: {} em formato GeoJSON", id);

        Rota rota = rotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rota não encontrada com ID: " + id));

        return rota.getGeoJson();
    }

    // ========== Métodos de Conversão ==========

    /**
     * Converte Entity para DTO
     */
    private RotaDTO toDTO(Rota rota) {
        RotaDTO dto = new RotaDTO();
        dto.setId(rota.getId());
        dto.setNome(rota.getNome());
        dto.setDescricao(rota.getDescricao());
        dto.setAtiva(rota.getAtiva());
        dto.setDataCadastro(rota.getDataCadastro());
        dto.setDataAtualizacao(rota.getDataAtualizacao());
        dto.setDistanciaTotal(rota.getDistanciaTotal());
        dto.setGeoJson(rota.getGeoJson());

        // IDs de relacionamentos
        if (rota.getCliente() != null) {
            dto.setClienteId(rota.getCliente().getId());
            dto.setClienteNome(rota.getCliente().getNome());
        }
        if (rota.getVeiculo() != null) {
            dto.setVeiculoId(rota.getVeiculo().getId());
            dto.setVeiculoPlaca(rota.getVeiculo().getPlaca());
        }
        if (rota.getCooperativa() != null) {
            dto.setCooperativaId(rota.getCooperativa().getId());
            dto.setCooperativaNome(rota.getCooperativa().getNome());
        }

        // Pontos
        if (rota.getPontos() != null) {
            dto.setPontos(rota.getPontos().stream()
                    .map(this::toPontoRotaDTO)
                    .collect(Collectors.toList()));
        }

        // Estatísticas
        if (rota.getEstatisticas() != null) {
            dto.setEstatisticas(toRotaEstatisticasDTO(rota.getEstatisticas()));
        }

        // Segmentos
        if (rota.getSegmentos() != null) {
            dto.setSegmentos(rota.getSegmentos().stream()
                    .map(this::toRotaSegmentoDTO)
                    .collect(Collectors.toList()));
        }

        // Pedágios
        if (rota.getPedagios() != null) {
            dto.setPedagios(rota.getPedagios().stream()
                    .map(this::toRotaPedagioDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    /**
     * Converte DTO para Entity
     */
    private Rota toEntity(RotaDTO dto) {
        Rota rota = new Rota();
        rota.setNome(dto.getNome());
        rota.setDescricao(dto.getDescricao());
        rota.setAtiva(dto.getAtiva());
        rota.setDistanciaTotal(dto.getDistanciaTotal());
        rota.setGeoJson(dto.getGeoJson());

        // Pontos
        if (dto.getPontos() != null) {
            dto.getPontos().forEach(pontoDTO -> {
                PontoRota ponto = toPontoRotaEntity(pontoDTO);
                rota.addPonto(ponto);
            });
        }

        // Estatísticas
        if (dto.getEstatisticas() != null) {
            rota.setEstatisticas(toRotaEstatisticasEntity(dto.getEstatisticas()));
        }

        // Segmentos
        if (dto.getSegmentos() != null) {
            dto.getSegmentos().forEach(segmentoDTO -> {
                RotaSegmento segmento = toRotaSegmentoEntity(segmentoDTO);
                rota.addSegmento(segmento);
            });
        }

        // Pedágios
        if (dto.getPedagios() != null) {
            dto.getPedagios().forEach(pedagioDTO -> {
                RotaPedagio pedagio = toRotaPedagioEntity(pedagioDTO);
                rota.addPedagio(pedagio);
            });
        }

        return rota;
    }

    private PontoRotaDTO toPontoRotaDTO(PontoRota ponto) {
        PontoRotaDTO dto = new PontoRotaDTO();
        dto.setId(ponto.getId());
        dto.setSequencia(ponto.getSequencia());
        dto.setTipo(ponto.getTipo());
        dto.setLatitude(ponto.getLatitude());
        dto.setLongitude(ponto.getLongitude());
        dto.setEndereco(ponto.getEndereco());
        dto.setNome(ponto.getNome());
        return dto;
    }

    private PontoRota toPontoRotaEntity(PontoRotaDTO dto) {
        PontoRota ponto = new PontoRota();
        ponto.setSequencia(dto.getSequencia());
        ponto.setTipo(dto.getTipo());
        ponto.setLatitude(dto.getLatitude());
        ponto.setLongitude(dto.getLongitude());
        ponto.setEndereco(dto.getEndereco());
        ponto.setNome(dto.getNome());
        return ponto;
    }

    private RotaEstatisticasDTO toRotaEstatisticasDTO(RotaEstatisticas stats) {
        RotaEstatisticasDTO dto = new RotaEstatisticasDTO();
        dto.setId(stats.getId());
        dto.setGanhoTotalElevacao(stats.getGanhoTotalElevacao());
        dto.setPerdaTotalElevacao(stats.getPerdaTotalElevacao());
        dto.setElevacaoMaxima(stats.getElevacaoMaxima());
        dto.setElevacaoMinima(stats.getElevacaoMinima());
        dto.setElevacaoMedia(stats.getElevacaoMedia());
        dto.setDificuldade(stats.getDificuldade());
        dto.setDistanciaSubida(stats.getDistanciaSubida());
        dto.setDistanciaDescida(stats.getDistanciaDescida());
        dto.setDistanciaPlano(stats.getDistanciaPlano());

        if (stats.getPontoElevacaoMaximaLat() != null) {
            dto.setPontoElevacaoMaxima(new RotaEstatisticasDTO.PontoElevacaoDTO(
                    stats.getPontoElevacaoMaximaLat(),
                    stats.getPontoElevacaoMaximaLng(),
                    stats.getPontoElevacaoMaximaElev()
            ));
        }

        if (stats.getPontoElevacaoMinimaLat() != null) {
            dto.setPontoElevacaoMinima(new RotaEstatisticasDTO.PontoElevacaoDTO(
                    stats.getPontoElevacaoMinimaLat(),
                    stats.getPontoElevacaoMinimaLng(),
                    stats.getPontoElevacaoMinimaElev()
            ));
        }

        return dto;
    }

    private RotaEstatisticas toRotaEstatisticasEntity(RotaEstatisticasDTO dto) {
        RotaEstatisticas stats = new RotaEstatisticas();
        stats.setGanhoTotalElevacao(dto.getGanhoTotalElevacao());
        stats.setPerdaTotalElevacao(dto.getPerdaTotalElevacao());
        stats.setElevacaoMaxima(dto.getElevacaoMaxima());
        stats.setElevacaoMinima(dto.getElevacaoMinima());
        stats.setElevacaoMedia(dto.getElevacaoMedia());
        stats.setDificuldade(dto.getDificuldade());
        stats.setDistanciaSubida(dto.getDistanciaSubida());
        stats.setDistanciaDescida(dto.getDistanciaDescida());
        stats.setDistanciaPlano(dto.getDistanciaPlano());

        if (dto.getPontoElevacaoMaxima() != null) {
            stats.setPontoElevacaoMaximaLat(dto.getPontoElevacaoMaxima().getLatitude());
            stats.setPontoElevacaoMaximaLng(dto.getPontoElevacaoMaxima().getLongitude());
            stats.setPontoElevacaoMaximaElev(dto.getPontoElevacaoMaxima().getElevacao());
        }

        if (dto.getPontoElevacaoMinima() != null) {
            stats.setPontoElevacaoMinimaLat(dto.getPontoElevacaoMinima().getLatitude());
            stats.setPontoElevacaoMinimaLng(dto.getPontoElevacaoMinima().getLongitude());
            stats.setPontoElevacaoMinimaElev(dto.getPontoElevacaoMinima().getElevacao());
        }

        return stats;
    }

    private void updateEstatisticas(RotaEstatisticas stats, RotaEstatisticasDTO dto) {
        stats.setGanhoTotalElevacao(dto.getGanhoTotalElevacao());
        stats.setPerdaTotalElevacao(dto.getPerdaTotalElevacao());
        stats.setElevacaoMaxima(dto.getElevacaoMaxima());
        stats.setElevacaoMinima(dto.getElevacaoMinima());
        stats.setElevacaoMedia(dto.getElevacaoMedia());
        stats.setDificuldade(dto.getDificuldade());
        stats.setDistanciaSubida(dto.getDistanciaSubida());
        stats.setDistanciaDescida(dto.getDistanciaDescida());
        stats.setDistanciaPlano(dto.getDistanciaPlano());

        if (dto.getPontoElevacaoMaxima() != null) {
            stats.setPontoElevacaoMaximaLat(dto.getPontoElevacaoMaxima().getLatitude());
            stats.setPontoElevacaoMaximaLng(dto.getPontoElevacaoMaxima().getLongitude());
            stats.setPontoElevacaoMaximaElev(dto.getPontoElevacaoMaxima().getElevacao());
        }

        if (dto.getPontoElevacaoMinima() != null) {
            stats.setPontoElevacaoMinimaLat(dto.getPontoElevacaoMinima().getLatitude());
            stats.setPontoElevacaoMinimaLng(dto.getPontoElevacaoMinima().getLongitude());
            stats.setPontoElevacaoMinimaElev(dto.getPontoElevacaoMinima().getElevacao());
        }
    }

    private RotaSegmentoDTO toRotaSegmentoDTO(RotaSegmento segmento) {
        RotaSegmentoDTO dto = new RotaSegmentoDTO();
        dto.setId(segmento.getId());
        dto.setSequencia(segmento.getSequencia());
        dto.setLatitudeInicio(segmento.getLatitudeInicio());
        dto.setLongitudeInicio(segmento.getLongitudeInicio());
        dto.setElevacaoInicio(segmento.getElevacaoInicio());
        dto.setLatitudeFim(segmento.getLatitudeFim());
        dto.setLongitudeFim(segmento.getLongitudeFim());
        dto.setElevacaoFim(segmento.getElevacaoFim());
        dto.setDirecao(segmento.getDirecao());
        dto.setNivelInclinacao(segmento.getNivelInclinacao());
        dto.setPorcentagemInclinacao(segmento.getPorcentagemInclinacao());
        dto.setVariacaoElevacao(segmento.getVariacaoElevacao());
        dto.setDistancia(segmento.getDistancia());
        dto.setCor(segmento.getCor());
        return dto;
    }

    private RotaSegmento toRotaSegmentoEntity(RotaSegmentoDTO dto) {
        RotaSegmento segmento = new RotaSegmento();
        segmento.setSequencia(dto.getSequencia());
        segmento.setLatitudeInicio(dto.getLatitudeInicio());
        segmento.setLongitudeInicio(dto.getLongitudeInicio());
        segmento.setElevacaoInicio(dto.getElevacaoInicio());
        segmento.setLatitudeFim(dto.getLatitudeFim());
        segmento.setLongitudeFim(dto.getLongitudeFim());
        segmento.setElevacaoFim(dto.getElevacaoFim());
        segmento.setDirecao(dto.getDirecao());
        segmento.setNivelInclinacao(dto.getNivelInclinacao());
        segmento.setPorcentagemInclinacao(dto.getPorcentagemInclinacao());
        segmento.setVariacaoElevacao(dto.getVariacaoElevacao());
        segmento.setDistancia(dto.getDistancia());
        segmento.setCor(dto.getCor());
        return segmento;
    }

    private RotaPedagioDTO toRotaPedagioDTO(RotaPedagio pedagio) {
        RotaPedagioDTO dto = new RotaPedagioDTO();
        dto.setId(pedagio.getId());
        dto.setNome(pedagio.getNome());
        dto.setRodovia(pedagio.getRodovia());
        dto.setKm(pedagio.getKm());
        dto.setMunicipio(pedagio.getMunicipio());
        dto.setUf(pedagio.getUf());
        dto.setConcessionaria(pedagio.getConcessionaria());
        dto.setLatitude(pedagio.getLatitude());
        dto.setLongitude(pedagio.getLongitude());
        dto.setDistanciaRota(pedagio.getDistanciaRota());
        return dto;
    }

    private RotaPedagio toRotaPedagioEntity(RotaPedagioDTO dto) {
        RotaPedagio pedagio = new RotaPedagio();
        pedagio.setNome(dto.getNome());
        pedagio.setRodovia(dto.getRodovia());
        pedagio.setKm(dto.getKm());
        pedagio.setMunicipio(dto.getMunicipio());
        pedagio.setUf(dto.getUf());
        pedagio.setConcessionaria(dto.getConcessionaria());
        pedagio.setLatitude(dto.getLatitude());
        pedagio.setLongitude(dto.getLongitude());
        pedagio.setDistanciaRota(dto.getDistanciaRota());
        return pedagio;
    }
}
