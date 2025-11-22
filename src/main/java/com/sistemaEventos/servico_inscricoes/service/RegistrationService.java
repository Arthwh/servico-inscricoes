package com.sistemaEventos.servico_inscricoes.service;

import com.sistemaEventos.servico_inscricoes.dto.RegistrationCreateDTO;
import com.sistemaEventos.servico_inscricoes.dto.RegistrationUpdateDTO;
import com.sistemaEventos.servico_inscricoes.exception.RegistrationNotFoundException;
import com.sistemaEventos.servico_inscricoes.model.Registration;
import com.sistemaEventos.servico_inscricoes.model.RegistrationStatus;
import com.sistemaEventos.servico_inscricoes.repository.RegistrationRepository;
import com.sistemaEventos.servico_inscricoes.security.AuthorizationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Serviço responsável pela lógica de negócios relacionada às inscrições em eventos.
 * <p>
 * Gerencia a criação, leitura, atualização e exclusão (CRUD) de inscrições,
 * além de validar regras de negócio como permissões de acesso e check-in.
 */
@Service
public class RegistrationService {
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private AuthorizationHelper authorizationHelper;

    /**
     * Retorna todas as inscrições ativas do sistema.
     * <p>
     * Operação restrita a administradores.
     *
     * @param requesterId O ID do usuário solicitante.
     * @param requesterRoles As roles do usuário solicitante.
     * @return Lista de todas as {@link Registration} ativas.
     * @throws AccessDeniedException Se o usuário não for administrador.
     */
    public List<Registration> getAllRegistrations (String requesterId, String requesterRoles) {
        authorizationHelper.checkIsAdmin(requesterRoles);

        return registrationRepository.findAllActive();
    }

