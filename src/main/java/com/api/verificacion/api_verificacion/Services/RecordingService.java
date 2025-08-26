package com.api.verificacion.api_verificacion.Services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecordingService {

    @Autowired
    private Firebase firestoreInitializer;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 8 * * * *") // todos los días a las 08:00
    public void enviarRecordatorios() throws Exception {
        Firestore db = firestoreInitializer.getFirestore();

        Date ahora = new Date();
        Date limite = new Date(ahora.getTime() + 72L * 60 * 60 * 1000); 

        // 1. Buscar contratos próximos a vencer
        ApiFuture<QuerySnapshot> future = db.collection("contracts")
                .whereGreaterThan("fechaFin", ahora)
                .whereLessThan("fechaFin", limite)
                .get();

        List<QueryDocumentSnapshot> contratos = future.get().getDocuments();

        // 2. Agrupar por userUID
        Map<String, List<QueryDocumentSnapshot>> contratosPorCliente = new HashMap<>();
        for (QueryDocumentSnapshot contrato : contratos) {
            String userUID = contrato.getString("userUID");
            if (userUID == null) continue;

            contratosPorCliente
                    .computeIfAbsent(userUID, k -> new ArrayList<>())
                    .add(contrato);
        }

        // 3. Mandar un mail por cliente
        for (Map.Entry<String, List<QueryDocumentSnapshot>> entry : contratosPorCliente.entrySet()) {
            String userUID = entry.getKey();
            List<QueryDocumentSnapshot> listaContratos = entry.getValue();
            String email = listaContratos.get(0).getString("emailEmpresa");

            // armar contenido del mail
            StringBuilder contenido = new StringBuilder();
            contenido.append("Estimado cliente, los siguientes contratos están próximos a vencer:\n\n");

            for (QueryDocumentSnapshot contrato : listaContratos) {
                String contratoId = contrato.getId();
                Date fechaFin = contrato.getDate("fechaFin");
                contenido.append("- Contrato ID: ").append(contratoId)
                        .append(" | Vence: ").append(fechaFin).append("\n");
            }

            emailService.enviarRecordatorio(email, contenido.toString());
        }
    }
}


