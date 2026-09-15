package com.trovian.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "comando")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comando {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    @Column(nullable = false, length = 200)
    private String comando;
    @Column(nullable = false)
    private Boolean comandoEnviado = false;
    @Column(name = "data", nullable = false, updatable = false)
    private OffsetDateTime dataCadastro;
    @Column(nullable = false)
    private Boolean retornoRecebido = false;
    @Column(nullable = false, length = 10)
    private String sigla;
    private String valorComando;
    private Boolean comandoEmProcesso;
    private OffsetDateTime dataFimProcesso;
    private String retornoAparelho;
    private Boolean dependeOutroComando;
    private Long idComandoReferente;
    private Boolean mostrarRetorno;
    private Boolean comandoComErro;
    private Boolean temMaisIbutton;
    private Integer ultimoIbuttonInserido;
    private Long cliente;
    private Integer sequencia;
    private String nomeDaFila;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo", referencedColumnName = "id", nullable = false)
    private Veiculo veiculo;

}
