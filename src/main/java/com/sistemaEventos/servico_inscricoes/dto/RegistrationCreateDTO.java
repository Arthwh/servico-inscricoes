package com.sistemaEventos.servico_inscricoes.dto;

import com.sistemaEventos.servico_inscricoes.model.Registration;

public record RegistrationCreateDTO (
        String eventId,
        String userId
) {
    public static RegistrationCreateDTO fromEntity(Registration registration) {
        if (registration == null) {
            return null;
        }

        return new RegistrationCreateDTO(
                registration.getEventId(),
                registration.getUserId()
        );
    }
}
