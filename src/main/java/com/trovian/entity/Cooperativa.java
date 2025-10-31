package com.trovian.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "cooperativas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cooperativa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    @Column(nullable = false, length = 200)
    private String nome;

    @NotBlank(message = "CNPJ é obrigatório")
    @Size(max = 18, message = "CNPJ deve ter no máximo 18 caracteres")
    @Column(nullable = false, length = 18, unique = true)
    private String cnpj;

    @Size(max = 300, message = "Endereço deve ter no máximo 300 caracteres")
    @Column(length = 300)
    private String endereco;

    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    @Column(length = 100)
    private String cidade;

    @Size(max = 2, message = "UF deve ter 2 caracteres")
    @Column(length = 2)
    private String uf;

    @Size(max = 9, message = "CEP deve ter no máximo 9 caracteres")
    @Column(length = 9)
    private String cep;

    @NotNull(message = "Status ativo é obrigatório")
    @Column(nullable = false)
    private Boolean ativa = true;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
