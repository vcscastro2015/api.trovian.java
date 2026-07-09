package com.trovian.repository;

import com.trovian.entity.AgendamentoComandoVeiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendamentoComandoVeiculoRepository extends JpaRepository<AgendamentoComandoVeiculo, Long> {

    List<AgendamentoComandoVeiculo> findByAtivoTrue();

    List<AgendamentoComandoVeiculo> findByVeiculoIdAndAtivoTrue(Long idVeiculo);
}
