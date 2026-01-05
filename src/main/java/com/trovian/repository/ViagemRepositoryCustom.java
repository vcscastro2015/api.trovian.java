package com.trovian.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ViagemRepositoryCustom {
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
}
