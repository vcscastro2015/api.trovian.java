package com.trovian.repository;

import com.trovian.entity.ImagemArquivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImagemArquivoRepository extends JpaRepository<ImagemArquivo, Long> {

    Optional<ImagemArquivo> findByContaReceberId(Long id);
    Optional<ImagemArquivo> findByAbastecimentoId(Long id);
}
