package com.trovian.enums;

public enum StatusNotificacao {
    PENDENTE("Pendente"),
    ENVIANDO("Enviando"),
    ENVIADA("Enviada"),
    LIDA("Lida"),
    RESPONDIDA("Respondida"),
    ERRO("Erro"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusNotificacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}