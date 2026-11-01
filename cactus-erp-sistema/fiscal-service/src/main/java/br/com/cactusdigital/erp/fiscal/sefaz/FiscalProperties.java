package br.com.cactusdigital.erp.fiscal.sefaz;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuração da emissão direta à SEFAZ ({@code cactus.fiscal.*}).
 *
 * @param permitirProducao trava de segurança: sem {@code true} explícito, nenhum documento é transmitido ao ambiente de
 *        produção. Uma nota de empresa configurada como PRODUCAO é recusada (422) em vez de desviada para homologação,
 *        porque a chave de acesso e o {@code tpAmb} já nascem com o ambiente — desviar em silêncio geraria um documento
 *        diferente do que o usuário pediu.
 * @param certificados certificados A1 por alias — o {@code certificadoAlias} da configuração fiscal da empresa
 *        (cadastro-base-service) aponta para uma entrada daqui. A senha vem do ambiente/secret manager, nunca do banco.
 * @param cadeiaCertificados keystore com a cadeia TLS dos servidores da SEFAZ, um por ambiente. <b>Obrigatório</b>: os
 *        autorizadores usam certificados ICP-Brasil, que não estão no truststore padrão do JDK. É gerado uma vez por um
 *        operador com {@link GeradorCadeiaCertificados}, conferido e distribuído como secret. Nunca baixado
 *        automaticamente em tempo de execução: o download aceita qualquer certificado devolvido pelo host
 *        (trust-on-first-use), a própria biblioteca proíbe esse uso fora de uma execução manual.
 * @param responsavelTecnico grupo {@code infRespTec}, exigido por várias UFs (rejeição 972 quando ausente). Opcional.
 */
@ConfigurationProperties("cactus.fiscal")
public record FiscalProperties(
        boolean permitirProducao,
        Map<String, Certificado> certificados,
        CadeiasCertificados cadeiaCertificados,
        ResponsavelTecnico responsavelTecnico) {

    public FiscalProperties {
        certificados = certificados == null ? Map.of() : Map.copyOf(certificados);
        cadeiaCertificados = cadeiaCertificados == null ? new CadeiasCertificados(null, null) : cadeiaCertificados;
    }

    /** @param arquivo recurso Spring ({@code file:/run/secrets/empresa.pfx}, {@code classpath:...}). */
    public record Certificado(String arquivo, String senha) {
    }

    public record CadeiasCertificados(Cadeia homologacao, Cadeia producao) {
    }

    /** @param arquivo recurso Spring do keystore (formato padrão do JDK, PKCS12), gerado por {@link GeradorCadeiaCertificados}. */
    public record Cadeia(String arquivo, String senha) {
    }

    public record ResponsavelTecnico(String cnpj, String contato, String email, String telefone) {
    }
}
