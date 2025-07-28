package com.api.verificacion.api_verificacion.Services;

import com.vonage.client.VonageClient;
import com.vonage.client.sms.MessageStatus;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${vonage.api.key}")
    private String apiKey;

    @Value("${vonage.api.secret}")
    private String apiSecret;

    @Value("${vonage.from}")
    private String from;

    public void enviarSms(String numeroDestino, String codigo) {
        try {
            VonageClient client = VonageClient.builder()
                    .apiKey(apiKey)
                    .apiSecret(apiSecret)
                    .build();

            String mensaje = "Tu código de verificación es: " + codigo;
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
