package com.trovian.entity.relatorios;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "relatorios_gerados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioGerado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private RelatorioTemplate template;

    @Column(nullable = false, length = 200)
    private String nomeArquivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FormatoRelatorio formato;

    @Column(columnDefinition = "TEXT")
    private String parametrosUsados; // JSON com os parâmetros utilizados

    @Column(nullable = false)
    private Long totalRegistros;

    @Column(length = 500)
    private String caminhoArquivo; // Caminho no servidor/S3

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(name = "usuario_id")
    private Long usuarioId; // Quem gerou o relatório

    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }
}
