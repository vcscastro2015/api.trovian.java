package com.trovian.dto;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.Set;
@Data
public class UsuarioDTO {
    private Long id;
    private String email;
    private String nome;
    private String telefone;
    private Boolean ativo;
    private Set<String> roles;
    private Set<String> funcionalidades;
    private OffsetDateTime ultimoLogin;
    private OffsetDateTime criadoEm;
}
