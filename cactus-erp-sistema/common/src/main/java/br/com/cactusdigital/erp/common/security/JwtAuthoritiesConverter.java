package br.com.cactusdigital.erp.common.security;

import java.util.Collection;
import java.util.List;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Lê o claim custom {@code authorities} emitido pelo Authorization Server
 * (cadastro-base-service) — já os códigos de {@code Funcionalidade} resolvidos
 * para o papel do usuário na empresa ativa do token — e converte 1:1 para
 * {@link GrantedAuthority}, sem prefixo {@code ROLE_} (checagem é sempre via
 * {@code hasAuthority(...)}, nunca {@code hasRole(...)}, porque o modelo é permissão
 * granular por funcionalidade, não papel hierárquico fixo).
 * <p>
 * Usado por TODOS os serviços (Resource Servers) para não precisarem entender o
 * modelo de perfilamento por trás do token — só leem authorities já resolvidas.
 */
public class JwtAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String CLAIM_AUTHORITIES = "authorities";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<String> authorities = jwt.getClaimAsStringList(CLAIM_AUTHORITIES);
        if (authorities == null) {
            return List.of();
        }
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
    }
}
