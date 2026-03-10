package com.trovian.dto;

import com.trovian.enums.Sexo;
import com.trovian.enums.StatusWhatsapp;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO para transferência de dados de Motorista
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de Motorista")
public class MotoristaDTO {

    @Schema(description = "ID do motorista", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nome do motorista", example = "João da Silva", required = true)
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @Schema(description = "Data de nascimento do motorista", example = "1990-05-15", required = true)
    @NotNull(message = "Data de nascimento é obrigatória")
    private Date dataNascimento;

    @Schema(description = "Sexo do motorista", example = "MASCULINO", allowableValues = {"MASCULINO", "FEMININO"})
    private Sexo sexo;

    @Schema(description = "CPF do motorista", example = "12345678900", required = true)
    @NotBlank(message = "CPF é obrigatório")
    @Size(max = 11, message = "CPF deve ter no máximo 11 caracteres")
    private String cpf;

    @Schema(description = "Número da CNH", example = "12345678901", required = true)
    @NotBlank(message = "Número da CNH é obrigatório")
    @Size(max = 15, message = "Número da CNH deve ter no máximo 15 caracteres")
    private String numeroCnh;

    @Schema(description = "Data de validade da CNH", example = "2025-12-31", required = true)
    @NotNull(message = "Validade da CNH é obrigatória")
    private Date validadeCnh;

    @Schema(description = "Data de admissão do motorista", example = "2020-01-15")
    private Date dataAdmissao;

    @Schema(description = "Categoria da CNH", example = "D", required = true)
    @NotBlank(message = "Categoria da CNH é obrigatória")
    @Size(max = 2, message = "Categoria da CNH deve ter no máximo 2 caracteres")
    private String categoriaCnh;

    @Schema(description = "Telefone do motorista", example = "11987654321")
    @Size(max = 15, message = "Telefone deve ter no máximo 15 caracteres")
    private String telefone;

    @NotNull(message = "Status é obrigatório")
    @Column(name = "status", nullable = false)
    private Boolean status;

    @Schema(description = "Comissão do motorista no frete (%)", example = "5.5")
    private Double comissao;

    @Schema(description = "Logradouro", example = "Rua das Flores")
    private String logradouro;

    @Schema(description = "Número do endereço", example = "123")
    private String numero;

    @Schema(description = "Bairro", example = "Centro")
    @Size(max = 45, message = "Bairro deve ter no máximo 45 caracteres")
    private String bairro;

    @Schema(description = "CEP", example = "12345-678")
    @Size(max = 9, message = "CEP deve ter no máximo 9 caracteres")
    private String cep;

    @Schema(description = "Complemento do endereço", example = "Apto 101")
    @Size(max = 50, message = "Complemento deve ter no máximo 50 caracteres")
    private String complemento;

    @Schema(description = "Cidade", example = "São Paulo")
    private String cidade;

    @Schema(description = "UF", example = "SP")
    @Size(max = 2, message = "UF deve ter no máximo 2 caracteres")
    private String uf;

    @Schema(description = "Liberar abastecimento via WhatsApp", example = "true")
    private Boolean liberarAbastecimentoWpp;

    @Schema(description = "Liberar checklist via WhatsApp", example = "true")
    private Boolean liberarCheckListWpp;

    @Schema(description = "Liberar documentos via WhatsApp", example = "true")
    private Boolean liberarDocumentosWpp;

    @Schema(description = "ID do cliente ao qual o motorista pertence", example = "1", required = true)
    @NotNull(message = "Cliente é obrigatório")
    private Long clienteId;

    @Schema(description = "Nome do cliente", example = "Transportadora XYZ", accessMode = Schema.AccessMode.READ_ONLY)
    private String clienteNome;

    @Schema(description = "Data de cadastro", example = "2024-01-01T10:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private Date dataCadastro;

    @Schema(description = "Data de última atualização", example = "2024-01-10T15:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private String updatedAt;

    @Schema(description = "Status do consentimento WhatsApp", accessMode = Schema.AccessMode.READ_ONLY)
    private StatusWhatsapp statusWhatsapp;
}
