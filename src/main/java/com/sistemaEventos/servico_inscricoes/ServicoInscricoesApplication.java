package com.sistemaEventos.servico_inscricoes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {
		SecurityAutoConfiguration.class,
		UserDetailsServiceAutoConfiguration.class
})
@EnableDiscoveryClient
@EnableFeignClients //Permite fazer requisições para outros serviços
public class ServicoInscricoesApplication {
	public static void main(String[] args) {
		SpringApplication.run(ServicoInscricoesApplication.class, args);
	}
}
