package com.trovian.service;

import com.trovian.dto.InspecaoPneuDTO;
import com.trovian.entity.*;
import com.trovian.enums.CondicaoVisual;
import com.trovian.enums.PrioridadeAlerta;
import com.trovian.enums.TipoAlerta;
import com.trovian.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InspecaoPneuService {

    private static final BigDecimal SULCO_MINIMO_LEGAL = new BigDecimal("1.6");
    private static final BigDecimal SULCO_RECOMENDADO = new BigDecimal("3.0");
    private static final BigDecimal TOLERANCIA_PRESSAO = new BigDecimal("0.10");

    private final InspecaoPneuRepository inspecaoPneuRepository;
    private final PneuRepository pneuRepository;
    private final VeiculoRepository veiculoRepository;
    private final MotoristaRepository motoristaRepository;
    private final AlocacaoPneuRepository alocacaoPneuRepository;
    private final AlertaManutencaoRepository alertaManutencaoRepository;

    @Transactional
    public InspecaoPneuDTO registrarInspecao(InspecaoPneuDTO dto) {
        Pneu pneu;
        Veiculo veiculo;
        AlocacaoPneu alocacao = null;

        if (dto.getAlocacaoId() != null) {
            alocacao = alocacaoPneuRepository.findById(dto.getAlocacaoId())
                    .orElseThrow(() -> new RuntimeException("Alocação não encontrada com id: " + dto.getAlocacaoId()));
            pneu = alocacao.getPneu();
            veiculo = alocacao.getVeiculo();
        } else {
            if (dto.getPneuId() == null) throw new RuntimeException("Informe alocacaoId ou pneuId");
            if (dto.getVeiculoId() == null) throw new RuntimeException("Informe alocacaoId ou veiculoId");
            pneu = pneuRepository.findById(dto.getPneuId())
                    .orElseThrow(() -> new RuntimeException("Pneu não encontrado com id: " + dto.getPneuId()));
            veiculo = veiculoRepository.findById(dto.getVeiculoId())
                    .orElseThrow(() -> new RuntimeException("Veículo não encontrado com id: " + dto.getVeiculoId()));
        }

        log.info("Registrando inspeção do pneu id: {}", pneu.getId());

        InspecaoPneu inspecao = new InspecaoPneu();
        inspecao.setPneu(pneu);
        inspecao.setVeiculo(veiculo);
        inspecao.setKmVeiculo(dto.getKmVeiculo());
        inspecao.setPressaoMedida(dto.getPressaoMedida());
        inspecao.setPressaoRecomendada(dto.getPressaoRecomendada());
        inspecao.setProfundidadeSulco(dto.getProfundidadeSulco());
        inspecao.setCondicaoVisual(dto.getCondicaoVisual());
        inspecao.setTemDeformacao(dto.getTemDeformacao());
        inspecao.setObservacoes(dto.getObservacoes());
        inspecao.setGeraAlerta(false);
        if (alocacao != null) inspecao.setAlocacao(alocacao);

        if (dto.getMotoristaId() != null) {
            Motorista motorista = motoristaRepository.findById(dto.getMotoristaId())
                    .orElseThrow(() -> new RuntimeException("Motorista não encontrado com id: " + dto.getMotoristaId()));
            inspecao.setMotorista(motorista);
        }

        InspecaoPneu saved = inspecaoPneuRepository.save(inspecao);

        boolean gerou = gerarAlertaSeNecessario(saved, pneu, veiculo);
        if (gerou) {
            saved.setGeraAlerta(true);
            inspecaoPneuRepository.save(saved);
        }

        return toDTO(saved);
    }

    private boolean gerarAlertaSeNecessario(InspecaoPneu inspecao, Pneu pneu, Veiculo veiculo) {
        boolean gerou = false;

        if (inspecao.getProfundidadeSulco() != null) {
            if (inspecao.getProfundidadeSulco().compareTo(SULCO_MINIMO_LEGAL) < 0) {
                criarAlerta(veiculo, pneu.getCliente(), TipoAlerta.TROCA_PNEUS, PrioridadeAlerta.CRITICA,
                        "Sulco abaixo do mínimo legal — Risco de acidente",
                        String.format("Pneu DOT %s com sulco de %.1fmm (mínimo legal: 1.6mm — CONTRAN).",
                                pneu.getNumeroDot(), inspecao.getProfundidadeSulco()),
                        inspecao.getKmVeiculo());
                gerou = true;
            } else if (inspecao.getProfundidadeSulco().compareTo(SULCO_RECOMENDADO) < 0) {
                criarAlerta(veiculo, pneu.getCliente(), TipoAlerta.TROCA_PNEUS, PrioridadeAlerta.ALTA,
                        "Sulco abaixo do recomendado",
                        String.format("Pneu DOT %s com sulco de %.1fmm (recomendado: 3.0mm).",
                                pneu.getNumeroDot(), inspecao.getProfundidadeSulco()),
                        inspecao.getKmVeiculo());
                gerou = true;
            }
        }

        if (inspecao.getPressaoMedida() != null && inspecao.getPressaoRecomendada() != null
                && inspecao.getPressaoRecomendada().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal tolerancia = inspecao.getPressaoRecomendada().multiply(TOLERANCIA_PRESSAO);
            BigDecimal diff = inspecao.getPressaoMedida().subtract(inspecao.getPressaoRecomendada()).abs();
            if (diff.compareTo(tolerancia) > 0) {
                criarAlerta(veiculo, pneu.getCliente(), TipoAlerta.MANUTENCAO_PENDENTE, PrioridadeAlerta.MEDIA,
                        "Pressão do pneu fora da faixa recomendada",
                        String.format("Pneu DOT %s: pressão medida %.1f PSI, recomendada %.1f PSI (tolerância ±10%%).",
                                pneu.getNumeroDot(), inspecao.getPressaoMedida(), inspecao.getPressaoRecomendada()),
                        inspecao.getKmVeiculo());
                gerou = true;
            }
        }

        if (inspecao.getCondicaoVisual() == CondicaoVisual.CRITICA) {
            criarAlerta(veiculo, pneu.getCliente(), TipoAlerta.TROCA_PNEUS, PrioridadeAlerta.ALTA,
                    "Condição visual crítica do pneu",
                    String.format("Pneu DOT %s apresenta condição visual crítica. Substituição imediata recomendada.",
                            pneu.getNumeroDot()),
                    inspecao.getKmVeiculo());
            gerou = true;
        }

        return gerou;
    }

    private void criarAlerta(Veiculo veiculo, Cliente cliente, TipoAlerta tipo, PrioridadeAlerta prioridade,
                              String titulo, String mensagem, Integer km) {
        AlertaManutencao alerta = new AlertaManutencao();
        alerta.setTipoAlerta(tipo);
        alerta.setPrioridade(prioridade);
        alerta.setVeiculo(veiculo);
        alerta.setCliente(cliente);
        alerta.setTitulo(titulo);
        alerta.setMensagem(mensagem);
        alerta.setKmVeiculo(km);
        alertaManutencaoRepository.save(alerta);
        log.info("Alerta {} {} criado para veículo {}", prioridade, tipo, veiculo.getPlaca());
    }

    @Transactional(readOnly = true)
    public Page<InspecaoPneuDTO> historicoByPneu(Long pneuId, Pageable pageable) {
        return inspecaoPneuRepository.findByPneuIdOrderByDataInspecaoDesc(pneuId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<InspecaoPneuDTO> listarPorCliente(Long clienteId, Pageable pageable) {
        return inspecaoPneuRepository.findByClienteId(clienteId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public List<InspecaoPneuDTO> ultimaInspecaoByVeiculo(Long veiculoId) {
        return inspecaoPneuRepository.findTop10ByVeiculoIdOrderByDataInspecaoDesc(veiculoId)
                .stream().map(this::toDTO).toList();
    }

    public InspecaoPneuDTO toDTO(InspecaoPneu inspecao) {
        InspecaoPneuDTO dto = new InspecaoPneuDTO();
        dto.setId(inspecao.getId());
        dto.setKmVeiculo(inspecao.getKmVeiculo());
        dto.setPressaoMedida(inspecao.getPressaoMedida());
        dto.setPressaoRecomendada(inspecao.getPressaoRecomendada());
        dto.setProfundidadeSulco(inspecao.getProfundidadeSulco());
        dto.setCondicaoVisual(inspecao.getCondicaoVisual());
        dto.setTemDeformacao(inspecao.getTemDeformacao());
        dto.setObservacoes(inspecao.getObservacoes());
        dto.setGeraAlerta(inspecao.getGeraAlerta());
        dto.setDataInspecao(inspecao.getDataInspecao());
        dto.setDataCadastro(inspecao.getDataCadastro());

        if (inspecao.getPneu() != null) {
            dto.setPneuId(inspecao.getPneu().getId());
            dto.setPneuDot(inspecao.getPneu().getNumeroDot());
        }
        if (inspecao.getVeiculo() != null) {
            dto.setVeiculoId(inspecao.getVeiculo().getId());
            dto.setVeiculoPlaca(inspecao.getVeiculo().getPlaca());
        }
        if (inspecao.getMotorista() != null) {
            dto.setMotoristaId(inspecao.getMotorista().getId());
            dto.setMotoristaNome(inspecao.getMotorista().getNome());
        }
        if (inspecao.getAlocacao() != null) {
            dto.setAlocacaoId(inspecao.getAlocacao().getId());
        }

        return dto;
    }
}
