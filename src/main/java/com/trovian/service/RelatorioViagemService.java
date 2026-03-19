package com.trovian.service;

import com.trovian.dto.relatorio.*;
import com.trovian.repository.ViagemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelatorioViagemService {

    private final ViagemRepository viagemRepository;

    // =========================================================================
    // 1. Consolidado de Viagens
    // =========================================================================

    @Transactional(readOnly = true)
    public ViagemConsolidadoDTO gerarConsolidado(LocalDate dataInicio, LocalDate dataFim,
                                                  Long clienteId, String statusViagem) {

        log.info("Gerando Consolidado de Viagens: {} a {} - Cliente: {}", dataInicio, dataFim, clienteId);

        ViagemConsolidadoDTO dto = new ViagemConsolidadoDTO();
        dto.setPeriodo(criarPeriodoConsolidado(dataInicio, dataFim));
        dto.setFiltros(criarFiltrosConsolidado(clienteId, statusViagem));

        // Resumo geral
        List<Object[]> resumoRows = viagemRepository.getResumoGeralPeriodo(clienteId, dataInicio, dataFim);
        dto.setResumoGeral(montarResumoGeralConsolidado(resumoRows));

        // Custos
        List<Object[]> custosRows = viagemRepository.getCustosOperacionaisPeriodo(clienteId, dataInicio, dataFim);
        dto.setCustosResumo(montarCustosResumo(custosRows));

        // Evolução mensal
        List<Object[]> tendenciasRows = viagemRepository.getTendenciasMensaisPeriodo(clienteId, dataInicio, dataFim);
        dto.setEvolucaoMensal(montarEvolucaoMensalConsolidado(tendenciasRows));

        // Comparativo com período anterior
        dto.setComparativo(calcularComparativoConsolidado(dataInicio, dataFim, clienteId, dto.getResumoGeral()));

        log.info("Consolidado gerado - Total viagens: {}", dto.getResumoGeral().getTotalViagens());
        return dto;
    }

    private ViagemConsolidadoDTO.PeriodoDTO criarPeriodoConsolidado(LocalDate dataInicio, LocalDate dataFim) {
        ViagemConsolidadoDTO.PeriodoDTO periodo = new ViagemConsolidadoDTO.PeriodoDTO();
        periodo.setDataInicio(dataInicio);
        periodo.setDataFim(dataFim);
        periodo.setTotalDias((int) ChronoUnit.DAYS.between(dataInicio, dataFim) + 1);
        periodo.setDescricao(gerarDescricaoPeriodo(dataInicio, dataFim));
        return periodo;
    }

    private ViagemConsolidadoDTO.FiltrosDTO criarFiltrosConsolidado(Long clienteId, String statusViagem) {
        ViagemConsolidadoDTO.FiltrosDTO filtros = new ViagemConsolidadoDTO.FiltrosDTO();
        filtros.setClienteId(clienteId);
        filtros.setStatusViagem(statusViagem);
        return filtros;
    }

    private ViagemConsolidadoDTO.ResumoGeralDTO montarResumoGeralConsolidado(List<Object[]> rows) {
        ViagemConsolidadoDTO.ResumoGeralDTO resumo = new ViagemConsolidadoDTO.ResumoGeralDTO();
        if (rows != null && !rows.isEmpty()) {
            Object[] r = rows.get(0);
            resumo.setTotalViagens(toLong(r[0]));
            resumo.setViagensAbertas(toLong(r[1]));
            resumo.setViagensAnalise(toLong(r[2]));
            resumo.setViagensFechadas(toLong(r[3]));
            resumo.setReceitaBruta(toBigDecimal(r[4]));
            resumo.setCustoTotal(toBigDecimal(r[5]));
            resumo.setLucroLiquido(toBigDecimal(r[6]));
            resumo.setMargemMedia(toBigDecimal(r[7]));
            resumo.setKmTotalPercorrido(toBigDecimal(r[8]));
        }
        return resumo;
    }

    private ViagemConsolidadoDTO.CustosResumoDTO montarCustosResumo(List<Object[]> rows) {
        ViagemConsolidadoDTO.CustosResumoDTO custos = new ViagemConsolidadoDTO.CustosResumoDTO();
        if (rows != null && !rows.isEmpty()) {
            Object[] r = rows.get(0);
            BigDecimal combustivel = toBigDecimal(r[0]);
            BigDecimal pedagios = toBigDecimal(r[1]);
            BigDecimal comissoes = toBigDecimal(r[2]);
            custos.setCombustivel(combustivel);
            custos.setPedagios(pedagios);
            custos.setComissoes(comissoes);
            custos.setTotalCustos(combustivel.add(pedagios).add(comissoes));
        }
        return custos;
    }

    private List<ViagemConsolidadoDTO.EvolucaoMensalDTO> montarEvolucaoMensalConsolidado(List<Object[]> rows) {
        if (rows == null) return Collections.emptyList();
        return rows.stream().map(r -> {
            ViagemConsolidadoDTO.EvolucaoMensalDTO evo = new ViagemConsolidadoDTO.EvolucaoMensalDTO();
            int ano = toInt(r[0]);
            int mes = toInt(r[1]);
            evo.setMes(formatarMesAno(mes, ano));
            evo.setReceita(toBigDecimal(r[2]));
            evo.setCustos(toBigDecimal(r[3]));
            evo.setLucro(toBigDecimal(r[4]));
            evo.setTotalViagens(toLong(r[5]));
            return evo;
        }).collect(Collectors.toList());
    }

    private ViagemConsolidadoDTO.ComparativoDTO calcularComparativoConsolidado(
            LocalDate dataInicio, LocalDate dataFim, Long clienteId,
            ViagemConsolidadoDTO.ResumoGeralDTO resumoAtual) {

        long dias = ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
        LocalDate inicioAnterior = dataInicio.minusDays(dias);
        LocalDate fimAnterior = dataInicio.minusDays(1);

        List<Object[]> rowsAnterior = viagemRepository.getResumoGeralPeriodo(clienteId, inicioAnterior, fimAnterior);

        ViagemConsolidadoDTO.ComparativoDTO comp = new ViagemConsolidadoDTO.ComparativoDTO();
        ViagemConsolidadoDTO.PeriodoDTO periodoAnt = new ViagemConsolidadoDTO.PeriodoDTO();
        periodoAnt.setDataInicio(inicioAnterior);
        periodoAnt.setDataFim(fimAnterior);
        periodoAnt.setDescricao(gerarDescricaoPeriodo(inicioAnterior, fimAnterior));
        periodoAnt.setTotalDias((int) dias);
        comp.setPeriodoAnterior(periodoAnt);

        if (rowsAnterior != null && !rowsAnterior.isEmpty()) {
            Object[] ra = rowsAnterior.get(0);
            BigDecimal receitaAnt = toBigDecimal(ra[4]);
            BigDecimal custoAnt = toBigDecimal(ra[5]);
            BigDecimal lucroAnt = toBigDecimal(ra[6]);

            comp.setReceitaAnterior(receitaAnt);
            comp.setCustoAnterior(custoAnt);
            comp.setLucroAnterior(lucroAnt);
            comp.setVariacaoReceita(calcularVariacao(resumoAtual.getReceitaBruta(), receitaAnt));
            comp.setVariacaoCusto(calcularVariacao(resumoAtual.getCustoTotal(), custoAnt));
            comp.setVariacaoLucro(calcularVariacao(resumoAtual.getLucroLiquido(), lucroAnt));

            BigDecimal varReceita = comp.getVariacaoReceita();
            comp.setTendencia(varReceita.compareTo(BigDecimal.ZERO) > 0 ? "CRESCIMENTO" :
                              varReceita.compareTo(BigDecimal.ZERO) < 0 ? "QUEDA" : "ESTAVEL");
        } else {
            comp.setReceitaAnterior(BigDecimal.ZERO);
            comp.setCustoAnterior(BigDecimal.ZERO);
            comp.setLucroAnterior(BigDecimal.ZERO);
            comp.setVariacaoReceita(BigDecimal.ZERO);
            comp.setVariacaoCusto(BigDecimal.ZERO);
            comp.setVariacaoLucro(BigDecimal.ZERO);
            comp.setTendencia("SEM_DADOS_ANTERIORES");
        }

        return comp;
    }

    // =========================================================================
    // 2. Rentabilidade por Veículo
    // =========================================================================

    @Transactional(readOnly = true)
    public RentabilidadeVeiculoDTO gerarRentabilidadeVeiculo(LocalDate dataInicio, LocalDate dataFim,
                                                              Long clienteId, Long veiculoId) {

        log.info("Gerando Rentabilidade por Veículo: {} a {}", dataInicio, dataFim);

        RentabilidadeVeiculoDTO dto = new RentabilidadeVeiculoDTO();
        dto.setPeriodo(criarPeriodoRentabilidade(dataInicio, dataFim));

        List<Object[]> rows = viagemRepository.getEficienciaVeiculosPeriodo(clienteId, dataInicio, dataFim);
        List<RentabilidadeVeiculoDTO.VeiculoDetalheDTO> veiculos = new ArrayList<>();

        BigDecimal receitaTotal = BigDecimal.ZERO;
        BigDecimal lucroTotal = BigDecimal.ZERO;
        int ranking = 1;

        for (Object[] r : rows) {
            Long vid = toLong(r[0]);
            if (veiculoId != null && !veiculoId.equals(vid)) continue;

            RentabilidadeVeiculoDTO.VeiculoDetalheDTO v = new RentabilidadeVeiculoDTO.VeiculoDetalheDTO();
            v.setVeiculoId(vid);
            v.setPlaca(toString(r[1]));
            v.setTotalViagens(toLong(r[2]));
            v.setReceitaBruta(toBigDecimal(r[3]));
            v.setCustoTotal(toBigDecimal(r[4]));
            v.setLucroLiquido(toBigDecimal(r[5]));
            v.setMargemPercentual(toBigDecimal(r[6]));
            v.setKmRodados(toBigDecimal(r[7]));
            v.setConsumoMedioKmL(toBigDecimal(r[8]));
            v.setTicketMedio(v.getTotalViagens() > 0 ?
                v.getReceitaBruta().divide(BigDecimal.valueOf(v.getTotalViagens()), 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO);
            v.setPosicaoRanking(ranking++);
            veiculos.add(v);

            receitaTotal = receitaTotal.add(v.getReceitaBruta());
            lucroTotal = lucroTotal.add(v.getLucroLiquido());
        }

        dto.setVeiculos(veiculos);

        // Resumo geral
        RentabilidadeVeiculoDTO.ResumoGeralDTO resumo = new RentabilidadeVeiculoDTO.ResumoGeralDTO();
        resumo.setTotalVeiculos(veiculos.size());
        resumo.setReceitaTotal(receitaTotal);
        resumo.setLucroTotal(lucroTotal);
        resumo.setMargemMediaGeral(!veiculos.isEmpty() ?
            veiculos.stream().map(RentabilidadeVeiculoDTO.VeiculoDetalheDTO::getMargemPercentual)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(veiculos.size()), 2, RoundingMode.HALF_UP) :
            BigDecimal.ZERO);
        dto.setResumoGeral(resumo);

        // Top veículos
        RentabilidadeVeiculoDTO.TopVeiculoDTO top = new RentabilidadeVeiculoDTO.TopVeiculoDTO();
        if (!veiculos.isEmpty()) {
            top.setMaisRentavel(veiculos.stream()
                .max(Comparator.comparing(RentabilidadeVeiculoDTO.VeiculoDetalheDTO::getMargemPercentual))
                .map(RentabilidadeVeiculoDTO.VeiculoDetalheDTO::getPlaca).orElse(null));
            top.setMaisUtilizado(veiculos.stream()
                .max(Comparator.comparing(RentabilidadeVeiculoDTO.VeiculoDetalheDTO::getTotalViagens))
                .map(RentabilidadeVeiculoDTO.VeiculoDetalheDTO::getPlaca).orElse(null));
            top.setMenorConsumo(veiculos.stream()
                .filter(v -> v.getConsumoMedioKmL().compareTo(BigDecimal.ZERO) > 0)
                .max(Comparator.comparing(RentabilidadeVeiculoDTO.VeiculoDetalheDTO::getConsumoMedioKmL))
                .map(RentabilidadeVeiculoDTO.VeiculoDetalheDTO::getPlaca).orElse(null));
        }
        dto.setTopVeiculos(top);

        log.info("Rentabilidade gerada - {} veículos", veiculos.size());
        return dto;
    }

    private RentabilidadeVeiculoDTO.PeriodoDTO criarPeriodoRentabilidade(LocalDate dataInicio, LocalDate dataFim) {
        RentabilidadeVeiculoDTO.PeriodoDTO p = new RentabilidadeVeiculoDTO.PeriodoDTO();
        p.setDataInicio(dataInicio);
        p.setDataFim(dataFim);
        p.setTotalDias((int) ChronoUnit.DAYS.between(dataInicio, dataFim) + 1);
        p.setDescricao(gerarDescricaoPeriodo(dataInicio, dataFim));
        return p;
    }

    // =========================================================================
    // 3. Desempenho por Motorista
    // =========================================================================

    @Transactional(readOnly = true)
    public DesempenhoMotoristaDTO gerarDesempenhoMotorista(LocalDate dataInicio, LocalDate dataFim,
                                                            Long clienteId, Long motoristaId) {

        log.info("Gerando Desempenho por Motorista: {} a {}", dataInicio, dataFim);

        DesempenhoMotoristaDTO dto = new DesempenhoMotoristaDTO();
        DesempenhoMotoristaDTO.PeriodoDTO periodo = new DesempenhoMotoristaDTO.PeriodoDTO();
        periodo.setDataInicio(dataInicio);
        periodo.setDataFim(dataFim);
        periodo.setTotalDias((int) ChronoUnit.DAYS.between(dataInicio, dataFim) + 1);
        periodo.setDescricao(gerarDescricaoPeriodo(dataInicio, dataFim));
        dto.setPeriodo(periodo);

        List<Object[]> rows = viagemRepository.getPerformanceMotoristasPeriodo(clienteId, dataInicio, dataFim);
        List<DesempenhoMotoristaDTO.MotoristaDetalheDTO> motoristas = new ArrayList<>();

        long totalViagens = 0;
        BigDecimal comissaoTotal = BigDecimal.ZERO;
        int ranking = 1;

        for (Object[] r : rows) {
            Long mid = toLong(r[0]);
            if (motoristaId != null && !motoristaId.equals(mid)) continue;

            DesempenhoMotoristaDTO.MotoristaDetalheDTO m = new DesempenhoMotoristaDTO.MotoristaDetalheDTO();
            m.setMotoristaId(mid);
            m.setNome(toString(r[1]));
            m.setTotalViagens(toLong(r[2]));
            m.setReceitaGerada(toBigDecimal(r[3]));
            m.setMargemMedia(toBigDecimal(r[4]));
            m.setComissaoTotal(toBigDecimal(r[5]));
            m.setConsumoMedio(toBigDecimal(r[6]));
            m.setKmPercorridos(toBigDecimal(r[7]));
            m.setPosicaoRanking(ranking++);
            motoristas.add(m);

            totalViagens += m.getTotalViagens();
            comissaoTotal = comissaoTotal.add(m.getComissaoTotal());
        }

        dto.setMotoristas(motoristas);

        // Resumo geral
        DesempenhoMotoristaDTO.ResumoGeralDTO resumo = new DesempenhoMotoristaDTO.ResumoGeralDTO();
        resumo.setTotalMotoristas(motoristas.size());
        resumo.setTotalViagens(totalViagens);
        resumo.setComissaoTotal(comissaoTotal);
        dto.setResumoGeral(resumo);

        // Top motoristas
        DesempenhoMotoristaDTO.TopMotoristaDTO top = new DesempenhoMotoristaDTO.TopMotoristaDTO();
        if (!motoristas.isEmpty()) {
            top.setMaisViagens(motoristas.stream()
                .max(Comparator.comparing(DesempenhoMotoristaDTO.MotoristaDetalheDTO::getTotalViagens))
                .map(DesempenhoMotoristaDTO.MotoristaDetalheDTO::getNome).orElse(null));
            top.setMaiorReceita(motoristas.stream()
                .max(Comparator.comparing(DesempenhoMotoristaDTO.MotoristaDetalheDTO::getReceitaGerada))
                .map(DesempenhoMotoristaDTO.MotoristaDetalheDTO::getNome).orElse(null));
            top.setMelhorMargem(motoristas.stream()
                .max(Comparator.comparing(DesempenhoMotoristaDTO.MotoristaDetalheDTO::getMargemMedia))
                .map(DesempenhoMotoristaDTO.MotoristaDetalheDTO::getNome).orElse(null));
        }
        dto.setTopMotoristas(top);

        log.info("Desempenho gerado - {} motoristas", motoristas.size());
        return dto;
    }

    // =========================================================================
    // 4. Análise de Rotas
    // =========================================================================

    @Transactional(readOnly = true)
    public AnaliseRotaDTO gerarAnaliseRotas(LocalDate dataInicio, LocalDate dataFim, Long clienteId) {

        log.info("Gerando Análise de Rotas: {} a {}", dataInicio, dataFim);

        AnaliseRotaDTO dto = new AnaliseRotaDTO();
        AnaliseRotaDTO.PeriodoDTO periodo = new AnaliseRotaDTO.PeriodoDTO();
        periodo.setDataInicio(dataInicio);
        periodo.setDataFim(dataFim);
        periodo.setTotalDias((int) ChronoUnit.DAYS.between(dataInicio, dataFim) + 1);
        periodo.setDescricao(gerarDescricaoPeriodo(dataInicio, dataFim));
        dto.setPeriodo(periodo);

        List<Object[]> rows = viagemRepository.getLucratividadePorRotaPeriodo(clienteId, dataInicio, dataFim);
        List<AnaliseRotaDTO.RotaDetalheDTO> rotas = new ArrayList<>();

        long totalViagens = 0;
        BigDecimal distanciaTotal = BigDecimal.ZERO;
        int ranking = 1;

        for (Object[] r : rows) {
            AnaliseRotaDTO.RotaDetalheDTO rota = new AnaliseRotaDTO.RotaDetalheDTO();
            rota.setRotaId(toLong(r[0]));
            rota.setNome(toString(r[1]));
            rota.setTotalViagens(toLong(r[2]));
            rota.setReceitaTotal(toBigDecimal(r[3]));
            rota.setCustoTotal(toBigDecimal(r[4]));
            rota.setLucroTotal(toBigDecimal(r[5]));
            rota.setMargemPercentual(toBigDecimal(r[6]));
            rota.setDistanciaKm(toBigDecimal(r[7]));
            rota.setCustoPedagioMedio(toBigDecimal(r[8]));
            rota.setReceitaPorKm(toBigDecimal(r[9]));
            rota.setPosicaoRanking(ranking++);
            rotas.add(rota);

            totalViagens += rota.getTotalViagens();
            distanciaTotal = distanciaTotal.add(rota.getDistanciaKm()
                .multiply(BigDecimal.valueOf(rota.getTotalViagens())));
        }

        dto.setRotas(rotas);

        // Resumo geral
        AnaliseRotaDTO.ResumoGeralDTO resumo = new AnaliseRotaDTO.ResumoGeralDTO();
        resumo.setTotalRotas(rotas.size());
        resumo.setTotalViagens(totalViagens);
        resumo.setDistanciaTotalKm(distanciaTotal);
        dto.setResumoGeral(resumo);

        // Top rotas
        AnaliseRotaDTO.TopRotaDTO top = new AnaliseRotaDTO.TopRotaDTO();
        if (!rotas.isEmpty()) {
            top.setMaisRentavel(rotas.stream()
                .max(Comparator.comparing(AnaliseRotaDTO.RotaDetalheDTO::getMargemPercentual))
                .map(AnaliseRotaDTO.RotaDetalheDTO::getNome).orElse(null));
            top.setMaisUtilizada(rotas.stream()
                .max(Comparator.comparing(AnaliseRotaDTO.RotaDetalheDTO::getTotalViagens))
                .map(AnaliseRotaDTO.RotaDetalheDTO::getNome).orElse(null));
            top.setMaiorDistancia(rotas.stream()
                .max(Comparator.comparing(AnaliseRotaDTO.RotaDetalheDTO::getDistanciaKm))
                .map(AnaliseRotaDTO.RotaDetalheDTO::getNome).orElse(null));
        }
        dto.setTopRotas(top);

        log.info("Análise de Rotas gerada - {} rotas", rotas.size());
        return dto;
    }

    // =========================================================================
    // 5. Análise de Combustível
    // =========================================================================

    @Transactional(readOnly = true)
    public AnaliseCombustivelDTO gerarAnaliseCombustivel(LocalDate dataInicio, LocalDate dataFim,
                                                          Long clienteId, Long veiculoId) {

        log.info("Gerando Análise de Combustível: {} a {}", dataInicio, dataFim);

        AnaliseCombustivelDTO dto = new AnaliseCombustivelDTO();
        AnaliseCombustivelDTO.PeriodoDTO periodo = new AnaliseCombustivelDTO.PeriodoDTO();
        periodo.setDataInicio(dataInicio);
        periodo.setDataFim(dataFim);
        periodo.setTotalDias((int) ChronoUnit.DAYS.between(dataInicio, dataFim) + 1);
        periodo.setDescricao(gerarDescricaoPeriodo(dataInicio, dataFim));
        dto.setPeriodo(periodo);

        // Consumo por veículo
        List<Object[]> veiculoRows = viagemRepository.getAnaliseCombustivelPorVeiculoPeriodo(clienteId, dataInicio, dataFim);
        List<AnaliseCombustivelDTO.ConsumoPorVeiculoDTO> consumoVeiculos = new ArrayList<>();

        BigDecimal custoTotalComb = BigDecimal.ZERO;
        BigDecimal litrosTotais = BigDecimal.ZERO;
        BigDecimal kmTotal = BigDecimal.ZERO;

        for (Object[] r : veiculoRows) {
            Long vid = toLong(r[0]);
            if (veiculoId != null && !veiculoId.equals(vid)) continue;

            AnaliseCombustivelDTO.ConsumoPorVeiculoDTO cv = new AnaliseCombustivelDTO.ConsumoPorVeiculoDTO();
            cv.setVeiculoId(vid);
            cv.setPlaca(toString(r[1]));
            cv.setConsumoEstimadoLitros(toBigDecimal(r[2]));
            cv.setCustoCombustivel(toBigDecimal(r[3]));
            cv.setMediaKmL(toBigDecimal(r[4]));
            cv.setKmRodados(toBigDecimal(r[5]));
            consumoVeiculos.add(cv);

            custoTotalComb = custoTotalComb.add(cv.getCustoCombustivel());
            litrosTotais = litrosTotais.add(cv.getConsumoEstimadoLitros());
            kmTotal = kmTotal.add(cv.getKmRodados());
        }
        dto.setConsumoPorVeiculo(consumoVeiculos);

        // Consumo por rota
        List<Object[]> rotaRows = viagemRepository.getAnaliseCombustivelPorRotaPeriodo(clienteId, dataInicio, dataFim);
        List<AnaliseCombustivelDTO.ConsumoPorRotaDTO> consumoRotas = rotaRows.stream().map(r -> {
            AnaliseCombustivelDTO.ConsumoPorRotaDTO cr = new AnaliseCombustivelDTO.ConsumoPorRotaDTO();
            cr.setRotaId(toLong(r[0]));
            cr.setNome(toString(r[1]));
            cr.setConsumoMedioLitros(toBigDecimal(r[2]));
            cr.setClassificacaoTerreno(toString(r[3]));
            cr.setFatorTerrenoMedio(toBigDecimal(r[4]));
            cr.setDistanciaKm(toBigDecimal(r[5]));
            return cr;
        }).collect(Collectors.toList());
        dto.setConsumoPorRota(consumoRotas);

        // Resumo geral
        AnaliseCombustivelDTO.ResumoGeralDTO resumo = new AnaliseCombustivelDTO.ResumoGeralDTO();
        resumo.setCustoTotalCombustivel(custoTotalComb);
        resumo.setLitrosTotaisEstimados(litrosTotais);
        resumo.setConsumoMedioKmL(litrosTotais.compareTo(BigDecimal.ZERO) > 0 ?
            kmTotal.divide(litrosTotais, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        // Calcular percentual da receita
        List<Object[]> resumoRows = viagemRepository.getResumoGeralPeriodo(clienteId, dataInicio, dataFim);
        if (resumoRows != null && !resumoRows.isEmpty()) {
            BigDecimal receita = toBigDecimal(resumoRows.get(0)[4]);
            resumo.setPercentualDaReceita(receita.compareTo(BigDecimal.ZERO) > 0 ?
                custoTotalComb.multiply(BigDecimal.valueOf(100))
                    .divide(receita, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        } else {
            resumo.setPercentualDaReceita(BigDecimal.ZERO);
        }
        dto.setResumoGeral(resumo);

        // Evolução mensal de combustível
        List<Object[]> tendencias = viagemRepository.getTendenciasMensaisPeriodo(clienteId, dataInicio, dataFim);
        dto.setEvolucaoMensal(montarEvolucaoMensalCombustivel(tendencias, clienteId, dataInicio, dataFim));

        log.info("Análise de Combustível gerada - {} veículos, {} rotas", consumoVeiculos.size(), consumoRotas.size());
        return dto;
    }

    private List<AnaliseCombustivelDTO.EvolucaoMensalDTO> montarEvolucaoMensalCombustivel(
            List<Object[]> tendencias, Long clienteId, LocalDate dataInicio, LocalDate dataFim) {

        // Use custos operacionais per period to get monthly fuel data
        // For simplicity, derive from tendencias (monthly revenue/costs data)
        if (tendencias == null) return Collections.emptyList();
        return tendencias.stream().map(r -> {
            AnaliseCombustivelDTO.EvolucaoMensalDTO evo = new AnaliseCombustivelDTO.EvolucaoMensalDTO();
            int ano = toInt(r[0]);
            int mes = toInt(r[1]);
            evo.setMes(formatarMesAno(mes, ano));
            BigDecimal custoMes = toBigDecimal(r[3]); // custo total do mês
            evo.setCustoTotal(custoMes);
            evo.setLitrosTotais(BigDecimal.ZERO); // seria necessário query específica
            evo.setPrecoMedioLitro(BigDecimal.ZERO);
            return evo;
        }).collect(Collectors.toList());
    }

    // =========================================================================
    // 6. Análise de Custos
    // =========================================================================

    @Transactional(readOnly = true)
    public AnaliseCustosViagemDTO gerarAnaliseCustos(LocalDate dataInicio, LocalDate dataFim,
                                                      Long clienteId) {

        log.info("Gerando Análise de Custos: {} a {}", dataInicio, dataFim);

        AnaliseCustosViagemDTO dto = new AnaliseCustosViagemDTO();
        AnaliseCustosViagemDTO.PeriodoDTO periodo = new AnaliseCustosViagemDTO.PeriodoDTO();
        periodo.setDataInicio(dataInicio);
        periodo.setDataFim(dataFim);
        periodo.setTotalDias((int) ChronoUnit.DAYS.between(dataInicio, dataFim) + 1);
        periodo.setDescricao(gerarDescricaoPeriodo(dataInicio, dataFim));
        dto.setPeriodo(periodo);

        // Composição dos custos
        List<Object[]> custosRows = viagemRepository.getCustosOperacionaisPeriodo(clienteId, dataInicio, dataFim);
        dto.setComposicaoCustos(montarComposicaoCustos(custosRows));

        // Viagens com baixa margem (margem <= 10%)
        List<Object[]> baixaMargemRows = viagemRepository.getViagensBaixaMargemPeriodo(
            clienteId, dataInicio, dataFim, 10.0);
        dto.setViagensBaixaMargem(montarViagensBaixaMargem(baixaMargemRows));

        // Estatísticas de margem
        List<Object[]> resumoRows = viagemRepository.getResumoGeralPeriodo(clienteId, dataInicio, dataFim);
        dto.setEstatisticas(montarEstatisticasCustos(resumoRows, baixaMargemRows));

        log.info("Análise de Custos gerada");
        return dto;
    }

    private AnaliseCustosViagemDTO.ComposicaoCustosDTO montarComposicaoCustos(List<Object[]> rows) {
        AnaliseCustosViagemDTO.ComposicaoCustosDTO comp = new AnaliseCustosViagemDTO.ComposicaoCustosDTO();
        if (rows != null && !rows.isEmpty()) {
            Object[] r = rows.get(0);
            BigDecimal combustivel = toBigDecimal(r[0]);
            BigDecimal pedagios = toBigDecimal(r[1]);
            BigDecimal comissoes = toBigDecimal(r[2]);
            BigDecimal total = combustivel.add(pedagios).add(comissoes);

            comp.setCombustivel(combustivel);
            comp.setPedagios(pedagios);
            comp.setComissoes(comissoes);
            comp.setTotal(total);

            if (total.compareTo(BigDecimal.ZERO) > 0) {
                comp.setCombustivelPercentual(combustivel.multiply(BigDecimal.valueOf(100))
                    .divide(total, 2, RoundingMode.HALF_UP));
                comp.setPedagiosPercentual(pedagios.multiply(BigDecimal.valueOf(100))
                    .divide(total, 2, RoundingMode.HALF_UP));
                comp.setComissoesPercentual(comissoes.multiply(BigDecimal.valueOf(100))
                    .divide(total, 2, RoundingMode.HALF_UP));
            } else {
                comp.setCombustivelPercentual(BigDecimal.ZERO);
                comp.setPedagiosPercentual(BigDecimal.ZERO);
                comp.setComissoesPercentual(BigDecimal.ZERO);
            }
        }
        return comp;
    }

    private List<AnaliseCustosViagemDTO.ViagemBaixaMargemDTO> montarViagensBaixaMargem(List<Object[]> rows) {
        if (rows == null) return Collections.emptyList();
        return rows.stream().map(r -> {
            AnaliseCustosViagemDTO.ViagemBaixaMargemDTO v = new AnaliseCustosViagemDTO.ViagemBaixaMargemDTO();
            v.setViagemId(toLong(r[0]));
            v.setRotaNome(toString(r[1]));
            v.setMotoristaNome(toString(r[2]));
            v.setVeiculoPlaca(toString(r[3]));
            v.setMargem(toBigDecimal(r[4]));
            v.setLucro(toBigDecimal(r[5]));
            v.setReceita(toBigDecimal(r[6]));
            if (r[7] instanceof java.sql.Date sqlDate) {
                v.setDataViagem(sqlDate.toLocalDate());
            } else if (r[7] instanceof java.util.Date utilDate) {
                v.setDataViagem(new java.sql.Date(utilDate.getTime()).toLocalDate());
            }
            return v;
        }).collect(Collectors.toList());
    }

    private AnaliseCustosViagemDTO.EstatisticasDTO montarEstatisticasCustos(
            List<Object[]> resumoRows, List<Object[]> baixaMargemRows) {

        AnaliseCustosViagemDTO.EstatisticasDTO stats = new AnaliseCustosViagemDTO.EstatisticasDTO();
        if (resumoRows != null && !resumoRows.isEmpty()) {
            Object[] r = resumoRows.get(0);
            long totalViagens = toLong(r[0]);
            BigDecimal margemMedia = toBigDecimal(r[7]);
            stats.setMargemMediaGeral(margemMedia);

            long viagensComPrejuizo = baixaMargemRows != null ?
                baixaMargemRows.stream()
                    .filter(row -> toBigDecimal(row[5]).compareTo(BigDecimal.ZERO) < 0)
                    .count() : 0;
            stats.setViagensComPrejuizo(viagensComPrejuizo);
            stats.setViagensComLucro(totalViagens - viagensComPrejuizo);

            // Margem máxima, mínima e desvio padrão seriam idealmente calculados com query separada
            // Usando os dados disponíveis do resumo
            stats.setMargemMaxima(margemMedia); // aproximação
            stats.setMargemMinima(baixaMargemRows != null && !baixaMargemRows.isEmpty() ?
                toBigDecimal(baixaMargemRows.get(baixaMargemRows.size() - 1)[4]) : margemMedia);
            stats.setDesvioPadrao(BigDecimal.ZERO);
        }
        return stats;
    }

    // =========================================================================
    // 7. Receita por Cliente
    // =========================================================================

    @Transactional(readOnly = true)
    public ReceitaPorClienteViagemDTO gerarReceitaPorCliente(LocalDate dataInicio, LocalDate dataFim,
                                                              Long clienteId) {

        log.info("Gerando Receita por Cliente: {} a {}", dataInicio, dataFim);

        ReceitaPorClienteViagemDTO dto = new ReceitaPorClienteViagemDTO();
        ReceitaPorClienteViagemDTO.PeriodoDTO periodo = new ReceitaPorClienteViagemDTO.PeriodoDTO();
        periodo.setDataInicio(dataInicio);
        periodo.setDataFim(dataFim);
        periodo.setTotalDias((int) ChronoUnit.DAYS.between(dataInicio, dataFim) + 1);
        periodo.setDescricao(gerarDescricaoPeriodo(dataInicio, dataFim));
        dto.setPeriodo(periodo);

        List<Object[]> rows = viagemRepository.getReceitaPorClientePeriodo(dataInicio, dataFim);
        List<ReceitaPorClienteViagemDTO.ClienteDetalheDTO> clientes = new ArrayList<>();

        BigDecimal receitaTotal = BigDecimal.ZERO;
        BigDecimal toneladasTotal = BigDecimal.ZERO;
        long totalViagens = 0;
        int ranking = 1;

        for (Object[] r : rows) {
            Long cid = toLong(r[0]);
            if (clienteId != null && !clienteId.equals(cid)) continue;

            ReceitaPorClienteViagemDTO.ClienteDetalheDTO c = new ReceitaPorClienteViagemDTO.ClienteDetalheDTO();
            c.setClienteId(cid);
            c.setNome(toString(r[1]));
            c.setTotalViagens(toLong(r[2]));
            c.setReceitaBruta(toBigDecimal(r[3]));
            c.setLucroLiquido(toBigDecimal(r[4]));
            c.setMargemMedia(toBigDecimal(r[5]));
            c.setToneladasTransportadas(toBigDecimal(r[6]));
            c.setTicketMedio(toBigDecimal(r[7]));
            String materiais = toString(r[8]);
            c.setMateriaisTransportados(materiais != null ?
                Arrays.asList(materiais.split(",\\s*")) : Collections.emptyList());
            c.setPosicaoRanking(ranking++);
            clientes.add(c);

            receitaTotal = receitaTotal.add(c.getReceitaBruta());
            toneladasTotal = toneladasTotal.add(c.getToneladasTransportadas());
            totalViagens += c.getTotalViagens();
        }

        dto.setClientes(clientes);

        // Resumo geral
        ReceitaPorClienteViagemDTO.ResumoGeralDTO resumo = new ReceitaPorClienteViagemDTO.ResumoGeralDTO();
        resumo.setTotalClientes(clientes.size());
        resumo.setReceitaTotal(receitaTotal);
        resumo.setToneladasTransportadas(toneladasTotal);
        resumo.setTicketMedio(totalViagens > 0 ?
            receitaTotal.divide(BigDecimal.valueOf(totalViagens), 2, RoundingMode.HALF_UP) :
            BigDecimal.ZERO);
        dto.setResumoGeral(resumo);

        // Top clientes
        ReceitaPorClienteViagemDTO.TopClienteDTO top = new ReceitaPorClienteViagemDTO.TopClienteDTO();
        if (!clientes.isEmpty()) {
            top.setMaiorReceita(clientes.stream()
                .max(Comparator.comparing(ReceitaPorClienteViagemDTO.ClienteDetalheDTO::getReceitaBruta))
                .map(ReceitaPorClienteViagemDTO.ClienteDetalheDTO::getNome).orElse(null));
            top.setMaisViagens(clientes.stream()
                .max(Comparator.comparing(ReceitaPorClienteViagemDTO.ClienteDetalheDTO::getTotalViagens))
                .map(ReceitaPorClienteViagemDTO.ClienteDetalheDTO::getNome).orElse(null));
            top.setMelhorMargem(clientes.stream()
                .max(Comparator.comparing(ReceitaPorClienteViagemDTO.ClienteDetalheDTO::getMargemMedia))
                .map(ReceitaPorClienteViagemDTO.ClienteDetalheDTO::getNome).orElse(null));
        }
        dto.setTopClientes(top);

        log.info("Receita por Cliente gerada - {} clientes", clientes.size());
        return dto;
    }

    // =========================================================================
    // Métodos utilitários
    // =========================================================================

    private String gerarDescricaoPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMMM/yyyy", new Locale("pt", "BR"));
        if (dataInicio.getMonth() == dataFim.getMonth() && dataInicio.getYear() == dataFim.getYear()) {
            return dataInicio.format(fmt);
        }
        return dataInicio.format(fmt) + " a " + dataFim.format(fmt);
    }

    private String formatarMesAno(int mes, int ano) {
        LocalDate date = LocalDate.of(ano, mes, 1);
        return date.format(DateTimeFormatter.ofPattern("MMMM/yyyy", new Locale("pt", "BR")));
    }

    private BigDecimal calcularVariacao(BigDecimal atual, BigDecimal anterior) {
        if (anterior == null || anterior.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return atual.subtract(anterior)
            .divide(anterior, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd) return bd;
        if (value instanceof Number num) return BigDecimal.valueOf(num.doubleValue());
        return BigDecimal.ZERO;
    }

    private Long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Number num) return num.longValue();
        return 0L;
    }

    private int toInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Number num) return num.intValue();
        return 0;
    }

    private String toString(Object value) {
        return value != null ? value.toString() : null;
    }
}
