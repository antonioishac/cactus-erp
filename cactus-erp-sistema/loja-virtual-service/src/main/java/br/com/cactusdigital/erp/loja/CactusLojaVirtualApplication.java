package br.com.cactusdigital.erp.loja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Ponto de entrada do loja-virtual-service. Mesmo padrão de {@code scanBasePackages}/
 * {@link EntityScan} dos demais serviços — ver Javadoc de
 * {@code CactusCadastroBaseApplication} pro porquê do {@link EntityScan} explícito ser
 * necessário (sem ele, {@code @Converter(autoApply=true)} do {@code common} não é
 * descoberto pelo Hibernate). {@code @EnableScheduling} (Fase 13, sub-fase 4) — mesmo
 * padrão de {@code business-intelligence-service.CactusBiApplication} — pra
 * {@code CarrinhoExpiracaoService} liberar reserva de carrinho abandonado por inatividade.
 */
@SpringBootApplication(scanBasePackages = "br.com.cactusdigital.erp")
@EntityScan(basePackages = "br.com.cactusdigital.erp")
@EnableScheduling
public class CactusLojaVirtualApplication {

    public static void main(String[] args) {
        SpringApplication.run(CactusLojaVirtualApplication.class, args);
    }
}
