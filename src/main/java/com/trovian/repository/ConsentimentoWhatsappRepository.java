package com.trovian.repository;

import com.trovian.entity.ConsentimentoWhatsapp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsentimentoWhatsappRepository extends JpaRepository<ConsentimentoWhatsapp, Long> {
}