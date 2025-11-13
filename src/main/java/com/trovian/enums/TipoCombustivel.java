package com.trovian.enums;

public enum TipoCombustivel {
    DIESEL("Diesel"),
    GASOLINA("Gasolina"),
    ETANOL("Etanol"),
    GNV("GNV"),
    ARLA32("Arla 32");

    private final String descricao;

    TipoCombustivel(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
