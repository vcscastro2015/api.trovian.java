package com.trovian.enums;

public enum TipoRecorrenciaComando {
    DIARIO("Diário"),
    MENSAL("Mensal"),
    DIA_ESPECIFICO("Dia Específico");

    private final String descricao;

    TipoRecorrenciaComando(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
