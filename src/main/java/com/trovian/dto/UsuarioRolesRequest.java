package com.trovian.dto;

import com.trovian.enums.Role;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class UsuarioRolesRequest {

    @NotEmpty(message = "Pelo menos uma role é obrigatória")
    private Set<Role> roles;
}
