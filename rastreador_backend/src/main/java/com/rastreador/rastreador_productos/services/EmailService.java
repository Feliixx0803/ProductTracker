package com.rastreador.rastreador_productos.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    
    @Value("${app.mail.from}")
    private String mailFrom;

    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void sendNotification(String toEmail, String productTitle, Double oldPrice, Double newPrice, String productUrl){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(toEmail);
        message.setSubject("¡Bajada de precio en producto: " + productTitle + "!");
        String content = 
                "¡Bajada de precio detectada en un producto que sigues!:\n\n" +
                "Producto: " + productTitle + "\n" +
                "Precio anterior: " + oldPrice + " €\n" +
                "Precio nuevo: " + newPrice + " €\n\n" +
                "Puedes acceder directamente al producto en Amazon a través del siguiente enlace:\n" + productUrl + "\n\n";

        message.setText(content);
        mailSender.send(message);
    }
}
