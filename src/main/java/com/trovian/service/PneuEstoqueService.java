package com.trovian.service;

import com.trovian.entity.Cliente;
import com.trovian.entity.MovimentacaoEstoque;
import com.trovian.entity.Peca;
import com.trovian.entity.Pneu;
import com.trovian.enums.TipoMovimentacaoEstoque;
import com.trovian.repository.MovimentacaoEstoqueRepository;
import com.trovian.repository.PecaRepository;
import com.trovian.repository.PneuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PneuEstoqueService {

    private final PecaRepository pecaRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final PneuRepository pneuRepository;

    @Transactional
    public void registrarEntrada(Pneu pneu, String observacao) {
        Peca peca = resolverPecaTipo(pneu);
        peca.atualizarValorMedio(pneu.getValorCompra() != null ? pneu.getValorCompra() : BigDecimal.ZERO, 1);
        peca.adicionarEstoque(1);
        pecaRepository.save(peca);
        criarMovimentacao(peca, pneu.getCliente(), TipoMovimentacaoEstoque.ENTRADA, 1, pneu.getValorCompra(), observacao);
    }

    @Transactional
    public void registrarSaida(Pneu pneu, String observacao) {
        Peca peca = resolverPecaTipo(pneu);
        if (peca.getEstoqueAtual() <= 0) {
            log.warn("Inconsistência de estoque: peça {} estoqueAtual={}, ignorando SAIDA para DOT {}",
                    peca.getCodigo(), peca.getEstoqueAtual(), pneu.getNumeroDot());
            return;
        }
        peca.removerEstoque(1);
        pecaRepository.save(peca);
        criarMovimentacao(peca, pneu.getCliente(), TipoMovimentacaoEstoque.SAIDA, 1, null, observacao);
    }

    private Peca resolverPecaTipo(Pneu pneu) {
        if (pneu.getPecaTipo() != null) {
            return pneu.getPecaTipo();
        }
        String codigo = gerarCodigo(pneu);
        Long clienteId = pneu.getCliente().getId();
        return pecaRepository.findByCodigoAndClienteId(codigo, clienteId)
                .orElseGet(() -> criarPecaTipo(pneu, codigo));
    }

    private Peca criarPecaTipo(Pneu pneu, String codigo) {
        Peca nova = new Peca();
        nova.setCodigo(codigo);
        nova.setDescricao(buildDescricao(pneu));
        nova.setCategoria("PNEU");
        nova.setEstoqueMinimo(0);
        nova.setEstoqueAtual(0);
        nova.setStatus(true);
        nova.setCliente(pneu.getCliente());
        if (pneu.getValorCompra() != null) {
            nova.setValorMedio(pneu.getValorCompra());
        }
        try {
            Peca saved = pecaRepository.save(nova);
            pneu.setPecaTipo(saved);
            pneuRepository.save(pneu);
            log.info("Peça tipo criada: {} para pneu DOT {}", codigo, pneu.getNumeroDot());
            return saved;
        } catch (DataIntegrityViolationException e) {
            return pecaRepository.findByCodigoAndClienteId(codigo, pneu.getCliente().getId()).orElseThrow();
        }
    }

    private void criarMovimentacao(Peca peca, Cliente cliente, TipoMovimentacaoEstoque tipo,
                                    Integer quantidade, BigDecimal valorUnitario, String observacao) {
        MovimentacaoEstoque mov = new MovimentacaoEstoque();
        mov.setPeca(peca);
        mov.setCliente(cliente);
        mov.setTipoMovimentacao(tipo);
        mov.setQuantidade(quantidade);
        mov.setDataMovimentacao(LocalDateTime.now());
        mov.setValorUnitario(valorUnitario);
        mov.setObservacao(observacao);
        mov.setUsuario("SISTEMA_PNEU");
        movimentacaoEstoqueRepository.save(mov);
    }

    private String gerarCodigo(Pneu pneu) {
        String marca = normalizar(pneu.getMarca(), "GENERICO");
        String modelo = normalizar(pneu.getModelo(), "GENERICO");
        String dimensao = normalizar(pneu.getDimensao(), "GENERICO");
        String raw = "PNEU-" + marca + "-" + modelo + "-" + dimensao;
        return raw.length() > 50 ? raw.substring(0, 50) : raw;
    }

    private String normalizar(String valor, String fallback) {
        if (valor == null || valor.isBlank()) return fallback;
        return valor.trim().toUpperCase()
                .replace(" ", "_")
                .replace("/", "_")
                .replace(".", "_")
                .replaceAll("[^A-Z0-9_+]", "");
    }

    private String buildDescricao(Pneu pneu) {
        String marca = pneu.getMarca() != null ? pneu.getMarca() : "";
        String modelo = pneu.getModelo() != null ? pneu.getModelo() : "";
        String dimensao = pneu.getDimensao() != null ? pneu.getDimensao() : "";
        return (marca + " " + modelo + " " + dimensao).trim();
    }
}
