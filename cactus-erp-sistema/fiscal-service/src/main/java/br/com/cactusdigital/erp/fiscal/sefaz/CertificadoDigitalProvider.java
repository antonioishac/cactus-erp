package br.com.cactusdigital.erp.fiscal.sefaz;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaConfiguracaoFiscalDTO;
import br.com.cactusdigital.erp.fiscal.exception.FiscalErrorCode;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * Resolve o certificado A1 de uma empresa a partir do {@code certificadoAlias} da configuração fiscal dela. O
 * arquivo e a senha moram em {@code cactus.fiscal.certificados.<alias>} (variável de ambiente/secret manager) —
 * o banco só guarda o alias. A3 (token/cartão) não é suportado: exige PKCS#11 no servidor.
 */
@Component
public class CertificadoDigitalProvider {

    private final FiscalProperties properties;
    private final ResourceLoader resourceLoader;
    private final Map<String, CertificadoA1> cache = new ConcurrentHashMap<>();

    public CertificadoDigitalProvider(FiscalProperties properties, ResourceLoader resourceLoader) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
    }

    public CertificadoA1 obter(EmpresaConfiguracaoFiscalDTO configuracao) {
        if ("A3".equals(configuracao.tipoCertificado())) {
            throw new BusinessException(FiscalErrorCode.CERTIFICADO_A3_NAO_SUPORTADO);
        }
        String alias = configuracao.certificadoAlias();
        if (alias == null || alias.isBlank()) {
            throw new BusinessException(FiscalErrorCode.CERTIFICADO_NAO_CONFIGURADO,
                    "A configuração fiscal da empresa #" + configuracao.empresaId() + " não tem certificadoAlias");
        }
        FiscalProperties.Certificado entrada = properties.certificados().get(alias);
        if (entrada == null || entrada.arquivo() == null || entrada.arquivo().isBlank()) {
            throw new BusinessException(FiscalErrorCode.CERTIFICADO_NAO_CONFIGURADO,
                    "Certificado '" + alias + "' não encontrado em cactus.fiscal.certificados (arquivo e senha vêm do ambiente)");
        }
        CertificadoA1 certificado = cache.computeIfAbsent(alias, a -> carregar(a, entrada));
        if (certificado.certificado().getNotAfter().toInstant().isBefore(Instant.now())) {
            throw new BusinessException(FiscalErrorCode.CERTIFICADO_INVALIDO,
                    "Certificado '" + alias + "' vencido em " + certificado.certificado().getNotAfter().toInstant());
        }
        return certificado;
    }

    private CertificadoA1 carregar(String alias, FiscalProperties.Certificado entrada) {
        String senha = entrada.senha() == null ? "" : entrada.senha();
        try (InputStream in = resourceLoader.getResource(entrada.arquivo()).getInputStream()) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(in, senha.toCharArray());
            String aliasChave = Collections.list(keyStore.aliases()).stream()
                    .filter(a -> isChavePrivada(keyStore, a))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("o arquivo não contém chave privada"));
            return new CertificadoA1(keyStore, senha, aliasChave, (X509Certificate) keyStore.getCertificate(aliasChave));
        } catch (Exception ex) {
            throw new BusinessException(FiscalErrorCode.CERTIFICADO_INVALIDO,
                    "Não foi possível abrir o certificado '" + alias + "': " + ex.getMessage());
        }
    }

    private static boolean isChavePrivada(KeyStore keyStore, String alias) {
        try {
            return keyStore.isKeyEntry(alias);
        } catch (Exception ex) {
            return false;
        }
    }

    public record CertificadoA1(KeyStore keyStore, String senha, String aliasChave, X509Certificate certificado) {
    }
}
