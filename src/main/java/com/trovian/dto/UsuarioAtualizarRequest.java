package com.trovian.dto;

import com.trovian.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UsuarioAtualizarRequest {

    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @Email(message = "Email inválido")
    private String email;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private String telefone;

    @NotEmpty(message = "Pelo menos uma role é obrigatória")
    private Set<Role> roles;

    private Boolean ativo;

    @NotNull(message = "ID do cliente é obrigatório")
    @Schema(description = "ID do cliente proprietário", example = "1", required = true)
    private Long clienteId;

    private String[] funcionalidades;

    private Boolean receberNotificacao;

    private Boolean consultarVeiculosWhatsapp;
}
