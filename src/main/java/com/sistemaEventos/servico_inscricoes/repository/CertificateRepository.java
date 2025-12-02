package com.sistemaEventos.servico_inscricoes.repository;

import com.sistemaEventos.servico_inscricoes.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, String> {
    @Query("SELECT c FROM Certificate c WHERE c.registrationId = ?1 AND c.deletedAt IS NULL")
    Optional<Certificate> findByRegistrationId(String registrationId);

    @Query("SELECT c FROM Certificate c WHERE c.authenticationCode = ?1 AND c.deletedAt IS NULL")
    Optional<Certificate> getCertificateByAuthenticationCode(String authenticationCode);
}
