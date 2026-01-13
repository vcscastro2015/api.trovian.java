package com.trovian.entity.relatorios;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parametros_relatorio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParametroRelatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private RelatorioTemplate template;

    @Column(nullable = false, length = 50)
    private String nome; // data_inicio, data_fim, veiculo_id, etc

    @Column(nullable = false, length = 100)
    private String label; // "Data Início", "Data Fim", "Veículo"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoParametro tipo;

    @Column(nullable = false)
    private Boolean obrigatorio = true;

    @Column(length = 255)
    private String valorPadrao;

    @Column(length = 500)
    private String opcoes; // JSON com opções para SELECT

    @Column(name = "ordem")
    private Integer ordem = 0;
}
