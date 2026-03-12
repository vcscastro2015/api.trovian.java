package com.trovian.service;

import com.trovian.dto.TransmissaoBasicaDTO;
import com.trovian.entity.TransmissaoBasica;
import com.trovian.repository.TransmissaoBasicaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransmissaoBasicaService {

    private final TransmissaoBasicaRepository transmissaoBasicaRepository;

    @Transactional(readOnly = true)
    public Page<TransmissaoBasicaDTO> findByVeiculo(Long veiculoId, Pageable pageable) {
        log.info("Buscando transmissões básicas do veículo {}", veiculoId);
        return transmissaoBasicaRepository.findByVeiculoId(veiculoId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public TransmissaoBasicaDTO findUltimaByVeiculo(Long veiculoId) {
        log.info("Buscando última transmissão básica do veículo {}", veiculoId);
        return transmissaoBasicaRepository.findTopByVeiculoIdOrderByDataTransmissaoDesc(veiculoId)
                .map(this::toDTO)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Nenhuma transmissão básica encontrada para o veículo " + veiculoId));
    }

    @Transactional(readOnly = true)
    public Page<TransmissaoBasicaDTO> findByVeiculoEPeriodo(Long veiculoId, Date dataInicial, Date dataFinal, Pageable pageable) {
        log.info("Buscando transmissões básicas do veículo {} entre {} e {}", veiculoId, dataInicial, dataFinal);
        return transmissaoBasicaRepository.findByVeiculoIdAndDataTransmissaoBetween(veiculoId, dataInicial, dataFinal, pageable)
                .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public List<TransmissaoBasicaDTO> findUltimasByVeiculos(List<Long> veiculoIds) {
        log.info("Buscando última transmissão básica dos veículos {}", veiculoIds);
        return transmissaoBasicaRepository.findUltimasByVeiculoIds(veiculoIds)
                .stream()
                .map(this::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TransmissaoBasicaDTO> findByVeiculosEPeriodo(List<Long> veiculoIds, Date dataInicial, Date dataFinal, Pageable pageable) {
        log.info("Buscando transmissões básicas dos veículos {} entre {} e {}", veiculoIds, dataInicial, dataFinal);
        return transmissaoBasicaRepository.findByVeiculoIdInAndDataTransmissaoBetween(veiculoIds, dataInicial, dataFinal, pageable)
                .map(this::toDTO);
    }

    private TransmissaoBasicaDTO toDTO(TransmissaoBasica t) {
        TransmissaoBasicaDTO dto = new TransmissaoBasicaDTO();
        dto.setId(t.getId());
        dto.setAltitude(t.getAltitude());
        dto.setDataTransmissao(t.getDataTransmissao());
        dto.setDirecao(t.getDirecao());
        dto.setExecessoVelocidade(t.getExecessoVelocidade());
        dto.setGprsAtivo(t.getGprsAtivo());
        dto.setGpsAtivo(t.getGpsAtivo());
        dto.setHodometro(t.getHodometro());
        dto.setHorimetro(t.getHorimetro());
        dto.setIgnicaoAtiva(t.getIgnicaoAtiva());
        dto.setLatitude(t.getLatitude());
        dto.setLongitude(t.getLongitude());
        dto.setPanico(t.getPanico());
        dto.setPercentualBateriaEquipamento(t.getPercentualBateriaEquipamento());
        dto.setPontoDeReferencia(t.getPontoDeReferencia());
        dto.setSaida4(t.getSaida4());
        dto.setTemperatura(t.getTemperatura());
        dto.setVelocidade(t.getVelocidade());
        dto.setVoltagem(t.getVoltagem());
        dto.setJamming(t.getJamming());
        dto.setSinalJamming(t.getSinalJamming());
        dto.setFalhaAlimentacaoExterna(t.getFalhaAlimentacaoExterna());
        dto.setPanicoVisualizado(t.getPanicoVisualizado());
        dto.setPanicoDesativado(t.getPanicoDesativado());
        dto.setConexaoPrimariaAtiva(t.getConexaoPrimariaAtiva());
        dto.setIdentificadorDeEvento(t.getIdentificadorDeEvento());
        dto.setDataEnvioSms(t.getDataEnvioSms());
        dto.setEntradaDigitalUm(t.getEntradaDigitalUm());
        dto.setUltimoEnvioSms(t.getUltimoEnvioSms());
        dto.setAlerta(t.getAlerta());
        dto.setDistanciaPercorrida(t.getDistanciaPercorrida());
        dto.setEvento(t.getEvento());
        dto.setIbutton(t.getIbutton());
        dto.setModo(t.getModo());
        dto.setRpmCorrente(t.getRpmCorrente());
        dto.setTipoAlerta(t.getTipoAlerta());
        dto.setTipoEvento(t.getTipoEvento());
        dto.setViagem(t.getViagem());
        dto.setDataRegistro(t.getDataRegistro());
        dto.setIdTransmissaoPai(t.getIdTransmissaoPai());
        dto.setEndereco(t.getEndereco());
        dto.setBairro(t.getBairro());
        dto.setCidade(t.getCidade());
        dto.setEstado(t.getEstado());
        if (t.getVeiculo() != null) {
            dto.setVeiculoId(t.getVeiculo().getId());
            dto.setVeiculoPlaca(t.getVeiculo().getPlaca());
            dto.setVeiculoTipo(t.getVeiculo().getTipo());
            if (t.getVeiculo().getModelo() != null) {
                dto.setVeiculoModelo(t.getVeiculo().getModelo().getFabricante() + " " + t.getVeiculo().getModelo().getMarca());
            }
        }
        return dto;
    }
}
