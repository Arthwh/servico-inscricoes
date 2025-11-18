package com.sistemaEventos.servico_inscricoes.dto;

import com.sistemaEventos.servico_inscricoes.model.Registration;
import com.sistemaEventos.servico_inscricoes.model.RegistrationStatus;

import java.time.Instant;

public record RegistrationUpdateDTO (
        Instant checkIn,
        RegistrationStatus status
) {
    public static RegistrationUpdateDTO fromEntity(Registration registration) {
        if (registration == null) {
            return null;
        }

        return new RegistrationUpdateDTO(
                registration.getCheckIn(),
                registration.getStatus()
        );
    }
}
