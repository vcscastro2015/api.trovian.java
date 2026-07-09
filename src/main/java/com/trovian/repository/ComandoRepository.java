package com.trovian.repository;

import com.trovian.entity.Comando;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComandoRepository extends JpaRepository<Comando, Integer> {

    Optional<Comando> findFirstByVeiculoIdOrderByDataCadastroDesc(Long idVeiculo);
}
