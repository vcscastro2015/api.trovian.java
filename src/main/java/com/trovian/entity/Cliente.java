package com.trovian.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    @Column(nullable = false, length = 200)
    private String nome;

    @NotBlank(message = "CNPJ/CPF é obrigatório")
    @Size(max = 18, message = "CNPJ/CPF deve ter no máximo 18 caracteres")
    @Column(nullable = false, unique = true, length = 18)
    private String cnpjCpf;

    @Size(max = 20, message = "IE deve ter no máximo 20 caracteres")
    @Column(length = 20)
    private String ie;

    @Size(max = 300, message = "Endereço deve ter no máximo 300 caracteres")
    @Column(length = 300)
    private String endereco;

    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    @Column(length = 100)
    private String bairro;

    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    @Column(length = 100)
    private String complemento;

    @Size(max = 20, message = "Número deve ter no máximo 20 caracteres")
    @Column(length = 20)
    private String numero;

    @Size(max = 9, message = "CEP deve ter no máximo 9 caracteres")
    @Column(length = 9)
    private String cep;

    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    @Column(length = 100)
    private String cidade;

    @Size(max = 2, message = "UF deve ter no máximo 2 caracteres")
    @Column(length = 2)
    private String uf;

    @Size(max = 500, message = "Contatos deve ter no máximo 500 caracteres")
    @Column(length = 500)
    private String contatos;

    @Size(max = 200, message = "Telefones deve ter no máximo 200 caracteres")
    @Column(length = 200)
    private String telefones;

    @NotNull(message = "Status é obrigatório")
    @Column(nullable = false)
    private Boolean status = true;

    @NotNull(message = "Cooperado é obrigatório")
    @Column(nullable = false)
    private Boolean cooperado = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cooperativa_id")
    private Cooperativa cooperativa;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        dataCadastro = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
