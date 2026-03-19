package com.trovian.repository;

import com.trovian.entity.ImagemResposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImagemRespostaRepository extends JpaRepository<ImagemResposta, Long> {

    Optional<ImagemResposta> findByRespostaItemChecklistId(UUID respostaItemChecklistId);

    void deleteByRespostaItemChecklistId(UUID respostaItemChecklistId);
}