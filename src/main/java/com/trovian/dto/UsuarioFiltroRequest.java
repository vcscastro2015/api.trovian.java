package com.trovian.dto;

import com.trovian.enums.Role;
import lombok.Data;

@Data
public class UsuarioFiltroRequest {

    private String nome;
    private String email;
    private Boolean ativo;
    private Role role;
}
