package com.sistemaEventos.servico_inscricoes.service;

import com.sistemaEventos.servico_inscricoes.client.EventClient;
import com.sistemaEventos.servico_inscricoes.client.UserClient;
import com.sistemaEventos.servico_inscricoes.dto.EventDTO;
import com.sistemaEventos.servico_inscricoes.dto.UserDTO;
import com.sistemaEventos.servico_inscricoes.exception.CertificateDataNotFoundException;
import com.sistemaEventos.servico_inscricoes.exception.NonApplicableForCertificationException;
import com.sistemaEventos.servico_inscricoes.exception.RegistrationNotFoundException;
import com.sistemaEventos.servico_inscricoes.model.Certificate;
import com.sistemaEventos.servico_inscricoes.model.Registration;
import com.sistemaEventos.servico_inscricoes.model.RegistrationStatus;
import com.sistemaEventos.servico_inscricoes.repository.CertificateRepository;
import com.sistemaEventos.servico_inscricoes.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import com.itextpdf.html2pdf.HtmlConverter;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.UUID;

@Service
public class CertificateService {
    @Autowired
    private SpringTemplateEngine templateEngine; // Thymeleaf
    @Autowired
    private UserClient userClient;   // Feign Client
    @Autowired
    private EventClient eventClient; // Feign Client
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private CertificateRepository certificateRepository;


    @Transactional
    public Certificate getCertificateByRegistration(String registrationId) {
        //Valida a inscrição
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RegistrationNotFoundException("Inscrição não encontrada."));

        if (!registration.getStatus().equals(RegistrationStatus.COMPLETED) &&
                !registration.getStatus().equals(RegistrationStatus.CHECKED_IN)) {
            throw new NonApplicableForCertificationException("A inscrição não é válida para receber um certificado.");
        }

        return certificateRepository.findByRegistrationId(registrationId)
                .orElseGet(() -> {
                    // Só entra aqui se NÃO encontrar o certificado
                    Certificate newCertificate = new Certificate();
                    newCertificate.setId(UUID.randomUUID().toString());
                    newCertificate.setRegistrationId(registrationId);
                    newCertificate.setAuthenticationCode(UUID.randomUUID().toString());
                    newCertificate.setIssueDate(Instant.now());

                    return certificateRepository.save(newCertificate);
                });
    }

    public Certificate getCertificateByAuthenticationCode(String authenticationCode) {
        return certificateRepository.getCertificateByAuthenticationCode(authenticationCode).orElseThrow(() -> new CertificateDataNotFoundException("Certificado não válido ou não encontrado."));
    }

    public byte[] generateCertificate(String authenticationCode) {
        Certificate certificate = certificateRepository.getCertificateByAuthenticationCode(authenticationCode)
                .orElseThrow(() -> new CertificateDataNotFoundException("Certificado não encontrado pelo código de autenticação."));

        Registration registration = registrationRepository.findById(certificate.getRegistrationId())
                .orElseThrow(() -> new RegistrationNotFoundException("Inscrição do certificado não encontrada."));


        UserDTO user = userClient.findById(registration.getUserId());
        EventDTO event = eventClient.findById(registration.getEventId());

        if (user == null || event == null) {
            throw new CertificateDataNotFoundException("Dados não encontrados para gerar certificado");
        }

        return this.generatePdfFile(user, event, certificate);
    }

    private byte[] generatePdfFile(UserDTO user, EventDTO event, Certificate certificate) {
        // Preparar o Contexto do Thymeleaf (os dados do template)
        Context context = new Context();
        context.setVariable("userFullname", user.fullname());
        context.setVariable("userCpf", user.cpf());
        context.setVariable("eventName", event.eventName());
        context.setVariable("eventDate", formatDate(event.eventDate()));
        context.setVariable("duration", event.duration());
        context.setVariable("authenticationCode", certificate.getAuthenticationCode());

        // Processar o HTML com os dados
        String htmlPreenchido = templateEngine.process("certificate", context);

        // Gerar o PDF a partir do HTML
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            HtmlConverter.convertToPdf(htmlPreenchido, outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF: ", e);
        }
    }

    private String formatDate(String eventDate){
        return eventDate.replace("T", " às ").replace("Z", " horas");
    }
}
