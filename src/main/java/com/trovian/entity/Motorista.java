package com.trovian.entity;

import com.trovian.enums.Sexo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Entidade que representa um Motorista no sistema
 */
@Entity
@Table(name = "motorista")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Motorista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", length = 100, nullable = false)
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @Column(name = "data_nascimento", nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull(message = "Data de nascimento é obrigatória")
    private Date dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", length = 20)
    private Sexo sexo;

    @Column(name = "cpf", length = 11, nullable = false)
    @NotBlank(message = "CPF é obrigatório")
    @Size(max = 11, message = "CPF deve ter no máximo 11 caracteres")
    private String cpf;

    @Column(name = "numero_cnh", length = 15, nullable = false)
    @NotBlank(message = "Número da CNH é obrigatório")
    @Size(max = 15, message = "Número da CNH deve ter no máximo 15 caracteres")
    private String numeroCnh;

    @Column(name = "validade_cnh", nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull(message = "Validade da CNH é obrigatória")
    private Date validadeCnh;

    @Column(name = "data_admissao")
    @Temporal(TemporalType.DATE)
    private Date dataAdmissao;

    @Column(name = "categoria_cnh", length = 2, nullable = false)
    @NotBlank(message = "Categoria da CNH é obrigatória")
    @Size(max = 2, message = "Categoria da CNH deve ter no máximo 2 caracteres")
    private String categoriaCnh;

    @Column(name = "telefone", length = 15)
    @Size(max = 15, message = "Telefone deve ter no máximo 15 caracteres")
    private String telefone;

    @NotNull(message = "Status é obrigatório")
    @Column(name = "status", nullable = false)
    private Boolean status;

    @Column(name = "comissao")
    private Double comissao;

    @Column(name = "logradouro")
    private String logradouro;

    @Column(name = "numero")
    private String numero;

    @Column(name = "bairro", length = 45)
    @Size(max = 45, message = "Bairro deve ter no máximo 45 caracteres")
    private String bairro;

    @Column(name = "cep", length = 9)
    @Size(max = 9, message = "CEP deve ter no máximo 9 caracteres")
    private String cep;

    @Column(name = "complemento", length = 50)
    @Size(max = 50, message = "Complemento deve ter no máximo 50 caracteres")
    private String complemento;

    @Column(name = "cidade")
    private String cidade;

    @Column(name = "uf", length = 2)
    @Size(max = 2, message = "UF deve ter no máximo 2 caracteres")
    private String uf;

    @Column(name = "liberar_abastecimento_wpp")
    private Boolean liberarAbastecimentoWpp;

    @Column(name = "liberar_check_list_wpp")
    private Boolean liberarCheckListWpp;

    @Column(name = "liberar_documentos_wpp")
    private Boolean liberarDocumentosWpp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "Cliente é obrigatório")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consentimento_whatsapp_id")
    private ConsentimentoWhatsapp consentimentoWhatsapp;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Método executado antes de persistir a entidade
     */
    @PrePersist
    protected void onCreate() {
        this.dataCadastro = new Date();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Método executado antes de atualizar a entidade
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
