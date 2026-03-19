package com.trovian.service;

import com.trovian.dto.relatorio.*;
import com.trovian.entity.Cliente;
import com.trovian.entity.ContaPagar;
import com.trovian.entity.ContaReceber;
import com.trovian.enums.StatusConta;
import com.trovian.repository.ClienteRepository;
import com.trovian.repository.ContaPagarRepository;
import com.trovian.repository.ContaReceberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelatorioFinanceiroService {

    private final ContaPagarRepository contaPagarRepository;
    private final ContaReceberRepository contaReceberRepository;
    private final ClienteRepository clienteRepository;

    // =========================================================================
    // 1. DRE - Demonstrativo de Resultado do Exercício
    // =========================================================================

    @Transactional(readOnly = true)
    public DreDTO gerarDRE(LocalDate dataInicio, LocalDate dataFim, Long clienteId,
                           Long centroCustoId, Boolean compararPeriodoAnterior) {

        log.info("Gerando DRE: {} a {} - Cliente: {} - Centro Custo: {}",
                 dataInicio, dataFim, clienteId, centroCustoId);

        DreDTO dre = new DreDTO();
        dre.setPeriodo(criarPeriodoDre(dataInicio, dataFim));
        dre.setFiltros(criarFiltrosDre(clienteId, centroCustoId, compararPeriodoAnterior));
        dre.setReceitas(calcularReceitasDre(dataInicio, dataFim, clienteId, centroCustoId));
        dre.setDespesas(calcularDespesasDre(dataInicio, dataFim, clienteId, centroCustoId));
        dre.setResultado(calcularResultadoDre(dre.getReceitas(), dre.getDespesas(),
                                              dre.getPeriodo().getTotalDias()));

        if (Boolean.TRUE.equals(compararPeriodoAnterior)) {
            dre.setComparativo(calcularComparativoDre(dataInicio, dataFim, clienteId,
                                                      centroCustoId, dre));
        }

        log.info("DRE gerado - Receitas: {} - Despesas: {} - Lucro: {}",
                 dre.getReceitas().getTotalReceitas(),
                 dre.getDespesas().getTotalDespesas(),
                 dre.getResultado().getLucroBruto());

        return dre;
    }

    private DreDTO.PeriodoDTO criarPeriodoDre(LocalDate dataInicio, LocalDate dataFim) {
        DreDTO.PeriodoDTO periodo = new DreDTO.PeriodoDTO();
        periodo.setDataInicio(dataInicio);
        periodo.setDataFim(dataFim);
        periodo.setTotalDias((int) ChronoUnit.DAYS.between(dataInicio, dataFim) + 1);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMMM/yyyy", new Locale("pt", "BR"));
        if (dataInicio.getMonth() == dataFim.getMonth() && dataInicio.getYear() == dataFim.getYear()) {
            periodo.setDescricao(dataInicio.format(fmt));
        } else {
            periodo.setDescricao(dataInicio.format(fmt) + " a " + dataFim.format(fmt));
        }
        return periodo;
    }

    private DreDTO.FiltrosDTO criarFiltrosDre(Long clienteId, Long centroCustoId,
                                              Boolean compararPeriodoAnterior) {
        DreDTO.FiltrosDTO filtros = new DreDTO.FiltrosDTO();
        filtros.setClienteId(clienteId);
        Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
        if(Objects.nonNull(cliente)){
            filtros.setClienteNome(cliente.getNome());
        }
        filtros.setCentroCustoId(centroCustoId);
        filtros.setCompararPeriodoAnterior(compararPeriodoAnterior);
        return filtros;
    }

    private DreDTO.ReceitasDTO calcularReceitasDre(LocalDate dataInicio, LocalDate dataFim,
                                                   Long clienteId, Long centroCustoId) {
        DreDTO.ReceitasDTO receitas = new DreDTO.ReceitasDTO();

        List<Object[]> rows = contaReceberRepository
            .somarReceitasPorCategoria(dataInicio, dataFim, clienteId, centroCustoId,
                                      StatusConta.RECEBIDO);

        BigDecimal totalReceitas = BigDecimal.ZERO;
        BigDecimal receitasFrete = BigDecimal.ZERO;
        BigDecimal outrasReceitas = BigDecimal.ZERO;
        int quantidadeFretes = 0;
        List<DreDTO.ItemReceitaDTO> detalhamento = new ArrayList<>();

        for (Object[] row : rows) {
            String nome = (String) row[0];
            BigDecimal valor = (BigDecimal) row[1];
            Integer qtd = ((Number) row[2]).intValue();

            totalReceitas = totalReceitas.add(valor);

            if (nome != null && nome.toLowerCase().contains("frete")) {
                receitasFrete = receitasFrete.add(valor);
                quantidadeFretes += qtd;
            } else {
                outrasReceitas = outrasReceitas.add(valor);
            }

            DreDTO.ItemReceitaDTO item = new DreDTO.ItemReceitaDTO();
            item.setCategoria(nome);
            item.setValor(valor);
            item.setQuantidade(qtd);
            detalhamento.add(item);
        }

        for (DreDTO.ItemReceitaDTO item : detalhamento) {
            if (totalReceitas.compareTo(BigDecimal.ZERO) > 0) {
                item.setPercentual(item.getValor()
                    .divide(totalReceitas, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)));
            }
        }

        receitas.setReceitasFrete(receitasFrete);
        receitas.setOutrasReceitas(outrasReceitas);
        receitas.setTotalReceitas(totalReceitas);
        receitas.setQuantidadeFretes(quantidadeFretes);
        receitas.setTicketMedioFrete(quantidadeFretes > 0
            ? receitasFrete.divide(BigDecimal.valueOf(quantidadeFretes), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO);
        receitas.setDetalhamento(detalhamento);
        return receitas;
    }

    private DreDTO.DespesasDTO calcularDespesasDre(LocalDate dataInicio, LocalDate dataFim,
                                                   Long clienteId, Long centroCustoId) {
        DreDTO.DespesasDTO despesas = new DreDTO.DespesasDTO();

        List<Object[]> rows = contaPagarRepository
            .somarDespesasPorCategoria(dataInicio, dataFim, clienteId, centroCustoId,
                                      StatusConta.PAGO);

        BigDecimal totalDespesas = BigDecimal.ZERO;
        BigDecimal combustivel = BigDecimal.ZERO, manutencao = BigDecimal.ZERO,
                   pneus = BigDecimal.ZERO, pedagios = BigDecimal.ZERO,
                   seguro = BigDecimal.ZERO, licenciamento = BigDecimal.ZERO,
                   salarios = BigDecimal.ZERO, pecas = BigDecimal.ZERO,
                   outras = BigDecimal.ZERO;

        List<DreDTO.ItemDespesaDTO> detalhamento = new ArrayList<>();

        for (Object[] row : rows) {
            String nome = (String) row[0];
            BigDecimal valor = (BigDecimal) row[1];
            Integer qtd = ((Number) row[2]).intValue();

            totalDespesas = totalDespesas.add(valor);

            String lower = nome != null ? nome.toLowerCase() : "";
            if (lower.contains("combustivel") || lower.contains("combustível")) combustivel = combustivel.add(valor);
            else if (lower.contains("manutenção") || lower.contains("manutencao")) manutencao = manutencao.add(valor);
            else if (lower.contains("pneu")) pneus = pneus.add(valor);
            else if (lower.contains("pedagio") || lower.contains("pedágio")) pedagios = pedagios.add(valor);
            else if (lower.contains("seguro")) seguro = seguro.add(valor);
            else if (lower.contains("licenciamento")) licenciamento = licenciamento.add(valor);
            else if (lower.contains("salario") || lower.contains("salário")) salarios = salarios.add(valor);
            else if (lower.contains("peça") || lower.contains("peca")) pecas = pecas.add(valor);
            else outras = outras.add(valor);

            DreDTO.ItemDespesaDTO item = new DreDTO.ItemDespesaDTO();
            item.setCategoria(nome);
            item.setValor(valor);
            item.setQuantidade(qtd);
            detalhamento.add(item);
        }

        for (DreDTO.ItemDespesaDTO item : detalhamento) {
            if (totalDespesas.compareTo(BigDecimal.ZERO) > 0) {
                item.setPercentual(item.getValor()
                    .divide(totalDespesas, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)));
            }
        }

        despesas.setCombustivel(combustivel);
        despesas.setManutencao(manutencao);
        despesas.setPneus(pneus);
        despesas.setPedagios(pedagios);
        despesas.setSeguro(seguro);
        despesas.setLicenciamento(licenciamento);
        despesas.setSalarios(salarios);
        despesas.setPecas(pecas);
        despesas.setOutrasDespesas(outras);
        despesas.setTotalDespesas(totalDespesas);
        despesas.setDetalhamento(detalhamento);
        return despesas;
    }

    private DreDTO.ResultadoDTO calcularResultadoDre(DreDTO.ReceitasDTO receitas,
                                                     DreDTO.DespesasDTO despesas,
                                                     Integer totalDias) {
        DreDTO.ResultadoDTO resultado = new DreDTO.ResultadoDTO();
        BigDecimal totalR = receitas.getTotalReceitas();
        BigDecimal totalD = despesas.getTotalDespesas();
        BigDecimal lucro = totalR.subtract(totalD);

        resultado.setLucroBruto(lucro);

        if (totalR.compareTo(BigDecimal.ZERO) > 0) {
            resultado.setMargemLucro(lucro.divide(totalR, 4, RoundingMode.HALF_UP)
                                         .multiply(BigDecimal.valueOf(100)));
        } else {
            resultado.setMargemLucro(BigDecimal.ZERO);
        }

        if (totalDias != null && totalDias > 0) {
            resultado.setReceitaMedia(totalR.divide(BigDecimal.valueOf(totalDias), 2, RoundingMode.HALF_UP));
            resultado.setDespesaMedia(totalD.divide(BigDecimal.valueOf(totalDias), 2, RoundingMode.HALF_UP));
        }

        if (lucro.compareTo(BigDecimal.ZERO) > 0) resultado.setSituacao("LUCRO");
        else if (lucro.compareTo(BigDecimal.ZERO) < 0) resultado.setSituacao("PREJUÍZO");
        else resultado.setSituacao("EQUILIBRADO");

        return resultado;
    }

    private DreDTO.ComparativoDTO calcularComparativoDre(LocalDate dataInicio, LocalDate dataFim,
                                                         Long clienteId, Long centroCustoId,
                                                         DreDTO dreAtual) {
        DreDTO.ComparativoDTO comparativo = new DreDTO.ComparativoDTO();
        long dias = ChronoUnit.DAYS.between(dataInicio, dataFim);
        LocalDate iniAnt = dataInicio.minusDays(dias + 1);
        LocalDate fimAnt = dataInicio.minusDays(1);

        comparativo.setPeriodoAnterior(criarPeriodoDre(iniAnt, fimAnt));

        DreDTO.ReceitasDTO recAnt = calcularReceitasDre(iniAnt, fimAnt, clienteId, centroCustoId);
        DreDTO.DespesasDTO despAnt = calcularDespesasDre(iniAnt, fimAnt, clienteId, centroCustoId);
        BigDecimal receitaAnt = recAnt.getTotalReceitas();
        BigDecimal despesaAnt = despAnt.getTotalDespesas();
        BigDecimal lucroAnt = receitaAnt.subtract(despesaAnt);

        comparativo.setReceitaAnterior(receitaAnt);
        comparativo.setDespesaAnterior(despesaAnt);
        comparativo.setLucroAnterior(lucroAnt);
        comparativo.setVariacaoReceita(calcularVariacaoPercentual(receitaAnt, dreAtual.getReceitas().getTotalReceitas()));
        comparativo.setVariacaoDespesa(calcularVariacaoPercentual(despesaAnt, dreAtual.getDespesas().getTotalDespesas()));
        BigDecimal varLucro = calcularVariacaoPercentual(lucroAnt, dreAtual.getResultado().getLucroBruto());
        comparativo.setVariacaoLucro(varLucro);

        if (varLucro.compareTo(BigDecimal.valueOf(5)) > 0) comparativo.setTendencia("CRESCIMENTO");
        else if (varLucro.compareTo(BigDecimal.valueOf(-5)) < 0) comparativo.setTendencia("QUEDA");
        else comparativo.setTendencia("ESTÁVEL");

        return comparativo;
    }

    // =========================================================================
    // 2. Contas a Pagar Consolidado
    // =========================================================================

    @Transactional(readOnly = true)
    public ContasPagarConsolidadoDTO gerarContasPagarConsolidado(LocalDate dataInicio,
                                                                  LocalDate dataFim,
                                                                  StatusConta statusFiltro,
                                                                  Long fornecedorId,
                                                                  Long categoriaId,
                                                                  Long clienteId) {
        log.info("Gerando Contas a Pagar Consolidado: {} a {}", dataInicio, dataFim);

        ContasPagarConsolidadoDTO relatorio = new ContasPagarConsolidadoDTO();

        ContasPagarConsolidadoDTO.PeriodoDTO periodo = new ContasPagarConsolidadoDTO.PeriodoDTO();
        periodo.setDataInicio(dataInicio);
        periodo.setDataFim(dataFim);
        periodo.setDescricao(gerarDescricaoPeriodo(dataInicio, dataFim));
        relatorio.setPeriodo(periodo);

        List<ContaPagar> contas = statusFiltro != null
            ? contaPagarRepository.findByDataVencimentoBetweenAndStatus(
                dataInicio, dataFim, clienteId, statusFiltro, Pageable.unpaged()).getContent()
            : contaPagarRepository.findByDataVencimentoBetweenAndClienteId(
                dataInicio, dataFim, clienteId, Pageable.unpaged()).getContent();


        contas = filtrarContaPagar(contas, fornecedorId, categoriaId);

        relatorio.setResumoGeral(calcularResumoGeralPagar(contas));
        relatorio.setContasPorStatus(calcularContasPorStatus(contas));
        relatorio.setVencimentos(calcularVencimentos(contas));
        relatorio.setTopFornecedores(calcularTopFornecedores(contas, 10));
        relatorio.setDespesasPorCategoria(calcularDespesasPorCategoriaPagar(contas));
        relatorio.setContas(converterParaDetalhesPagar(contas));
        relatorio.setAlertas(gerarAlertasPagar(contas));

        return relatorio;
    }




    private ContasPagarConsolidadoDTO.ResumoGeralDTO calcularResumoGeralPagar(List<ContaPagar> contas) {
        ContasPagarConsolidadoDTO.ResumoGeralDTO resumo = new ContasPagarConsolidadoDTO.ResumoGeralDTO();
        BigDecimal valorTotal = BigDecimal.ZERO;
        BigDecimal valorPago = BigDecimal.ZERO;
        int qtdPaga = 0, qtdPendente = 0;

        for (ContaPagar c : contas) {
            valorTotal = valorTotal.add(c.getValorTotal());
            valorPago = valorPago.add(c.getValorPago());
            if (StatusConta.PAGO.equals(c.getStatus())) qtdPaga++;
            else if (StatusConta.PENDENTE.equals(c.getStatus()) || StatusConta.PARCIAL.equals(c.getStatus())) qtdPendente++;
        }

        resumo.setQuantidadeTotal(contas.size());
        resumo.setQuantidadePaga(qtdPaga);
        resumo.setQuantidadePendente(qtdPendente);
        resumo.setValorTotalContas(valorTotal);
        resumo.setValorPago(valorPago);
        resumo.setSaldoAPagar(valorTotal.subtract(valorPago));

        if (valorTotal.compareTo(BigDecimal.ZERO) > 0) {
            resumo.setPercentualPago(valorPago.divide(valorTotal, 4, RoundingMode.HALF_UP)
                                             .multiply(BigDecimal.valueOf(100)));
        }
        return resumo;
    }

    private ContasPagarConsolidadoDTO.ContasPorStatusDTO calcularContasPorStatus(List<ContaPagar> contas) {
        ContasPagarConsolidadoDTO.ContasPorStatusDTO porStatus = new ContasPagarConsolidadoDTO.ContasPorStatusDTO();
        Map<StatusConta, List<ContaPagar>> agrupado = contas.stream()
            .collect(Collectors.groupingBy(ContaPagar::getStatus));
        BigDecimal totalGeral = contas.stream().map(ContaPagar::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        porStatus.setPendentes(criarStatusResumoPagar(agrupado.get(StatusConta.PENDENTE), totalGeral));
        porStatus.setPagas(criarStatusResumoPagar(agrupado.get(StatusConta.PAGO), totalGeral));
        porStatus.setVencidas(criarStatusResumoPagar(agrupado.get(StatusConta.VENCIDO), totalGeral));
        porStatus.setParciais(criarStatusResumoPagar(agrupado.get(StatusConta.PARCIAL), totalGeral));
        porStatus.setCanceladas(criarStatusResumoPagar(agrupado.get(StatusConta.CANCELADO), totalGeral));
        return porStatus;
    }

    private ContasPagarConsolidadoDTO.StatusResumoDTO criarStatusResumoPagar(
            List<ContaPagar> contas, BigDecimal totalGeral) {
        ContasPagarConsolidadoDTO.StatusResumoDTO resumo = new ContasPagarConsolidadoDTO.StatusResumoDTO();
        if (contas == null || contas.isEmpty()) {
            resumo.setQuantidade(0);
            resumo.setValor(BigDecimal.ZERO);
            resumo.setPercentual(BigDecimal.ZERO);
            return resumo;
        }
        BigDecimal valor = contas.stream().map(ContaPagar::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        resumo.setQuantidade(contas.size());
        resumo.setValor(valor);
        if (totalGeral.compareTo(BigDecimal.ZERO) > 0) {
            resumo.setPercentual(valor.divide(totalGeral, 4, RoundingMode.HALF_UP)
                                     .multiply(BigDecimal.valueOf(100)));
        }
        return resumo;
    }

    private ContasPagarConsolidadoDTO.VencimentosDTO calcularVencimentos(List<ContaPagar> contas) {
        ContasPagarConsolidadoDTO.VencimentosDTO venc = new ContasPagarConsolidadoDTO.VencimentosDTO();
        LocalDate hoje = LocalDate.now();

        List<ContaPagar> naoPageas = contas.stream()
            .filter(c -> !StatusConta.PAGO.equals(c.getStatus()) && !StatusConta.CANCELADO.equals(c.getStatus()))
            .collect(Collectors.toList());

        LocalDate l7 = hoje.plusDays(7), l30 = hoje.plusDays(30),
                  l60 = hoje.plusDays(60), l90 = hoje.plusDays(90);

        venc.setVencidas(criarVencimentoFaixa(naoPageas.stream()
            .filter(c -> c.getDataVencimento().isBefore(hoje)).collect(Collectors.toList()), "Vencidas"));
        venc.setProximos7Dias(criarVencimentoFaixa(naoPageas.stream()
            .filter(c -> !c.getDataVencimento().isBefore(hoje) && !c.getDataVencimento().isAfter(l7))
            .collect(Collectors.toList()), "Próximos 7 dias"));
        venc.setProximos30Dias(criarVencimentoFaixa(naoPageas.stream()
            .filter(c -> c.getDataVencimento().isAfter(l7) && !c.getDataVencimento().isAfter(l30))
            .collect(Collectors.toList()), "8 a 30 dias"));
        venc.setProximos60Dias(criarVencimentoFaixa(naoPageas.stream()
            .filter(c -> c.getDataVencimento().isAfter(l30) && !c.getDataVencimento().isAfter(l60))
            .collect(Collectors.toList()), "31 a 60 dias"));
        venc.setProximos90Dias(criarVencimentoFaixa(naoPageas.stream()
            .filter(c -> c.getDataVencimento().isAfter(l60) && !c.getDataVencimento().isAfter(l90))
            .collect(Collectors.toList()), "61 a 90 dias"));
        venc.setApos90Dias(criarVencimentoFaixa(naoPageas.stream()
            .filter(c -> c.getDataVencimento().isAfter(l90)).collect(Collectors.toList()), "Após 90 dias"));

        return venc;
    }

    private ContasPagarConsolidadoDTO.VencimentoFaixaDTO criarVencimentoFaixa(
            List<ContaPagar> contas, String descricao) {
        ContasPagarConsolidadoDTO.VencimentoFaixaDTO faixa = new ContasPagarConsolidadoDTO.VencimentoFaixaDTO();
        faixa.setDescricao(descricao);
        faixa.setQuantidade(contas.size());
        faixa.setValor(contas.stream().map(ContaPagar::calcularSaldo).reduce(BigDecimal.ZERO, BigDecimal::add));
        return faixa;
    }

    private List<ContasPagarConsolidadoDTO.TopFornecedorDTO> calcularTopFornecedores(
            List<ContaPagar> contas, int top) {
        Map<Long, List<ContaPagar>> porFornecedor = contas.stream()
            .filter(c -> c.getFornecedor() != null)
            .collect(Collectors.groupingBy(c -> c.getFornecedor().getId()));
        BigDecimal totalGeral = contas.stream().map(ContaPagar::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return porFornecedor.entrySet().stream().map(e -> {
            List<ContaPagar> cf = e.getValue();
            BigDecimal vf = cf.stream().map(ContaPagar::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            ContasPagarConsolidadoDTO.TopFornecedorDTO dto = new ContasPagarConsolidadoDTO.TopFornecedorDTO();
            dto.setFornecedorId(e.getKey());
            dto.setFornecedorNome(cf.get(0).getFornecedor().getRazaoSocial());
            dto.setValorTotal(vf);
            dto.setQuantidadeContas(cf.size());
            if (totalGeral.compareTo(BigDecimal.ZERO) > 0) {
                dto.setPercentualDoTotal(vf.divide(totalGeral, 4, RoundingMode.HALF_UP)
                                          .multiply(BigDecimal.valueOf(100)));
            }
            dto.setTicketMedio(vf.divide(BigDecimal.valueOf(cf.size()), 2, RoundingMode.HALF_UP));
            return dto;
        }).sorted((a, b) -> b.getValorTotal().compareTo(a.getValorTotal()))
          .limit(top).collect(Collectors.toList());
    }

    private List<ContasPagarConsolidadoDTO.DespesaPorCategoriaDTO> calcularDespesasPorCategoriaPagar(
            List<ContaPagar> contas) {
        Map<Long, List<ContaPagar>> porCategoria = contas.stream()
            .filter(c -> c.getCategoria() != null)
            .collect(Collectors.groupingBy(c -> c.getCategoria().getId()));
        BigDecimal totalGeral = contas.stream().map(ContaPagar::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return porCategoria.entrySet().stream().map(e -> {
            List<ContaPagar> cc = e.getValue();
            BigDecimal vc = cc.stream().map(ContaPagar::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            ContasPagarConsolidadoDTO.DespesaPorCategoriaDTO dto = new ContasPagarConsolidadoDTO.DespesaPorCategoriaDTO();
            dto.setCategoriaId(e.getKey());
            dto.setCategoriaNome(cc.get(0).getCategoria().getNome());
            dto.setValor(vc);
            dto.setQuantidade(cc.size());
            if (totalGeral.compareTo(BigDecimal.ZERO) > 0) {
                dto.setPercentual(vc.divide(totalGeral, 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)));
            }
            return dto;
        }).sorted((a, b) -> b.getValor().compareTo(a.getValor())).collect(Collectors.toList());
    }

    private List<ContasPagarConsolidadoDTO.ContaPagarDetalheDTO> converterParaDetalhesPagar(
            List<ContaPagar> contas) {
        LocalDate hoje = LocalDate.now();
        return contas.stream().map(c -> {
            ContasPagarConsolidadoDTO.ContaPagarDetalheDTO dto = new ContasPagarConsolidadoDTO.ContaPagarDetalheDTO();
            dto.setId(c.getId());
            dto.setDescricao(c.getDescricao());
            dto.setNumeroDocumento(c.getNumeroDocumento());
            dto.setFornecedorNome(c.getFornecedor() != null ? c.getFornecedor().getRazaoSocial() : null);
            dto.setCategoriaNome(c.getCategoria() != null ? c.getCategoria().getNome() : null);
            dto.setValorOriginal(c.getValorOriginal());
            dto.setValorTotal(c.getValorTotal());
            dto.setValorPago(c.getValorPago());
            dto.setSaldo(c.calcularSaldo());
            dto.setDataEmissao(c.getDataEmissao());
            dto.setDataVencimento(c.getDataVencimento());
            dto.setDataPagamento(c.getDataPagamento());
            dto.setStatus(c.getStatus());
            dto.setVencida(c.isVencida());
            if (c.isVencida()) {
                dto.setDiasAtraso((int) ChronoUnit.DAYS.between(c.getDataVencimento(), hoje));
            }
            return dto;
        }).collect(Collectors.toList());
    }

    private ContasPagarConsolidadoDTO.AlertasDTO gerarAlertasPagar(List<ContaPagar> contas) {
        ContasPagarConsolidadoDTO.AlertasDTO alertas = new ContasPagarConsolidadoDTO.AlertasDTO();
        LocalDate hoje = LocalDate.now();
        List<String> mensagens = new ArrayList<>();

        List<ContaPagar> vencidas = contas.stream().filter(ContaPagar::isVencida).collect(Collectors.toList());
        alertas.setContasVencidas(vencidas.size());
        BigDecimal valorVencido = vencidas.stream().map(ContaPagar::calcularSaldo)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        alertas.setValorVencido(valorVencido);
        if (!vencidas.isEmpty()) {
            mensagens.add(String.format("%d conta(s) vencida(s) no valor de R$ %.2f",
                                       vencidas.size(), valorVencido));
        }

        long vencemHoje = contas.stream()
            .filter(c -> c.getDataVencimento().equals(hoje) && !StatusConta.PAGO.equals(c.getStatus()))
            .count();
        alertas.setContasVencemHoje((int) vencemHoje);
        if (vencemHoje > 0) mensagens.add(String.format("%d conta(s) vencem hoje", vencemHoje));

        LocalDate fimSemana = hoje.plusDays(7);
        long vencemSemana = contas.stream()
            .filter(c -> !c.getDataVencimento().isBefore(hoje) && !c.getDataVencimento().isAfter(fimSemana)
                         && !StatusConta.PAGO.equals(c.getStatus()))
            .count();
        alertas.setContasVencemEstaSemana((int) vencemSemana);
        if (vencemSemana > 0) mensagens.add(String.format("%d conta(s) vencem nos próximos 7 dias", vencemSemana));

        alertas.setMensagens(mensagens);
        return alertas;
    }

    // =========================================================================
    // 3. Contas a Receber Consolidado
    // =========================================================================

    @Transactional(readOnly = true)
    public ContasReceberConsolidadoDTO gerarContasReceberConsolidado(LocalDate dataInicio,
                                                                      LocalDate dataFim,
                                                                      StatusConta statusFiltro,
                                                                      Long clienteId,
                                                                      Long fornecedorId,
                                                                      Long categoriaId) {
        log.info("Gerando Contas a Receber Consolidado: {} a {}", dataInicio, dataFim);

        ContasReceberConsolidadoDTO relatorio = new ContasReceberConsolidadoDTO();

        ContasReceberConsolidadoDTO.PeriodoDTO periodo = new ContasReceberConsolidadoDTO.PeriodoDTO();
        periodo.setDataInicio(dataInicio);
        periodo.setDataFim(dataFim);
        periodo.setDescricao(gerarDescricaoPeriodo(dataInicio, dataFim));
        relatorio.setPeriodo(periodo);

        List<ContaReceber> contas = statusFiltro != null
            ? contaReceberRepository.findByDataVencimentoBetweenAndStatusAndClienteId(
                dataInicio, dataFim, clienteId, statusFiltro, Pageable.unpaged()).getContent()
            : contaReceberRepository.findByDataVencimentoBetweenAndClienteId(
                dataInicio, dataFim, clienteId, Pageable.unpaged()).getContent();

        contas = filtrarContaReceber(contas, fornecedorId, categoriaId);
        relatorio.setResumoGeral(calcularResumoGeralReceber(contas));
        relatorio.setContasPorStatus(calcularContasPorStatusReceber(contas));
        relatorio.setAging(calcularAging(contas));
        relatorio.setTopFornecedores(calcularTopClientes(contas, 10));
        relatorio.setReceitasPorCategoria(calcularReceitasPorCategoriaReceber(contas));
        relatorio.setContas(converterParaDetalhesReceber(contas));
        relatorio.setAlertas(gerarAlertasReceber(contas));

        return relatorio;
    }

    private ContasReceberConsolidadoDTO.ResumoGeralDTO calcularResumoGeralReceber(
            List<ContaReceber> contas) {
        ContasReceberConsolidadoDTO.ResumoGeralDTO resumo = new ContasReceberConsolidadoDTO.ResumoGeralDTO();
        BigDecimal valorTotal = BigDecimal.ZERO, valorRecebido = BigDecimal.ZERO;
        int qtdRecebida = 0, qtdPendente = 0;
        long totalDiasRec = 0;
        int contasRecebidas = 0;

        for (ContaReceber c : contas) {
            valorTotal = valorTotal.add(c.getValorTotal());
            valorRecebido = valorRecebido.add(c.getValorRecebido());
            if (StatusConta.RECEBIDO.equals(c.getStatus())) {
                qtdRecebida++;
                if (c.getDataRecebimento() != null) {
                    totalDiasRec += ChronoUnit.DAYS.between(c.getDataEmissao(), c.getDataRecebimento());
                    contasRecebidas++;
                }
            } else if (StatusConta.PENDENTE.equals(c.getStatus()) || StatusConta.PARCIAL.equals(c.getStatus())) {
                qtdPendente++;
            }
        }

        resumo.setQuantidadeTotal(contas.size());
        resumo.setQuantidadeRecebida(qtdRecebida);
        resumo.setQuantidadePendente(qtdPendente);
        resumo.setValorTotalContas(valorTotal);
        resumo.setValorRecebido(valorRecebido);
        resumo.setSaldoAReceber(valorTotal.subtract(valorRecebido));

        if (valorTotal.compareTo(BigDecimal.ZERO) > 0) {
            resumo.setPercentualRecebido(valorRecebido.divide(valorTotal, 4, RoundingMode.HALF_UP)
                                                      .multiply(BigDecimal.valueOf(100)));
        }
        if (contasRecebidas > 0) {
            resumo.setPrazoMedioRecebimento(BigDecimal.valueOf(totalDiasRec)
                .divide(BigDecimal.valueOf(contasRecebidas), 2, RoundingMode.HALF_UP));
        }
        long vencidas = contas.stream().filter(ContaReceber::isVencida).count();
        if (!contas.isEmpty()) {
            resumo.setTaxaInadimplencia(BigDecimal.valueOf(vencidas)
                .divide(BigDecimal.valueOf(contas.size()), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)));
        }
        return resumo;
    }

    private ContasReceberConsolidadoDTO.ContasPorStatusDTO calcularContasPorStatusReceber(
            List<ContaReceber> contas) {
        ContasReceberConsolidadoDTO.ContasPorStatusDTO porStatus = new ContasReceberConsolidadoDTO.ContasPorStatusDTO();
        Map<StatusConta, List<ContaReceber>> agrupado = contas.stream()
            .collect(Collectors.groupingBy(ContaReceber::getStatus));
        BigDecimal totalGeral = contas.stream().map(ContaReceber::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        porStatus.setPendentes(criarStatusResumoReceber(agrupado.get(StatusConta.PENDENTE), totalGeral));
        porStatus.setRecebidas(criarStatusResumoReceber(agrupado.get(StatusConta.RECEBIDO), totalGeral));
        porStatus.setVencidas(criarStatusResumoReceber(agrupado.get(StatusConta.VENCIDO), totalGeral));
        porStatus.setParciais(criarStatusResumoReceber(agrupado.get(StatusConta.PARCIAL), totalGeral));
        return porStatus;
    }

    private ContasReceberConsolidadoDTO.StatusResumoDTO criarStatusResumoReceber(
            List<ContaReceber> contas, BigDecimal totalGeral) {
        ContasReceberConsolidadoDTO.StatusResumoDTO resumo = new ContasReceberConsolidadoDTO.StatusResumoDTO();
        if (contas == null || contas.isEmpty()) {
            resumo.setQuantidade(0);
            resumo.setValor(BigDecimal.ZERO);
            resumo.setPercentual(BigDecimal.ZERO);
            return resumo;
        }
        BigDecimal valor = contas.stream().map(ContaReceber::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        resumo.setQuantidade(contas.size());
        resumo.setValor(valor);
        if (totalGeral.compareTo(BigDecimal.ZERO) > 0) {
            resumo.setPercentual(valor.divide(totalGeral, 4, RoundingMode.HALF_UP)
                                     .multiply(BigDecimal.valueOf(100)));
        }
        return resumo;
    }

    private ContasReceberConsolidadoDTO.AgingDTO calcularAging(List<ContaReceber> contas) {
        ContasReceberConsolidadoDTO.AgingDTO aging = new ContasReceberConsolidadoDTO.AgingDTO();
        LocalDate hoje = LocalDate.now();

        List<ContaReceber> naoRecebidas = contas.stream()
            .filter(c -> StatusConta.PENDENTE.equals(c.getStatus()) || StatusConta.PARCIAL.equals(c.getStatus()))
            .collect(Collectors.toList());
        BigDecimal totalNaoRec = naoRecebidas.stream().map(ContaReceber::calcularSaldo)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        aging.setEmDia(criarFaixaAging(naoRecebidas.stream()
            .filter(c -> !c.getDataVencimento().isBefore(hoje)).collect(Collectors.toList()),
            "Em dia", totalNaoRec));
        aging.setAtraso0a30(criarFaixaAging(naoRecebidas.stream().filter(c -> {
            if (!c.getDataVencimento().isBefore(hoje)) return false;
            long d = ChronoUnit.DAYS.between(c.getDataVencimento(), hoje);
            return d >= 0 && d <= 30;
        }).collect(Collectors.toList()), "0 a 30 dias", totalNaoRec));
        aging.setAtraso31a60(criarFaixaAging(naoRecebidas.stream().filter(c -> {
            long d = ChronoUnit.DAYS.between(c.getDataVencimento(), hoje);
            return d >= 31 && d <= 60;
        }).collect(Collectors.toList()), "31 a 60 dias", totalNaoRec));
        aging.setAtraso61a90(criarFaixaAging(naoRecebidas.stream().filter(c -> {
            long d = ChronoUnit.DAYS.between(c.getDataVencimento(), hoje);
            return d >= 61 && d <= 90;
        }).collect(Collectors.toList()), "61 a 90 dias", totalNaoRec));
        aging.setAtrasoMais90(criarFaixaAging(naoRecebidas.stream().filter(c -> {
            long d = ChronoUnit.DAYS.between(c.getDataVencimento(), hoje);
            return d > 90;
        }).collect(Collectors.toList()), "Mais de 90 dias", totalNaoRec));

        long qtdVencidas = naoRecebidas.stream().filter(ContaReceber::isVencida).count();
        StringBuilder analise = new StringBuilder();
        if (totalNaoRec.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal valorVencido = naoRecebidas.stream().filter(ContaReceber::isVencida)
                .map(ContaReceber::calcularSaldo).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal pct = valorVencido.divide(totalNaoRec, 4, RoundingMode.HALF_UP)
                                         .multiply(BigDecimal.valueOf(100));
            analise.append(String.format("%.2f%% das contas a receber estão vencidas. ", pct));
        }
        List<ContaReceber> mais90 = naoRecebidas.stream().filter(c -> {
            long d = ChronoUnit.DAYS.between(c.getDataVencimento(), hoje);
            return d > 90;
        }).collect(Collectors.toList());
        if (!mais90.isEmpty()) {
            analise.append(String.format("%d conta(s) com mais de 90 dias de atraso.", mais90.size()));
        }
        aging.setAnalise(analise.toString());
        return aging;
    }

    private ContasReceberConsolidadoDTO.FaixaAgingDTO criarFaixaAging(
            List<ContaReceber> contas, String descricao, BigDecimal totalGeral) {
        ContasReceberConsolidadoDTO.FaixaAgingDTO faixa = new ContasReceberConsolidadoDTO.FaixaAgingDTO();
        faixa.setDescricao(descricao);
        faixa.setQuantidade(contas.size());
        BigDecimal valor = contas.stream().map(ContaReceber::calcularSaldo)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        faixa.setValor(valor);
        if (totalGeral.compareTo(BigDecimal.ZERO) > 0) {
            faixa.setPercentual(valor.divide(totalGeral, 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)));
        }
        return faixa;
    }

    private List<ContasReceberConsolidadoDTO.TopFornecedorDTO> calcularTopClientes(
            List<ContaReceber> contas, int top) {
        Map<Long, List<ContaReceber>> porCliente = contas.stream()
            .filter(c -> c.getFornecedor() != null)
            .collect(Collectors.groupingBy(c -> c.getFornecedor().getId()));
        BigDecimal totalGeral = contas.stream().map(ContaReceber::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return porCliente.entrySet().stream().map(e -> {
            List<ContaReceber> cc = e.getValue();
            BigDecimal vc = cc.stream().map(ContaReceber::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            ContasReceberConsolidadoDTO.TopFornecedorDTO dto = new ContasReceberConsolidadoDTO.TopFornecedorDTO();
            dto.setFornecedorId(e.getKey());
            dto.setFornecedorNome(cc.get(0).getFornecedor().getRazaoSocial());
            dto.setValorTotal(vc);
            dto.setQuantidadeContas(cc.size());
            if (totalGeral.compareTo(BigDecimal.ZERO) > 0) {
                dto.setPercentualDoTotal(vc.divide(totalGeral, 4, RoundingMode.HALF_UP)
                                          .multiply(BigDecimal.valueOf(100)));
            }
            dto.setTicketMedio(vc.divide(BigDecimal.valueOf(cc.size()), 2, RoundingMode.HALF_UP));

            List<ContaReceber> recebidas = cc.stream()
                .filter(c -> c.getDataRecebimento() != null).collect(Collectors.toList());
            if (!recebidas.isEmpty()) {
                long totalDias = recebidas.stream()
                    .mapToLong(c -> ChronoUnit.DAYS.between(c.getDataEmissao(), c.getDataRecebimento()))
                    .sum();
                dto.setPrazoMedioRecebimento(BigDecimal.valueOf(totalDias)
                    .divide(BigDecimal.valueOf(recebidas.size()), 2, RoundingMode.HALF_UP));
            }

            long qtdVencidas = cc.stream().filter(ContaReceber::isVencida).count();
            if (!cc.isEmpty()) {
                dto.setTaxaInadimplencia(BigDecimal.valueOf(qtdVencidas)
                    .divide(BigDecimal.valueOf(cc.size()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)));
            }
            return dto;
        }).sorted((a, b) -> b.getValorTotal().compareTo(a.getValorTotal()))
          .limit(top).collect(Collectors.toList());
    }

    private List<ContasReceberConsolidadoDTO.ReceitaPorCategoriaDTO> calcularReceitasPorCategoriaReceber(
            List<ContaReceber> contas) {
        Map<Long, List<ContaReceber>> porCategoria = contas.stream()
            .filter(c -> c.getCategoria() != null)
            .collect(Collectors.groupingBy(c -> c.getCategoria().getId()));
        BigDecimal totalGeral = contas.stream().map(ContaReceber::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return porCategoria.entrySet().stream().map(e -> {
            List<ContaReceber> cc = e.getValue();
            BigDecimal vc = cc.stream().map(ContaReceber::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            ContasReceberConsolidadoDTO.ReceitaPorCategoriaDTO dto = new ContasReceberConsolidadoDTO.ReceitaPorCategoriaDTO();
            dto.setCategoriaId(e.getKey());
            dto.setCategoriaNome(cc.get(0).getCategoria().getNome());
            dto.setValor(vc);
            dto.setQuantidade(cc.size());
            if (totalGeral.compareTo(BigDecimal.ZERO) > 0) {
                dto.setPercentual(vc.divide(totalGeral, 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)));
            }
            return dto;
        }).sorted((a, b) -> b.getValor().compareTo(a.getValor())).collect(Collectors.toList());
    }

    private List<ContasReceberConsolidadoDTO.ContaReceberDetalheDTO> converterParaDetalhesReceber(
            List<ContaReceber> contas) {
        LocalDate hoje = LocalDate.now();
        return contas.stream().map(c -> {
            ContasReceberConsolidadoDTO.ContaReceberDetalheDTO dto = new ContasReceberConsolidadoDTO.ContaReceberDetalheDTO();
            dto.setId(c.getId());
            dto.setDescricao(c.getDescricao());
            dto.setNumeroDocumento(c.getNumeroDocumento());
            dto.setNumeroCte(c.getNumeroCte());
            dto.setFornecedorNome(c.getCliente() != null ? c.getFornecedor().getRazaoSocial() : null);
            dto.setCategoriaNome(c.getCategoria() != null ? c.getCategoria().getNome() : null);
            dto.setValorOriginal(c.getValorOriginal());
            dto.setValorTotal(c.getValorTotal());
            dto.setValorRecebido(c.getValorRecebido());
            dto.setSaldo(c.calcularSaldo());
            dto.setDataEmissao(c.getDataEmissao());
            dto.setDataVencimento(c.getDataVencimento());
            dto.setDataRecebimento(c.getDataRecebimento());
            dto.setStatus(c.getStatus());
            dto.setVencida(c.isVencida());
            dto.setOrigemFrete(c.getOrigemFrete());
            dto.setDestinoFrete(c.getDestinoFrete());
            if (c.isVencida()) {
                dto.setDiasAtraso((int) ChronoUnit.DAYS.between(c.getDataVencimento(), hoje));
            }
            return dto;
        }).collect(Collectors.toList());
    }

    private ContasReceberConsolidadoDTO.AlertasDTO gerarAlertasReceber(List<ContaReceber> contas) {
        ContasReceberConsolidadoDTO.AlertasDTO alertas = new ContasReceberConsolidadoDTO.AlertasDTO();
        LocalDate hoje = LocalDate.now();
        List<String> mensagens = new ArrayList<>();

        List<ContaReceber> vencidas = contas.stream().filter(ContaReceber::isVencida).collect(Collectors.toList());
        alertas.setContasVencidas(vencidas.size());
        BigDecimal valorVencido = vencidas.stream().map(ContaReceber::calcularSaldo)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        alertas.setValorVencido(valorVencido);
        if (!vencidas.isEmpty()) {
            mensagens.add(String.format("%d conta(s) vencida(s) no valor de R$ %.2f",
                                       vencidas.size(), valorVencido));
        }

        long vencemHoje = contas.stream()
            .filter(c -> c.getDataVencimento().equals(hoje) && !StatusConta.RECEBIDO.equals(c.getStatus()))
            .count();
        alertas.setContasVencemHoje((int) vencemHoje);
        if (vencemHoje > 0) mensagens.add(String.format("%d conta(s) vencem hoje", vencemHoje));

        LocalDate fimSemana = hoje.plusDays(7);
        long vencemSemana = contas.stream()
            .filter(c -> !c.getDataVencimento().isBefore(hoje) && !c.getDataVencimento().isAfter(fimSemana)
                         && !StatusConta.RECEBIDO.equals(c.getStatus()))
            .count();
        alertas.setContasVencemEstaSemana((int) vencemSemana);

        long clientesInadimplentes = contas.stream().filter(ContaReceber::isVencida)
            .filter(c -> c.getCliente() != null)
            .map(c -> c.getCliente().getId()).distinct().count();
        alertas.setClientesInadimplentes((int) clientesInadimplentes);
        if (clientesInadimplentes > 0) {
            mensagens.add(String.format("%d cliente(s) com contas vencidas", clientesInadimplentes));
        }

        alertas.setMensagens(mensagens);
        return alertas;
    }

    // =========================================================================
    // 4. Fluxo de Caixa Realizado
    // =========================================================================

    @Transactional(readOnly = true)
    public FluxoCaixaRealizadoDTO gerarFluxoCaixaRealizado(LocalDate dataInicio, LocalDate dataFim,
                                                            Long clienteId,
                                                            Long centroCustoId,
                                                            Boolean incluirEvolucaoMensal,
                                                            Boolean compararPeriodoAnterior) {
        log.info("Gerando Fluxo de Caixa Realizado: {} a {}", dataInicio, dataFim);

        FluxoCaixaRealizadoDTO fluxo = new FluxoCaixaRealizadoDTO();

        FluxoCaixaRealizadoDTO.PeriodoDTO periodo = new FluxoCaixaRealizadoDTO.PeriodoDTO();
        periodo.setDataInicio(dataInicio);
        periodo.setDataFim(dataFim);
        periodo.setDescricao(gerarDescricaoPeriodo(dataInicio, dataFim));
        periodo.setTotalDias((int) ChronoUnit.DAYS.between(dataInicio, dataFim) + 1);
        fluxo.setPeriodo(periodo);

        FluxoCaixaRealizadoDTO.SaldoInicialDTO saldoInicial = new FluxoCaixaRealizadoDTO.SaldoInicialDTO();
        saldoInicial.setValor(BigDecimal.ZERO);
        saldoInicial.setData(dataInicio.minusDays(1));
        saldoInicial.setDescricao("Saldo em " + saldoInicial.getData());
        fluxo.setSaldoInicial(saldoInicial);

        fluxo.setEntradas(calcularEntradasFluxo(dataInicio, dataFim, clienteId, centroCustoId));
        fluxo.setSaidas(calcularSaidasFluxo(dataInicio, dataFim, clienteId, centroCustoId));
        fluxo.setResultado(calcularResultadoFluxo(fluxo.getSaldoInicial(), fluxo.getEntradas(),
                                                   fluxo.getSaidas(), periodo.getTotalDias()));

        if (Boolean.TRUE.equals(incluirEvolucaoMensal)) {
            fluxo.setEvolucaoMensal(calcularEvolucaoMensalFluxo(dataFim, clienteId, centroCustoId));
        }

        if (Boolean.TRUE.equals(compararPeriodoAnterior)) {
            fluxo.setComparativo(calcularComparativoFluxo(dataInicio, dataFim, clienteId, centroCustoId, fluxo));
        }

        return fluxo;
    }

    private FluxoCaixaRealizadoDTO.EntradasDTO calcularEntradasFluxo(LocalDate dataInicio,
                                                                      LocalDate dataFim,
                                                                      Long clienteId,
                                                                      Long centroCustoId) {
        FluxoCaixaRealizadoDTO.EntradasDTO entradas = new FluxoCaixaRealizadoDTO.EntradasDTO();

        List<Object[]> rows = contaReceberRepository
            .somarReceitasPorCategoria(dataInicio, dataFim, clienteId, centroCustoId, StatusConta.RECEBIDO);

        BigDecimal total = BigDecimal.ZERO, receitasRec = BigDecimal.ZERO, outras = BigDecimal.ZERO;
        int qtd = 0;
        List<FluxoCaixaRealizadoDTO.ItemEntradaDTO> det = new ArrayList<>();

        for (Object[] row : rows) {
            String nome = (String) row[0];
            BigDecimal valor = (BigDecimal) row[1];
            Integer q = ((Number) row[2]).intValue();
            total = total.add(valor);
            qtd += q;
            String lower = nome != null ? nome.toLowerCase() : "";
            if (lower.contains("frete") || lower.contains("receita")) receitasRec = receitasRec.add(valor);
            else outras = outras.add(valor);
            FluxoCaixaRealizadoDTO.ItemEntradaDTO item = new FluxoCaixaRealizadoDTO.ItemEntradaDTO();
            item.setCategoria(nome);
            item.setValor(valor);
            item.setQuantidade(q);
            det.add(item);
        }

        final BigDecimal totalFinal = total;
        det.forEach(item -> {
            if (totalFinal.compareTo(BigDecimal.ZERO) > 0) {
                item.setPercentual(item.getValor().divide(totalFinal, 4, RoundingMode.HALF_UP)
                                       .multiply(BigDecimal.valueOf(100)));
            }
        });

        entradas.setReceitasRecebidas(receitasRec);
        entradas.setOutrasEntradas(outras);
        entradas.setTotalEntradas(total);
        entradas.setQuantidadeRecebimentos(qtd);
        entradas.setDetalhamento(det);
        return entradas;
    }

    private FluxoCaixaRealizadoDTO.SaidasDTO calcularSaidasFluxo(LocalDate dataInicio,
                                                                   LocalDate dataFim,
                                                                   Long clienteId,
                                                                   Long centroCustoId) {
        FluxoCaixaRealizadoDTO.SaidasDTO saidas = new FluxoCaixaRealizadoDTO.SaidasDTO();

        List<Object[]> rows = contaPagarRepository
            .somarDespesasPorCategoria(dataInicio, dataFim, clienteId, centroCustoId, StatusConta.PAGO);

        BigDecimal total = BigDecimal.ZERO, despesasPagas = BigDecimal.ZERO;
        int qtd = 0;
        List<FluxoCaixaRealizadoDTO.ItemSaidaDTO> det = new ArrayList<>();

        for (Object[] row : rows) {
            String nome = (String) row[0];
            BigDecimal valor = (BigDecimal) row[1];
            Integer q = ((Number) row[2]).intValue();
            total = total.add(valor);
            despesasPagas = despesasPagas.add(valor);
            qtd += q;
            FluxoCaixaRealizadoDTO.ItemSaidaDTO item = new FluxoCaixaRealizadoDTO.ItemSaidaDTO();
            item.setCategoria(nome);
            item.setValor(valor);
            item.setQuantidade(q);
            det.add(item);
        }

        final BigDecimal totalFinal = total;
        det.forEach(item -> {
            if (totalFinal.compareTo(BigDecimal.ZERO) > 0) {
                item.setPercentual(item.getValor().divide(totalFinal, 4, RoundingMode.HALF_UP)
                                       .multiply(BigDecimal.valueOf(100)));
            }
        });

        saidas.setDespesasPagas(despesasPagas);
        saidas.setOutrasSaidas(BigDecimal.ZERO);
        saidas.setTotalSaidas(total);
        saidas.setQuantidadePagamentos(qtd);
        saidas.setDetalhamento(det);
        return saidas;
    }

    private FluxoCaixaRealizadoDTO.ResultadoDTO calcularResultadoFluxo(
            FluxoCaixaRealizadoDTO.SaldoInicialDTO saldoInicial,
            FluxoCaixaRealizadoDTO.EntradasDTO entradas,
            FluxoCaixaRealizadoDTO.SaidasDTO saidas,
            Integer totalDias) {
        FluxoCaixaRealizadoDTO.ResultadoDTO resultado = new FluxoCaixaRealizadoDTO.ResultadoDTO();
        BigDecimal saldoPeriodo = entradas.getTotalEntradas().subtract(saidas.getTotalSaidas());
        resultado.setSaldoPeriodo(saldoPeriodo);
        resultado.setSaldoFinal(saldoInicial.getValor().add(saldoPeriodo));
        if (totalDias != null && totalDias > 0) {
            resultado.setSaldoMedioDiario(saldoPeriodo.divide(BigDecimal.valueOf(totalDias), 2, RoundingMode.HALF_UP));
        }
        if (saldoPeriodo.compareTo(BigDecimal.ZERO) > 0) resultado.setSituacao("POSITIVO");
        else if (saldoPeriodo.compareTo(BigDecimal.ZERO) < 0) resultado.setSituacao("NEGATIVO");
        else resultado.setSituacao("EQUILIBRADO");
        return resultado;
    }

    private List<FluxoCaixaRealizadoDTO.MovimentacaoMensalDTO> calcularEvolucaoMensalFluxo(
            LocalDate dataFim, Long clienteId, Long centroCustoId) {
        List<FluxoCaixaRealizadoDTO.MovimentacaoMensalDTO> evolucao = new ArrayList<>();
        LocalDate mesInicio = dataFim.minusMonths(11).withDayOfMonth(1);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMMM/yyyy", new Locale("pt", "BR"));

        for (int i = 0; i < 12; i++) {
            LocalDate ini = mesInicio.plusMonths(i);
            LocalDate fim = ini.withDayOfMonth(ini.lengthOfMonth());
            if (ini.isAfter(dataFim)) break;

            FluxoCaixaRealizadoDTO.EntradasDTO e = calcularEntradasFluxo(ini, fim, clienteId, centroCustoId);
            FluxoCaixaRealizadoDTO.SaidasDTO s = calcularSaidasFluxo(ini, fim, clienteId, centroCustoId);

            FluxoCaixaRealizadoDTO.MovimentacaoMensalDTO mov = new FluxoCaixaRealizadoDTO.MovimentacaoMensalDTO();
            mov.setMes(ini.format(fmt));
            mov.setEntradas(e.getTotalEntradas());
            mov.setSaidas(s.getTotalSaidas());
            mov.setSaldo(e.getTotalEntradas().subtract(s.getTotalSaidas()));
            evolucao.add(mov);
        }
        return evolucao;
    }

    private FluxoCaixaRealizadoDTO.ComparativoDTO calcularComparativoFluxo(LocalDate dataInicio,
                                                                            LocalDate dataFim,
                                                                            Long clienteId,
                                                                            Long centroCustoId,
                                                                            FluxoCaixaRealizadoDTO fluxoAtual) {
        FluxoCaixaRealizadoDTO.ComparativoDTO comp = new FluxoCaixaRealizadoDTO.ComparativoDTO();
        long dias = ChronoUnit.DAYS.between(dataInicio, dataFim);
        LocalDate iniAnt = dataInicio.minusDays(dias + 1);
        LocalDate fimAnt = dataInicio.minusDays(1);

        FluxoCaixaRealizadoDTO.PeriodoDTO periodoAnt = new FluxoCaixaRealizadoDTO.PeriodoDTO();
        periodoAnt.setDataInicio(iniAnt);
        periodoAnt.setDataFim(fimAnt);
        periodoAnt.setDescricao(gerarDescricaoPeriodo(iniAnt, fimAnt));
        comp.setPeriodoAnterior(periodoAnt);

        FluxoCaixaRealizadoDTO.EntradasDTO entAnt = calcularEntradasFluxo(iniAnt, fimAnt, clienteId,  centroCustoId);
        FluxoCaixaRealizadoDTO.SaidasDTO saiAnt = calcularSaidasFluxo(iniAnt, fimAnt, clienteId, centroCustoId);
        comp.setEntradasAnterior(entAnt.getTotalEntradas());
        comp.setSaidasAnterior(saiAnt.getTotalSaidas());
        comp.setSaldoAnterior(entAnt.getTotalEntradas().subtract(saiAnt.getTotalSaidas()));
        comp.setVariacaoEntradas(calcularVariacaoPercentual(entAnt.getTotalEntradas(), fluxoAtual.getEntradas().getTotalEntradas()));
        comp.setVariacaoSaidas(calcularVariacaoPercentual(saiAnt.getTotalSaidas(), fluxoAtual.getSaidas().getTotalSaidas()));
        comp.setVariacaoSaldo(calcularVariacaoPercentual(comp.getSaldoAnterior(), fluxoAtual.getResultado().getSaldoPeriodo()));
        return comp;
    }

    // =========================================================================
    // 5. Análise de Despesas por Categoria
    // =========================================================================

    @Transactional(readOnly = true)
    public DespesasPorCategoriaDTO gerarDespesasPorCategoria(LocalDate dataInicio,
                                                              LocalDate dataFim,
                                                              Long clienteId,
                                                              Boolean incluirEvolucaoMensal,
                                                              Boolean compararPeriodoAnterior) {
        log.info("Gerando Análise de Despesas por Categoria: {} a {}", dataInicio, dataFim);

        DespesasPorCategoriaDTO analise = new DespesasPorCategoriaDTO();

        DespesasPorCategoriaDTO.PeriodoDTO periodo = new DespesasPorCategoriaDTO.PeriodoDTO();
        periodo.setDataInicio(dataInicio);
        periodo.setDataFim(dataFim);
        periodo.setDescricao(gerarDescricaoPeriodo(dataInicio, dataFim));
        analise.setPeriodo(periodo);

        List<ContaPagar> contas = contaPagarRepository
            .findByDataPagamentoBetweenAndClienteIdAndStatus(dataInicio, dataFim, clienteId, StatusConta.PAGO, Pageable.unpaged())
            .getContent();

        analise.setResumoGeral(calcularResumoGeralDespesas(contas));
        analise.setCategorias(calcularCategoriasDespesas(contas));
        analise.setTop10Despesas(calcularTop10Despesas(contas));

        if (Boolean.TRUE.equals(incluirEvolucaoMensal)) {
            analise.setEvolucaoMensal(calcularEvolucaoMensalDespesas(dataFim, clienteId));
        }

        if (Boolean.TRUE.equals(compararPeriodoAnterior)) {
            analise.setComparativo(calcularComparativoDespesas(dataInicio, dataFim, clienteId, analise));
        }

        analise.setInsights(gerarInsightsDespesas(analise));
        return analise;
    }

    private DespesasPorCategoriaDTO.ResumoGeralDTO calcularResumoGeralDespesas(List<ContaPagar> contas) {
        DespesasPorCategoriaDTO.ResumoGeralDTO resumo = new DespesasPorCategoriaDTO.ResumoGeralDTO();
        BigDecimal total = contas.stream().map(ContaPagar::getValorPago)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        long categorias = contas.stream().filter(c -> c.getCategoria() != null)
            .map(c -> c.getCategoria().getId()).distinct().count();

        resumo.setTotalDespesas(total);
        resumo.setQuantidadeCategorias((int) categorias);
        resumo.setQuantidadeContas(contas.size());
        if (categorias > 0) {
            resumo.setDespesaMediaPorCategoria(total.divide(BigDecimal.valueOf(categorias), 2, RoundingMode.HALF_UP));
        }
        if (!contas.isEmpty()) {
            resumo.setDespesaMediaPorConta(total.divide(BigDecimal.valueOf(contas.size()), 2, RoundingMode.HALF_UP));
        }
        return resumo;
    }

    private List<DespesasPorCategoriaDTO.CategoriaDetalheDTO> calcularCategoriasDespesas(
            List<ContaPagar> contas) {
        Map<Long, List<ContaPagar>> porCategoria = contas.stream()
            .filter(c -> c.getCategoria() != null)
            .collect(Collectors.groupingBy(c -> c.getCategoria().getId()));
        BigDecimal totalGeral = contas.stream().map(ContaPagar::getValorPago)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<DespesasPorCategoriaDTO.CategoriaDetalheDTO> categorias = porCategoria.entrySet().stream().map(e -> {
            List<ContaPagar> cc = e.getValue();
            BigDecimal vc = cc.stream().map(ContaPagar::getValorPago).reduce(BigDecimal.ZERO, BigDecimal::add);

            DespesasPorCategoriaDTO.CategoriaDetalheDTO dto = new DespesasPorCategoriaDTO.CategoriaDetalheDTO();
            dto.setCategoriaId(e.getKey());
            dto.setCategoriaNome(cc.get(0).getCategoria().getNome());
            dto.setValorTotal(vc);
            dto.setQuantidadeContas(cc.size());
            if (totalGeral.compareTo(BigDecimal.ZERO) > 0) {
                dto.setPercentualDoTotal(vc.divide(totalGeral, 4, RoundingMode.HALF_UP)
                                          .multiply(BigDecimal.valueOf(100)));
            }
            dto.setTicketMedio(vc.divide(BigDecimal.valueOf(cc.size()), 2, RoundingMode.HALF_UP));

            Map<String, List<ContaPagar>> porForn = cc.stream()
                .filter(c -> c.getFornecedor() != null)
                .collect(Collectors.groupingBy(c -> c.getFornecedor().getRazaoSocial()));
            List<DespesasPorCategoriaDTO.FornecedorCategoria> fornecedores = porForn.entrySet().stream().map(f -> {
                BigDecimal vf = f.getValue().stream().map(ContaPagar::getValorPago)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                DespesasPorCategoriaDTO.FornecedorCategoria fc = new DespesasPorCategoriaDTO.FornecedorCategoria();
                fc.setFornecedorNome(f.getKey());
                fc.setValor(vf);
                fc.setQuantidade(f.getValue().size());
                return fc;
            }).sorted((a, b) -> b.getValor().compareTo(a.getValor())).limit(3).collect(Collectors.toList());
            dto.setPrincipaisFornecedores(fornecedores);

            return dto;
        }).sorted((a, b) -> b.getValorTotal().compareTo(a.getValorTotal())).collect(Collectors.toList());

        for (int i = 0; i < categorias.size(); i++) categorias.get(i).setPosicaoRanking(i + 1);
        return categorias;
    }

    private List<DespesasPorCategoriaDTO.Top10DespesaDTO> calcularTop10Despesas(List<ContaPagar> contas) {
        return contas.stream()
            .sorted((a, b) -> b.getValorPago().compareTo(a.getValorPago()))
            .limit(10)
            .map(c -> {
                DespesasPorCategoriaDTO.Top10DespesaDTO dto = new DespesasPorCategoriaDTO.Top10DespesaDTO();
                dto.setContaId(c.getId());
                dto.setDescricao(c.getDescricao());
                dto.setFornecedorNome(c.getFornecedor() != null ? c.getFornecedor().getRazaoSocial() : null);
                dto.setCategoriaNome(c.getCategoria() != null ? c.getCategoria().getNome() : null);
                dto.setValor(c.getValorPago());
                dto.setDataPagamento(c.getDataPagamento());
                if (c.getVeiculo() != null) dto.setVeiculoPlaca(c.getVeiculo().getPlaca());
                return dto;
            }).collect(Collectors.toList());
    }

    private List<DespesasPorCategoriaDTO.EvolucaoMensalDTO> calcularEvolucaoMensalDespesas(
            LocalDate dataFim, Long clienteId) {
        List<DespesasPorCategoriaDTO.EvolucaoMensalDTO> evolucao = new ArrayList<>();
        LocalDate mesInicio = dataFim.minusMonths(11).withDayOfMonth(1);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMMM/yyyy", new Locale("pt", "BR"));

        for (int i = 0; i < 12; i++) {
            LocalDate ini = mesInicio.plusMonths(i);
            LocalDate fim = ini.withDayOfMonth(ini.lengthOfMonth());
            if (ini.isAfter(dataFim)) break;

            List<ContaPagar> contasMes = contaPagarRepository
                .findByDataPagamentoBetweenAndClienteIdAndStatus(ini, fim,clienteId ,StatusConta.PAGO, Pageable.unpaged())
                .getContent();

            Map<String, BigDecimal> porCat = contasMes.stream()
                .filter(c -> c.getCategoria() != null)
                .collect(Collectors.groupingBy(c -> c.getCategoria().getNome(),
                    Collectors.reducing(BigDecimal.ZERO, ContaPagar::getValorPago, BigDecimal::add)));

            List<DespesasPorCategoriaDTO.ValorCategoriaDTO> cats = porCat.entrySet().stream()
                .map(e -> new DespesasPorCategoriaDTO.ValorCategoriaDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

            DespesasPorCategoriaDTO.EvolucaoMensalDTO evo = new DespesasPorCategoriaDTO.EvolucaoMensalDTO();
            evo.setMes(ini.format(fmt));
            evo.setCategorias(cats);
            evolucao.add(evo);
        }
        return evolucao;
    }

    private DespesasPorCategoriaDTO.ComparativoDTO calcularComparativoDespesas(LocalDate dataInicio,
                                                                                LocalDate dataFim,
                                                                                Long clienteId,
                                                                                DespesasPorCategoriaDTO analiseAtual) {
        DespesasPorCategoriaDTO.ComparativoDTO comp = new DespesasPorCategoriaDTO.ComparativoDTO();
        long dias = ChronoUnit.DAYS.between(dataInicio, dataFim);
        LocalDate iniAnt = dataInicio.minusDays(dias + 1);
        LocalDate fimAnt = dataInicio.minusDays(1);

        DespesasPorCategoriaDTO.PeriodoDTO periodoAnt = new DespesasPorCategoriaDTO.PeriodoDTO();
        periodoAnt.setDataInicio(iniAnt);
        periodoAnt.setDataFim(fimAnt);
        periodoAnt.setDescricao(gerarDescricaoPeriodo(iniAnt, fimAnt));
        comp.setPeriodoAnterior(periodoAnt);

        List<ContaPagar> contasAnt = contaPagarRepository
            .findByDataPagamentoBetweenAndClienteIdAndStatus(iniAnt, fimAnt, clienteId, StatusConta.PAGO, Pageable.unpaged())
            .getContent();
        BigDecimal totalAnt = contasAnt.stream().map(ContaPagar::getValorPago)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        comp.setTotalDespesasAnterior(totalAnt);
        comp.setVariacao(calcularVariacaoPercentual(totalAnt, analiseAtual.getResumoGeral().getTotalDespesas()));

        Map<String, BigDecimal> catAnt = contasAnt.stream()
            .filter(c -> c.getCategoria() != null)
            .collect(Collectors.groupingBy(c -> c.getCategoria().getNome(),
                Collectors.reducing(BigDecimal.ZERO, ContaPagar::getValorPago, BigDecimal::add)));

        List<DespesasPorCategoriaDTO.VariacaoCategoriaDTO> variacoes = analiseAtual.getCategorias().stream()
            .map(cat -> {
                BigDecimal valorAnt = catAnt.getOrDefault(cat.getCategoriaNome(), BigDecimal.ZERO);
                BigDecimal variacao = calcularVariacaoPercentual(valorAnt, cat.getValorTotal());
                DespesasPorCategoriaDTO.VariacaoCategoriaDTO v = new DespesasPorCategoriaDTO.VariacaoCategoriaDTO();
                v.setCategoriaNome(cat.getCategoriaNome());
                v.setValorAtual(cat.getValorTotal());
                v.setValorAnterior(valorAnt);
                v.setVariacao(variacao);
                v.setSituacao(variacao.compareTo(BigDecimal.ZERO) >= 0 ? "AUMENTO" : "REDUÇÃO");

                if (variacao.compareTo(BigDecimal.valueOf(5)) > 0) cat.setTendencia("AUMENTO");
                else if (variacao.compareTo(BigDecimal.valueOf(-5)) < 0) cat.setTendencia("REDUÇÃO");
                else cat.setTendencia("ESTÁVEL");
                cat.setVariacaoPeriodoAnterior(variacao);
                return v;
            }).sorted((a, b) -> b.getVariacao().abs().compareTo(a.getVariacao().abs()))
              .collect(Collectors.toList());

        comp.setVariacoesPorCategoria(variacoes);
        return comp;
    }

    private DespesasPorCategoriaDTO.InsightsDTO gerarInsightsDespesas(DespesasPorCategoriaDTO analise) {
        DespesasPorCategoriaDTO.InsightsDTO insights = new DespesasPorCategoriaDTO.InsightsDTO();
        List<String> alertas = new ArrayList<>();

        if (analise.getCategorias() != null && !analise.getCategorias().isEmpty()) {
            DespesasPorCategoriaDTO.CategoriaDetalheDTO principal = analise.getCategorias().get(0);
            insights.setPrincipalCategoria(principal.getCategoriaNome());
            insights.setPercentualPrincipalCategoria(principal.getPercentualDoTotal());
            alertas.add(String.format("%s representa %.2f%% das despesas totais",
                                     principal.getCategoriaNome(), principal.getPercentualDoTotal()));
        }

        if (analise.getComparativo() != null && analise.getComparativo().getVariacoesPorCategoria() != null) {
            analise.getComparativo().getVariacoesPorCategoria().stream()
                .filter(v -> "AUMENTO".equals(v.getSituacao())).findFirst().ifPresent(v -> {
                    insights.setCategoriaComMaiorAumento(v.getCategoriaNome());
                    insights.setPercentualAumento(v.getVariacao());
                    alertas.add(String.format("%s teve aumento de %.2f%% comparado ao período anterior",
                                             v.getCategoriaNome(), v.getVariacao()));
                });
            analise.getComparativo().getVariacoesPorCategoria().stream()
                .filter(v -> "REDUÇÃO".equals(v.getSituacao())).findFirst().ifPresent(v -> {
                    insights.setCategoriaComMaiorReducao(v.getCategoriaNome());
                    insights.setPercentualReducao(v.getVariacao());
                });
        }

        insights.setAlertas(alertas);
        return insights;
    }

    // =========================================================================
    // 6. Análise de Receitas por Cliente
    // =========================================================================

    @Transactional(readOnly = true)
    public ReceitasPorClienteDTO gerarReceitasPorCliente(LocalDate dataInicio, LocalDate dataFim,
                                                         Long clienteId,
                                                         Boolean incluirEvolucaoMensal,
                                                         Boolean compararPeriodoAnterior) {
        log.info("Gerando Análise de Receitas por Cliente: {} a {}", dataInicio, dataFim);

        ReceitasPorClienteDTO analise = new ReceitasPorClienteDTO();

        ReceitasPorClienteDTO.PeriodoDTO periodo = new ReceitasPorClienteDTO.PeriodoDTO();
        periodo.setDataInicio(dataInicio);
        periodo.setDataFim(dataFim);
        periodo.setDescricao(gerarDescricaoPeriodo(dataInicio, dataFim));
        analise.setPeriodo(periodo);

        List<ContaReceber> contas = contaReceberRepository
            .findByDataVencimentoBetweenAndStatusAndClienteId(dataInicio, dataFim, clienteId, StatusConta.RECEBIDO, Pageable.unpaged())
            .getContent();

        analise.setResumoGeral(calcularResumoGeralReceitas(contas));
        analise.setFornecedores(calcularClientesReceitas(contas));
        analise.setTop10Receitas(calcularTop10Receitas(contas));

        if (Boolean.TRUE.equals(incluirEvolucaoMensal)) {
            analise.setEvolucaoMensal(calcularEvolucaoMensalReceitas(dataFim, clienteId));
        }

        if (Boolean.TRUE.equals(compararPeriodoAnterior)) {
            analise.setComparativo(calcularComparativoReceitas(dataInicio, dataFim, clienteId, analise));
        }

        analise.setInsights(gerarInsightsReceitas(analise));
        return analise;
    }

    private ReceitasPorClienteDTO.ResumoGeralDTO calcularResumoGeralReceitas(List<ContaReceber> contas) {
        ReceitasPorClienteDTO.ResumoGeralDTO resumo = new ReceitasPorClienteDTO.ResumoGeralDTO();
        BigDecimal total = contas.stream().map(ContaReceber::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        long forncedores = contas.stream().filter(c -> c.getFornecedor() != null)
            .map(c -> c.getFornecedor().getId()).distinct().count();

        resumo.setTotalReceitas(total);
        resumo.setQuantidadeFornecedores((int) forncedores);
        resumo.setQuantidadeFretes(contas.size());
        if (forncedores > 0) {
            resumo.setReceitaMediaPorCliente(total.divide(BigDecimal.valueOf(forncedores), 2, RoundingMode.HALF_UP));
        }
        if (!contas.isEmpty()) {
            resumo.setTicketMedioFrete(total.divide(BigDecimal.valueOf(contas.size()), 2, RoundingMode.HALF_UP));
        }

        List<ContaReceber> recebidas = contas.stream()
            .filter(c -> c.getDataRecebimento() != null).collect(Collectors.toList());
        if (!recebidas.isEmpty()) {
            long totalDias = recebidas.stream()
                .mapToLong(c -> ChronoUnit.DAYS.between(c.getDataEmissao(), c.getDataRecebimento()))
                .sum();
            resumo.setPrazoMedioRecebimento(BigDecimal.valueOf(totalDias)
                .divide(BigDecimal.valueOf(recebidas.size()), 2, RoundingMode.HALF_UP));
        }
        return resumo;
    }

    private List<ReceitasPorClienteDTO.FornecedorDetalheDTO> calcularClientesReceitas(List<ContaReceber> contas) {
        Map<Long, List<ContaReceber>> porCliente = contas.stream()
            .filter(c -> c.getFornecedor() != null)
            .collect(Collectors.groupingBy(c -> c.getFornecedor().getId()));
        BigDecimal totalGeral = contas.stream().map(ContaReceber::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ReceitasPorClienteDTO.FornecedorDetalheDTO> clientes = porCliente.entrySet().stream().map(e -> {
            List<ContaReceber> cc = e.getValue();
            BigDecimal vc = cc.stream().map(ContaReceber::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

            ReceitasPorClienteDTO.FornecedorDetalheDTO dto = new ReceitasPorClienteDTO.FornecedorDetalheDTO();
            dto.setFornecedorId(e.getKey());
            dto.setFornecedorNome(cc.get(0).getFornecedor().getRazaoSocial());
            dto.setValorTotal(vc);
            dto.setQuantidadeFretes(cc.size());
            if (totalGeral.compareTo(BigDecimal.ZERO) > 0) {
                dto.setPercentualDoTotal(vc.divide(totalGeral, 4, RoundingMode.HALF_UP)
                                          .multiply(BigDecimal.valueOf(100)));
            }
            dto.setTicketMedio(vc.divide(BigDecimal.valueOf(cc.size()), 2, RoundingMode.HALF_UP));

            List<ContaReceber> recebidas = cc.stream()
                .filter(c -> c.getDataRecebimento() != null).collect(Collectors.toList());
            BigDecimal prazoMedio = BigDecimal.ZERO;
            if (!recebidas.isEmpty()) {
                long totalDias = recebidas.stream()
                    .mapToLong(c -> ChronoUnit.DAYS.between(c.getDataEmissao(), c.getDataRecebimento()))
                    .sum();
                prazoMedio = BigDecimal.valueOf(totalDias)
                    .divide(BigDecimal.valueOf(recebidas.size()), 2, RoundingMode.HALF_UP);
                dto.setPrazoMedioRecebimento(prazoMedio);
            }

            long qtdVencidas = cc.stream().filter(ContaReceber::isVencida).count();
            BigDecimal taxaInadimplencia = BigDecimal.ZERO;
            if (!cc.isEmpty()) {
                taxaInadimplencia = BigDecimal.valueOf(qtdVencidas)
                    .divide(BigDecimal.valueOf(cc.size()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
                dto.setTaxaInadimplencia(taxaInadimplencia);
            }

            BigDecimal pctFaturamento = totalGeral.compareTo(BigDecimal.ZERO) > 0
                ? vc.divide(totalGeral, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
            dto.setScoreCliente(calcularScoreCliente(pctFaturamento, taxaInadimplencia, prazoMedio));
            return dto;
        }).sorted((a, b) -> b.getValorTotal().compareTo(a.getValorTotal())).collect(Collectors.toList());

        for (int i = 0; i < clientes.size(); i++) clientes.get(i).setPosicaoRanking(i + 1);
        return clientes;
    }

    private List<ReceitasPorClienteDTO.Top10ReceitaDTO> calcularTop10Receitas(List<ContaReceber> contas) {
        return contas.stream()
            .sorted((a, b) -> b.getValorTotal().compareTo(a.getValorTotal()))
            .limit(10)
            .map(c -> {
                ReceitasPorClienteDTO.Top10ReceitaDTO dto = new ReceitasPorClienteDTO.Top10ReceitaDTO();
                dto.setContaId(c.getId());
                dto.setDescricao(c.getDescricao());
                dto.setFornecedorNome(c.getFornecedor() != null ? c.getFornecedor().getRazaoSocial() : null);
                dto.setValor(c.getValorTotal());
                dto.setDataRecebimento(c.getDataRecebimento());
                dto.setOrigemFrete(c.getOrigemFrete());
                dto.setDestinoFrete(c.getDestinoFrete());
                return dto;
            }).collect(Collectors.toList());
    }

    private List<ReceitasPorClienteDTO.EvolucaoMensalDTO> calcularEvolucaoMensalReceitas(LocalDate dataFim, Long clienteId) {
        List<ReceitasPorClienteDTO.EvolucaoMensalDTO> evolucao = new ArrayList<>();
        LocalDate mesInicio = dataFim.minusMonths(11).withDayOfMonth(1);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMMM/yyyy", new Locale("pt", "BR"));

        for (int i = 0; i < 12; i++) {
            LocalDate ini = mesInicio.plusMonths(i);
            LocalDate fim = ini.withDayOfMonth(ini.lengthOfMonth());
            if (ini.isAfter(dataFim)) break;

            List<ContaReceber> contasMes = contaReceberRepository
                .findByDataVencimentoBetweenAndStatusAndClienteId(ini, fim, clienteId, StatusConta.RECEBIDO, Pageable.unpaged())
                .getContent();

            Map<String, BigDecimal> porCliente = contasMes.stream()
                .filter(c -> c.getCliente() != null)
                .collect(Collectors.groupingBy(c -> c.getCliente().getNome(),
                    Collectors.reducing(BigDecimal.ZERO, ContaReceber::getValorTotal, BigDecimal::add)));

            List<ReceitasPorClienteDTO.ValorClienteDTO> clientes = porCliente.entrySet().stream()
                .map(e -> new ReceitasPorClienteDTO.ValorClienteDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

            ReceitasPorClienteDTO.EvolucaoMensalDTO evo = new ReceitasPorClienteDTO.EvolucaoMensalDTO();
            evo.setMes(ini.format(fmt));
            evo.setClientes(clientes);
            evolucao.add(evo);
        }
        return evolucao;
    }

    private ReceitasPorClienteDTO.ComparativoDTO calcularComparativoReceitas(LocalDate dataInicio,
                                                                              LocalDate dataFim,
                                                                              Long clienteId,
                                                                              ReceitasPorClienteDTO analiseAtual) {
        ReceitasPorClienteDTO.ComparativoDTO comp = new ReceitasPorClienteDTO.ComparativoDTO();
        long dias = ChronoUnit.DAYS.between(dataInicio, dataFim);
        LocalDate iniAnt = dataInicio.minusDays(dias + 1);
        LocalDate fimAnt = dataInicio.minusDays(1);

        ReceitasPorClienteDTO.PeriodoDTO periodoAnt = new ReceitasPorClienteDTO.PeriodoDTO();
        periodoAnt.setDataInicio(iniAnt);
        periodoAnt.setDataFim(fimAnt);
        periodoAnt.setDescricao(gerarDescricaoPeriodo(iniAnt, fimAnt));
        comp.setPeriodoAnterior(periodoAnt);

        List<ContaReceber> contasAnt = contaReceberRepository
            .findByDataVencimentoBetweenAndStatusAndClienteId(iniAnt, fimAnt, clienteId, StatusConta.RECEBIDO, Pageable.unpaged())
            .getContent();
        BigDecimal totalAnt = contasAnt.stream().map(ContaReceber::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        comp.setTotalReceitasAnterior(totalAnt);
        comp.setVariacao(calcularVariacaoPercentual(totalAnt, analiseAtual.getResumoGeral().getTotalReceitas()));

        Map<String, BigDecimal> clienteAnt = contasAnt.stream()
            .filter(c -> c.getCliente() != null)
            .collect(Collectors.groupingBy(c -> c.getCliente().getNome(),
                Collectors.reducing(BigDecimal.ZERO, ContaReceber::getValorTotal, BigDecimal::add)));

        List<ReceitasPorClienteDTO.VariacaoClienteDTO> variacoes = analiseAtual.getFornecedores().stream()
            .map(cli -> {
                BigDecimal valorAnt = clienteAnt.getOrDefault(cli.getFornecedorNome(), BigDecimal.ZERO);
                BigDecimal variacao = calcularVariacaoPercentual(valorAnt, cli.getValorTotal());
                ReceitasPorClienteDTO.VariacaoClienteDTO v = new ReceitasPorClienteDTO.VariacaoClienteDTO();
                v.setClienteNome(cli.getFornecedorNome());
                v.setValorAtual(cli.getValorTotal());
                v.setValorAnterior(valorAnt);
                v.setVariacao(variacao);
                v.setSituacao(variacao.compareTo(BigDecimal.ZERO) >= 0 ? "CRESCIMENTO" : "QUEDA");

                cli.setTendencia(variacao.compareTo(BigDecimal.valueOf(5)) > 0 ? "CRESCIMENTO"
                    : variacao.compareTo(BigDecimal.valueOf(-5)) < 0 ? "QUEDA" : "ESTÁVEL");
                return v;
            }).collect(Collectors.toList());

        comp.setVariacoesPorCliente(variacoes);
        return comp;
    }

    private ReceitasPorClienteDTO.InsightsDTO gerarInsightsReceitas(ReceitasPorClienteDTO analise) {
        ReceitasPorClienteDTO.InsightsDTO insights = new ReceitasPorClienteDTO.InsightsDTO();
        List<String> recomendacoes = new ArrayList<>();

        if (analise.getFornecedores() != null && !analise.getFornecedores().isEmpty()) {
            ReceitasPorClienteDTO.FornecedorDetalheDTO principal = analise.getFornecedores().get(0);
            insights.setPrincipalCliente(principal.getFornecedorNome());
            insights.setPercentualPrincipalCliente(principal.getPercentualDoTotal());
            recomendacoes.add(String.format("Cliente %s é o principal: %.2f%% do faturamento",
                                            principal.getFornecedorNome(), principal.getPercentualDoTotal()));
        }

        if (analise.getComparativo() != null && analise.getComparativo().getVariacoesPorCliente() != null) {
            analise.getComparativo().getVariacoesPorCliente().stream()
                .filter(v -> "CRESCIMENTO".equals(v.getSituacao())).findFirst().ifPresent(v -> {
                    insights.setClienteComMaiorCrescimento(v.getClienteNome());
                    insights.setPercentualCrescimento(v.getVariacao());
                });
        }

        insights.setRecomendacoes(recomendacoes);
        return insights;
    }

    private String calcularScoreCliente(BigDecimal percentualFaturamento,
                                        BigDecimal taxaInadimplencia,
                                        BigDecimal prazoRecebimento) {
        int pontos = 0;
        if (percentualFaturamento.compareTo(BigDecimal.valueOf(10)) >= 0) pontos += 40;
        else if (percentualFaturamento.compareTo(BigDecimal.valueOf(5)) >= 0) pontos += 30;
        else if (percentualFaturamento.compareTo(BigDecimal.valueOf(2)) >= 0) pontos += 20;
        else pontos += 10;

        if (taxaInadimplencia.compareTo(BigDecimal.valueOf(5)) < 0) pontos += 40;
        else if (taxaInadimplencia.compareTo(BigDecimal.valueOf(10)) < 0) pontos += 30;
        else if (taxaInadimplencia.compareTo(BigDecimal.valueOf(20)) < 0) pontos += 20;
        else pontos += 10;

        if (prazoRecebimento.compareTo(BigDecimal.valueOf(15)) < 0) pontos += 20;
        else if (prazoRecebimento.compareTo(BigDecimal.valueOf(30)) < 0) pontos += 15;
        else if (prazoRecebimento.compareTo(BigDecimal.valueOf(45)) < 0) pontos += 10;
        else pontos += 5;

        if (pontos >= 85) return "A";
        if (pontos >= 70) return "B";
        if (pontos >= 50) return "C";
        return "D";
    }

    // =========================================================================
    // 7. Inadimplência Detalhada
    // =========================================================================

    @Transactional(readOnly = true)
    public InadimplenciaDetalhadaDTO gerarInadimplenciaDetalhada(
            InadimplenciaDetalhadaDTO.TipoAnalise tipo, Long entidadeId) {
        log.info("Gerando Relatório de Inadimplência Detalhada - Tipo: {}", tipo);

        LocalDate hoje = LocalDate.now();
        InadimplenciaDetalhadaDTO relatorio = new InadimplenciaDetalhadaDTO();
        relatorio.setDataBase(hoje);
        relatorio.setTipo(tipo);

        List<ContaReceber> contasReceberVencidas = new ArrayList<>();
        List<ContaPagar> contasPagarVencidas = new ArrayList<>();

        if (tipo == InadimplenciaDetalhadaDTO.TipoAnalise.RECEBER ||
            tipo == InadimplenciaDetalhadaDTO.TipoAnalise.AMBOS) {
            contasReceberVencidas = contaReceberRepository
                .findVencidas(hoje, Pageable.unpaged()).getContent();
        }

        if (tipo == InadimplenciaDetalhadaDTO.TipoAnalise.PAGAR ||
            tipo == InadimplenciaDetalhadaDTO.TipoAnalise.AMBOS) {
            contasPagarVencidas = contaPagarRepository
                .findVencidas(hoje, Pageable.unpaged()).getContent();
        }

        relatorio.setResumo(calcularResumoInadimplencia(contasReceberVencidas, contasPagarVencidas, hoje));
        relatorio.setAging(calcularAgingInadimplencia(contasReceberVencidas, contasPagarVencidas, hoje));
        relatorio.setInadimplentes(identificarInadimplentes(contasReceberVencidas, contasPagarVencidas, hoje));
        relatorio.setContasVencidas(listarContasVencidas(contasReceberVencidas, contasPagarVencidas, hoje));
        relatorio.setImpacto(calcularImpactoFinanceiro(contasReceberVencidas, contasPagarVencidas));
        relatorio.setAcoesRecomendadas(gerarAcoesRecomendadas(relatorio.getContasVencidas()));
        relatorio.setHistorico(calcularHistoricoInadimplencia(tipo, hoje));

        return relatorio;
    }

    private InadimplenciaDetalhadaDTO.ResumoInadimplenciaDTO calcularResumoInadimplencia(
            List<ContaReceber> receber, List<ContaPagar> pagar, LocalDate hoje) {
        InadimplenciaDetalhadaDTO.ResumoInadimplenciaDTO resumo = new InadimplenciaDetalhadaDTO.ResumoInadimplenciaDTO();
        int total = receber.size() + pagar.size();
        BigDecimal valorTotal = BigDecimal.ZERO;
        long totalDiasAtraso = 0;
        long maiorAtraso = 0;

        for (ContaReceber c : receber) {
            valorTotal = valorTotal.add(c.calcularSaldo());
            long dias = ChronoUnit.DAYS.between(c.getDataVencimento(), hoje);
            totalDiasAtraso += dias;
            if (dias > maiorAtraso) maiorAtraso = dias;
        }
        for (ContaPagar c : pagar) {
            valorTotal = valorTotal.add(c.calcularSaldo());
            long dias = ChronoUnit.DAYS.between(c.getDataVencimento(), hoje);
            totalDiasAtraso += dias;
            if (dias > maiorAtraso) maiorAtraso = dias;
        }

        resumo.setTotalContasVencidas(total);
        resumo.setValorTotalVencido(valorTotal);
        resumo.setDiasMediaAtraso(total > 0 ? (int) (totalDiasAtraso / total) : 0);
        resumo.setMaiorAtraso(BigDecimal.valueOf(maiorAtraso));
        resumo.setQuantidadeInadimplentes(
            (int) (receber.stream().filter(c -> c.getCliente() != null)
                       .map(c -> c.getCliente().getId()).distinct().count() +
                   pagar.stream().filter(c -> c.getFornecedor() != null)
                       .map(c -> c.getFornecedor().getId()).distinct().count()));
        return resumo;
    }

    private InadimplenciaDetalhadaDTO.AgingDetalhadoDTO calcularAgingInadimplencia(
            List<ContaReceber> receber, List<ContaPagar> pagar, LocalDate hoje) {
        InadimplenciaDetalhadaDTO.AgingDetalhadoDTO aging = new InadimplenciaDetalhadaDTO.AgingDetalhadoDTO();

        BigDecimal totalGeral = receber.stream().map(ContaReceber::calcularSaldo)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .add(pagar.stream().map(ContaPagar::calcularSaldo).reduce(BigDecimal.ZERO, BigDecimal::add));

        aging.setAte30Dias(criarFaixaAgingInadimplencia(receber, pagar, hoje, 1, 30, "Até 30 dias", "BAIXO", totalGeral));
        aging.setDe31a60Dias(criarFaixaAgingInadimplencia(receber, pagar, hoje, 31, 60, "31 a 60 dias", "MÉDIO", totalGeral));
        aging.setDe61a90Dias(criarFaixaAgingInadimplencia(receber, pagar, hoje, 61, 90, "61 a 90 dias", "ALTO", totalGeral));
        aging.setDe91a180Dias(criarFaixaAgingInadimplencia(receber, pagar, hoje, 91, 180, "91 a 180 dias", "ALTO", totalGeral));
        aging.setAcima180Dias(criarFaixaAgingInadimplencia(receber, pagar, hoje, 181, Integer.MAX_VALUE, "Acima de 180 dias", "CRÍTICO", totalGeral));

        return aging;
    }

    private InadimplenciaDetalhadaDTO.FaixaAgingDTO criarFaixaAgingInadimplencia(
            List<ContaReceber> receber, List<ContaPagar> pagar, LocalDate hoje,
            int diasMin, int diasMax, String descricao, String nivelRisco, BigDecimal totalGeral) {
        InadimplenciaDetalhadaDTO.FaixaAgingDTO faixa = new InadimplenciaDetalhadaDTO.FaixaAgingDTO();
        faixa.setDescricao(descricao);
        faixa.setNivelRisco(nivelRisco);

        BigDecimal valor = BigDecimal.ZERO;
        int qtd = 0;

        for (ContaReceber c : receber) {
            long dias = ChronoUnit.DAYS.between(c.getDataVencimento(), hoje);
            if (dias >= diasMin && dias <= diasMax) { valor = valor.add(c.calcularSaldo()); qtd++; }
        }
        for (ContaPagar c : pagar) {
            long dias = ChronoUnit.DAYS.between(c.getDataVencimento(), hoje);
            if (dias >= diasMin && dias <= diasMax) { valor = valor.add(c.calcularSaldo()); qtd++; }
        }

        faixa.setQuantidade(qtd);
        faixa.setValor(valor);
        if (totalGeral.compareTo(BigDecimal.ZERO) > 0) {
            faixa.setPercentual(valor.divide(totalGeral, 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)));
        }
        return faixa;
    }

    private List<InadimplenciaDetalhadaDTO.InadimplenteDTO> identificarInadimplentes(
            List<ContaReceber> receber, List<ContaPagar> pagar, LocalDate hoje) {
        List<InadimplenciaDetalhadaDTO.InadimplenteDTO> inadimplentes = new ArrayList<>();

        Map<Long, List<ContaReceber>> porCliente = receber.stream()
            .filter(c -> c.getCliente() != null)
            .collect(Collectors.groupingBy(c -> c.getCliente().getId()));

        porCliente.forEach((clienteId, contas) -> {
            InadimplenciaDetalhadaDTO.InadimplenteDTO dto = new InadimplenciaDetalhadaDTO.InadimplenteDTO();
            dto.setEntidadeId(clienteId);
            dto.setNome(contas.get(0).getCliente().getNome());
            dto.setTipo("CLIENTE");
            dto.setQuantidadeContasVencidas(contas.size());
            BigDecimal valorTotal = contas.stream().map(ContaReceber::calcularSaldo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setValorTotalVencido(valorTotal);
            long maxAtraso = contas.stream()
                .mapToLong(c -> ChronoUnit.DAYS.between(c.getDataVencimento(), hoje)).max().orElse(0);
            long mediaAtraso = (long) contas.stream()
                .mapToLong(c -> ChronoUnit.DAYS.between(c.getDataVencimento(), hoje)).average().orElse(0);
            dto.setMaiorAtraso((int) maxAtraso);
            dto.setDiasMediaAtraso((int) mediaAtraso);
            if (maxAtraso > 90) dto.setScoreRisco("E");
            else if (maxAtraso > 60) dto.setScoreRisco("D");
            else if (maxAtraso > 30) dto.setScoreRisco("C");
            else dto.setScoreRisco("B");
            inadimplentes.add(dto);
        });

        return inadimplentes.stream()
            .sorted((a, b) -> b.getValorTotalVencido().compareTo(a.getValorTotalVencido()))
            .collect(Collectors.toList());
    }

    private List<InadimplenciaDetalhadaDTO.ContaVencidaDTO> listarContasVencidas(
            List<ContaReceber> receber, List<ContaPagar> pagar, LocalDate hoje) {
        List<InadimplenciaDetalhadaDTO.ContaVencidaDTO> lista = new ArrayList<>();

        for (ContaReceber c : receber) {
            InadimplenciaDetalhadaDTO.ContaVencidaDTO dto = new InadimplenciaDetalhadaDTO.ContaVencidaDTO();
            dto.setContaId(c.getId());
            dto.setTipo("RECEBER");
            dto.setDescricao(c.getDescricao());
            dto.setEntidadeNome(c.getCliente() != null ? c.getCliente().getNome() : null);
            dto.setValorOriginal(c.getValorOriginal());
            dto.setValorPagoRecebido(c.getValorRecebido());
            dto.setSaldo(c.calcularSaldo());
            dto.setDataEmissao(c.getDataEmissao());
            dto.setDataVencimento(c.getDataVencimento());
            int diasAtraso = (int) ChronoUnit.DAYS.between(c.getDataVencimento(), hoje);
            dto.setDiasAtraso(diasAtraso);
            dto.setPrioridade(diasAtraso > 90 ? "ALTA" : diasAtraso > 30 ? "MÉDIA" : "BAIXA");
            lista.add(dto);
        }

        for (ContaPagar c : pagar) {
            InadimplenciaDetalhadaDTO.ContaVencidaDTO dto = new InadimplenciaDetalhadaDTO.ContaVencidaDTO();
            dto.setContaId(c.getId());
            dto.setTipo("PAGAR");
            dto.setDescricao(c.getDescricao());
            dto.setEntidadeNome(c.getFornecedor() != null ? c.getFornecedor().getRazaoSocial() : null);
            dto.setValorOriginal(c.getValorOriginal());
            dto.setValorPagoRecebido(c.getValorPago());
            dto.setSaldo(c.calcularSaldo());
            dto.setDataEmissao(c.getDataEmissao());
            dto.setDataVencimento(c.getDataVencimento());
            int diasAtraso = (int) ChronoUnit.DAYS.between(c.getDataVencimento(), hoje);
            dto.setDiasAtraso(diasAtraso);
            dto.setPrioridade(diasAtraso > 90 ? "ALTA" : diasAtraso > 30 ? "MÉDIA" : "BAIXA");
            lista.add(dto);
        }

        return lista.stream().sorted((a, b) -> b.getDiasAtraso().compareTo(a.getDiasAtraso()))
                    .collect(Collectors.toList());
    }

    private InadimplenciaDetalhadaDTO.ImpactoFinanceiroDTO calcularImpactoFinanceiro(
            List<ContaReceber> receber, List<ContaPagar> pagar) {
        InadimplenciaDetalhadaDTO.ImpactoFinanceiroDTO impacto = new InadimplenciaDetalhadaDTO.ImpactoFinanceiroDTO();
        BigDecimal valorBloqueado = receber.stream().map(ContaReceber::calcularSaldo)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal valorPagar = pagar.stream().map(ContaPagar::calcularSaldo)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        impacto.setValorBloqueado(valorBloqueado);
        impacto.setImpactoFluxoCaixa(valorBloqueado.subtract(valorPagar));
        impacto.setJurosAcumulados(BigDecimal.ZERO);
        impacto.setMultasAcumuladas(BigDecimal.ZERO);
        impacto.setPerdaEstimada(BigDecimal.ZERO);

        int totalContas = receber.size() + pagar.size();
        if (totalContas > 10) impacto.setSituacao("CRÍTICA");
        else if (totalContas > 3) impacto.setSituacao("ATENÇÃO");
        else impacto.setSituacao("CONTROLADA");

        return impacto;
    }

    private List<InadimplenciaDetalhadaDTO.AcaoRecomendadaDTO> gerarAcoesRecomendadas(
            List<InadimplenciaDetalhadaDTO.ContaVencidaDTO> contasVencidas) {
        List<InadimplenciaDetalhadaDTO.AcaoRecomendadaDTO> acoes = new ArrayList<>();

        contasVencidas.stream().filter(c -> c.getDiasAtraso() > 90).forEach(c -> {
            InadimplenciaDetalhadaDTO.AcaoRecomendadaDTO acao = new InadimplenciaDetalhadaDTO.AcaoRecomendadaDTO();
            acao.setPrioridade("URGENTE");
            acao.setAcao("Cobrança judicial/negociação urgente");
            acao.setEntidadeNome(c.getEntidadeNome());
            acao.setValorEnvolvido(c.getSaldo());
            acao.setPrazo("Imediato");
            acoes.add(acao);
        });

        contasVencidas.stream()
            .filter(c -> c.getSaldo() != null && c.getSaldo().compareTo(BigDecimal.valueOf(10000)) > 0
                         && c.getDiasAtraso() <= 90 && c.getDiasAtraso() > 30)
            .forEach(c -> {
                InadimplenciaDetalhadaDTO.AcaoRecomendadaDTO acao = new InadimplenciaDetalhadaDTO.AcaoRecomendadaDTO();
                acao.setPrioridade("ALTA");
                acao.setAcao("Contato telefônico + email de cobrança");
                acao.setEntidadeNome(c.getEntidadeNome());
                acao.setValorEnvolvido(c.getSaldo());
                acao.setPrazo("Esta semana");
                acoes.add(acao);
            });

        Map<String, Integer> ordem = Map.of("URGENTE", 1, "ALTA", 2, "MÉDIA", 3, "BAIXA", 4);
        return acoes.stream()
            .sorted((a, b) -> {
                int cmp = ordem.getOrDefault(a.getPrioridade(), 4)
                              .compareTo(ordem.getOrDefault(b.getPrioridade(), 4));
                if (cmp != 0) return cmp;
                return b.getValorEnvolvido().compareTo(a.getValorEnvolvido());
            }).collect(Collectors.toList());
    }

    private InadimplenciaDetalhadaDTO.HistoricoDTO calcularHistoricoInadimplencia(
            InadimplenciaDetalhadaDTO.TipoAnalise tipo, LocalDate hoje) {
        InadimplenciaDetalhadaDTO.HistoricoDTO historico = new InadimplenciaDetalhadaDTO.HistoricoDTO();
        List<InadimplenciaDetalhadaDTO.EvolucaoMensalDTO> evolucao = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMMM/yyyy", new Locale("pt", "BR"));

        for (int i = 11; i >= 0; i--) {
            LocalDate refDate = hoje.minusMonths(i);
            InadimplenciaDetalhadaDTO.EvolucaoMensalDTO evo = new InadimplenciaDetalhadaDTO.EvolucaoMensalDTO();
            evo.setMes(refDate.format(fmt));
            evo.setQuantidadeVencidas(0);
            evo.setValorVencido(BigDecimal.ZERO);
            evo.setTaxaInadimplencia(BigDecimal.ZERO);
            evolucao.add(evo);
        }

        historico.setEvolucao(evolucao);
        historico.setTendencia("ESTÁVEL");
        historico.setVariacaoMesAnterior(BigDecimal.ZERO);
        return historico;
    }

    // =========================================================================
    // Métodos utilitários
    // =========================================================================

    private BigDecimal calcularVariacaoPercentual(BigDecimal valorAnterior, BigDecimal valorAtual) {
        if (valorAnterior == null || valorAnterior.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return valorAtual.subtract(valorAnterior)
                         .divide(valorAnterior, 4, RoundingMode.HALF_UP)
                         .multiply(BigDecimal.valueOf(100));
    }

    private String gerarDescricaoPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dataInicio.format(fmt) + " a " + dataFim.format(fmt);
    }

    private List<ContaPagar> filtrarContaPagar(List<ContaPagar> contas,
                                     Long idFornecedor,
                                     Long idCategoria) {

        Predicate<ContaPagar> porFornecedor = c -> idFornecedor == null ||
                (c.getFornecedor() != null && c.getFornecedor().getId().equals(idFornecedor));
        Predicate<ContaPagar> porCategoria = c ->  idCategoria == null ||
                (c.getCategoria() != null && c.getCategoria().getId().equals(idCategoria));

        return contas.stream()
                .filter(porFornecedor.and(porCategoria))
                .toList();

    }


    private List<ContaReceber> filtrarContaReceber(List<ContaReceber> contas,
                                     Long idFornecedor,
                                     Long idCategoria) {

        Predicate<ContaReceber> porFornecedor = c -> idFornecedor == null ||
                (c.getFornecedor() != null && c.getFornecedor().getId().equals(idFornecedor));
        Predicate<ContaReceber> porCategoria = c ->  idCategoria == null ||
                (c.getCategoria() != null && c.getCategoria().getId().equals(idCategoria));

        return contas.stream()
                .filter(porFornecedor.and(porCategoria))
                .toList();

    }


}
