package com.trovian.dto;

import com.trovian.enums.TipoFornecedor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para Fornecedor")
public class FornecedorDTO {

    @Schema(description = "ID do fornecedor", example = "1")
    private Long id;

    @NotBlank(message = "Razão social é obrigatória")
    @Size(max = 200, message = "Razão social deve ter no máximo 200 caracteres")
    @Schema(description = "Razão social", example = "Auto Peças Silva LTDA")
    private String razaoSocial;

    @Size(max = 200, message = "Nome fantasia deve ter no máximo 200 caracteres")
    @Schema(description = "Nome fantasia", example = "Silva Peças")
    private String nomeFantasia;

    @Size(max = 18, message = "CNPJ/CPF deve ter no máximo 18 caracteres")
    @Schema(description = "CNPJ ou CPF", example = "12.345.678/0001-90")
    private String cnpjCpf;

    @Size(max = 20, message = "Inscrição estadual deve ter no máximo 20 caracteres")
    @Schema(description = "Inscrição estadual")
    private String inscricaoEstadual;

    @Size(max = 20, message = "Inscrição municipal deve ter no máximo 20 caracteres")
    @Schema(description = "Inscrição municipal")
    private String inscricaoMunicipal;

    @Schema(description = "Logradouro", example = "Rua das Flores")
    private String logradouro;

    @Schema(description = "Número", example = "123")
    private String numero;

    @Schema(description = "Complemento", example = "Sala 5")
    private String complemento;

    @Schema(description = "Bairro", example = "Centro")
    private String bairro;

    @Schema(description = "CEP", example = "12345-678")
    private String cep;

    @Schema(description = "Cidade", example = "São Paulo")
    private String cidade;

    @Schema(description = "UF", example = "SP")
    private String uf;

    @Schema(description = "Telefone 1", example = "11 99999-9999")
    private String telefone1;

    @Schema(description = "Telefone 2")
    private String telefone2;

    @Email(message = "Email inválido")
    @Schema(description = "Email", example = "contato@silvapecas.com.br")
    private String email;

    @Schema(description = "Site", example = "www.silvapecas.com.br")
    private String site;

    @Schema(description = "Contato principal", example = "João Silva")
    private String contatoPrincipal;

    @Schema(description = "Banco", example = "Banco do Brasil")
    private String banco;

    @Schema(description = "Agência", example = "1234-5")
    private String agencia;

    @Schema(description = "Conta", example = "12345-6")
    private String conta;

    @Schema(description = "Tipo de conta", example = "Corrente")
    private String tipoConta;

    @Schema(description = "Chave PIX", example = "12345678901")
    private String chavePix;

    @NotNull(message = "Tipo de fornecedor é obrigatório")
    @Schema(description = "Tipo de fornecedor", example = "PECAS")
    private TipoFornecedor tipo;

    @Schema(description = "Observações")
    private String observacao;

    @Schema(description = "Status ativo/inativo", example = "true")
    private Boolean status;
}
