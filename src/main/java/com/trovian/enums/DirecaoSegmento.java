package com.trovian.enums;

/**
 * Enum que define a direção de um segmento de rota
 */
public enum DirecaoSegmento {
    SUBIDA("Subida"),
    DESCIDA("Descida"),
    PLANO("Plano");

    private final String descricao;

    DirecaoSegmento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
