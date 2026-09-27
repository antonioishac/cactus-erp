package br.com.cactusdigital.erp.sincronizacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Ponto de entrada do sincronizacao-service (Fase 6). Mesmo padrão de
 * {@code scanBasePackages}/{@link EntityScan} dos demais serviços — ver Javadoc de
 * {@code CactusCadastroBaseApplication} pro porquê do {@link EntityScan} explícito ser
 * necessário. {@link EnableScheduling} liga o job de relay do outbox
 * ({@code OutboxRelayService}).
 */
@SpringBootApplication(scanBasePackages = "br.com.cactusdigital.erp")
@EntityScan(basePackages = "br.com.cactusdigital.erp")
@EnableScheduling
public class CactusSincronizacaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CactusSincronizacaoApplication.class, args);
    }
}
