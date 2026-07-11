package com.trovian.enums;

public enum StatusComando {
    BLOQUEAR("BLOQUEAR", "BLOQ"),
    DESBLOQUEAR("DESBLOQUEAR", "DESB");

    private final String descricao;
    private final String sigla;

    StatusComando(String descricao, String sigla) {
        this.descricao = descricao;
        this.sigla = sigla;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getSigla(){
        return sigla;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
