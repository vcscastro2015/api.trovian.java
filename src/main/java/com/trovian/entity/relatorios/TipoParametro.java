package com.trovian.entity.relatorios;

public enum TipoParametro {
    DATA("Data"),
    DATA_PERIODO("Período de Datas"),
    NUMERO("Número"),
    TEXTO("Texto"),
    SELECT("Seleção"),
    MULTISELECT("Múltipla Seleção"),
    BOOLEAN("Sim/Não");

    private final String descricao;

    TipoParametro(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
