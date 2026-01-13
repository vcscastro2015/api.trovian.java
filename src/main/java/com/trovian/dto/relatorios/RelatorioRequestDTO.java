package com.trovian.dto.relatorios;

import com.trovian.entity.relatorios.FormatoRelatorio;
import lombok.Data;

import java.util.Map;

@Data
public class RelatorioRequestDTO {
    private Long templateId;
    private FormatoRelatorio formato;
    private Map<String, Object> parametros;
    private Boolean incluirGraficos = false;
}
