package com.sistemaEventos.servico_inscricoes.dto;

import com.sistemaEventos.servico_inscricoes.model.Registration;
import com.sistemaEventos.servico_inscricoes.model.RegistrationStatus;

import java.time.Instant;

public record RegistrationResponseDTO (
        String id,
        String eventId,
        String userId,
        Instant checkIn,
        RegistrationStatus status,
        Instant created_at

) {
    public static RegistrationResponseDTO fromEntity(Registration registration) {
        if (registration == null) {
            return null;
        }

        return new RegistrationResponseDTO(
                registration.getId(),
                registration.getEventId(),
                registration.getUserId(),
                registration.getCheckIn(),
                registration.getStatus(),
                registration.getCreatedAt()
        );
    }
}


