package com.sistemaEventos.servico_inscricoes.repository;

import com.sistemaEventos.servico_inscricoes.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, String> {
    /**
     * Retorna uma lista de todas as inscrições *ativas* (deleted_at = false).
     *
     * @return uma {@link List} de {@link Registration} ativos ou uma {@link List<>} vazia se não for encontrado nada.
     */
    @Query("SELECT r FROM Registration r WHERE r.deletedAt IS NULL")
    List<Registration> findAllActive();

    /**
     * Busca todas as inscrições *ativas* (deleted_at = false) associadas a um usuário específico.
     *
     * @param userId O ID (UUID) do usuário cujas inscrições se deseja buscar.
     * @return Uma {@link List} de {@link Registration} contendo todas as inscrições ativas
     * do usuário. Retorna uma {@link List} vazia se nenhuma for encontrada.
     */
    @Query("SELECT r FROM Registration r WHERE r.userId = ?1 AND r.deletedAt IS NULL")
    List<Registration> findAllActiveByUserId(String userId);

    @Query("SELECT r FROM Registration r WHERE r.userId = ?1 AND r.eventId = ?2 AND r.deletedAt IS NULL")
    Registration findActiveByUserAndEventId(String userId, String eventId);
}
