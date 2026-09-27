package br.com.cactusdigital.erp.contabilidade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

/**
 * Ponto de entrada do contabilidade-service. Mesmo padrão de {@code scanBasePackages}/
 * {@link EntityScan} dos demais serviços — o {@link EntityScan} explícito é necessário
 * pra o Hibernate descobrir {@code @Converter(autoApply=true)} do {@code common} (ex:
 * {@code SituacaoConverter}), que não é alcançado só pelo pacote da classe principal.
 */
@SpringBootApplication(scanBasePackages = "br.com.cactusdigital.erp")
@EntityScan(basePackages = "br.com.cactusdigital.erp")
public class CactusContabilidadeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CactusContabilidadeApplication.class, args);
    }
}
