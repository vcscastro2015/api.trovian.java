package com.trovian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parametro_local")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParametroLocal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "limite_veiculos_mesmo_local")
    private Integer limiteVeiculosMesmoLocal;

    @Column(name = "tempo_minimo_permanencia")
    private Integer tempoMinimoDePermanencia;

    @Column(name = "tempo_maximo_permanencia")
    private Integer tempoMaximoDePermanencia;
}
