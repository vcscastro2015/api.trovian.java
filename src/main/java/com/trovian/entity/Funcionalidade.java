package com.trovian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "funcionalidade")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Funcionalidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String categoria;

    @Column
    private Integer ordemMenu;
}
