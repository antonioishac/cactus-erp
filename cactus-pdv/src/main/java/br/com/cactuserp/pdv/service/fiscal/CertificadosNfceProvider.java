package br.com.cactuserp.pdv.service.fiscal;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Carrega (uma vez) o certificado A1 da loja e a cadeia TLS de cada ambiente, a partir de {@link FiscalPdvProperties}.
 * Erro de configuração vira {@link NfceConfiguracaoException} com mensagem para o responsável — a venda continua (fica
 * pendente de emissão), nunca é desfeita.
 */
@Component
public class CertificadosNfceProvider {

    private final FiscalPdvProperties properties;
    private final Map<String, KeyStore> cache = new ConcurrentHashMap<>();

    public CertificadosNfceProvider(FiscalPdvProperties properties) {
        this.properties = properties;
    }

    public CertificadoA1 certificado() {
        FiscalPdvProperties.Arquivo arquivo = properties.certificado();
        if (!arquivo.configurado()) {
            throw new NfceConfiguracaoException("Certificado A1 não configurado neste terminal "
                    + "(variáveis CACTUS_PDV_CERTIFICADO_ARQUIVO e CACTUS_PDV_CERTIFICADO_SENHA).");
        }
        KeyStore keyStore = cache.computeIfAbsent("certificado", k -> carregar(arquivo, "PKCS12", "certificado A1"));
        String alias = Collections.list(aliases(keyStore)).stream()
                .filter(a -> chavePrivada(keyStore, a))
                .findFirst()
                .orElseThrow(() -> new NfceConfiguracaoException("O arquivo do certificado não contém chave privada."));
        X509Certificate x509 = certificadoX509(keyStore, alias);
        if (x509.getNotAfter().toInstant().isBefore(Instant.now())) {
            throw new NfceConfiguracaoException("Certificado A1 vencido em " + x509.getNotAfter() + ": renove para voltar a emitir.");
        }
        return new CertificadoA1(keyStore, senha(arquivo), alias, x509);
    }

    public CadeiaTls cadeia(boolean producao) {
        FiscalPdvProperties.Arquivo arquivo = producao ? properties.cadeiaCertificados().producao() : properties.cadeiaCertificados().homologacao();
        if (arquivo == null || !arquivo.configurado()) {
            throw new NfceConfiguracaoException("Cadeia de certificados da SEFAZ de " + (producao ? "produção" : "homologação")
                    + " não configurada (cactus.pdv.fiscal.cadeia-certificados." + (producao ? "producao" : "homologacao") + ".arquivo).");
        }
        String chave = producao ? "cadeia-producao" : "cadeia-homologacao";
        return new CadeiaTls(cache.computeIfAbsent(chave, k -> carregar(arquivo, KeyStore.getDefaultType(), "cadeia TLS")), senha(arquivo));
    }

    private static KeyStore carregar(FiscalPdvProperties.Arquivo arquivo, String tipo, String descricao) {
        try (InputStream in = Files.newInputStream(Path.of(arquivo.arquivo()))) {
            KeyStore keyStore = KeyStore.getInstance(tipo);
            keyStore.load(in, senha(arquivo).toCharArray());
            return keyStore;
        } catch (Exception ex) {
            throw new NfceConfiguracaoException("Não foi possível abrir o " + descricao + " (" + arquivo.arquivo() + "): " + ex.getMessage());
        }
    }

    private static String senha(FiscalPdvProperties.Arquivo arquivo) {
        return arquivo.senha() == null ? "" : arquivo.senha();
    }

    private static java.util.Enumeration<String> aliases(KeyStore keyStore) {
        try {
            return keyStore.aliases();
        } catch (Exception ex) {
            throw new NfceConfiguracaoException("Certificado ilegível: " + ex.getMessage());
        }
    }

    private static boolean chavePrivada(KeyStore keyStore, String alias) {
        try {
            return keyStore.isKeyEntry(alias);
        } catch (Exception ex) {
            return false;
        }
    }

    private static X509Certificate certificadoX509(KeyStore keyStore, String alias) {
        try {
            return (X509Certificate) keyStore.getCertificate(alias);
        } catch (Exception ex) {
            throw new NfceConfiguracaoException("Certificado ilegível: " + ex.getMessage());
        }
    }

    public record CertificadoA1(KeyStore keyStore, String senha, String alias, X509Certificate x509) {
    }

    public record CadeiaTls(KeyStore keyStore, String senha) {
    }
}
