package com.trovian.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comando")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comando {

    @Id
    @SequenceGenerator(name = "comando_id_seq", sequenceName = "comando_id_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(generator = "comando_id_seq")
    private Integer id;
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    @Column(nullable = false, length = 200)
    private String comando;
    @Column(nullable = false)
    private Boolean comandoEnviado = false;
    @Column(name = "data", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;
    @Column(nullable = false)
    private Boolean retornoRecebido = false;
    @Column(nullable = false, length = 10)
    private String sigla;
    private String valorComando;
    private Boolean comandoEmProcesso;
    private LocalDateTime dataFimProcesso;
    private String retornoAparelho;
    private Boolean dependeOutroComando;
    private Integer idComandoReferente;
    private Boolean mostrarRetorno;
    private Boolean comandoComErro;
    private Boolean temMaisIbutton;
    private Integer ultimoIbuttonInserido;
    private Integer cliente;
    private Integer sequencia;
    private String nomeDaFila;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo", referencedColumnName = "id", nullable = false)
    private Veiculo veiculo;

}
