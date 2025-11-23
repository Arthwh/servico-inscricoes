package com.sistemaEventos.servico_inscricoes.controller;

import com.sistemaEventos.servico_inscricoes.dto.CertificateResponseDTO;
import com.sistemaEventos.servico_inscricoes.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {
    @Autowired
    private CertificateService certificateService;

    @GetMapping("/{registrationId}")
    public ResponseEntity<CertificateResponseDTO> getCertificateByRegistration(@PathVariable String registrationId) {
        CertificateResponseDTO response = CertificateResponseDTO.fromEntity(certificateService.getCertificateByRegistration(registrationId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify/{authenticationCode}")
    public ResponseEntity<CertificateResponseDTO> getCertificateByAuthenticationCode(@PathVariable String authenticationCode) {
        CertificateResponseDTO response = CertificateResponseDTO.fromEntity(certificateService.getCertificateByAuthenticationCode(authenticationCode));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{authenticationCode}")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable String authenticationCode) {
        byte[] pdfBytes = certificateService.generateCertificate(authenticationCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Para baixar o arquivo ao invés de abrir no navegador:
        headers.setContentDispositionFormData("attachment", "certificado.pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