    /**
     * Busca uma inscrição específica pelo seu ID.
     * <p>
     * Permite acesso apenas se o solicitante for o dono da inscrição ou um administrador.
     *
     * @param id O ID (UUID) da inscrição.
     * @param requesterId O ID do usuário solicitante.
     * @param requesterRoles As roles do usuário solicitante.
     * @return A entidade {@link Registration} encontrada.
     * @throws RegistrationNotFoundException Se a inscrição não for encontrada.
     * @throws AccessDeniedException Se o solicitante não tiver permissão.
     */
    public Registration getRegistrationById (String id, String requesterId, String requesterRoles) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RegistrationNotFoundException(id));

        authorizationHelper.checkOwnershipOrAdmin(registration.getUserId(), requesterId, requesterRoles);

        return registration;
    }

    /**
     * Busca todas as inscrições de um usuário específico.
     * <p>
     * Permite acesso apenas se o solicitante for o próprio usuário alvo ou um administrador.
     *
     * @param userId O ID do usuário cujas inscrições serão buscadas.
     * @param requesterId O ID do usuário solicitante.
     * @param requesterRoles As roles do usuário solicitante.
     * @return Lista de {@link Registration} pertencentes ao usuário.
     * @throws AccessDeniedException Se o solicitante não tiver permissão.
     */
    public List<Registration> getAllRegistrationsByUser (String userId, String requesterId, String requesterRoles) {
        authorizationHelper.checkOwnershipOrAdmin(userId, requesterId, requesterRoles);

        return registrationRepository.findAllActiveByUserId(userId);
    }

    /**
     * Cria uma nova inscrição.
     * <p>
     * Gera um novo UUID para a inscrição e salva no banco.
     * Valida se o solicitante pode criar uma inscrição para o usuário informado no DTO.
     * Por padrão, o status é definido como {@link RegistrationStatus} CONFIRMED
     *
     * @param dto Os dados para criação da inscrição.
     * @param requesterId O ID do usuário solicitante.
     * @param requesterRoles As roles do usuário solicitante.
     * @return A nova entidade {@link Registration} salva.
     * @throws AccessDeniedException Se o solicitante não tiver permissão.
     */
    public Registration createRegistration (RegistrationCreateDTO dto, String requesterId, String requesterRoles) {
        authorizationHelper.checkOwnershipOrAdmin(dto.userId(), requesterId , requesterRoles);

        registrationRepository.findActiveByUserAndEventId(dto.userId(), dto.eventId())
                .stream()
                .filter(registration -> {
                    if (!registration.getStatus().equals(RegistrationStatus.CANCELED)) {
                        throw new IllegalStateException("O usuário já está inscrito nesse evento.");
                }
                    return false;
                });

        Registration registration = new Registration();
        registration.setId(UUID.randomUUID().toString());
        registration.setUserId(dto.userId());
        registration.setEventId(dto.eventId());
        registration.setStatus(RegistrationStatus.CONFIRMED);

        return registrationRepository.save(registration);
    }

    /**
     * Atualiza os dados de uma inscrição existente.
     * <p>
     * Busca a inscrição pelo ID, verifica permissões e atualiza os campos permitidos.
     *
     * @param dto Os dados para atualização.
     * @param registrationId O ID da inscrição a ser atualizada.
     * @param requesterId O ID do usuário solicitante.
     * @param requesterRoles As roles do usuário solicitante.
     * @return A entidade {@link Registration} atualizada.
     * @throws RegistrationNotFoundException Se a inscrição não for encontrada.
     * @throws AccessDeniedException Se o solicitante não tiver permissão.
     */
    public Registration updateRegistration (RegistrationUpdateDTO dto, String registrationId, String requesterId, String requesterRoles) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RegistrationNotFoundException(registrationId));

        authorizationHelper.checkOwnershipOrAdmin(registration.getUserId(), requesterId , requesterRoles);

        registration.setStatus(dto.status());
        registration.setCheckIn(dto.checkIn());

        return registrationRepository.save(registration);
    }

    /**
     * Processa o check-in de um participante.
     * <p>
     * Define o carimbo de tempo (timestamp) atual no campo {@code checkIn} da inscrição.
     *
     * @param id O ID da inscrição a ser atualizada.
     * @param requesterId O ID do usuário que está realizando a ação.
     * @param requesterRoles As roles do usuário que está realizando a ação.
     * @return A entidade {@link Registration} atualizada e salva.
     * @throws RegistrationNotFoundException Se a inscrição não for encontrada.
     * @throws AccessDeniedException Se o solicitante não for o dono nem um admin.
     * @throws IllegalStateException Se o check-in já foi realizado anteriormente.
     */
    public Registration checkInRegistration(String id, String requesterId, String requesterRoles) {
        Instant checkIn = Instant.now();
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RegistrationNotFoundException(id));

        authorizationHelper.checkOwnershipOrAdmin(registration.getUserId(), requesterId , requesterRoles);

        if (registration.getCheckIn() != null) {
            throw new IllegalStateException("A inscrição já realizou o check-in.");
        }

        registration.setCheckIn(checkIn);
        registration.setStatus(RegistrationStatus.CHECKED_IN);

        return registrationRepository.save(registration);
    }

    /**
     * Cancela uma inscrição.
     * <p>
     * Altera o status para {@code CANCELED}. O registro permanece no banco de dados.
     *
     * @param id O ID da inscrição.
     * @param requesterId O ID do usuário solicitante.
     * @param requesterRoles As roles do usuário solicitante.
     * @return A inscrição atualizada com status cancelado.
     * @throws IllegalStateException Se a inscrição já tiver check-in realizado (não pode cancelar quem já foi).
     */
    public Registration cancelRegistration(String id, String requesterId, String requesterRoles) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RegistrationNotFoundException(id));

        authorizationHelper.checkOwnershipOrAdmin(registration.getUserId(), requesterId, requesterRoles);

        if (registration.getCheckIn() != null) {
            throw new IllegalStateException("Não é possível cancelar uma inscrição que já realizou check-in.");
        }

        if (registration.getStatus() == RegistrationStatus.CANCELED) {
            throw new IllegalStateException("Esta inscrição já está cancelada.");
        }

        registration.setStatus(RegistrationStatus.CANCELED);

        return registrationRepository.save(registration);
    }

    /**
     * Remove logicamente uma inscrição.
     *
     * @param id O ID da inscrição a ser removida.
     * @param requesterId O ID do usuário solicitante.
     * @param requesterRoles As roles do usuário solicitante.
     * @throws RegistrationNotFoundException Se a inscrição não for encontrada.
     * @throws AccessDeniedException Se o solicitante não tiver permissão.
     */
    public void deleteRegistration (String id, String requesterId, String requesterRoles) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RegistrationNotFoundException(id));

        authorizationHelper.checkOwnershipOrAdmin(registration.getUserId(), requesterId , requesterRoles);

        registration.setStatus(RegistrationStatus.DELETED);
        registration.setDeletedAt(Instant.now());
        registrationRepository.save(registration);
    }
}
