package com.trovian.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPageResponse {

    private List<UsuarioResponse> usuarios;
    private int paginaAtual;
    private int totalPaginas;
    private long totalElementos;
    private int tamanhoPagina;
    private boolean primeira;
    private boolean ultima;
}
