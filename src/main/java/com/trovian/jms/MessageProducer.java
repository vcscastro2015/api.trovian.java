package com.trovian.jms;

import com.trovian.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageProducer {

    private final JmsTemplate jmsTemplate;

    public void sendProductMessage(String message) {
        log.info("Enviando mensagem para a fila de produtos: {}", message);
        jmsTemplate.convertAndSend(JmsConfig.PRODUCT_QUEUE, message);
    }

    public void sendNotification(String notification) {
        log.info("Enviando notificação: {}", notification);
        jmsTemplate.convertAndSend(JmsConfig.NOTIFICATION_QUEUE, notification);
    }

    public void sendToQueue(String queueName, Object message) {
        log.info("Enviando mensagem para a fila {}: {}", queueName, message);
        jmsTemplate.convertAndSend(queueName, message);
    }
}
