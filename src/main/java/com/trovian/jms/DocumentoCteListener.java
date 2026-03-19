package com.trovian.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trovian.config.JmsConfig;
import com.trovian.dto.DadosFilaDTO;
import com.trovian.repository.DocumentoCteErroRepository;
import com.trovian.service.ContaReceberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import jakarta.jms.BytesMessage;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class DocumentoCteListener {

    @Autowired
    private ResourceLoader resourceLoader;

    private final ContaReceberService contaReceberService;
    private final ObjectMapper objectMapper;
    private final DocumentoCteErroRepository documentoCteErroRepository;

    @JmsListener(destination = JmsConfig.DOCUMENTOS_CTE)
    public void receiveDocumentoCte(Message message) {
        try {
            String mensagemJson;
            if (message instanceof TextMessage) {
                mensagemJson = ((TextMessage) message).getText();

            } else if (message instanceof BytesMessage) {
                BytesMessage bm = (BytesMessage) message;
                byte[] data = new byte[(int) bm.getBodyLength()];
                bm.readBytes(data);
                mensagemJson = new String(data, StandardCharsets.UTF_8);

            } else {
                throw new IllegalStateException(
                        "Tipo JMS não suportado: " + message.getClass()
                );
            }
            DadosFilaDTO dadosFilaDTO = objectMapper.readValue(mensagemJson, DadosFilaDTO.class);
            contaReceberService.processarDocumentoCte(dadosFilaDTO);
        } catch (Exception e) {
            // Re-lançar exceção para acionar mecanismo de retry do JMS
            throw new RuntimeException("Erro ao processar CT-e", e);
        }
    }

}
