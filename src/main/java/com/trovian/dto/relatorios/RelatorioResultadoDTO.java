package com.trovian.dto.relatorios;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioResultadoDTO {
    private String nomeRelatorio;
    private List<String> colunas;
    private List<Map<String, Object>> dados;
    private Long totalRegistros;
    private Map<String, Object> totalizadores;
}
