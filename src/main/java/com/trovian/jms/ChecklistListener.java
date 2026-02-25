package com.trovian.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trovian.config.JmsConfig;
import com.trovian.dto.DadosFilaDTO;
import com.trovian.service.ChecklistRealizadoService;
import jakarta.jms.BytesMessage;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChecklistListener {


    private final ObjectMapper objectMapper;
    private final ChecklistRealizadoService checklistRealizadoService;

    @JmsListener(destination = JmsConfig.CHECK_LIST)
    public void receiveAbastecimento(Message message) {
        try{
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
            checklistRealizadoService.processarCheckListDoWpp(dadosFilaDTO);
        }catch (Exception e){
            throw new RuntimeException("Erro ao processar CHECK_LIST", e);
        }
    }
}
