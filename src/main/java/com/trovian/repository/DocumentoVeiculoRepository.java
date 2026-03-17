package com.trovian.repository;

import com.trovian.entity.DocumentoVeiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DocumentoVeiculoRepository extends JpaRepository<DocumentoVeiculo, Long> {

    List<DocumentoVeiculo> findByVeiculoId(Long veiculoId);

    List<DocumentoVeiculo> findByVeiculoIdAndDataEnvioBetween(Long veiculoId, LocalDateTime inicio, LocalDateTime fim);

    List<DocumentoVeiculo> findByClienteId(Long clienteId);
}
