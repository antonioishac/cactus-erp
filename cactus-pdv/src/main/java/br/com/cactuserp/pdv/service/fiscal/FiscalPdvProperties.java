package br.com.cactuserp.pdv.service.fiscal;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuração da NFC-e direta na SEFAZ ({@code cactus.pdv.fiscal.*}). Segredos (arquivo e senha do certificado, da
 * cadeia) só por variável de ambiente — nunca no banco nem no {@code application.properties} versionado.
 *
 * @param certificado A1 (PKCS12) da loja. A3 não é suportado (exige PKCS#11 e driver do token no terminal).
 * @param cadeiaCertificados keystore com a cadeia TLS dos autorizadores, um por ambiente. <b>Obrigatório</b>: os
 *        autorizadores usam ICP-Brasil, fora do truststore do JDK. Gerado uma vez por um operador
 *        ({@code GeradorCadeiaCertificados} do fiscal-service, mesma biblioteca) e conferido — nunca baixado em tempo de
 *        execução (o download aceita qualquer certificado devolvido, trust-on-first-use).
 * @param permitirProducao trava: sem {@code true} explícito, terminal configurado em produção não emite.
 * @param timeoutMs tempo máximo de espera da SEFAZ na emissão online antes de cair para a contingência offline. No
 *        checkout, cada segundo conta: padrão 8s (prática de mercado: 5 a 10s).
 * @param responsavelTecnico grupo {@code infRespTec} (rejeição 972 em várias UFs quando ausente).
 */
@ConfigurationProperties("cactus.pdv.fiscal")
public record FiscalPdvProperties(
        Arquivo certificado,
        Cadeias cadeiaCertificados,
        boolean permitirProducao,
        Integer timeoutMs,
        ResponsavelTecnico responsavelTecnico) {

    public FiscalPdvProperties {
        certificado = certificado == null ? new Arquivo(null, null) : certificado;
        cadeiaCertificados = cadeiaCertificados == null ? new Cadeias(null, null) : cadeiaCertificados;
        timeoutMs = timeoutMs == null || timeoutMs <= 0 ? 8_000 : timeoutMs;
    }

    /** @param arquivo caminho do arquivo ({@code C:/cactus/certificado/loja.pfx}). */
    public record Arquivo(String arquivo, String senha) {

        public boolean configurado() {
            return arquivo != null && !arquivo.isBlank();
        }
    }

    public record Cadeias(Arquivo homologacao, Arquivo producao) {
    }

    public record ResponsavelTecnico(String cnpj, String contato, String email, String telefone) {
    }
}
