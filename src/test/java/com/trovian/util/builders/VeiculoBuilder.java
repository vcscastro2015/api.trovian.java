package com.trovian.util.builders;

import com.trovian.entity.Veiculo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Builder de dados de teste para Veiculo
 */
public class VeiculoBuilder {

    private Long id = 1L;
    private String placa = "ABC-1234";
    private BigDecimal tara = BigDecimal.valueOf(15.0);     // 15 toneladas
    private Double cargaMaxima = 50.0;                      // 50 toneladas
    private Integer numeroEixos = 6;
    private Date dataCadastro = new Date();

    public static VeiculoBuilder umVeiculo() {
        return new VeiculoBuilder();
    }

    public VeiculoBuilder comId(Long id) {
        this.id = id;
        return this;
    }

    public VeiculoBuilder comPlaca(String placa) {
        this.placa = placa;
        return this;
    }

    public VeiculoBuilder comTara(BigDecimal tara) {
        this.tara = tara;
        return this;
    }

    public VeiculoBuilder comCargaMaxima(Double cargaMaxima) {
        this.cargaMaxima = cargaMaxima;
        return this;
    }

    public VeiculoBuilder comNumeroEixos(Integer numeroEixos) {
        this.numeroEixos = numeroEixos;
        return this;
    }

    public VeiculoBuilder semCargaMaxima() {
        this.cargaMaxima = null;
        return this;
    }

    public VeiculoBuilder semNumeroEixos() {
        this.numeroEixos = null;
        return this;
    }

    public Veiculo build() {
        Veiculo veiculo = new Veiculo();
        veiculo.setId(id);
        veiculo.setPlaca(placa);
        veiculo.setTara(tara);
        veiculo.setCargaMaxima(cargaMaxima);
        veiculo.setNumeroEixos(numeroEixos);
        veiculo.setDataCadastro(dataCadastro);
        return veiculo;
    }
}
