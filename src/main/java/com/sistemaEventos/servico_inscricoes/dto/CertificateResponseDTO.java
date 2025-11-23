package com.sistemaEventos.servico_inscricoes.dto;

import com.sistemaEventos.servico_inscricoes.model.Certificate;

import java.time.Instant;

public record CertificateResponseDTO (
        String authenticationCode,
        String registrationId,
        Instant issueDate
) {
    public static CertificateResponseDTO fromEntity(Certificate certificate) {
        if (certificate == null) {
            return null;
        }

        return new CertificateResponseDTO(
                certificate.getAuthenticationCode(),
                certificate.getRegistrationId(),
                certificate.getIssueDate()
        );
    }
}
