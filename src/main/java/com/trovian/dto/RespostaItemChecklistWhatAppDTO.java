package com.trovian.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespostaItemChecklistWhatAppDTO {

    private String itemId;
    private String descricao;
    private String resposta;
    private String fotoBase64;
    private String observacao;
}
