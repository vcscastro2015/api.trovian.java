package com.trovian.dto;

import com.trovian.enums.Role;
import com.trovian.enums.StatusWhatsapp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private Boolean ativo;
    private Set<Role> roles;
    private Set<String> funcionalidades;
    private LocalDateTime ultimoLogin;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private Long clienteId;
    private String clienteNome;
    private Boolean receberNotificacao;
    private Boolean consultarVeiculosWhatsapp;
    private StatusWhatsapp statusWhatsapp;
}
