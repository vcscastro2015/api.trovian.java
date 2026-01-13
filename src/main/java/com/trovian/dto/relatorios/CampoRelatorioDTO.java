package com.trovian.dto.relatorios;

import com.trovian.entity.relatorios.TipoCampo;
import lombok.Data;

@Data
public class CampoRelatorioDTO {
    private Long id;
    private String nome;
    private String label;
    private TipoCampo tipo;
    private Boolean visivel;
    private Boolean totalizavel;
    private String formato;
    private Integer ordem;
    private Integer largura;
}
