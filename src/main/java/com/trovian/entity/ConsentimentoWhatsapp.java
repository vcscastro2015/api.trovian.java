package com.trovian.entity;

import com.trovian.enums.StatusWhatsapp;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "consentimento_whatsapp")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsentimentoWhatsapp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String telefone;
    private String ipDoTelefone;
    private boolean autorizado = false;
    private LocalDate dataAutorizacao;
    private String origem;
    private String mensagemRecebida;
    @Column(nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();
    @Column(nullable = false)
    private OffsetDateTime dataCadastro = OffsetDateTime.now();
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private StatusWhatsapp statusWhatsapp;
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

}
