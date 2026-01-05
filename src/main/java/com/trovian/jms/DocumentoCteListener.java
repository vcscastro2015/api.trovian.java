package com.trovian.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.trovian.config.JmsConfig;
import com.trovian.document.DocumentoCteErro;
import com.trovian.dto.DocumentoCteDTO;
import com.trovian.repository.DocumentoCteErroRepository;
import com.trovian.service.ContaReceberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class DocumentoCteListener {

    private final ContaReceberService contaReceberService;
    private final ObjectMapper objectMapper;
    private final DocumentoCteErroRepository documentoCteErroRepository;

    @JmsListener(destination = JmsConfig.DOCUMENTOS_CTE)
    public void receiveDocumentoCte(String mensagemJson) {
        DocumentoCteDTO documentoCte = null;
        try {
            log.info("Mensagem CT-e recebida da fila");
            log.debug("JSON recebido: {}", mensagemJson);

            // Converter String JSON para objeto DocumentoCteDTO
            documentoCte = objectMapper.readValue(mensagemJson, DocumentoCteDTO.class);

            log.info("CT-e deserializado - Número: {}, Chave: {}",
                    documentoCte.getNumero(),
                    documentoCte.getChaveAcesso());

            // Validar campos obrigatórios
            validarCteBasico(documentoCte, mensagemJson);

            // Processar o documento CT-e
            contaReceberService.processarDocumentoCte(documentoCte);

            log.info("CT-e processado com sucesso - Número: {}", documentoCte.getNumero());

        } catch (Exception e) {
            if (documentoCte != null) {
                log.error("Erro ao processar CT-e - Número: {}, Chave: {}",
                        documentoCte.getNumero(),
                        documentoCte.getChaveAcesso(),
                        e);
            } else {
                log.error("Erro ao processar CT-e - Falha na deserialização do JSON", e);
            }
            // Re-lançar exceção para acionar mecanismo de retry do JMS
            throw new RuntimeException("Erro ao processar CT-e", e);
        }
    }

    private void validarCteBasico(DocumentoCteDTO cteDTO, String jsonOriginal) {
        String mensagemErro = null;

        if (cteDTO.getNumero() == null || cteDTO.getNumero().isEmpty()) {
            mensagemErro = "CT-e sem número";
        } else if (cteDTO.getEmitente() == null || cteDTO.getEmitente().getCnpj() == null) {
            mensagemErro = "CT-e sem dados do emitente";
        } else if (cteDTO.getValoresPrestacao() == null ||
                cteDTO.getValoresPrestacao().getValorTotalPrestacao() == null ||
                cteDTO.getValoresPrestacao().getValorTotalPrestacao().compareTo(BigDecimal.ZERO) <= 0) {
            mensagemErro = "CT-e sem valor de prestação válido";
        } else if (cteDTO.getDataEmissao() == null) {
            mensagemErro = "CT-e sem data de emissão";
        }

        // Se houver erro de validação, gravar no MongoDB antes de lançar a exceção
        if (mensagemErro != null) {
            gravarCteErroNoMongoDB(cteDTO, mensagemErro, jsonOriginal);
            throw new IllegalArgumentException(mensagemErro);
        }
    }

    private void gravarCteErroNoMongoDB(DocumentoCteDTO cteDTO, String mensagemErro, String jsonOriginal) {
        try {
            log.info("Gravando CT-e com erro no MongoDB - Número: {}, Erro: {}",
                    cteDTO.getNumero(), mensagemErro);

            DocumentoCteErro documentoErro = new DocumentoCteErro();

            // Dados principais
            documentoErro.setNumero(cteDTO.getNumero());
            documentoErro.setSerie(cteDTO.getSerie());
            documentoErro.setChaveAcesso(cteDTO.getChaveAcesso());
            documentoErro.setDataEmissao(cteDTO.getDataEmissao());
            documentoErro.setModelo(cteDTO.getModelo());
            documentoErro.setTipoDocumento(cteDTO.getTipoDocumento());

            // Dados do emitente
            if (cteDTO.getEmitente() != null) {
                documentoErro.setEmitenteRazaoSocial(cteDTO.getEmitente().getRazaoSocial());
                documentoErro.setEmitenteCnpj(cteDTO.getEmitente().getCnpj());
                documentoErro.setEmitenteIe(cteDTO.getEmitente().getIe());
                documentoErro.setEmitenteEndereco(cteDTO.getEmitente().getEndereco());
                documentoErro.setEmitenteMunicipio(cteDTO.getEmitente().getMunicipio());
                documentoErro.setEmitenteUf(cteDTO.getEmitente().getUf());
                documentoErro.setEmitenteCep(cteDTO.getEmitente().getCep());
            }

            // Dados do remetente
            if (cteDTO.getRemetente() != null) {
                documentoErro.setRemetenteRazaoSocial(cteDTO.getRemetente().getRazaoSocial());
                documentoErro.setRemetenteCnpj(cteDTO.getRemetente().getCnpj());
                documentoErro.setRemetenteIe(cteDTO.getRemetente().getIe());
                documentoErro.setRemetenteEndereco(cteDTO.getRemetente().getEndereco());
                documentoErro.setRemetenteMunicipio(cteDTO.getRemetente().getMunicipio());
                documentoErro.setRemetenteUf(cteDTO.getRemetente().getUf());
                documentoErro.setRemetenteCep(cteDTO.getRemetente().getCep());
            }

            // Dados do destinatário
            if (cteDTO.getDestinatario() != null) {
                documentoErro.setDestinatarioRazaoSocial(cteDTO.getDestinatario().getRazaoSocial());
                documentoErro.setDestinatarioCnpj(cteDTO.getDestinatario().getCnpj());
                documentoErro.setDestinatarioIe(cteDTO.getDestinatario().getIe());
                documentoErro.setDestinatarioEndereco(cteDTO.getDestinatario().getEndereco());
                documentoErro.setDestinatarioMunicipio(cteDTO.getDestinatario().getMunicipio());
                documentoErro.setDestinatarioUf(cteDTO.getDestinatario().getUf());
                documentoErro.setDestinatarioCep(cteDTO.getDestinatario().getCep());
                documentoErro.setDestinatarioPais(cteDTO.getDestinatario().getPais());
            }

            // Dados do tomador
            if (cteDTO.getTomador() != null) {
                documentoErro.setTomadorRazaoSocial(cteDTO.getTomador().getRazaoSocial());
                documentoErro.setTomadorCnpj(cteDTO.getTomador().getCnpj());
                documentoErro.setTomadorIe(cteDTO.getTomador().getIe());
                documentoErro.setTomadorEndereco(cteDTO.getTomador().getEndereco());
                documentoErro.setTomadorMunicipio(cteDTO.getTomador().getMunicipio());
                documentoErro.setTomadorUf(cteDTO.getTomador().getUf());
                documentoErro.setTomadorCep(cteDTO.getTomador().getCep());
                documentoErro.setTomadorPais(cteDTO.getTomador().getPais());
            }

            // Dados da carga
            if (cteDTO.getDadosCarga() != null) {
                documentoErro.setProdutoPredominante(cteDTO.getDadosCarga().getProdutoPredominante());
                documentoErro.setOutrasCaracteristicas(cteDTO.getDadosCarga().getOutrasCaracteristicas());
                documentoErro.setQuantidade(cteDTO.getDadosCarga().getQuantidade());
                documentoErro.setUnidade(cteDTO.getDadosCarga().getUnidade());
                documentoErro.setPesoBruto(cteDTO.getDadosCarga().getPesoBruto());
                documentoErro.setPesoLiquido(cteDTO.getDadosCarga().getPesoLiquido());
                documentoErro.setValorTotalMercadoria(cteDTO.getDadosCarga().getValorTotalMercadoria() != null
                        ? cteDTO.getDadosCarga().getValorTotalMercadoria().doubleValue() : null);
            }

            // Valores da prestação
            if (cteDTO.getValoresPrestacao() != null) {
                documentoErro.setValorTotalPrestacao(cteDTO.getValoresPrestacao().getValorTotalPrestacao() != null
                        ? cteDTO.getValoresPrestacao().getValorTotalPrestacao().doubleValue() : null);
                documentoErro.setPedagio(cteDTO.getValoresPrestacao().getPedagio() != null
                        ? cteDTO.getValoresPrestacao().getPedagio().doubleValue() : null);
                documentoErro.setDespachoArifa(cteDTO.getValoresPrestacao().getDespachoArifa() != null
                        ? cteDTO.getValoresPrestacao().getDespachoArifa().doubleValue() : null);
                documentoErro.setOutrosValores(cteDTO.getValoresPrestacao().getOutrosValores() != null
                        ? cteDTO.getValoresPrestacao().getOutrosValores().doubleValue() : null);
            }

            documentoErro.setObservacoes(cteDTO.getObservacoes());

            // Informações do erro
            documentoErro.setMensagemErro(mensagemErro);
            documentoErro.setDataHoraErro(LocalDateTime.now());
            documentoErro.setJsonOriginal(jsonOriginal);

            // Salvar no MongoDB
            DocumentoCteErro saved = documentoCteErroRepository.save(documentoErro);
            log.info("CT-e com erro salvo no MongoDB com ID: {}", saved.getId());

        } catch (Exception e) {
            // Não queremos que erro ao gravar no MongoDB impeça o fluxo
            log.error("Erro ao gravar CT-e com erro no MongoDB", e);
        }
    }
}
