package com.sistemaEventos.servico_inscricoes.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import com.sistemaEventos.servico_inscricoes.repository.RegistrationRepository;

/**
 * Representa a entidade principal de Inscrição (Registration) no sistema.
 * <p>
 * Esta classe modela ?
 * <p>
 * Implementa o padrão "Soft Delete" (exclusão lógica) através da anotação
 * {@link SQLDelete}. Quando um 'delete' é executado (ex: via {@link RegistrationRepository#deleteById}),
 * o Hibernate irá, em vez disso, executar o SQL customizado, preenchendo o campo {@code deletedAt}
 * e preservando o registro no banco de dados.
 */
@Entity
@Table(name = "registrations")
//Intercepta qualquer chamada de 'delete' e roda este SQL
@SQLDelete(sql = "UPDATE registrations SET deleted_at = NOW() WHERE id = ?")
public class Registration {
    /**
     * O identificador único (UUID) da inscrição.
     */
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private String id;

    @Column(name = "events_id", nullable = false, updatable = false)
    private String eventId;

    @Column(name = "users_id", nullable = false, updatable = false)
    private String userId;

    @Column(name = "check_in")
    private Instant checkIn;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RegistrationStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    //Constructor
    public Registration() {}

    //Getters e Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Instant getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Instant checkIn) {
        this.checkIn = checkIn;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }
}
