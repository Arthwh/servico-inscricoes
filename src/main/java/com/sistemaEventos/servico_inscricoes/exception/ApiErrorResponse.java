package com.sistemaEventos.servico_inscricoes.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record ApiErrorResponse(
    HttpStatus status,
    String message,
    Instant timestamp
) {}
