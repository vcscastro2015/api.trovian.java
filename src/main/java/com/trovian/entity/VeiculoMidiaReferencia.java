package com.trovian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "veiculo_midia_referencia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoMidiaReferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo", nullable = false)
    private Veiculo veiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "angulo", length = 30, nullable = false)
    private String angulo;

    @Column(name = "bucket", length = 100, nullable = false)
    private String bucket;

    @Column(name = "s3_key", nullable = false, columnDefinition = "TEXT")
    private String s3Key;

    @Column(name = "content_type", length = 50, nullable = false)
    private String contentType = "image/jpeg";

    @Column(name = "bytes")
    private Integer bytes;

    @Column(name = "largura_px")
    private Integer larguraPx;

    @Column(name = "altura_px")
    private Integer alturaPx;

    @Column(name = "sha256", nullable = false, columnDefinition = "bytea")
    private byte[] sha256;

    @Column(name = "phash", columnDefinition = "bit(64)")
    private String phash;

    @Column(name = "dhash", columnDefinition = "bit(64)")
    private String dhash;

    @Column(name = "embedding_versao", length = 20)
    private String embeddingVersao;

    @Column(name = "principal", nullable = false)
    private Boolean principal = false;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @Column(name = "capturada_em", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime capturadaEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capturada_por")
    private Usuario capturadaPor;

    @Column(name = "origem", length = 20, nullable = false)
    private String origem = "CADASTRO";

    @Column(name = "valida_ate")
    private LocalDate validaAte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substituida_por")
    private VeiculoMidiaReferencia substituida_por;

    @Column(name = "motivo_baixa", columnDefinition = "TEXT")
    private String motivoBaixa;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    @PrePersist
    protected void onCreate() {
        if (capturadaEm == null) {
            capturadaEm = OffsetDateTime.now();
        }
    }
}
