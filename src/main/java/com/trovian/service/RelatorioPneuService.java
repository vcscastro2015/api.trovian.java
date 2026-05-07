package com.trovian.service;

import com.trovian.dto.CpkPneuDTO;
import com.trovian.entity.AlocacaoPneu;
import com.trovian.entity.Pneu;
import com.trovian.enums.StatusPneu;
import com.trovian.repository.AlocacaoPneuRepository;
import com.trovian.repository.PneuRepository;
import com.trovian.repository.RecapagemPneuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelatorioPneuService {

    private final PneuRepository pneuRepository;
    private final AlocacaoPneuRepository alocacaoPneuRepository;
    private final RecapagemPneuRepository recapagemPneuRepository;
    private final PneuService pneuService;

    @Transactional(readOnly = true)
    public List<CpkPneuDTO> getCpkPorVeiculo(Long veiculoId) {
        List<Pneu> pneus = pneuRepository.findPneuAtualByVeiculo(veiculoId);
        return pneus.stream().map(p -> pneuService.calcularCpk(p.getId())).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getCpkPorMarca(Long clienteId) {
        List<Pneu> pneus = pneuRepository.findByClienteId(clienteId, Pageable.unpaged()).getContent();
        return pneus.stream()
                .filter(p -> p.getMarca() != null && p.getKmAcumulado() != null && p.getKmAcumulado() > 0)
                .collect(Collectors.groupingBy(
                        Pneu::getMarca,
                        Collectors.averagingDouble(p -> {
                            BigDecimal valorCompra = p.getValorCompra() != null ? p.getValorCompra() : BigDecimal.ZERO;
                            BigDecimal recapagens = recapagemPneuRepository.sumValorRecapagensByPneuId(p.getId());
                            BigDecimal custo = valorCompra.add(recapagens != null ? recapagens : BigDecimal.ZERO);
                            return custo.divide(BigDecimal.valueOf(p.getKmAcumulado()), 4, RoundingMode.HALF_UP).doubleValue();
                        })
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> BigDecimal.valueOf(e.getValue()).setScale(4, RoundingMode.HALF_UP)));
    }

    @Transactional(readOnly = true)
    public List<CpkPneuDTO> getPneusProximosTroca(Long clienteId) {
        List<Pneu> pneus = pneuRepository.findByClienteId(clienteId, Pageable.unpaged()).getContent();
        LocalDate limiteIdade = LocalDate.now().minusYears(4);
        return pneus.stream()
                .filter(p -> p.getStatus() == StatusPneu.EM_USO || p.getStatus() == StatusPneu.NOVO)
                .filter(p -> estaProximoTroca(p, limiteIdade))
                .map(p -> pneuService.calcularCpk(p.getId()))
                .collect(Collectors.toList());
    }

    private boolean estaProximoTroca(Pneu pneu, LocalDate limiteIdade) {
        if (pneu.getDataFabricacao() != null && pneu.getDataFabricacao().isBefore(limiteIdade)) {
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public Map<StatusPneu, Long> getDistribuicaoStatus(Long clienteId) {
        List<Pneu> pneus = pneuRepository.findByClienteId(clienteId, Pageable.unpaged()).getContent();
        return pneus.stream().collect(Collectors.groupingBy(Pneu::getStatus, Collectors.counting()));
    }

    @Transactional(readOnly = true)
    public BigDecimal getCustoTotalFrota(Long clienteId, LocalDate desde) {
        List<Pneu> pneus = pneuRepository.findByClienteId(clienteId, Pageable.unpaged()).getContent();
        return pneus.stream()
                .filter(p -> p.getDataCompra() != null && !p.getDataCompra().isBefore(desde))
                .map(p -> {
                    BigDecimal compra = p.getValorCompra() != null ? p.getValorCompra() : BigDecimal.ZERO;
                    BigDecimal recap = recapagemPneuRepository.sumValorRecapagensByPneuId(p.getId());
                    return compra.add(recap != null ? recap : BigDecimal.ZERO);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getVidaMediaPorMarcaModelo(Long clienteId) {
        List<Pneu> pneus = pneuRepository.findByClienteId(clienteId, Pageable.unpaged()).getContent();
        return pneus.stream()
                .filter(p -> p.getMarca() != null && p.getModelo() != null
                        && p.getKmAcumulado() != null && p.getKmAcumulado() > 0)
                .collect(Collectors.groupingBy(
                        p -> p.getMarca() + " " + p.getModelo(),
                        Collectors.averagingDouble(Pneu::getKmAcumulado)
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> BigDecimal.valueOf(e.getValue()).setScale(0, RoundingMode.HALF_UP)));
    }
}
