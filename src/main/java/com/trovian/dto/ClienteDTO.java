package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para transferência de dados de Cliente")
public class ClienteDTO {

    @Schema(description = "ID do cliente", accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;

    @Schema(description = "UUID único do cliente", accessMode = Schema.AccessMode.READ_ONLY, example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID uuid;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    @Schema(description = "Nome completo do cliente", required = true, example = "João da Silva")
    private String nome;

    @NotBlank(message = "CNPJ/CPF é obrigatório")
    @Size(max = 18, message = "CNPJ/CPF deve ter no máximo 18 caracteres")
    @Schema(description = "CNPJ ou CPF do cliente", required = true, example = "123.456.789-00")
    private String cnpjCpf;

    @Size(max = 20, message = "IE deve ter no máximo 20 caracteres")
    @Schema(description = "Inscrição Estadual", example = "123.456.789.012")
    private String ie;

    @Size(max = 300, message = "Endereço deve ter no máximo 300 caracteres")
    @Schema(description = "Endereço completo", example = "Rua das Flores, 123")
    private String endereco;

    @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    @Schema(description = "Bairro", example = "Centro")
    private String bairro;

    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    @Schema(description = "Complemento do endereço", example = "Apto 101")
    private String complemento;

    @Size(max = 20, message = "Número deve ter no máximo 20 caracteres")
    @Schema(description = "Número do endereço", example = "123")
    private String numero;

    @Size(max = 9, message = "CEP deve ter no máximo 9 caracteres")
    @Schema(description = "CEP", example = "12345-678")
    private String cep;

    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    @Schema(description = "Cidade", example = "São Paulo")
    private String cidade;

    @Size(max = 2, message = "UF deve ter no máximo 2 caracteres")
    @Schema(description = "Estado (UF)", example = "SP")
    private String uf;

    @Size(max = 500, message = "Contatos deve ter no máximo 500 caracteres")
    @Schema(description = "Informações de contato", example = "email@exemplo.com")
    private String contatos;

    @Size(max = 200, message = "Telefones deve ter no máximo 200 caracteres")
    @Schema(description = "Telefones de contato", example = "(11) 98765-4321")
    private String telefones;

    @NotNull(message = "Status é obrigatório")
    @Schema(description = "Status do cliente (ativo/inativo)", required = true, example = "true")
    private Boolean status = true;

    @NotNull(message = "Cooperado é obrigatório")
    @Schema(description = "Indica se o cliente é cooperado", required = true, example = "false")
    private Boolean cooperado = false;

    @Schema(description = "ID da cooperativa (se for cooperado)", example = "1")
    private Long cooperativaId;

    @Schema(description = "Nome da cooperativa (somente leitura)", accessMode = Schema.AccessMode.READ_ONLY, example = "Cooperativa Central")
    private String cooperativaNome;

    @Schema(description = "Data de cadastro", accessMode = Schema.AccessMode.READ_ONLY, example = "2025-10-30T10:00:00")
    private LocalDateTime dataCadastro;

    @Schema(description = "Data da última atualização", accessMode = Schema.AccessMode.READ_ONLY, example = "2025-10-30T10:00:00")
    private LocalDateTime updatedAt;
}
