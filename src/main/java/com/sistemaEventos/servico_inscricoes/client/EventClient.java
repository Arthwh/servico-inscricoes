package com.sistemaEventos.servico_inscricoes.client;

import com.sistemaEventos.servico_inscricoes.config.FeignConfig;
import com.sistemaEventos.servico_inscricoes.dto.EventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "evento-service", url = "http://localhost:8080/events", configuration = FeignConfig.class)
public interface EventClient {

    @GetMapping("/{id}")
    EventDTO findById(@PathVariable("id") String id);
}
