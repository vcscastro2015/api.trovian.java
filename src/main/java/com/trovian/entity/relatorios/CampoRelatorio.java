package com.trovian.entity.relatorios;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "campos_relatorio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampoRelatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private RelatorioTemplate template;

    @Column(nullable = false, length = 100)
    private String nome; // Nome do campo no SQL

    @Column(nullable = false, length = 100)
    private String label; // Label para exibição

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoCampo tipo;

    @Column(nullable = false)
    private Boolean visivel = true;

    @Column(nullable = false)
    private Boolean totalizavel = false; // Pode fazer SUM, COUNT, etc

    @Column(length = 50)
    private String formato; // Para datas, moedas, etc

    @Column(name = "ordem")
    private Integer ordem = 0;

    @Column(name = "largura")
    private Integer largura; // Largura em pixels ou %
}
