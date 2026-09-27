package br.com.cactusdigital.erp.common.domain;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Resolve {@code criadoPor}/{@code atualizadoPor} ({@link BaseEntity}) a partir do
 * usuário autenticado no request atual (claim {@code login} do JWT). Fora de um
 * request autenticado (job agendado, seed, teste), cai para {@code "system"}.
 */
public class SecurityAuditorAware implements AuditorAware<String> {

    private static final String SISTEMA = "system";

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of(SISTEMA);
        }
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String login = jwt.getClaimAsString("login");
            return Optional.of(login != null ? login : jwt.getSubject());
        }
        return Optional.of(authentication.getName());
    }
}
