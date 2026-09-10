package com.trovian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "imagem_resposta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagemResposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resposta_item_checklist_id", nullable = false, unique = true)
    private RespostaItemChecklist respostaItemChecklist;

    @Column(name = "conteudo_binario", nullable = false, columnDefinition = "bytea")
    private byte[] conteudoBinario;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;
}