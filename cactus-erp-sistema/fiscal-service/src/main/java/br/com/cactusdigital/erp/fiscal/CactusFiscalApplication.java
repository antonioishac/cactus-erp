package br.com.cactusdigital.erp.fiscal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

/**
 * Ponto de entrada do fiscal-service. Mesmo padrão de {@code scanBasePackages}/
 * {@link EntityScan} dos demais serviços — ver Javadoc de
 * {@code CactusCadastroBaseApplication} pro porquê do {@link EntityScan} explícito ser
 * necessário (sem ele, {@code @Converter(autoApply=true)} do {@code common} não é
 * descoberto pelo Hibernate).
 */
@SpringBootApplication(scanBasePackages = "br.com.cactusdigital.erp")
@EntityScan(basePackages = "br.com.cactusdigital.erp")
public class CactusFiscalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CactusFiscalApplication.class, args);
    }
}
