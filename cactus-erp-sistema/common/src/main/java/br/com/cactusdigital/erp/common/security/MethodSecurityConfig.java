package br.com.cactusdigital.erp.common.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Liga o processamento de {@code @PreAuthorize}/{@code @PostAuthorize} em todo serviço
 * (via {@code scanBasePackages="br.com.cactusdigital.erp"}, que pega esta classe junto).
 * <p>
 * <b>Achado real (não escondido)</b>: até esta classe existir, {@code @EnableMethodSecurity}
 * nunca tinha sido declarado em lugar nenhum do monorepo — todo {@code @PreAuthorize} em
 * todo controller de todo serviço (cadastro-base-service, controle-estoque-service,
 * gestao-compras-service) era metadado inerte, nunca avaliado pelo Spring Security.
 * Qualquer usuário autenticado (com qualquer authority, ou nenhuma) passava em qualquer
 * endpoint. Achado ao investigar por que o papel ADMIN não tinha as authorities
 * {@code ESTOQUE_*} concedidas via {@code papel_funcionalidade} (ver
 * {@code V6__backfill_estoque_papel_funcionalidade.sql} no seguranca-service) mas mesmo
 * assim toda chamada de teste "passava" — a resposta certa não era "authority não é
 * necessária", era "a checagem nunca rodava".
 */
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
}
