package com.sistemaEventos.servico_inscricoes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sistemaEventos.servico_inscricoes.model.NotificationChannel;
import com.sistemaEventos.servico_inscricoes.model.NotificationTemplate;
import java.util.Map;

public record NotificationDTO (
        NotificationChannel channel,
        @JsonProperty("template_id")
        NotificationTemplate template_id,
        String recipient,
        Map<String, String> variables
) {}
