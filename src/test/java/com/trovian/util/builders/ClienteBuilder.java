package com.trovian.util.builders;

import com.trovian.entity.Cliente;

import java.util.UUID;

/**
 * Builder de dados de teste para Cliente
 */
public class ClienteBuilder {

    private Long id = 1L;
    private UUID uuid = UUID.randomUUID();
    private String nome = "Transportadora Teste Ltda";
    private String cnpjCpf = "12.345.678/0001-90";

    public static ClienteBuilder umCliente() {
        return new ClienteBuilder();
    }

    public ClienteBuilder comId(Long id) {
        this.id = id;
        return this;
    }

    public ClienteBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public ClienteBuilder comCnpjCpf(String cnpjCpf) {
        this.cnpjCpf = cnpjCpf;
        return this;
    }

    public Cliente build() {
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setUuid(uuid);
        cliente.setNome(nome);
        cliente.setCnpjCpf(cnpjCpf);
        return cliente;
    }
}
