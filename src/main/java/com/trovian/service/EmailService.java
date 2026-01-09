package com.trovian.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${trovian.app.url}")
    private String appUrl;

    public void enviarEmailRecuperacaoSenha(String email, String token) {
        String resetUrl = appUrl + "/redefinir-senha?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Recuperação de Senha - Trovian");
        message.setText("Olá!\n\n" +
                "Você solicitou a recuperação de senha.\n" +
                "Clique no link abaixo para redefinir sua senha:\n\n" +
                resetUrl + "\n\n" +
                "Este link expira em 1 hora.\n\n" +
                "Se você não solicitou esta recuperação, ignore este email.\n\n" +
                "Atenciosamente,\n" +
                "Equipe Trovian");

        mailSender.send(message);
    }
}
