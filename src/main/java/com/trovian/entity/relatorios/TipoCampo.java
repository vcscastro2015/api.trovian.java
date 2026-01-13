package com.trovian.entity.relatorios;

public enum TipoCampo {
    TEXTO("Texto"),
    NUMERO("Número"),
    DECIMAL("Decimal"),
    MOEDA("Moeda"),
    DATA("Data"),
    DATAHORA("Data e Hora"),
    BOOLEAN("Sim/Não"),
    PERCENTUAL("Percentual");

    private final String descricao;

    TipoCampo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
