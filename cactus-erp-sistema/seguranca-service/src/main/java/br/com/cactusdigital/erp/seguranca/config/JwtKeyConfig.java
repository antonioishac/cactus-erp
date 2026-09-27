package br.com.cactusdigital.erp.seguranca.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

/**
 * Par de chaves RSA que assina os JWTs emitidos pelo Authorization Server.
 * <p>
 * Dois beans mutuamente exclusivos por perfil, não um só condicional dentro do método —
 * são estratégias genuinamente diferentes, não uma variação de parâmetro:
 * <ul>
 *   <li>{@link #jwkSourceDev()} (perfil {@code !prod}, ou seja, o default de quem roda
 *   local sem configurar nada): gera um par de chaves RSA efêmero, em memória, a cada
 *   boot. Zero arquivo, zero script, zero segredo — o único efeito colateral é que todo
 *   restart invalida os tokens já emitidos (usuário loga de novo), o que é irrelevante
 *   em desenvolvimento e elimina de vez a fricção de gerar/versionar um keystore local.</li>
 *   <li>{@link #jwkSourceProd(ResourceLoader, String, String, String)} (perfil {@code prod}):
 *   carrega de um keystore PKCS12 persistido — necessário em produção porque (a) a chave
 *   precisa sobreviver a um restart/deploy sem deslogar todo mundo, e (b) se o serviço
 *   escalar horizontalmente, todas as instâncias precisam assinar com a MESMA chave pra
 *   os outros serviços validarem contra um JWKS único.</li>
 * </ul>
 * <b>Sobre o ".p12" não ser um certificado SSL/TLS</b>: PKCS12 é só um formato de
 * arquivo pra guardar chave privada + chave pública juntas — é o mesmo mecanismo que
 * guardaria um certificado de servidor HTTPS, mas aqui não há cadeia de confiança nem
 * identidade sendo validada, é usado puramente como um contêiner de par de chaves RSA
 * (a API {@code java.security.KeyStore} do Java exige esse formato pra ler chave
 * assimétrica de um arquivo). Nenhum outro serviço confia no "certificado" em si — eles
 * só pegam a chave pública exposta em {@code /oauth2/jwks} pra validar assinatura.
 */
@Configuration
public class JwtKeyConfig {

    private static final Logger log = LoggerFactory.getLogger(JwtKeyConfig.class);

    @Bean
    @Profile("!prod")
    public JWKSource<SecurityContext> jwkSourceDev() {
        log.warn("Authorization Server assinando com par de chaves RSA EFÊMERO (perfil != prod) — "
                + "tokens emitidos não sobrevivem a um restart deste processo. Nunca usar fora de desenvolvimento local.");
        KeyPair keyPair = gerarParDeChaves();
        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, context) -> jwkSelector.select(jwkSet);
    }

    @Bean
    @Profile("prod")
    public JWKSource<SecurityContext> jwkSourceProd(
            ResourceLoader resourceLoader,
            @Value("${cactus.security.jwt-keystore.path}") String keystorePath,
            @Value("${cactus.security.jwt-keystore.password}") String keystorePassword,
            @Value("${cactus.security.jwt-keystore.alias}") String keystoreAlias) throws Exception {

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream in = resourceLoader.getResource(keystorePath).getInputStream()) {
            keyStore.load(in, keystorePassword.toCharArray());
        }

        RSAPrivateKey privateKey = (RSAPrivateKey) keyStore.getKey(keystoreAlias, keystorePassword.toCharArray());
        RSAPublicKey publicKey = (RSAPublicKey) keyStore.getCertificate(keystoreAlias).getPublicKey();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(keystoreAlias)
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, context) -> jwkSelector.select(jwkSet);
    }

    /**
     * Usado pelo Authorization Server internamente para emitir os JWTs normais do
     * login, e também diretamente por {@code AuthController#trocarEmpresa}/
     * {@code DevAuthController#devLogin} — mesma chave, mesmo formato, então o token
     * resultante é validado pelos outros serviços exatamente como qualquer token emitido
     * pelo fluxo OAuth2 padrão.
     */
    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    private static KeyPair gerarParDeChaves() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Algoritmo RSA indisponível na JVM", ex);
        }
    }
}
