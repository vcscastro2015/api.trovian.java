package com.trovian.service;

import com.trovian.dto.AlocacaoPneuDTO;
import com.trovian.dto.InspecaoPneuDTO;
import com.trovian.dto.MapaVeiculoPneusDTO;
import com.trovian.dto.PneuDTO;
import com.trovian.dto.PosicaoPneuDetalheDTO;
import com.trovian.dto.RodizioItemDTO;
import com.trovian.entity.AlocacaoPneu;
import com.trovian.entity.InspecaoPneu;
import com.trovian.entity.Pneu;
import com.trovian.entity.Veiculo;
import com.trovian.enums.CondicaoVisual;
import com.trovian.enums.MotivoDesmontagem;
import com.trovian.enums.PosicaoPneu;
import com.trovian.enums.StatusPneu;
import com.trovian.repository.AlocacaoPneuRepository;
import com.trovian.repository.InspecaoPneuRepository;
import com.trovian.repository.PneuRepository;
import com.trovian.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlocacaoPneuService {

    private final AlocacaoPneuRepository alocacaoPneuRepository;
    private final PneuRepository pneuRepository;
    private final VeiculoRepository veiculoRepository;
    private final InspecaoPneuRepository inspecaoPneuRepository;
    private final InspecaoPneuService inspecaoPneuService;
    private final PneuEstoqueService pneuEstoqueService;

    @Transactional
    public AlocacaoPneuDTO montar(Long pneuId, Long veiculoId, PosicaoPneu posicao, Integer km, String responsavel) {
        log.info("Montando pneu {} no veículo {} posição {}", pneuId, veiculoId, posicao);

        Pneu pneu = pneuRepository.findById(pneuId)
                .orElseThrow(() -> new RuntimeException("Pneu não encontrado com id: " + pneuId));

        if (pneu.getStatus() != StatusPneu.NOVO && pneu.getStatus() != StatusPneu.RECAPADO) {
            throw new RuntimeException("Pneu não disponível para montagem. Status atual: " + pneu.getStatus());
        }

        Veiculo veiculo = veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com id: " + veiculoId));

        if (alocacaoPneuRepository.existsAlocacaoAtivaByVeiculoIdAndPosicao(veiculoId, posicao)) {
            throw new RuntimeException("Já existe pneu montado na posição " + posicao + " do veículo " + veiculo.getPlaca());
        }

        AlocacaoPneu alocacao = new AlocacaoPneu();
        alocacao.setPneu(pneu);
        alocacao.setVeiculo(veiculo);
        alocacao.setPosicao(posicao);
        alocacao.setDataMontagem(LocalDateTime.now());
        alocacao.setKmMontagem(km);
        alocacao.setResponsavel(responsavel);

        pneu.setStatus(StatusPneu.EM_USO);
        pneuRepository.save(pneu);
        pneuEstoqueService.registrarSaida(pneu, "Montagem DOT " + pneu.getNumeroDot()
                + " veículo " + veiculo.getPlaca() + " posição " + posicao);

        return toDTO(alocacaoPneuRepository.save(alocacao));
    }

    @Transactional
    public AlocacaoPneuDTO desmontar(Long alocacaoId, Integer kmRemocao, MotivoDesmontagem motivo) {
        log.info("Desmontando alocação id: {}", alocacaoId);

        AlocacaoPneu alocacao = alocacaoPneuRepository.findById(alocacaoId)
                .orElseThrow(() -> new RuntimeException("Alocação não encontrada com id: " + alocacaoId));

        if (alocacao.getDataRemocao() != null) {
            throw new RuntimeException("Esta alocação já foi encerrada");
        }

        alocacao.setDataRemocao(LocalDateTime.now());
        alocacao.setKmRemocao(kmRemocao);
        alocacao.setMotivoRemocao(motivo);

        Pneu pneu = alocacao.getPneu();
        if (alocacao.getKmMontagem() != null && kmRemocao != null) {
            int kmRodados = kmRemocao - alocacao.getKmMontagem();
            if (kmRodados > 0) {
                pneu.setKmAcumulado(pneu.getKmAcumulado() + kmRodados);
            }
        }

        StatusPneu novoStatus = switch (motivo) {
            case ENVIO_RECAPAGEM -> StatusPneu.EM_RECAPAGEM;
            case DESCARTE, SUBSTITUICAO_POR_DANO, SUBSTITUICAO_POR_DESGASTE -> StatusPneu.INATIVO;
            default -> StatusPneu.RECAPADO;
        };
        pneu.setStatus(novoStatus);
        pneuRepository.save(pneu);

        if (novoStatus == StatusPneu.NOVO || novoStatus == StatusPneu.RECAPADO) {
            pneuEstoqueService.registrarEntrada(pneu, "Desmontagem DOT " + pneu.getNumeroDot() + " motivo " + motivo);
        } else if (novoStatus == StatusPneu.INATIVO) {
            pneuEstoqueService.registrarSaida(pneu, "Desmontagem para inativo DOT " + pneu.getNumeroDot() + " motivo " + motivo);
        }
        // EM_RECAPAGEM: sem movimento - tratado em enviarParaRecapagem/registrarRetorno

        return toDTO(alocacaoPneuRepository.save(alocacao));
    }

    @Transactional
    public void realizarRodizio(Long veiculoId, List<RodizioItemDTO> posicoes, Integer km, String responsavel) {
        log.info("Realizando rodízio no veículo {} com {} pneus", veiculoId, posicoes.size());

        for (RodizioItemDTO item : posicoes) {
            AlocacaoPneu alocacaoAtiva = alocacaoPneuRepository.findAlocacaoAtivaByPneuId(item.getPneuId())
                    .orElseThrow(() -> new RuntimeException("Pneu " + item.getPneuId() + " não está montado"));
            desmontar(alocacaoAtiva.getId(), km, MotivoDesmontagem.RODIZIO);
        }

        for (RodizioItemDTO item : posicoes) {
            montar(item.getPneuId(), veiculoId, item.getPosicaoDestino(), km, responsavel);
        }

        log.info("Rodízio concluído no veículo {}", veiculoId);
    }

    @Transactional(readOnly = true)
    public MapaVeiculoPneusDTO mapaVeiculo(Long veiculoId) {
        Veiculo veiculo = veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com id: " + veiculoId));

        List<AlocacaoPneu> alocacoesAtivas = alocacaoPneuRepository.findAlocacoesAtivasByVeiculoId(veiculoId);

        List<PosicaoPneuDetalheDTO> posicoes = Arrays.stream(PosicaoPneu.values())
                .map(posicao -> {
                    AlocacaoPneu alocacao = alocacoesAtivas.stream()
                            .filter(a -> a.getPosicao() == posicao)
                            .findFirst()
                            .orElse(null);

                    PosicaoPneuDetalheDTO detalhe = new PosicaoPneuDetalheDTO();
                    detalhe.setPosicao(posicao);
                    if (alocacao != null) {
                        detalhe.setAlocacaoId(alocacao.getId());
                        detalhe.setPneu(toPneuDetalheDTO(alocacao));

                        inspecaoPneuRepository.findFirstByAlocacaoIdOrderByDataInspecaoDesc(alocacao.getId())
                                .ifPresent(inspecao -> {
                                    detalhe.setUltimaInspecao(inspecaoPneuService.toDTO(inspecao));
                                    detalhe.setStatusSemaforo(calcularSemaforo(inspecao, alocacao.getPneu()));
                                });
                    }
                    return detalhe;
                })
                .collect(Collectors.toList());

        MapaVeiculoPneusDTO mapa = new MapaVeiculoPneusDTO();
        mapa.setVeiculoId(veiculoId);
        mapa.setPlaca(veiculo.getPlaca());
        mapa.setPosicoes(posicoes);
        return mapa;
    }

    @Transactional(readOnly = true)
    public Page<AlocacaoPneuDTO> historicoByPneu(Long pneuId, Pageable pageable) {
        return alocacaoPneuRepository.findByPneuIdOrderByDataMontagem(pneuId, pageable).map(this::toDTO);
    }

    private PneuDTO toPneuDetalheDTO(AlocacaoPneu alocacao) {
        Pneu p = alocacao.getPneu();
        PneuDTO dto = new PneuDTO();
        dto.setId(p.getId());
        dto.setNumeroDot(p.getNumeroDot());
        dto.setNumeroFogo(p.getNumeroFogo());
        dto.setMarca(p.getMarca());
        dto.setModelo(p.getModelo());
        dto.setDimensao(p.getDimensao());
        dto.setTipoPneu(p.getTipoPneu());
        dto.setEixo(p.getEixo());
        dto.setProfundidadeInicial(p.getProfundidadeInicial());
        dto.setProfundidadeMinima(p.getProfundidadeMinima());
        dto.setKmLimite(p.getKmLimite());
        dto.setKmAcumulado(p.getKmAcumulado());
        dto.setStatus(p.getStatus());
        dto.setNumeroRecapagens(p.getNumeroRecapagens());
        dto.setVeiculoAtualId(alocacao.getVeiculo().getId());
        dto.setVeiculoAtualPlaca(alocacao.getVeiculo().getPlaca());
        dto.setPosicaoAtual(alocacao.getPosicao());
        if (p.getCliente() != null) dto.setClienteId(p.getCliente().getId());
        return dto;
    }

    private String calcularSemaforo(InspecaoPneu inspecao, Pneu pneu) {
        if (Boolean.TRUE.equals(inspecao.getTemDeformacao()) || inspecao.getCondicaoVisual() == CondicaoVisual.CRITICA) {
            return "VERMELHO";
        }
        if (inspecao.getProfundidadeSulco() != null) {
            BigDecimal sulcoMinimo = pneu.getProfundidadeMinima() != null
                    ? pneu.getProfundidadeMinima()
                    : new BigDecimal("1.6");
            if (inspecao.getProfundidadeSulco().compareTo(sulcoMinimo) <= 0) {
                return "VERMELHO";
            }
            if (inspecao.getProfundidadeSulco().compareTo(new BigDecimal("3.0")) <= 0) {
                return "AMARELO";
            }
        }
        if (inspecao.getCondicaoVisual() == CondicaoVisual.ATENCAO) {
            return "AMARELO";
        }
        return "VERDE";
    }

    public AlocacaoPneuDTO toDTO(AlocacaoPneu alocacao) {
        AlocacaoPneuDTO dto = new AlocacaoPneuDTO();
        dto.setId(alocacao.getId());
        dto.setPosicao(alocacao.getPosicao());
        dto.setDataMontagem(alocacao.getDataMontagem());
        dto.setDataRemocao(alocacao.getDataRemocao());
        dto.setKmMontagem(alocacao.getKmMontagem());
        dto.setKmRemocao(alocacao.getKmRemocao());
        dto.setMotivoRemocao(alocacao.getMotivoRemocao());
        dto.setResponsavel(alocacao.getResponsavel());
        dto.setDataCadastro(alocacao.getDataCadastro());

        if (alocacao.getPneu() != null) {
            dto.setPneuId(alocacao.getPneu().getId());
            dto.setPneuDot(alocacao.getPneu().getNumeroDot());
            dto.setPneuDescricao(alocacao.getPneu().getMarca() + " " + alocacao.getPneu().getModelo());
        }
        if (alocacao.getVeiculo() != null) {
            dto.setVeiculoId(alocacao.getVeiculo().getId());
            dto.setVeiculoPlaca(alocacao.getVeiculo().getPlaca());
        }

        return dto;
    }
}
