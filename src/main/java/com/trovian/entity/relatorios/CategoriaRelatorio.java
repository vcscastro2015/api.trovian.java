package com.trovian.entity.relatorios;

public enum CategoriaRelatorio {
    FINANCEIRO("Financeiro"),
    MANUTENCAO("Manutenção"),
    CHECKLIST("Checklist"),
    VEICULOS("Veículos"),
    MOTORISTAS("Motoristas"),
    OPERACIONAL("Operacional"),
    GERENCIAL("Gerencial"),
    PERSONALIZADO("Personalizado");

    private final String descricao;

    CategoriaRelatorio(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
