package com.trovian.enums;

/**
 * Enum que define o nível de inclinação de um segmento de rota
 */
public enum NivelInclinacao {
    PLANO("Plano"),
    LEVE("Leve"),
    MODERADO("Moderado"),
    FORTE("Forte"),
    MUITO_FORTE("Muito Forte");

    private final String descricao;

    NivelInclinacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
