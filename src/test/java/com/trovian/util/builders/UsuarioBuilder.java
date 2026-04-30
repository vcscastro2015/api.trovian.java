package com.trovian.util.builders;

import com.trovian.entity.Cliente;
import com.trovian.entity.Usuario;
import com.trovian.enums.Role;

import java.util.Set;

/**
 * Builder de dados de teste para Usuario
 */
public class UsuarioBuilder {

    private Long id = 1L;
    private String email = "usuario@teste.com";
    private String senha = "$2a$10$hashedPasswordForTests";
    private String nome = "Usuário Teste";
    private String telefone = "11999999999";
    private Boolean ativo = true;
    private Set<Role> roles = Set.of(Role.USER);
    private Cliente cliente = ClienteBuilder.umCliente().build();

    public static UsuarioBuilder umUsuario() {
        return new UsuarioBuilder();
    }

    public UsuarioBuilder comId(Long id) {
        this.id = id;
        return this;
    }

    public UsuarioBuilder comEmail(String email) {
        this.email = email;
        return this;
    }

    public UsuarioBuilder comSenha(String senha) {
        this.senha = senha;
        return this;
    }

    public UsuarioBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public UsuarioBuilder comRoles(Set<Role> roles) {
        this.roles = roles;
        return this;
    }

    public UsuarioBuilder comCliente(Cliente cliente) {
        this.cliente = cliente;
        return this;
    }

    public UsuarioBuilder inativo() {
        this.ativo = false;
        return this;
    }

    public Usuario build() {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setNome(nome);
        usuario.setTelefone(telefone);
        usuario.setAtivo(ativo);
        usuario.setRoles(roles);
        usuario.setCliente(cliente);
        return usuario;
    }
}
