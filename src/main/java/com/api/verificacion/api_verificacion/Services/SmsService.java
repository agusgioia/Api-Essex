package com.api.verificacion.api_verificacion.Services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Autowired
    private Firebase firebaseService;

    @Value("${vonage.api.key}")
    private String apiKey;

    @Value("${vonage.api.secret}")
    private String apiSecret;

    @Value("${vonage.from}")
    private String from;

    public void enviarSms(String numeroDestino, String codigo, String idContrato) {
        try {
            VonageClient client = VonageClient.builder()
                    .apiKey(apiKey)
                    .apiSecret(apiSecret)
                    .build();
            Firestore firestore = firebaseService.getFirestore();
            DocumentReference documentReference = firestore.collection("contracts").document(idContrato);
            ApiFuture<DocumentSnapshot> future = documentReference.get();
            DocumentSnapshot document = future.get();

            String accessToken = document.getString("accessToken");
            String mensaje = "Tu código de verificación es: " + codigo + " es válido por 72 horas" +
                    " y siguiendo este link podés completar el " +
                    "formulario con tus datos. http://179.43.117.6:3000/sellers/ClientVerification?id="+idContrato +
                    "&token="+accessToken;
            TextMessage message = new TextMessage(from, numeroDestino, mensaje);

            SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);

            if (response.getMessages().get(0).getStatus() != MessageStatus.OK) {
                throw new RuntimeException("Fallo al enviar SMS: " + response.getMessages().get(0).getErrorText());
            }
        } catch (Exception e) {
            System.out.println("Error enviando SMS: " + e.getMessage());
            throw new RuntimeException("No se pudo enviar el SMS.");
        }
    }
}
