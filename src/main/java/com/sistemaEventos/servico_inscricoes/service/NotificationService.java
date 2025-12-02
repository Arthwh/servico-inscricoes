package com.sistemaEventos.servico_inscricoes.service;

import com.sistemaEventos.servico_inscricoes.client.EventClient;
import com.sistemaEventos.servico_inscricoes.client.UserClient;
import com.sistemaEventos.servico_inscricoes.config.RabbitMqConfig;
import com.sistemaEventos.servico_inscricoes.dto.EventDTO;
import com.sistemaEventos.servico_inscricoes.dto.NotificationDTO;
import com.sistemaEventos.servico_inscricoes.dto.UserDTO;
import com.sistemaEventos.servico_inscricoes.model.NotificationChannel;
import com.sistemaEventos.servico_inscricoes.model.NotificationTemplate;
import com.sistemaEventos.servico_inscricoes.model.Registration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {
    @Autowired
    private UserClient userClient;
    @Autowired
    private EventClient eventClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendNotification(Registration registration, NotificationChannel channel, NotificationTemplate template) {
        UserDTO user = userClient.findById(registration.getUserId());
        EventDTO event = eventClient.findById(registration.getEventId());

        Map<String, String> variables = new HashMap<>();
        variables.put("name", user.fullname());
        variables.put("event", event.eventName());
        variables.put("date", event.eventDate());

        NotificationDTO notification = new NotificationDTO(
                channel,
                template,
                user.email(),
                variables
        );

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.ROUTING_KEY,
                notification
        );
    }
}
