package com.trovian.dto.relatorios;

import com.trovian.entity.relatorios.CategoriaRelatorio;
import lombok.Data;

import java.util.List;

@Data
public class RelatorioTemplateDTO {
    private Long id;
    private String nome;
    private String descricao;
    private CategoriaRelatorio categoria;
    private Boolean ativo;
    private List<ParametroRelatorioDTO> parametros;
    private List<CampoRelatorioDTO> campos;
}
