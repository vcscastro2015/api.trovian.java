package com.trovian.enums;

/**
 * Enum para representar o sexo do motorista
 */
public enum Sexo {
    MASCULINO("Masculino"),
    FEMININO("Feminino");

    private final String descricao;

    Sexo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
