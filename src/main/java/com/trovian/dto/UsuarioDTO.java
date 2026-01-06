package com.trovian.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UsuarioDTO {
    private Long id;
    private String email;
    private String nome;
    private String telefone;
    private Boolean ativo;
    private Set<String> roles;
    private LocalDateTime ultimoLogin;
    private LocalDateTime criadoEm;
}
