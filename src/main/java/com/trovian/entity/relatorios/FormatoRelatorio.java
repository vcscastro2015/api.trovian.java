package com.trovian.entity.relatorios;

public enum FormatoRelatorio {
    PDF("application/pdf", ".pdf"),
    EXCEL("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx"),
    CSV("text/csv", ".csv"),
    JSON("application/json", ".json");

    private final String mimeType;
    private final String extensao;

    FormatoRelatorio(String mimeType, String extensao) {
        this.mimeType = mimeType;
        this.extensao = extensao;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtensao() {
        return extensao;
    }
}
