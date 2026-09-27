package br.com.cactusdigital.erp.seguranca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

/**
 * Ponto de entrada do seguranca-service — Authorization Server (OAuth2/JWT) do
 * ecossistema e dono do RBAC (Usuario/Papel/Funcionalidade/Modulo/UsuarioPapelEmpresa).
 * <p>
 * {@code scanBasePackages} sobe até {@code br.com.cactusdigital.erp} pra também
 * alcançar {@code br.com.cactusdigital.erp.common} (exception handler, entidade base
 * auditável, filtros de log/correlação, baseline de Resource Server) — mas isso afeta
 * só o {@code @ComponentScan} (beans Spring). O escaneamento de {@code @Entity} e de
 * {@code @Converter(autoApply=true)} é um mecanismo JPA separado que, sem
 * {@link EntityScan} explícito, usaria só o pacote desta classe — mesmo ajuste já
 * validado (e necessário) no cadastro-base-service.
 */
@SpringBootApplication(scanBasePackages = "br.com.cactusdigital.erp")
@EntityScan(basePackages = "br.com.cactusdigital.erp")
public class CactusSegurancaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CactusSegurancaApplication.class, args);
    }
}
