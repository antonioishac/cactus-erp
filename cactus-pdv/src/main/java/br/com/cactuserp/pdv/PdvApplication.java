package br.com.cactuserp.pdv;

import br.com.cactuserp.pdv.service.fiscal.FiscalPdvProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import br.com.cactuserp.pdv.config.JavaFxApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// @EnableScheduling habilita o job de reprocessamento fiscal
// (ReprocessamentoFiscalService) — ver fase 6 do ROADMAP.md.
@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(FiscalPdvProperties.class)
public class PdvApplication {
    public static void main(String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }
}