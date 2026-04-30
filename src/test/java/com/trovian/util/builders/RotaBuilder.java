package com.trovian.util.builders;

import com.trovian.entity.Rota;
import com.trovian.entity.RotaEstatisticas;

import java.util.Date;

/**
 * Builder de dados de teste para Rota.
 * Fornece uma rota completa com estatísticas de elevação para que
 * ViagemService.calcularFatorTerreno() funcione sem divisão por zero.
 */
public class RotaBuilder {

    private Long id = 1L;
    private String nome = "SP - RJ (Teste)";
    private Double distanciaTotal = 500_000.0;  // 500 km em metros
    private Boolean ativa = true;

    // Estatísticas de elevação com valores não-zero
    private Double ganhoTotalElevacao = 800.0;   // metros de subida total
    private Double perdaTotalElevacao = 750.0;   // metros de descida total
    private Double distanciaSubida = 150_000.0;  // 150 km em metros
    private Double distanciaDescida = 100_000.0; // 100 km em metros
    private Double distanciaPlano = 250_000.0;   // 250 km em metros

    public static RotaBuilder umaRota() {
        return new RotaBuilder();
    }

    public RotaBuilder comId(Long id) {
        this.id = id;
        return this;
    }

    public RotaBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public RotaBuilder comDistanciaTotal(Double distanciaTotal) {
        this.distanciaTotal = distanciaTotal;
        return this;
    }

    public Rota build() {
        RotaEstatisticas estatisticas = new RotaEstatisticas();
        estatisticas.setId(id);
        estatisticas.setGanhoTotalElevacao(ganhoTotalElevacao);
        estatisticas.setPerdaTotalElevacao(perdaTotalElevacao);
        estatisticas.setDistanciaSubida(distanciaSubida);
        estatisticas.setDistanciaDescida(distanciaDescida);
        estatisticas.setDistanciaPlano(distanciaPlano);

        Rota rota = new Rota();
        rota.setId(id);
        rota.setNome(nome);
        rota.setDistanciaTotal(distanciaTotal);
        rota.setAtiva(ativa);
        rota.setDataCadastro(new Date());
        rota.setEstatisticas(estatisticas);

        return rota;
    }
}
