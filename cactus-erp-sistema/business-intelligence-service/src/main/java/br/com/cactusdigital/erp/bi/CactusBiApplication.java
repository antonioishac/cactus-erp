package br.com.cactusdigital.erp.bi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Ponto de entrada do business-intelligence-service. Mesmo padrão de
 * {@code scanBasePackages}/{@link EntityScan} dos demais serviços — ver Javadoc de
 * {@code CactusCadastroBaseApplication} pro porquê do {@link EntityScan} explícito ser
 * necessário. {@link EnableScheduling} liga o ETL diário (ver {@code EtlService}).
 */
@SpringBootApplication(scanBasePackages = "br.com.cactusdigital.erp")
@EntityScan(basePackages = "br.com.cactusdigital.erp")
@EnableScheduling
public class CactusBiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CactusBiApplication.class, args);
    }
}
