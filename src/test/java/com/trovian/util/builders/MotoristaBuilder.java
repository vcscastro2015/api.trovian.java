package com.trovian.util.builders;

import com.trovian.entity.Motorista;
import com.trovian.enums.Sexo;

import java.util.Date;

/**
 * Builder de dados de teste para Motorista
 */
public class MotoristaBuilder {

    private Long id = 1L;
    private String nome = "João da Silva";
    private String cpf = "12345678901";
    private String numeroCnh = "12345678901";
    private Date validadeCnh = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
    private Date dataNascimento = new Date(631152000000L); // 1990-01-01
    private Double comissao = 8.0;  // 8%

    public static MotoristaBuilder umMotorista() {
        return new MotoristaBuilder();
    }

    public MotoristaBuilder comId(Long id) {
        this.id = id;
        return this;
    }

    public MotoristaBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public MotoristaBuilder comComissao(Double comissao) {
        this.comissao = comissao;
        return this;
    }

    public MotoristaBuilder semComissao() {
        this.comissao = null;
        return this;
    }

    public Motorista build() {
        Motorista motorista = new Motorista();
        motorista.setId(id);
        motorista.setNome(nome);
        motorista.setCpf(cpf);
        motorista.setNumeroCnh(numeroCnh);
        motorista.setValidadeCnh(validadeCnh);
        motorista.setDataNascimento(dataNascimento);
        motorista.setComissao(comissao);
        return motorista;
    }
}
