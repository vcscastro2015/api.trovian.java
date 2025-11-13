package com.trovian.enums;

/**
 * Enum que define os tipos de pontos em uma rota
 */
public enum TipoPonto {
    ORIGEM("Ponto de Origem"),
    DESTINO("Ponto de Destino"),
    WAYPOINT("Ponto Intermediário");

    private final String descricao;

    TipoPonto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
