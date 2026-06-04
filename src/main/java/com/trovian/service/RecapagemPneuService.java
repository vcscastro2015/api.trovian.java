package com.trovian.service;

import com.trovian.dto.RecapagemPneuDTO;
import com.trovian.dto.RecapagemRetornoDTO;
import com.trovian.entity.*;
import com.trovian.enums.MotivoDesmontagem;
import com.trovian.enums.StatusPneu;
import com.trovian.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecapagemPneuService {

    private static final int MAX_RECAPAGENS = 3;

    private final RecapagemPneuRepository recapagemPneuRepository;
    private final PneuRepository pneuRepository;
    private final FornecedorRepository fornecedorRepository;
    private final AlocacaoPneuRepository alocacaoPneuRepository;
    private final AlocacaoPneuService alocacaoPneuService;
    private final PneuEstoqueService pneuEstoqueService;

    @Transactional
    public RecapagemPneuDTO enviarParaRecapagem(RecapagemPneuDTO dto) {
        log.info("Enviando pneu {} para recapagem", dto.getPneuId());

        Pneu pneu = pneuRepository.findById(dto.getPneuId())
                .orElseThrow(() -> new RuntimeException("Pneu não encontrado com id: " + dto.getPneuId()));

        validarViabilidade(pneu);

        StatusPneu statusAnterior = pneu.getStatus();

        alocacaoPneuRepository.findAlocacaoAtivaByPneuId(pneu.getId())
                .ifPresent(alocacao -> alocacaoPneuService.desmontar(
                        alocacao.getId(), dto.getKmEnvio(), MotivoDesmontagem.ENVIO_RECAPAGEM));

        RecapagemPneu recapagem = new RecapagemPneu();
        recapagem.setPneu(pneu);
        recapagem.setNumeroRecapagem(pneu.getNumeroRecapagens() + 1);
        recapagem.setDataEnvio(dto.getDataEnvio());
        recapagem.setKmEnvio(dto.getKmEnvio());
        recapagem.setTipoRecapagem(dto.getTipoRecapagem());

        if (dto.getFornecedorId() != null) {
            Fornecedor fornecedor = fornecedorRepository.findById(dto.getFornecedorId())
                    .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado com id: " + dto.getFornecedorId()));
            recapagem.setFornecedor(fornecedor);
        }

        pneu.setStatus(StatusPneu.EM_RECAPAGEM);
        pneuRepository.save(pneu);

        if (statusAnterior == StatusPneu.NOVO || statusAnterior == StatusPneu.RECAPADO) {
            pneuEstoqueService.registrarSaida(pneu, "Envio para recapagem DOT " + pneu.getNumeroDot());
        }

        return toDTO(recapagemPneuRepository.save(recapagem));
    }

    @Transactional
    public RecapagemPneuDTO registrarRetorno(Long recapagemId, RecapagemRetornoDTO dto) {
        log.info("Registrando retorno da recapagem id: {}", recapagemId);

        RecapagemPneu recapagem = recapagemPneuRepository.findById(recapagemId)
                .orElseThrow(() -> new RuntimeException("Recapagem não encontrada com id: " + recapagemId));

        if (recapagem.getDataRetorno() != null) {
            throw new RuntimeException("Retorno desta recapagem já foi registrado");
        }

        recapagem.setDataRetorno(dto.getDataRetorno());
        recapagem.setAprovado(dto.getAprovado());
        recapagem.setValorRecapagem(dto.getValorRecapagem());
        recapagem.setGarantiaKm(dto.getGarantiaKm());
        recapagem.setProfundidadeAposRecapagem(dto.getProfundidadeAposRecapagem());
        recapagem.setMotivoRejeicao(dto.getMotivoRejeicao());

        Pneu pneu = recapagem.getPneu();
        if (Boolean.TRUE.equals(dto.getAprovado())) {
            pneu.setNumeroRecapagens(pneu.getNumeroRecapagens() + 1);
            pneu.setStatus(StatusPneu.RECAPADO);
            if (dto.getProfundidadeAposRecapagem() != null) {
                pneu.setProfundidadeInicial(dto.getProfundidadeAposRecapagem());
            }
        } else {
            pneu.setStatus(StatusPneu.INATIVO);
            log.info("Pneu DOT {} rejeitado na recapagem: {}", pneu.getNumeroDot(), dto.getMotivoRejeicao());
        }

        pneuRepository.save(pneu);

        if (Boolean.TRUE.equals(dto.getAprovado())) {
            pneuEstoqueService.registrarEntrada(pneu, "Retorno recapagem nº" + recapagem.getNumeroRecapagem()
                    + " DOT " + pneu.getNumeroDot());
        }
        // Recapagem rejeitada: pneu já estava fora do estoque, sem movimento adicional

        return toDTO(recapagemPneuRepository.save(recapagem));
    }

    private void validarViabilidade(Pneu pneu) {
        if (pneu.getNumeroRecapagens() >= MAX_RECAPAGENS) {
            throw new RuntimeException("Pneu atingiu o limite máximo de " + MAX_RECAPAGENS + " recapagens");
        }
        if (pneu.getKmLimite() != null && pneu.getKmAcumulado() != null) {
            double percentualKm = (double) pneu.getKmAcumulado() / pneu.getKmLimite();
            if (percentualKm > 0.80) {
                log.warn("Pneu DOT {} com {}% do km limite atingido. Recapagem pode não ser viável.",
                        pneu.getNumeroDot(), (int) (percentualKm * 100));
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<RecapagemPneuDTO> listarEmProcesso(Long clienteId, Pageable pageable) {
        return recapagemPneuRepository.findEmProcessoByClienteId(clienteId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public List<RecapagemPneuDTO> historicoByPneu(Long pneuId) {
        return recapagemPneuRepository.findByPneuId(pneuId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public RecapagemPneuDTO toDTO(RecapagemPneu recapagem) {
        RecapagemPneuDTO dto = new RecapagemPneuDTO();
        dto.setId(recapagem.getId());
        dto.setNumeroRecapagem(recapagem.getNumeroRecapagem());
        dto.setDataEnvio(recapagem.getDataEnvio());
        dto.setDataRetorno(recapagem.getDataRetorno());
        dto.setKmEnvio(recapagem.getKmEnvio());
        dto.setTipoRecapagem(recapagem.getTipoRecapagem());
        dto.setDataCadastro(recapagem.getDataCadastro());

        if (recapagem.getPneu() != null) {
            dto.setPneuId(recapagem.getPneu().getId());
            dto.setPneuDot(recapagem.getPneu().getNumeroDot());
        }
        if (recapagem.getFornecedor() != null) {
            dto.setFornecedorId(recapagem.getFornecedor().getId());
            dto.setFornecedorNome(recapagem.getFornecedor().getRazaoSocial());
        }

        return dto;
    }
}
