package com.api.verificacion.api_verificacion.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCodigo(String to, String codigo, String idContrato){
        SimpleMailMessage message = new SimpleMailMessage();
        String link = "https://essex-40828.web.app/sellers/ClientVerification?id="+idContrato;
        message.setTo(to);
        message.setSubject("Código de verificación");
        message.setText("Tú código es: "+ codigo+"\n" + "Link para firma y confirmación : "+link);
        mailSender.send(message);
    }

    public void enviarLink(String link,String to){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Link del Formulario del Contrato");
        message.setText("Tú enlace al formulario : "+ link);
        mailSender.send(message);
    }
}
