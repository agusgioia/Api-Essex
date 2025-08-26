package com.api.verificacion.api_verificacion.Services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class EmailService {

    @Autowired
    private Firebase firebaseService;

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCodigo(String to, String codigo, String idContrato) throws ExecutionException, InterruptedException {
        SimpleMailMessage message = new SimpleMailMessage();
        Firestore firestore = firebaseService.getFirestore();
        DocumentReference documentReference = firestore.collection("contracts").document(idContrato);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

        String accessToken = document.getString("accessToken");
        String link = "http://179.43.117.6:3000/sellers/ClientVerification?id="+idContrato+"&token="+accessToken;
        message.setTo(to);
        message.setSubject("Código de verificación");
        message.setText("Tú código es: "+ codigo+" es válido por 72 horas\n" + "Link para firma y confirmación : "+link);
        mailSender.send(message);
    }

    public void enviarLink(String link,String to){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Link del Formulario del Contrato");
        message.setText("Tú enlace al formulario : "+ link);
        mailSender.send(message);
    }

    public void enviarRecordatorio(String email,String contenido){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Recordatorio de Vencimientos de contratos");
        message.setText(contenido);
        mailSender.send(message);
    }
}
