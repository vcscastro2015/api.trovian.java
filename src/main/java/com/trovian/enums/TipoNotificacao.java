package com.trovian.enums;

public enum TipoNotificacao {
    MANUTENCAO_VENCIDA("Manutenção Vencida"),
    MANUTENCAO_PROXIMA("Manutenção Próxima"),
    CHECKLIST_PENDENTE("Checklist Pendente"),
    FINANCEIRO_PENDENTE("Pendência Financeira"),
    ALERTA_GERAL("Alerta Geral"),
    CONFIRMACAO("Confirmação"),
    LEMBRETE("Lembrete"),
    MENSAGEM_BEM_VINDO("Bem vindo"),
    EXCESSO_VELOCIDADE("Excesso Velocidade");

    private final String descricao;

    TipoNotificacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}