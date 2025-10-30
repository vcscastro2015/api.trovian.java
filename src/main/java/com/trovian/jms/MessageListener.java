package com.trovian.jms;

import com.trovian.config.JmsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageListener {

    @JmsListener(destination = JmsConfig.PRODUCT_QUEUE)
    public void receiveProductMessage(String message) {
        log.info("Mensagem recebida da fila de produtos: {}", message);
        // Processar mensagem de produto
        processProductMessage(message);
    }

    @JmsListener(destination = JmsConfig.NOTIFICATION_QUEUE)
    public void receiveNotification(String notification) {
        log.info("Notificação recebida: {}", notification);
        // Processar notificação
        processNotification(notification);
    }

    private void processProductMessage(String message) {
        log.info("Processando mensagem de produto: {}", message);
        // Implementar lógica de processamento
    }

    private void processNotification(String notification) {
        log.info("Processando notificação: {}", notification);
        // Implementar lógica de notificação
    }
}
