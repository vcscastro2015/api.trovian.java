package com.trovian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "midia_angulo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MidiaAngulo {

    @Id
    @Column(name = "codigo", length = 30)
    private String codigo;

    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "instrucao_app", columnDefinition = "TEXT")
    private String instrucaoApp;

    @Column(name = "sorteavel", nullable = false)
    private Boolean sorteavel = true;

    @Column(name = "obrigatorio", nullable = false)
    private Boolean obrigatorio = false;

    @Column(name = "ordem", nullable = false)
    private Short ordem = 100;
}
