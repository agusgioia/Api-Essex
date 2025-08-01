package com.api.verificacion.api_verificacion.Controllers;

import com.api.verificacion.api_verificacion.Models.DatosVerificacion;
import com.api.verificacion.api_verificacion.Services.CodeService;
import com.api.verificacion.api_verificacion.Services.EmailService;
import com.api.verificacion.api_verificacion.Services.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verificacion")
@CrossOrigin(origins = {"https://essex-40828.web.app","http://localhost:3000","http://179.43.117.6:3000"})
public class VerificationController {

    @Autowired
    private CodeService codeService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @PostMapping("/completar-contrato/{email}/{idContrato}")
    public ResponseEntity<String> completarContrato(
            @PathVariable("email") String email,@PathVariable("idContrato") String idContrato){
        try{
            String link = "https://179.43.117.6/sellers/New?id=" + idContrato + "&mode=usuario";
            emailService.enviarLink(link,email);
            return ResponseEntity.ok("Link generado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al generar link: " + e.getMessage());
        }
    }

    @PostMapping("/generar-codigo-email")
    public ResponseEntity<String> generarCodigo(@RequestBody DatosVerificacion datos) {
        try {
            String codigo = codeService.generarCodigo(
                    datos.getIdContract(),
                    datos.getEmail(),
                    datos.getTelefono()
            );
            return ResponseEntity.ok(codigo);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al generar código: " + e.getMessage());
        }
    }

    @PostMapping("/generar-codigo-sms/{telefono}")
    public ResponseEntity<String> generarCodigoSms(@PathVariable("telefono") String telefono){
        String codigo = codeService.generarCodigoVerificacion();
        try{
            smsService.enviarSms(telefono,codigo);
            return ResponseEntity.ok("Código enviado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al generar código: " + e.getMessage());
        }
    }

    @PostMapping("/verificar-codigo/{idContrato}")
    public ResponseEntity<String> verificarCodigo(
            @PathVariable String idContrato,
            @RequestParam String codigoIngresado) {
        try {
            boolean esValido = codeService.verificarCodigo(idContrato, codigoIngresado);
            if (esValido) {
                return ResponseEntity.ok("Código verificado correctamente.");
            } else {
                return ResponseEntity.status(400).body("Código incorrecto.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al verificar código: " + e.getMessage());
        }
    }
}
