package com.trovian.dto;

import com.trovian.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

@Data
public class UsuarioCriarRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private String telefone;

    @NotEmpty(message = "Pelo menos uma role é obrigatória")
    private Set<Role> roles;

    private Boolean ativo = true;

    @NotNull(message = "ID do cliente é obrigatório")
    @Schema(description = "ID do cliente proprietário", example = "1", required = true)
    private Long clienteId;

    private String[] funcionalidades;

    private Boolean receberNotificacao = false;

    private Boolean consultarVeiculosWhatsapp = false;
}
