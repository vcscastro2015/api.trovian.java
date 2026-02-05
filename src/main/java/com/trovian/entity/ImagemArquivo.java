package com.trovian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "imagem_arquivo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagemArquivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob // Indica ao JPA que é um Large Object
    @Column(name = "conteudo_binario", nullable = false)
    private byte[] conteudoBinario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_receber_id")
    private ContaReceber contaReceber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "abastecimento_id")
    private Abastecimento abastecimento;

}
