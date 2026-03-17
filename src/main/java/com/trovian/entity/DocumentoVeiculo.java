package com.trovian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "documento_veiculo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoVeiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motorista_id")
    private Motorista motorista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "nome_arquivo", length = 255)
    private String nomeArquivo;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Lob
    @Column(name = "conteudo_binario", nullable = false)
    private byte[] conteudoBinario;

    @Column(name = "data_envio")
    private LocalDateTime dataEnvio;

    @Column(name = "numero_telefone", length = 20)
    private String numeroTelefone;

    @PrePersist
    protected void onCreate() {
        if (dataEnvio == null) {
            dataEnvio = LocalDateTime.now();
        }
    }
}
