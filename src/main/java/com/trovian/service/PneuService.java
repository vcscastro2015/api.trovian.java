package com.trovian.service;

import com.trovian.dto.CpkPneuDTO;
import com.trovian.dto.PneuDTO;
import com.trovian.dto.ResumoFrotaPneusDTO;
import com.trovian.entity.*;
import com.trovian.enums.PrioridadeAlerta;
import com.trovian.enums.StatusPneu;
import com.trovian.enums.TipoAlerta;
import com.trovian.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PneuService {

    private final PneuRepository pneuRepository;
    private final ClienteRepository clienteRepository;
    private final FornecedorRepository fornecedorRepository;
    private final AlocacaoPneuRepository alocacaoPneuRepository;
    private final RecapagemPneuRepository recapagemPneuRepository;
    private final AlertaManutencaoRepository alertaManutencaoRepository;
    private final VeiculoRepository veiculoRepository;

    @Transactional
    public PneuDTO cadastrar(PneuDTO dto) {
        log.info("Cadastrando pneu DOT: {}, clienteId: {}", dto.getNumeroDot(), dto.getClienteId());

        if (pneuRepository.findByNumeroDotAndClienteId(dto.getNumeroDot(), dto.getClienteId()).isPresent()) {
            throw new RuntimeException("Já existe um pneu com o número DOT '" + dto.getNumeroDot() + "' para este cliente");
        }

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + dto.getClienteId()));

        Pneu pneu = toEntity(dto);
        pneu.setCliente(cliente);

        if (dto.getFornecedorId() != null) {
            Fornecedor fornecedor = fornecedorRepository.findById(dto.getFornecedorId())
                    .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado com id: " + dto.getFornecedorId()));
            pneu.setFornecedor(fornecedor);
        }

        return toDTO(pneuRepository.save(pneu));
    }

    @Transactional
    public PneuDTO atualizar(Long id, PneuDTO dto) {
        log.info("Atualizando pneu id: {}", id);

        Pneu pneu = pneuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pneu não encontrado com id: " + id));

        pneu.setNumeroFogo(dto.getNumeroFogo());
        pneu.setMarca(dto.getMarca());
        pneu.setModelo(dto.getModelo());
        pneu.setDimensao(dto.getDimensao());
        pneu.setTipoPneu(dto.getTipoPneu());
        pneu.setEixo(dto.getEixo());
        pneu.setDataFabricacao(dto.getDataFabricacao());
        pneu.setDataCompra(dto.getDataCompra());
        pneu.setValorCompra(dto.getValorCompra());
        pneu.setProfundidadeInicial(dto.getProfundidadeInicial());
        pneu.setProfundidadeMinima(dto.getProfundidadeMinima());
        pneu.setKmLimite(dto.getKmLimite());
        pneu.setObservacoes(dto.getObservacoes());

        if (dto.getFornecedorId() != null) {
            Fornecedor fornecedor = fornecedorRepository.findById(dto.getFornecedorId())
                    .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado com id: " + dto.getFornecedorId()));
            pneu.setFornecedor(fornecedor);
        }

        return toDTO(pneuRepository.save(pneu));
    }

    @Transactional(readOnly = true)
    public Page<PneuDTO> listar(Long clienteId, StatusPneu status, Pageable pageable) {
        if (status != null) {
            return pneuRepository.findByClienteIdAndStatus(clienteId, status, pageable).map(this::toDTO);
        }
        return pneuRepository.findByClienteId(clienteId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public PneuDTO buscarPorId(Long id) {
        Pneu pneu = pneuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pneu não encontrado com id: " + id));
        return toDTO(pneu);
    }

    @Transactional
    public void descartar(Long id, String motivo) {
        log.info("Descartando pneu id: {}", id);

        Pneu pneu = pneuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pneu não encontrado com id: " + id));

        if (pneu.getStatus() == StatusPneu.EM_USO) {
            throw new RuntimeException("Não é possível descartar pneu em uso. Desmonte-o primeiro.");
        }

        pneu.setStatus(StatusPneu.DESCARTADO);
        pneu.setObservacoes(motivo != null ? motivo : pneu.getObservacoes());
        pneuRepository.save(pneu);
        log.info("Pneu id: {} descartado com sucesso", id);
    }

    @Transactional(readOnly = true)
    public CpkPneuDTO calcularCpk(Long pneuId) {
        Pneu pneu = pneuRepository.findById(pneuId)
                .orElseThrow(() -> new RuntimeException("Pneu não encontrado com id: " + pneuId));

        BigDecimal valorCompra = pneu.getValorCompra() != null ? pneu.getValorCompra() : BigDecimal.ZERO;
        BigDecimal totalRecapagens = recapagemPneuRepository.sumValorRecapagensByPneuId(pneuId);
        BigDecimal custoTotal = valorCompra.add(totalRecapagens != null ? totalRecapagens : BigDecimal.ZERO);

        BigDecimal cpk = BigDecimal.ZERO;
        if (pneu.getKmAcumulado() != null && pneu.getKmAcumulado() > 0) {
            cpk = custoTotal.divide(BigDecimal.valueOf(pneu.getKmAcumulado()), 4, RoundingMode.HALF_UP);
        }

        CpkPneuDTO dto = new CpkPneuDTO();
        dto.setPneuId(pneu.getId());
        dto.setNumeroDot(pneu.getNumeroDot());
        dto.setMarca(pneu.getMarca());
        dto.setModelo(pneu.getModelo());
        dto.setKmTotal(pneu.getKmAcumulado());
        dto.setCustoTotal(custoTotal);
        dto.setCpk(cpk);
        return dto;
    }

    @Transactional
    public void verificarIdadePneu(Long pneuId) {
        Pneu pneu = pneuRepository.findById(pneuId)
                .orElseThrow(() -> new RuntimeException("Pneu não encontrado com id: " + pneuId));

        if (pneu.getDataFabricacao() == null) return;

        LocalDate limiteIdade = LocalDate.now().minusYears(5);
        if (pneu.getDataFabricacao().isBefore(limiteIdade)) {
            Veiculo veiculo = alocacaoPneuRepository.findAlocacaoAtivaByPneuId(pneuId)
                    .map(AlocacaoPneu::getVeiculo)
                    .orElse(null);

            if (veiculo != null) {
                AlertaManutencao alerta = new AlertaManutencao();
                alerta.setTipoAlerta(TipoAlerta.TROCA_PNEUS);
                alerta.setPrioridade(PrioridadeAlerta.ALTA);
                alerta.setVeiculo(veiculo);
                alerta.setCliente(pneu.getCliente());
                alerta.setTitulo("Pneu com mais de 5 anos — Risco de segurança");
                alerta.setMensagem(String.format(
                        "Pneu DOT %s (fabricação %s) tem mais de 5 anos. Conforme INMETRO NBR 15619, recomenda-se substituição.",
                        pneu.getNumeroDot(), pneu.getDataFabricacao()));
                alertaManutencaoRepository.save(alerta);
                log.warn("Alerta de pneu velho gerado para DOT {}", pneu.getNumeroDot());
            }
        }
    }

    @Transactional(readOnly = true)
    public ResumoFrotaPneusDTO getResumoFrota(Long clienteId) {
        List<Pneu> todos = pneuRepository.findByClienteId(clienteId, Pageable.unpaged()).getContent();

        int total = todos.size();
        long emUso = todos.stream().filter(p -> p.getStatus() == StatusPneu.EM_USO).count();
        long emRecapagem = todos.stream().filter(p -> p.getStatus() == StatusPneu.EM_RECAPAGEM).count();

        ResumoFrotaPneusDTO resumo = new ResumoFrotaPneusDTO();
        resumo.setTotalPneus(total);
        resumo.setEmUso((int) emUso);
        resumo.setEmRecapagem((int) emRecapagem);
        resumo.setEmAlerta(0);
        resumo.setProximosTroca(0);
        resumo.setCpkMedio(BigDecimal.ZERO);
        resumo.setCustoMesAtual(BigDecimal.ZERO);
        return resumo;
    }

    public PneuDTO toDTO(Pneu pneu) {
        PneuDTO dto = new PneuDTO();
        dto.setId(pneu.getId());
        dto.setNumeroDot(pneu.getNumeroDot());
        dto.setNumeroFogo(pneu.getNumeroFogo());
        dto.setMarca(pneu.getMarca());
        dto.setModelo(pneu.getModelo());
        dto.setDimensao(pneu.getDimensao());
        dto.setTipoPneu(pneu.getTipoPneu());
        dto.setEixo(pneu.getEixo());
        dto.setDataFabricacao(pneu.getDataFabricacao());
        dto.setDataCompra(pneu.getDataCompra());
        dto.setValorCompra(pneu.getValorCompra());
        dto.setProfundidadeInicial(pneu.getProfundidadeInicial());
        dto.setProfundidadeMinima(pneu.getProfundidadeMinima());
        dto.setKmLimite(pneu.getKmLimite());
        dto.setStatus(pneu.getStatus());
        dto.setNumeroRecapagens(pneu.getNumeroRecapagens());
        dto.setKmAcumulado(pneu.getKmAcumulado());
        dto.setObservacoes(pneu.getObservacoes());
        dto.setDataCadastro(pneu.getDataCadastro());
        dto.setUpdatedAt(pneu.getUpdatedAt());

        if (pneu.getCliente() != null) {
            dto.setClienteId(pneu.getCliente().getId());
        }
        if (pneu.getFornecedor() != null) {
            dto.setFornecedorId(pneu.getFornecedor().getId());
            dto.setFornecedorNome(pneu.getFornecedor().getRazaoSocial());
        }

        alocacaoPneuRepository.findAlocacaoAtivaByPneuId(pneu.getId()).ifPresent(alocacao -> {
            dto.setVeiculoAtualId(alocacao.getVeiculo().getId());
            dto.setVeiculoAtualPlaca(alocacao.getVeiculo().getPlaca());
            dto.setPosicaoAtual(alocacao.getPosicao());
        });

        return dto;
    }

    private Pneu toEntity(PneuDTO dto) {
        Pneu pneu = new Pneu();
        pneu.setNumeroDot(dto.getNumeroDot());
        pneu.setNumeroFogo(dto.getNumeroFogo());
        pneu.setMarca(dto.getMarca());
        pneu.setModelo(dto.getModelo());
        pneu.setDimensao(dto.getDimensao());
        pneu.setTipoPneu(dto.getTipoPneu());
        pneu.setEixo(dto.getEixo());
        pneu.setDataFabricacao(dto.getDataFabricacao());
        pneu.setDataCompra(dto.getDataCompra());
        pneu.setValorCompra(dto.getValorCompra());
        pneu.setProfundidadeInicial(dto.getProfundidadeInicial());
        pneu.setProfundidadeMinima(dto.getProfundidadeMinima());
        pneu.setKmLimite(dto.getKmLimite());
        pneu.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusPneu.NOVO);
        pneu.setObservacoes(dto.getObservacoes());
        return pneu;
    }
}
