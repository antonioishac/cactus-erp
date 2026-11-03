package br.com.cactuserp.pdv.service.fiscal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Modo de contingência offline do terminal, como os PDVs de mercado: depois da primeira falha de comunicação, as
 * vendas seguintes já saem offline (sem esperar o timeout da SEFAZ a cada cupom, o que travaria a fila do caixa) até o
 * job confirmar que o serviço de autorização voltou (status 107). Um terminal = um processo: o estado vive em memória;
 * reiniciar o PDV volta a tentar online.
 */
@Component
public class ContingenciaNfceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContingenciaNfceService.class);

    private volatile LocalDateTime ativaDesde;
    private volatile String motivo;

    public boolean ativa() {
        return ativaDesde != null;
    }

    public synchronized void entrar(String motivo) {
        if (ativaDesde == null) {
            ativaDesde = LocalDateTime.now(NfceDocumentoBuilder.FUSO_BRASILIA);
            LOGGER.warn("NFC-e: terminal ENTROU em contingência offline — {}", motivo);
        }
        this.motivo = motivo;
    }

    public synchronized void sair() {
        if (ativaDesde != null) {
            LOGGER.info("NFC-e: SEFAZ disponível, terminal SAIU da contingência offline (ativa desde {}).", ativaDesde);
        }
        ativaDesde = null;
        motivo = null;
    }

    public Optional<LocalDateTime> ativaDesde() {
        return Optional.ofNullable(ativaDesde);
    }

    public String motivo() {
        return motivo;
    }
}
