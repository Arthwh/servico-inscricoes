package com.sistemaEventos.servico_inscricoes.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Componente utilitário que centraliza a lógica de autorização baseada em "Roles" (papéis).
 * <p>
 * Esta classe fornece métodos reutilizáveis para verificar se um usuário
 * possui uma role específica (ex: ADMIN) ou se ele é o "dono" de um recurso,
 * abstraindo essa lógica dos serviços de negócio.
 */
@Component
public class AuthorizationHelper {
    private static final String ADMIN_ROLE = "ROLE_ADMIN";

    /**
     * Verifica se o header de roles contém a role de ADMIN.
     * @param requesterRoles O header "X-User-Roles" (ex: "ROLE_USER,ROLE_ADMIN").
     * @return true se for admin, false caso contrário.
     */
    public boolean isAdmin(String requesterRoles) {
        if (requesterRoles == null || requesterRoles.isEmpty()) {
            return false;
        }
        return requesterRoles.contains(ADMIN_ROLE);
    }

    /**
     * Verifica se o header de roles contém a role de ADMIN.
     * @param requesterRoles O header "X-User-Roles" (ex: "ROLE_USER,ROLE_ADMIN").
     * @throws AccessDeniedException se o {@code requesterRoles} não conter
     * a role "ROLE_ADMIN".
     */
    public void checkIsAdmin(String requesterRoles) {
        if (!isAdmin(requesterRoles)) {
            throw new AccessDeniedException("Acesso negado. O usuário não tem permissões para acessar ou modificar o recurso.");
        }
    }

    /**
     * Verifica se o usuário é dono do recurso OU se é Admin.
     * @param resourceOwner O ID do dono do objeto ou recurso alvo.
     * @param requesterId O ID do usuário logado (do token).
     * @param requesterRoles O header de roles do usuário logado.
     * @throws AccessDeniedException (via AuthorizationHelper) se o {@code requesterId} não for o dono
     * do recurso nem um ADMIN.
     */
    public void checkOwnershipOrAdmin(String resourceOwner, String requesterId, String requesterRoles) {
        //Verifica se o usuário é admin
        if (isAdmin(requesterRoles)) {
            return;
        }
        //Verifica se o usuário é o dono do recurso
        if (Objects.equals(requesterId, resourceOwner)) {
            return;
        }

        throw new AccessDeniedException("Acesso negado. O usuário não tem permissões para acessar ou modificar o recurso.");
    }
}
