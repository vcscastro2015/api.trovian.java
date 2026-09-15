package com.trovian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "midia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Midia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "entidade_tipo", length = 40, nullable = false)
    private String entidadeTipo;

    @Column(name = "entidade_id", length = 64, nullable = false)
    private String entidadeId;

    @Column(name = "papel", length = 30)
    private String papel;

    @Column(name = "bucket", length = 100, nullable = false)
    private String bucket;

    @Column(name = "s3_key", nullable = false, columnDefinition = "TEXT")
    private String s3Key;

    @Column(name = "content_type", length = 60, nullable = false)
    private String contentType = "image/jpeg";

    @Column(name = "bytes")
    private Long bytes;

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

    @Column(name = "origem", length = 20, nullable = false)
    private String origem = "WEB";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enviada_por")
    private Usuario enviadaPor;

    @Column(name = "numero_telefone", length = 20)
    private String numeroTelefone;

    @Column(name = "capturada_em", columnDefinition = "timestamptz")
    private OffsetDateTime capturadaEm;

    @Column(name = "recebida_em", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime recebidaEm;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "evento_id")
    private UUID eventoId;

    @Column(name = "desafio_id")
    private UUID desafioId;

    @Column(name = "reuso_detectado", nullable = false)
    private Boolean reusoDetectado = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "midia_original_id")
    private Midia midiaOriginal;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @Column(name = "expurgar_em")
    private LocalDate expurgarEm;

    @Column(name = "expurgada_em", columnDefinition = "timestamptz")
    private OffsetDateTime expurgadaEm;

    @PrePersist
    protected void onCreate() {
        if (recebidaEm == null) {
            recebidaEm = OffsetDateTime.now();
        }
    }
}
