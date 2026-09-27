package br.com.cactusdigital.erp.compras;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

/**
 * Ponto de entrada do gestao-compras-service. Mesmo padrão de
 * {@code scanBasePackages}/{@link EntityScan} dos demais serviços — ver Javadoc de
 * {@code CactusCadastroBaseApplication} pro porquê do {@link EntityScan} explícito ser
 * necessário (sem ele, {@code @Converter(autoApply=true)} do {@code common} não é
 * descoberto pelo Hibernate).
 */
@SpringBootApplication(scanBasePackages = "br.com.cactusdigital.erp")
@EntityScan(basePackages = "br.com.cactusdigital.erp")
public class CactusComprasApplication {

    public static void main(String[] args) {
        SpringApplication.run(CactusComprasApplication.class, args);
    }
}
