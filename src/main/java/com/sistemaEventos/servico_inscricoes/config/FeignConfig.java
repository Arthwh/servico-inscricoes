package com.sistemaEventos.servico_inscricoes.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // Pega o contexto da requisição HTTP atual (que chegou no controller)
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();

                    //Repassa o Token (Authorization)
                    String authorizationHeader = request.getHeader("Authorization");
                    if (authorizationHeader != null) {
                        template.header("Authorization", authorizationHeader);
                    }

                    //Repassa o ID do Usuário injetado pelo Gateway (X-User-Id)
                    String userIdHeader = request.getHeader("X-User-Id");
                    if (userIdHeader != null) {
                        template.header("X-User-Id", userIdHeader);
                    }

                    String userRolesHeader = request.getHeader("X-User-Roles");
                    if (userRolesHeader != null) {
                        template.header("X-User-Roles", userRolesHeader);
                    }
                }
            }
        };
    }
}