package com.trovian.service;

import com.trovian.dto.TransmissaoDTO;
import com.trovian.entity.Transmissao;
import com.trovian.repository.TransmissaoRepository;
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
public class TransmissaoService {

    private final TransmissaoRepository transmissaoRepository;

    @Transactional(readOnly = true)
    public Page<TransmissaoDTO> findByVeiculo(Long veiculoId, Pageable pageable) {
        log.info("Buscando transmissões do veículo {}", veiculoId);
        return transmissaoRepository.findByVeiculoId(veiculoId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public TransmissaoDTO findUltimaByVeiculo(Long veiculoId) {
        log.info("Buscando última transmissão do veículo {}", veiculoId);
        return transmissaoRepository.findTopByVeiculoIdOrderByDataTransmissaoDesc(veiculoId)
                .map(this::toDTO)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException(
                        "Nenhuma transmissão encontrada para o veículo " + veiculoId));
    }

    @Transactional(readOnly = true)
    public Page<TransmissaoDTO> findByVeiculoEPeriodo(Long veiculoId, Date dataInicial, Date dataFinal, Pageable pageable) {
        log.info("Buscando transmissões do veículo {} entre {} e {}", veiculoId, dataInicial, dataFinal);
        return transmissaoRepository.findByVeiculoIdAndDataTransmissaoBetween(veiculoId, dataInicial, dataFinal, pageable)
                .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<TransmissaoDTO> findByVeiculosEPeriodo(List<Long> veiculoIds, Date dataInicial, Date dataFinal, Pageable pageable) {
        log.info("Buscando transmissões dos veículos {} entre {} e {}", veiculoIds, dataInicial, dataFinal);
        return transmissaoRepository.findByVeiculoIdInAndDataTransmissaoBetween(veiculoIds, dataInicial, dataFinal, pageable)
                .map(this::toDTO);
    }

    private TransmissaoDTO toDTO(Transmissao t) {
        TransmissaoDTO dto = new TransmissaoDTO();
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
        dto.setConexaoPrimariaAtiva(t.getConexaoPrimariaAtiva());
        dto.setEntradaDigital1(t.getEntradaDigital1());
        dto.setEntradaDigital2(t.getEntradaDigital2());
        dto.setEntradaDigital3(t.getEntradaDigital3());
        dto.setEntradaDigital4(t.getEntradaDigital4());
        dto.setIdentificadorDoEvento(t.getIdentificadorDoEvento());
        dto.setUltimoEnvioSms(t.getUltimoEnvioSms());
        dto.setDataEnvioSms(t.getDataEnvioSms());
        dto.setDistanciaPercorrida(t.getDistanciaPercorrida());
        dto.setModo(t.getModo());
        dto.setRpmCorrente(t.getRpmCorrente());
        dto.setIButton(t.getIButton());
        dto.setEvento(t.getEvento());
        dto.setTipoEvento(t.getTipoEvento());
        dto.setAlerta(t.getAlerta());
        dto.setTipoAlerta(t.getTipoAlerta());
        dto.setViagem(t.getViagem());
        dto.setEndereco(t.getEndereco());
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
