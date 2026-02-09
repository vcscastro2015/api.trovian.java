package com.trovian.service;

import com.trovian.dto.dashboard.*;
import com.trovian.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final ViagemRepository viagemRepository;
    private final RotaRepository rotaRepository;
    private final VeiculoRepository veiculoRepository;
    private final MotoristaRepository motoristaRepository;
    private final ManutencaoRepository manutencaoRepository;
    private final ContaPagarRepository contaPagarRepository;
    private final ContaReceberRepository contaReceberRepository;
    private final ComissaoMotoristaRepository comissaoMotoristaRepository;

    // Limiar para alertas de baixa margem
    private static final double MARGEM_CRITICA = 5.0;
    private static final double MARGEM_ATENCAO = 10.0;
    private static final double MARGEM_BAIXA = 15.0;

    /**
     * Retorna o dashboard completo com todos os indicadores
     */
    @Cacheable(value = "dashboardCompleto", key = "#clienteId + '_' + #diasAnalise")
    public DashboardCompletoDTO getDashboardCompleto(Long clienteId, Integer diasAnalise) {
        log.info("Gerando dashboard completo para cliente {} - {} dias", clienteId, diasAnalise);
        
        LocalDate dataInicio = LocalDate.now().minusDays(diasAnalise);
        
        return DashboardCompletoDTO.builder()
                .resumo(getResumo(clienteId, dataInicio))
                .lucratividadeRotas(getLucratividadeRotas(clienteId, dataInicio))
                .comparacaoRotas(getComparacaoRotas(clienteId, dataInicio))
                .alertas(getAlertasBaixaMargem(clienteId, dataInicio))
                .eficienciaVeiculos(getEficienciaVeiculos(clienteId, dataInicio))
                .performanceMotoristas(getPerformanceMotoristas(clienteId, dataInicio))
                .tendencias(getTendenciasMensais(clienteId, 12))
                .custosOperacionais(getCustosOperacionais(clienteId, dataInicio))
                .indicadoresRotas(getIndicadoresRotas(clienteId, dataInicio))
                .mapaCalor(getMapaCalorRotas(clienteId, dataInicio))
                .previsaoMeta(getPrevisaoMeta(clienteId))
                .build();
    }

    /**
     * 2.2 - Resumo Geral do Dashboard
     */
    public DashboardResumoDTO getResumo(Long clienteId, LocalDate dataInicio) {
        List<Object[]> resultado = viagemRepository.getResumoGeral(clienteId, dataInicio);
        LocalDate dataFim = LocalDate.now();

        // Calcular totais financeiros
        BigDecimal totalContasAPagar = calcularTotalContasAPagar(clienteId, dataInicio, dataFim);
        BigDecimal totalContasAReceber = calcularTotalContasAReceber(clienteId, dataInicio, dataFim);
        BigDecimal totalComissoesMotoristas = calcularTotalComissoesMotoristas(clienteId, dataInicio, dataFim);

        if (resultado.isEmpty()) {
            return DashboardResumoDTO.builder()
                    .totalViagens(0)
                    .viagensAbertas(0)
                    .viagensFinalizadas(0)
                    .receitaTotal(BigDecimal.ZERO)
                    .lucroTotal(BigDecimal.ZERO)
                    .margemMediaPercentual(0.0)
                    .kmTotalPercorrido(0.0)
                    .custoTotalCombustivel(BigDecimal.ZERO)
                    .custoTotalPedagios(BigDecimal.ZERO)
                    .totalVeiculosAtivos(0)
                    .totalMotoristas(0)
                    .periodoAnalise(calcularPeriodoAnalise(dataInicio))
                    .totalContasAPagar(totalContasAPagar)
                    .totalContasAReceber(totalContasAReceber)
                    .totalComissoesMotoristas(totalComissoesMotoristas)
                    .build();
        }

        Object[] row = resultado.get(0);

        return DashboardResumoDTO.builder()
                .totalViagens(((Number) row[0]).intValue())
                .viagensAbertas(((Number) row[1]).intValue())
                .viagensFinalizadas(((Number) row[2]).intValue())
                .receitaTotal((BigDecimal) row[3])
                .lucroTotal((BigDecimal) row[4])
                .margemMediaPercentual(((Number) row[5]).doubleValue())
                .kmTotalPercorrido(((Number) row[6]).doubleValue())
                .custoTotalCombustivel((BigDecimal) row[7])
                .custoTotalPedagios((BigDecimal) row[8])
                .totalVeiculosAtivos(veiculoRepository.countByClienteIdAndStatusTrue(clienteId))
                .totalMotoristas(motoristaRepository.countByClienteIdAndStatusTrue(clienteId))
                .periodoAnalise(calcularPeriodoAnalise(dataInicio))
                .totalContasAPagar(totalContasAPagar)
                .totalContasAReceber(totalContasAReceber)
                .totalComissoesMotoristas(totalComissoesMotoristas)
                .build();
    }

    /**
     * 2.3 - Lucratividade por Rota
     */
    public List<LucratividadeRotaDTO> getLucratividadeRotas(Long clienteId, LocalDate dataInicio) {
        List<Object[]> resultados = viagemRepository.getLucratividadePorRota(clienteId, dataInicio);
        
        return resultados.stream()
                .map(row -> {
                    BigDecimal receita = (BigDecimal) row[3];
                    BigDecimal custo = (BigDecimal) row[4];
                    BigDecimal lucro = (BigDecimal) row[5];
                    Double margem = ((Number) row[6]).doubleValue();
                    
                    return LucratividadeRotaDTO.builder()
                            .rotaId(((Number) row[0]).longValue())
                            .nomeRota((String) row[1])
                            .totalViagens(((Number) row[2]).intValue())
                            .receitaTotal(receita)
                            .custoTotal(custo)
                            .lucroTotal(lucro)
                            .margemPercentual(margem)
                            .distanciaMediaKm(((Number) row[7]).doubleValue())
                            .lucroMedioPorViagem(
                                lucro.divide(BigDecimal.valueOf(((Number) row[2]).intValue()), 2, RoundingMode.HALF_UP)
                            )
                            .receitaPorKm(calcularReceitaPorKm(receita, ((Number) row[7]).doubleValue()))
                            .classificacao(classificarLucratividade(margem))
                            .build();
                })
                .sorted(Comparator.comparing(LucratividadeRotaDTO::getLucroTotal).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 2.4 - ComparaÃ§Ã£o de Rotas
     */
    public ComparacaoRotasDTO getComparacaoRotas(Long clienteId, LocalDate dataInicio) {
        List<LucratividadeRotaDTO> rotas = getLucratividadeRotas(clienteId, dataInicio);
        
        if (rotas.isEmpty()) {
            return ComparacaoRotasDTO.builder()
                    .rotas(Collections.emptyList())
                    .build();
        }
        
        LucratividadeRotaDTO maisLucrativa = rotas.stream()
                .max(Comparator.comparing(LucratividadeRotaDTO::getLucroTotal))
                .orElse(null);
        
        LucratividadeRotaDTO menosLucrativa = rotas.stream()
                .min(Comparator.comparing(LucratividadeRotaDTO::getLucroTotal))
                .orElse(null);
        
        LucratividadeRotaDTO maiorVolume = rotas.stream()
                .max(Comparator.comparing(LucratividadeRotaDTO::getTotalViagens))
                .orElse(null);
        
        return ComparacaoRotasDTO.builder()
                .rotas(rotas)
                .rotaMaisLucrativa(maisLucrativa)
                .rotaMenosLucrativa(menosLucrativa)
                .rotaMaiorVolume(maiorVolume)
                .build();
    }

    /**
     * 2.5 - Alertas de Baixa Margem
     */
    public List<AlertaBaixaMargemDTO> getAlertasBaixaMargem(Long clienteId, LocalDate dataInicio) {
        List<Object[]> resultados = viagemRepository.getViagensBaixaMargem(clienteId, dataInicio, MARGEM_BAIXA);
        
        return resultados.stream()
                .map(row -> {
                    Double margem = row[4] != null ? ((Number) row[4]).doubleValue() : 0.0;
                    
                    return AlertaBaixaMargemDTO.builder()
                            .viagemId(((Number) row[0]).longValue())
                            .nomeRota((String) row[1])
                            .nomeMotorista((String) row[2])
                            .placaVeiculo((String) row[3])
                            .margemPercentual(BigDecimal.valueOf(margem))
                            .lucro((BigDecimal) row[5])
                            .receita((BigDecimal) row[6])
                            .dataViagem(((java.sql.Timestamp) row[7]).toLocalDateTime())
                            .nivelAlerta(classificarNivelAlerta(margem))
                            .motivo(identificarMotivoAlerta(row))
                            .build();
                })
                .sorted(Comparator.comparing(AlertaBaixaMargemDTO::getMargemPercentual))
                .collect(Collectors.toList());
    }

    /**
     * 2.6 - EficiÃªncia de VeÃ­culos
     */
    public List<EficienciaVeiculoDTO> getEficienciaVeiculos(Long clienteId, LocalDate dataInicio) {
        List<Object[]> resultados = viagemRepository.getEficienciaVeiculos(clienteId, dataInicio);
        
        return resultados.stream()
                .map(row -> {
                    Long veiculoId = ((Number) row[0]).longValue();
                    
                    return EficienciaVeiculoDTO.builder()
                            .veiculoId(veiculoId)
                            .placa((String) row[1])
                            .modelo("NSA")
                            .totalViagens(((Number) row[3]).intValue())
                            .lucroTotal((BigDecimal) row[4])
                            .receitaTotal((BigDecimal) row[5])
                            .kmTotal(((Number) row[6]).doubleValue())
                            .mediaConsumoKmL(((Number) row[7]).doubleValue())
                            .custoManutencao(getCustoManutencao(veiculoId, dataInicio))
                            .lucroPorKm(calcularLucroPorKm((BigDecimal) row[4], ((Number) row[6]).doubleValue()))
                            .taxaOcupacao(calcularTaxaOcupacao(veiculoId, dataInicio))
                            .statusManutencao(getStatusManutencao(dataInicio, veiculoId))
                            .build();
                })
                .sorted(Comparator.comparing(EficienciaVeiculoDTO::getLucroTotal).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 2.7 - Performance de Motoristas
     */
    public List<PerformanceMotoristaDTO> getPerformanceMotoristas(Long clienteId, LocalDate dataInicio) {
        List<Object[]> resultados = viagemRepository.getPerformanceMotoristas(clienteId, dataInicio);
        
        return resultados.stream()
                .map(row -> {
                    Integer totalViagens = ((Number) row[2]).intValue();
                    Integer viagensNoPrazo = row[5] != null ? ((Number) row[5]).intValue() : totalViagens;
                    
                    return PerformanceMotoristaDTO.builder()
                            .motoristaId(((Number) row[0]).longValue())
                            .nome((String) row[1])
                            .totalViagens(totalViagens)
                            .mediaConsumoKmL(((Number) row[3]).doubleValue())
                            .comissaoTotal((BigDecimal) row[4])
                            .viagensNoPrazo(viagensNoPrazo)
                            .viagensAtrasadas(totalViagens - viagensNoPrazo)
                            .taxaCumprimentoPrazo(calcularTaxaCumprimento(viagensNoPrazo, totalViagens))
                            .kmTotalPercorrido(((Number) row[6]).doubleValue())
                            .build();
                })
                .sorted(Comparator.comparing(PerformanceMotoristaDTO::getTotalViagens).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 2.8 - TendÃªncias Mensais
     */
    public List<TendenciaLucratividadeDTO> getTendenciasMensais(Long clienteId, Integer meses) {
        LocalDate dataInicio = LocalDate.now().minusMonths(meses);
        List<Object[]> resultados = viagemRepository.getTendenciasMensais(clienteId, dataInicio);
        
        return resultados.stream()
                .map(row -> {
                    String periodo = formatarPeriodo((Integer) row[0], (Integer) row[1]);
                    BigDecimal receita = (BigDecimal) row[2];
                    BigDecimal custo = (BigDecimal) row[3];
                    BigDecimal lucro = (BigDecimal) row[4];
                    
                    return TendenciaLucratividadeDTO.builder()
                            .periodo(periodo)
                            .receita(receita)
                            .custoTotal(custo)
                            .lucro(lucro)
                            .margemPercentual(calcularMargem(lucro, receita))
                            .totalViagens(((Number) row[5]).intValue())
                            .build();
                })
                .sorted(Comparator.comparing(TendenciaLucratividadeDTO::getPeriodo))
                .collect(Collectors.toList());
    }

    /**
     * 2.9 - Custos Operacionais
     */
    public CustoOperacionalDTO getCustosOperacionais(Long clienteId, LocalDate dataInicio) {
        List<Object[]> resultado = viagemRepository.getCustosOperacionais(clienteId, dataInicio);
        
        if (resultado.isEmpty()) {
            return CustoOperacionalDTO.builder()
                    .combustivel(BigDecimal.ZERO)
                    .pedagios(BigDecimal.ZERO)
                    .manutencao(BigDecimal.ZERO)
                    .comissoes(BigDecimal.ZERO)
                    .impostos(BigDecimal.ZERO)
                    .outros(BigDecimal.ZERO)
                    .total(BigDecimal.ZERO)
                    .build();
        }
        
        Object[] row = resultado.get(0);
        BigDecimal combustivel = (BigDecimal) row[0];
        BigDecimal pedagios = (BigDecimal) row[1];
        BigDecimal comissoes = (BigDecimal) row[2];
        BigDecimal impostos = calcularImpostosTotal(clienteId, dataInicio);
        BigDecimal manutencao = getManutencaoTotal(clienteId, dataInicio);
        BigDecimal outros = BigDecimal.ZERO;
        
        BigDecimal total = combustivel.add(pedagios).add(comissoes)
                .add(impostos).add(manutencao).add(outros);
        
        return CustoOperacionalDTO.builder()
                .combustivel(combustivel)
                .pedagios(pedagios)
                .manutencao(manutencao)
                .comissoes(comissoes)
                .impostos(impostos)
                .outros(outros)
                .total(total)
                .percentualCombustivel(calcularPercentual(combustivel, total))
                .percentualPedagios(calcularPercentual(pedagios, total))
                .percentualManutencao(calcularPercentual(manutencao, total))
                .percentualComissoes(calcularPercentual(comissoes, total))
                .percentualImpostos(calcularPercentual(impostos, total))
                .build();
    }

    /**
     * 2.10 - Indicadores de Rotas
     */
    public List<IndicadorRotaDTO> getIndicadoresRotas(Long clienteId, LocalDate dataInicio) {
        List<Object[]> resultados = viagemRepository.getIndicadoresRotas(clienteId, dataInicio);
        
        return resultados.stream()
                .map(row -> {
                    Double kmTotal = ((Number) row[3]).doubleValue();
                    Double kmComCarga = row[4] != null ? ((Number) row[4]).doubleValue() : kmTotal;
                    Double kmVazio = kmTotal - kmComCarga;
                    BigDecimal receitaPorKm =  row[5] != null ?  BigDecimal.valueOf((Double) row[5]) : BigDecimal.ZERO;
                    return IndicadorRotaDTO.builder()
                            .rotaId(((Number) row[0]).longValue())
                            .nomeRota((String) row[1])
                            .taxaOcupacao(((Number) row[2]).doubleValue())
                            .kmProdutivo(kmComCarga)
                            .kmVazio(kmVazio)
                            .taxaRetornoVazio(calcularTaxaRetornoVazio(kmVazio, kmTotal))
                            .receitaPorKm(receitaPorKm)
                            .tempoMedioViagem(row[6] != null ? ((Number) row[6]).doubleValue() : 0.0)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 2.11 - Mapa de Calor de Rotas
     */
    public List<MapaCalorRotaDTO> getMapaCalorRotas(Long clienteId, LocalDate dataInicio) {
        List<Object[]> resultados = viagemRepository.getMapaCalorRotas(clienteId, dataInicio);
        
        // Calcular min e max para normalizaÃ§Ã£o
        BigDecimal maxLucro = resultados.stream()
                .map(row -> (BigDecimal) row[6])
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ONE);
        
        return resultados.stream()
                .map(row -> {
                    BigDecimal lucro = (BigDecimal) row[6];
                    Integer frequencia = ((Number) row[7]).intValue();
                    
                    return MapaCalorRotaDTO.builder()
                            .rotaId(((Number) row[0]).longValue())
                            .nomeRota((String) row[1])
                            .latitudeOrigem(((Number) row[2]).doubleValue())
                            .longitudeOrigem(((Number) row[3]).doubleValue())
                            .latitudeDestino(((Number) row[4]).doubleValue())
                            .longitudeDestino(((Number) row[5]).doubleValue())
                            .lucratividade(lucro)
                            .frequenciaViagens(frequencia)
                            .intensidade(classificarIntensidade(lucro, maxLucro))
                            .cor(gerarCorIntensidade(lucro, maxLucro))
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 2.12 - PrevisÃ£o e Metas
     */
    public PrevisaoMetaDTO getPrevisaoMeta(Long clienteId) {
        // Buscar meta configurada (vocÃª pode criar uma entidade Meta)
        // Por enquanto, vamos usar valores fixos como exemplo
        BigDecimal metaReceitaMensal = new BigDecimal("100000.00");
        BigDecimal metaLucroMensal = new BigDecimal("20000.00");
        
        // Dados do mÃªs atual
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        DashboardResumoDTO resumoMes = getResumo(clienteId, inicioMes);
        
        // Calcular projeÃ§Ãµes
        int diasDecorridos = LocalDate.now().getDayOfMonth();
        int diasNoMes = LocalDate.now().lengthOfMonth();
        double fatorProjecao = (double) diasNoMes / diasDecorridos;
        
        BigDecimal projecaoReceita = resumoMes.getReceitaTotal()
                .multiply(BigDecimal.valueOf(fatorProjecao));
        BigDecimal projecaoLucro = resumoMes.getLucroTotal()
                .multiply(BigDecimal.valueOf(fatorProjecao));
        
        return PrevisaoMetaDTO.builder()
                .periodo("Mensal")
                .metaReceita(metaReceitaMensal)
                .receitaAtual(resumoMes.getReceitaTotal())
                .metaLucro(metaLucroMensal)
                .lucroAtual(resumoMes.getLucroTotal())
                .percentualAlcancadoReceita(calcularPercentualMeta(resumoMes.getReceitaTotal(), metaReceitaMensal))
                .percentualAlcancadoLucro(calcularPercentualMeta(resumoMes.getLucroTotal(), metaLucroMensal))
                .projecaoReceita(projecaoReceita)
                .projecaoLucro(projecaoLucro)
                .status(determinarStatusMeta(projecaoReceita, metaReceitaMensal))
                .build();
    }

    // ========================================================================
    // MÃ‰TODOS AUXILIARES
    // ========================================================================

    private String calcularPeriodoAnalise(LocalDate dataInicio) {
        long dias = java.time.temporal.ChronoUnit.DAYS.between(dataInicio, LocalDate.now());
        if (dias <= 7) return "Últimos 7 dias";
        if (dias <= 30) return "Últimos 30 dias";
        if (dias <= 90) return "Últimos 3 meses";
        return "Últimos " + (dias / 30) + " meses";
    }

    private BigDecimal calcularReceitaPorKm(BigDecimal receita, Double km) {
        if (km == null || km == 0) return BigDecimal.ZERO;
        return receita.divide(BigDecimal.valueOf(km), 2, RoundingMode.HALF_UP);
    }

    private String classificarLucratividade(Double margem) {
        if (margem >= 20) return "Alta";
        if (margem >= 10) return "Média";
        return "Baixa";
    }

    private String classificarNivelAlerta(Double margem) {
        if (margem < MARGEM_CRITICA) return "CRÍTICO";
        if (margem < MARGEM_ATENCAO) return "ATENÇÃO";
        return "BAIXO";
    }

    private String identificarMotivoAlerta(Object[] row) {
        // LÃ³gica para identificar o principal motivo da baixa margem
        // Pode comparar custos de combustÃ­vel, pedÃ¡gios, etc
        return "Alto custo operacional";
    }

    private BigDecimal getCustoManutencao(Long veiculoId, LocalDate dataInicio) {
        return manutencaoRepository.getCustoTotalPorVeiculo(veiculoId, dataInicio)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calcularLucroPorKm(BigDecimal lucro, Double km) {
        if (km == null || km == 0) return BigDecimal.ZERO;
        return lucro.divide(BigDecimal.valueOf(km), 2, RoundingMode.HALF_UP);
    }

    private Double calcularTaxaOcupacao(Long veiculoId, LocalDate dataInicio) {
        // Calcular % mÃ©dia de capacidade utilizada
        return viagemRepository.getTaxaOcupacaoVeiculo(veiculoId, dataInicio)
                .orElse(0.0);
    }

    private String getStatusManutencao(LocalDate dataLimite, Long veiculoId) {
        // Verificar se ha manutenção pendente ou próxima
        return manutencaoRepository.getStatusManutencao(dataLimite, veiculoId)
                .orElse("Em dia");
    }

    private Double calcularTaxaCumprimento(Integer noPrazo, Integer total) {
        if (total == 0) return 0.0;
        return (noPrazo.doubleValue() / total.doubleValue()) * 100;
    }

    private String formatarPeriodo(Integer ano, Integer mes) {
        return String.format("%04d-%02d", ano, mes);
    }

    private Double calcularMargem(BigDecimal lucro, BigDecimal receita) {
        if (receita.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        return lucro.divide(receita, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    private BigDecimal calcularImpostosTotal(Long clienteId, LocalDate dataInicio) {
        return viagemRepository.getImpostosTotal(clienteId, dataInicio)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal getManutencaoTotal(Long clienteId, LocalDate dataInicio) {
        return manutencaoRepository.getCustoTotalPorCliente(clienteId, dataInicio)
                .orElse(BigDecimal.ZERO);
    }

    private Double calcularPercentual(BigDecimal valor, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        return valor.divide(total, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    private Double calcularTaxaRetornoVazio(Double kmVazio, Double kmTotal) {
        if (kmTotal == 0) return 0.0;
        return (kmVazio / kmTotal) * 100;
    }

    private String classificarIntensidade(BigDecimal lucro, BigDecimal maxLucro) {
        double percentual = lucro.divide(maxLucro, 2, RoundingMode.HALF_UP).doubleValue();
        if (percentual >= 0.7) return "ALTA";
        if (percentual >= 0.4) return "MÉDIA";
        return "BAIXA";
    }

    private String gerarCorIntensidade(BigDecimal lucro, BigDecimal maxLucro) {
        double percentual = lucro.divide(maxLucro, 2, RoundingMode.HALF_UP).doubleValue();
        
        if (percentual >= 0.7) return "#00C853"; // Verde
        if (percentual >= 0.4) return "#FFD600"; // Amarelo
        return "#FF1744"; // Vermelho
    }

    private Double calcularPercentualMeta(BigDecimal atual, BigDecimal meta) {
        if (meta.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        return atual.divide(meta, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    private String determinarStatusMeta(BigDecimal projecao, BigDecimal meta) {
        double percentual = calcularPercentualMeta(projecao, meta);
        if (percentual >= 100) return "SUPERADO";
        if (percentual >= 85) return "NO_CAMINHO";
        return "ATRASADO";
    }

    private BigDecimal calcularTotalContasAPagar(Long clienteId, LocalDate dataInicio, LocalDate dataFim) {
        BigDecimal total = contaPagarRepository.sumTotalAPagarPorClienteEPeriodo(clienteId, dataInicio, dataFim);
        return total != null ? total : BigDecimal.ZERO;
    }

    private BigDecimal calcularTotalContasAReceber(Long clienteId, LocalDate dataInicio, LocalDate dataFim) {
        BigDecimal total = contaReceberRepository.sumTotalAReceberPorClienteEPeriodo(clienteId, dataInicio, dataFim);
        return total != null ? total : BigDecimal.ZERO;
    }

    private BigDecimal calcularTotalComissoesMotoristas(Long clienteId, LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime dataInicioDateTime = dataInicio.atStartOfDay();
        LocalDateTime dataFimDateTime = dataFim.atTime(23, 59, 59);
        BigDecimal total = comissaoMotoristaRepository.sumTotalComissoesPorClienteEPeriodo(clienteId, dataInicioDateTime, dataFimDateTime);
        return total != null ? total : BigDecimal.ZERO;
    }
}
