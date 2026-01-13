package com.trovian.dto.relatorios;

import com.trovian.entity.relatorios.TipoParametro;
import lombok.Data;

@Data
public class ParametroRelatorioDTO {
    private Long id;
    private String nome;
    private String label;
    private TipoParametro tipo;
    private Boolean obrigatorio;
    private String valorPadrao;
    private String opcoes;
    private Integer ordem;
}
