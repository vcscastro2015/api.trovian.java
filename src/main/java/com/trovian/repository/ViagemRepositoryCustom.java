package com.trovian.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ViagemRepositoryCustom {
    // Métodos existentes (sem dataFim)
    List<Object[]> getResumoGeral(Long clienteId, LocalDate dataInicio);
    List<Object[]> getLucratividadePorRota(Long clienteId, LocalDate dataInicio);
    List<Object[]> getViagensBaixaMargem(Long clienteId, LocalDate dataInicio, Double margemMaxima);
    List<Object[]> getEficienciaVeiculos(Long clienteId, LocalDate dataInicio);
    List<Object[]> getPerformanceMotoristas(Long clienteId, LocalDate dataInicio);
    List<Object[]> getTendenciasMensais(Long clienteId, LocalDate dataInicio);
    List<Object[]> getCustosOperacionais(Long clienteId, LocalDate dataInicio);
    List<Object[]> getIndicadoresRotas(Long clienteId, LocalDate dataInicio);
    List<Object[]> getMapaCalorRotas(Long clienteId, LocalDate dataInicio);
    Optional<BigDecimal> getImpostosTotal(Long clienteId, LocalDate dataInicio);
    Optional<Double> getTaxaOcupacaoVeiculo(Long veiculoId, LocalDate dataInicio);

    // Métodos com range de data (dataInicio + dataFim) para relatórios
    List<Object[]> getResumoGeralPeriodo(Long clienteId, LocalDate dataInicio, LocalDate dataFim);
    List<Object[]> getLucratividadePorRotaPeriodo(Long clienteId, LocalDate dataInicio, LocalDate dataFim);
    List<Object[]> getEficienciaVeiculosPeriodo(Long clienteId, LocalDate dataInicio, LocalDate dataFim);
    List<Object[]> getPerformanceMotoristasPeriodo(Long clienteId, LocalDate dataInicio, LocalDate dataFim);
    List<Object[]> getTendenciasMensaisPeriodo(Long clienteId, LocalDate dataInicio, LocalDate dataFim);
    List<Object[]> getCustosOperacionaisPeriodo(Long clienteId, LocalDate dataInicio, LocalDate dataFim);
    List<Object[]> getViagensBaixaMargemPeriodo(Long clienteId, LocalDate dataInicio, LocalDate dataFim, Double margemMaxima);
    List<Object[]> getReceitaPorClientePeriodo(LocalDate dataInicio, LocalDate dataFim);
    List<Object[]> getAnaliseCombustivelPorVeiculoPeriodo(Long clienteId, LocalDate dataInicio, LocalDate dataFim);
    List<Object[]> getAnaliseCombustivelPorRotaPeriodo(Long clienteId, LocalDate dataInicio, LocalDate dataFim);
}
