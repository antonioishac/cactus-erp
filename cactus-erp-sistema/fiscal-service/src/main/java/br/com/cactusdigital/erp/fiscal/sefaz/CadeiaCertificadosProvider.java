package br.com.cactusdigital.erp.fiscal.sefaz;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.exception.FiscalErrorCode;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * Carrega (uma vez por ambiente) o keystore com a cadeia TLS dos autorizadores da SEFAZ, configurado em
 * {@code cactus.fiscal.cadeia-certificados.<ambiente>}. Sem configuração, recusa: ver {@link FiscalProperties} pro
 * porquê de nunca baixar a cadeia automaticamente.
 */
@Component
public class CadeiaCertificadosProvider {

    private final FiscalProperties properties;
    private final ResourceLoader resourceLoader;
    private final Map<AmbienteFiscal, CadeiaCertificados> cache = new ConcurrentHashMap<>();

    public CadeiaCertificadosProvider(FiscalProperties properties, ResourceLoader resourceLoader) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
    }

    public CadeiaCertificados obter(AmbienteFiscal ambiente) {
        FiscalProperties.Cadeia entrada = ambiente == AmbienteFiscal.PRODUCAO
                ? properties.cadeiaCertificados().producao()
                : properties.cadeiaCertificados().homologacao();
        if (entrada == null || entrada.arquivo() == null || entrada.arquivo().isBlank()) {
            throw new BusinessException(FiscalErrorCode.CADEIA_CERTIFICADOS_NAO_CONFIGURADA,
                    "Cadeia de certificados da SEFAZ não configurada para " + ambiente
                            + ": gere com GeradorCadeiaCertificados e aponte cactus.fiscal.cadeia-certificados."
                            + ambiente.name().toLowerCase() + ".arquivo/senha");
        }
        return cache.computeIfAbsent(ambiente, a -> carregar(a, entrada));
    }

    private CadeiaCertificados carregar(AmbienteFiscal ambiente, FiscalProperties.Cadeia entrada) {
        String senha = entrada.senha() == null ? "" : entrada.senha();
        try (InputStream in = resourceLoader.getResource(entrada.arquivo()).getInputStream()) {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(in, senha.toCharArray());
            return new CadeiaCertificados(keyStore, senha);
        } catch (Exception ex) {
            throw new BusinessException(FiscalErrorCode.CADEIA_CERTIFICADOS_NAO_CONFIGURADA,
                    "Não foi possível abrir a cadeia de certificados de " + ambiente + ": " + ex.getMessage());
        }
    }

    public record CadeiaCertificados(KeyStore keyStore, String senha) {
    }
}
