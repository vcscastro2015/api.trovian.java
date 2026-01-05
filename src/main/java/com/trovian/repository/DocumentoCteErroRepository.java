package com.trovian.repository;

import com.trovian.document.DocumentoCteErro;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentoCteErroRepository extends MongoRepository<DocumentoCteErro, String> {

    // Buscar por chave de acesso
    Optional<DocumentoCteErro> findByChaveAcesso(String chaveAcesso);

    // Buscar por número do CT-e
    List<DocumentoCteErro> findByNumero(String numero);

    // Buscar por CNPJ do emitente
    List<DocumentoCteErro> findByEmitenteCnpj(String emitenteCnpj);
}
