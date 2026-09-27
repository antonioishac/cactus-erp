package br.com.cactusdigital.erp.cadastro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

/**
 * Ponto de entrada do cadastro-base-service.
 * <p>
 * {@code scanBasePackages} sobe até {@code br.com.cactusdigital.erp} pra também
 * alcançar {@code br.com.cactusdigital.erp.common} (exception handler, entidade base
 * auditável, configuração de Resource Server etc.) — mas isso afeta só o
 * {@code @ComponentScan} (beans Spring). O escaneamento de {@code @Entity} e, mais
 * sutilmente, de {@code @Converter(autoApply=true)} é um mecanismo JPA separado que,
 * sem {@link EntityScan} explícito, usa só o pacote desta classe (só
 * {@code ...erp.cadastro}) — foi exatamente isso que causou um bug real na primeira
 * subida da aplicação: {@code SituacaoConverter} (em {@code common.domain}) nunca era
 * descoberto, e o Hibernate caía no mapeamento padrão de enum ({@code ORDINAL} →
 * {@code TINYINT}), batendo de frente com a coluna {@code CHAR(1)} das migrations.
 * {@link EntityScan} explícito nos dois pacotes resolve isso.
 */
@SpringBootApplication(scanBasePackages = "br.com.cactusdigital.erp")
@EntityScan(basePackages = "br.com.cactusdigital.erp")
public class CactusCadastroBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(CactusCadastroBaseApplication.class, args);
    }
}
