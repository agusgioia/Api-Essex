package com.api.verificacion.api_verificacion.Services;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;


@Service
public class CodeService {
    @Autowired
    private Firebase firestoreInitializer;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    public String generarCodigoVerificacion() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public String generarCodigo(String contratoId, String email, String telefono) throws ExecutionException, InterruptedException {
        try {
            Firestore db = firestoreInitializer.getFirestore();

            String codigo = generarCodigoVerificacion();
            long duracionHoras = 72;
            Date tiempoLimite = new Date(System.currentTimeMillis() + duracionHoras * 60 * 60 * 1000);

            Map<String, Object> data = new HashMap<>();
            data.put("codigoVerificacion", codigo);
            data.put("verificado", false);
            data.put("tiempoLimite", tiempoLimite);

            DocumentReference docRef = db.collection("contracts").document(contratoId);
            docRef.set(data, SetOptions.merge()).get();

            emailService.enviarCodigo(email, codigo, contratoId);
            return codigo;
        }catch (Exception e){
            System.out.println("Error: "+e.getMessage());
            throw e;
        }
    }

    public boolean verificarCodigo(String contratoId, String codigoIngresado) throws ExecutionException, InterruptedException {
        Firestore db = firestoreInitializer.getFirestore();
        DocumentReference docRef = db.collection("contracts").document(contratoId);
        DocumentSnapshot snapshot = docRef.get().get();
        if (!snapshot.exists()) {
            return false;
        }
        String codigoGuardado = snapshot.getString("codigoVerificacion");
        Date tiempoLimite = snapshot.getDate("tiempoLimite");
        if (codigoIngresado == null || codigoGuardado == null || tiempoLimite == null) {
            return false;
        }
        Date ahora = new Date();
        if (!codigoIngresado.equals(codigoGuardado)) {
            return false;
        }
        return !ahora.after(tiempoLimite); // vencido
    }

}