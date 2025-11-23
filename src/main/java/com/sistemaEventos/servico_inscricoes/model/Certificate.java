package com.sistemaEventos.servico_inscricoes.model;

import com.sistemaEventos.servico_inscricoes.repository.CertificateRepository;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * Representa a entidade principal de Certificados (Certificate) no sistema.
 * <p>
 * Implementa o padrão "Soft Delete" (exclusão lógica) através da anotação
 * {@link SQLDelete}. Quando um 'delete' é executado (ex: via {@link CertificateRepository#deleteById}),
 * o Hibernate irá, em vez disso, executar o SQL customizado, preenchendo o campo {@code deletedAt}
 * e preservando o registro no banco de dados.
 */
@Entity
@Table(name = "certificates")
//Intercepta qualquer chamada de 'delete' e roda este SQL
@SQLDelete(sql = "UPDATE certificates SET deleted_at = NOW() WHERE id = ?")
public class Certificate {
    /**
     * O identificador único (UUID) do certificado.
     */
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private String id;

    @Column(name = "registrations_id", nullable = false, updatable = false)
    private String registrationId;

    @Column(name = "authentication_code", nullable = false, updatable = false)
    private String authenticationCode;

    @Column(name = "issue_date", nullable = false, updatable = false)
    private Instant issueDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    //Constructor
    public Certificate() {}

    public Certificate(String id, String registrationId, String authenticationCode, Instant issueDate) {
        this.id = id;
        this.registrationId = registrationId;
        this.authenticationCode = authenticationCode;
        this.issueDate = issueDate;
    }

    //Getters e Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getAuthenticationCode() {
        return authenticationCode;
    }

    public void setAuthenticationCode(String authenticationCode) {
        this.authenticationCode = authenticationCode;
    }

    public Instant getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Instant issueDate) {
        this.issueDate = issueDate;
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
