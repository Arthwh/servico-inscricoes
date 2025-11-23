package com.sistemaEventos.servico_inscricoes.client;

import com.sistemaEventos.servico_inscricoes.config.FeignConfig;
import com.sistemaEventos.servico_inscricoes.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "usuario-service", url = "http://localhost:8080/users", configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/{id}")
    UserDTO findById(@PathVariable("id") String id);
}
