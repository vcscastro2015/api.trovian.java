package com.trovian.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEstatisticasResponse {
    private long totalUsuarios;
    private long usuariosAtivos;
    private long usuariosInativos;
}
