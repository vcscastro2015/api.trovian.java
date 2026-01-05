package com.trovian.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "documentos_cte_erro")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoCteErro {

    @Id
    private String id;

    private String numero;
    private String serie;
    private String chaveAcesso;
    private LocalDateTime dataEmissao;
    private String modelo;
    private String tipoDocumento;

    // Dados do emitente
    private String emitenteRazaoSocial;
    private String emitenteCnpj;
    private String emitenteIe;
    private String emitenteEndereco;
    private String emitenteMunicipio;
    private String emitenteUf;
    private String emitenteCep;

    // Dados do remetente
    private String remetenteRazaoSocial;
    private String remetenteCnpj;
    private String remetenteIe;
    private String remetenteEndereco;
    private String remetenteMunicipio;
    private String remetenteUf;
    private String remetenteCep;

    // Dados do destinatário
    private String destinatarioRazaoSocial;
    private String destinatarioCnpj;
    private String destinatarioIe;
    private String destinatarioEndereco;
    private String destinatarioMunicipio;
    private String destinatarioUf;
    private String destinatarioCep;
    private String destinatarioPais;

    // Dados do tomador
    private String tomadorRazaoSocial;
    private String tomadorCnpj;
    private String tomadorIe;
    private String tomadorEndereco;
    private String tomadorMunicipio;
    private String tomadorUf;
    private String tomadorCep;
    private String tomadorPais;

    // Dados da carga
    private String produtoPredominante;
    private String outrasCaracteristicas;
    private Integer quantidade;
    private String unidade;
    private Double pesoBruto;
    private Double pesoLiquido;
    private Double valorTotalMercadoria;

    // Valores da prestação
    private Double valorTotalPrestacao;
    private Double pedagio;
    private Double despachoArifa;
    private Double outrosValores;

    private String observacoes;

    // Informações do erro
    private String mensagemErro;
    private LocalDateTime dataHoraErro;
    private String jsonOriginal;
}
