package br.com.cactusdigital.erp.fiscal.sefaz;

import com.fincatto.documentofiscal.DFAmbiente;
import com.fincatto.documentofiscal.utils.DFCadeiaCertificados;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HexFormat;

/**
 * Ferramenta de operador (não roda dentro do serviço): gera o keystore com a cadeia TLS de todos os autorizadores
 * NF-e/NFC-e de um ambiente e imprime o SHA-256 de cada certificado capturado, para conferência antes de distribuir o
 * arquivo como secret. Ver {@link FiscalProperties} pro porquê de isso não ser automático.
 *
 * <pre>
 * java -cp fiscal-service.jar -Dloader.main=br.com.cactusdigital.erp.fiscal.sefaz.GeradorCadeiaCertificados \
 *      org.springframework.boot.loader.launch.PropertiesLauncher HOMOLOGACAO /caminho/homologacao.cacerts senha
 * </pre>
 */
public final class GeradorCadeiaCertificados {

    private GeradorCadeiaCertificados() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("uso: GeradorCadeiaCertificados <HOMOLOGACAO|PRODUCAO> <arquivo-saida> <senha>");
            System.exit(2);
        }
        DFAmbiente ambiente = DFAmbiente.valueOf(args[0]);
        byte[] keystore = DFCadeiaCertificados.geraCadeiaCertificados(ambiente, args[2]);
        Files.write(Path.of(args[1]), keystore);

        KeyStore lido = KeyStore.getInstance(KeyStore.getDefaultType());
        lido.load(new ByteArrayInputStream(keystore), args[2].toCharArray());
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        for (String alias : Collections.list(lido.aliases())) {
            Certificate certificado = lido.getCertificate(alias);
            String titular = certificado instanceof X509Certificate x509 ? x509.getSubjectX500Principal().getName() : "?";
            System.out.println(HexFormat.ofDelimiter(":").formatHex(sha256.digest(certificado.getEncoded())) + "  " + alias + "  " + titular);
        }
        System.out.println(lido.size() + " certificados gravados em " + args[1] + " — confira os fingerprints antes de distribuir.");
    }
}
