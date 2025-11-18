package com.sistemaEventos.servico_inscricoes.controller;

import com.sistemaEventos.servico_inscricoes.dto.RegistrationCreateDTO;
import com.sistemaEventos.servico_inscricoes.dto.RegistrationResponseDTO;
import com.sistemaEventos.servico_inscricoes.dto.RegistrationUpdateDTO;
import com.sistemaEventos.servico_inscricoes.service.RegistrationService;
import com.sistemaEventos.servico_inscricoes.model.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para as operações de CRUD (Criar, Ler, Atualizar, Deletar) da entidade {@link Registration}.
 * <p>
 * Gerencia o ciclo de vida das inscrições em eventos.
 * Todos os endpoints deste controlador são protegidos e esperam que o API Gateway
 * injete os headers de segurança (`X-User-Id`, `X-User-Roles`) após a
 * validação do token JWT.
 */
@RestController
@RequestMapping("/registrations")
public class RegistrationController {
    @Autowired
    private RegistrationService registrationService;

    /**
     * Retorna uma lista de todas as inscrições do sistema.
     * <p>
     * Este endpoint é restrito a administradores, pois lista
     * inscrições de todos os usuários em todos os eventos.
     *
     * @param requesterId O ID do usuário que faz a requisição.
     * @param requesterRoles As roles do usuário que faz a requisição.
     * @return Um {@link ResponseEntity} com status {@code 200 OK} e a lista de {@link RegistrationResponseDTO}.
     */
    @GetMapping
    public ResponseEntity<List<RegistrationResponseDTO>> getRegistrations(
            @RequestHeader("X-User-Id") String requesterId,
            @RequestHeader("X-User-Roles") String requesterRoles
    ) {

        List<RegistrationResponseDTO> response = registrationService.getAllRegistrations(requesterId, requesterRoles)
                .stream()
                .map(RegistrationResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Busca uma inscrição específica pelo seu ID.
     * <p>
     * O acesso é permitido apenas ao dono da inscrição (o usuário inscrito) ou a um administrador.
     *
     * @param id O ID (UUID) da inscrição a ser buscada.
     * @param requesterId O ID do usuário solicitante.
     * @param requesterRoles As roles do usuário solicitante.
     * @return Um {@link ResponseEntity} com status {@code 200 OK} e o DTO da inscrição.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RegistrationResponseDTO> getRegistrationById(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String requesterId,
            @RequestHeader("X-User-Roles") String requesterRoles
    ) {

        RegistrationResponseDTO response = RegistrationResponseDTO.fromEntity(registrationService.getRegistrationById(id, requesterId, requesterRoles));

        return ResponseEntity.ok(response);
    }

    /**
     * Busca todas as inscrições de um usuário específico.
     * <p>
     * O acesso é restrito ao próprio usuário ou a um administrador.
     *
     * @param id O ID (UUID) do usuário cujas inscrições se deseja buscar.
     * @param requesterId O ID do usuário solicitante.
     * @param requesterRoles As roles do usuário solicitante.
     * @return Um {@link ResponseEntity} com status {@code 200 OK} e a lista de inscrições do usuário.
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<List<RegistrationResponseDTO>> getRegistrationByUserId(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String requesterId,
            @RequestHeader("X-User-Roles") String requesterRoles
    ) {

        List<RegistrationResponseDTO> response = registrationService.getAllRegistrationsByUser(id, requesterId, requesterRoles)
                .stream()
                .map(RegistrationResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Cria uma nova inscrição em um evento.
     *
     * @param requesterId O ID do usuário que está se inscrevendo.
     * @param requesterRoles As roles do usuário.
     * @param dto O DTO {@link RegistrationCreateDTO} contendo o ID do evento.
     * @return Um {@link ResponseEntity} com status {@code 201 Created} e os dados da nova inscrição.
     */
    @PostMapping
    public ResponseEntity<RegistrationResponseDTO> createRegistration(
            @RequestHeader("X-User-Id") String requesterId,
            @RequestHeader("X-User-Roles") String requesterRoles,
            @RequestBody RegistrationCreateDTO dto
    ) {

        RegistrationResponseDTO response = RegistrationResponseDTO.fromEntity(registrationService.createRegistration(dto, requesterId, requesterRoles));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Atualiza uma inscrição existente.
     *
     * @param id O ID da inscrição a ser atualizada.
     * @param requesterId O ID do usuário solicitante.
     * @param requesterRoles As roles do usuário solicitante.
     * @param dto O DTO {@link RegistrationUpdateDTO} com os novos dados.
     * @return Um {@link ResponseEntity} com status {@code 200 OK} e a inscrição atualizada.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RegistrationResponseDTO> updateRegistration(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String requesterId,
            @RequestHeader("X-User-Roles") String requesterRoles,
            @RequestBody RegistrationUpdateDTO dto
    ) {

        RegistrationResponseDTO response = RegistrationResponseDTO.fromEntity(registrationService.updateRegistration(dto, id, requesterId, requesterRoles));
        return ResponseEntity.ok(response);
    }

    /**
     * Realiza o check-in de uma inscrição.
     * <p>
     * Este endpoint marca a presença do participante no evento.
     * O horário do check-in é registrado automaticamente pelo servidor.
     *
     * @param id O ID (UUID) da inscrição.
     * @param requesterId O ID do usuário solicitante.
     * @param requesterRoles As roles do usuário solicitante.
     * @return Um {@link ResponseEntity} com status {@code 200 OK} e os dados atualizados da inscrição.
     */
    @PatchMapping("/{id}/check-in")
    public ResponseEntity<RegistrationResponseDTO> checkInRegistration(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String requesterId,
            @RequestHeader("X-User-Roles") String requesterRoles
    ) {

        RegistrationResponseDTO response = RegistrationResponseDTO.fromEntity(registrationService.checkInRegistration(id, requesterId, requesterRoles));
        return ResponseEntity.ok(response);
    }

    /**
     * Cancela uma inscrição.
     * <p>
     * O registro não é removido do banco de dados, apenas seu status é alterado
     * para {@code CANCELED}.
     *
     * @param id O ID da inscrição a ser cancelada.
     * @param requesterId O ID do usuário solicitante.
     * @param requesterRoles As roles do usuário solicitante.
     * @return A inscrição atualizada.
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<RegistrationResponseDTO> cancelRegistration(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String requesterId,
            @RequestHeader("X-User-Roles") String requesterRoles
    ) {
        RegistrationResponseDTO response = RegistrationResponseDTO.fromEntity(registrationService.cancelRegistration(id, requesterId, requesterRoles));

        return ResponseEntity.ok(response);
    }

    /**
     * Deleta logicamente uma inscrição.
     *
     * @param id O ID da inscrição a ser cancelada.
     * @param requesterId O ID do usuário solicitante.
     * @param requesterRoles As roles do usuário solicitante.
     * @return Um {@link ResponseEntity} com status {@code 200 OK} (ou 204 No Content).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistration(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String requesterId,
            @RequestHeader("X-User-Roles") String requesterRoles
    ) {
        registrationService.deleteRegistration(id, requesterId, requesterRoles);
        return ResponseEntity.ok().build();
    }
}