package br.com.cactusdigital.erp.fiscal.sefaz;

import com.fincatto.documentofiscal.DFAmbiente;
import com.fincatto.documentofiscal.DFConfig;
import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.nfe.NFeConfig;
import java.security.KeyStore;
import java.util.TimeZone;

/**
 * {@link NFeConfig} de uma emissão: UF/ambiente da empresa emitente + certificado A1 + cadeia TLS do ambiente. Imutável
 * e montado por emissão por {@link NfeConfigFactory} — a biblioteca foi desenhada para um emitente por configuração.
 */
final class CactusNFeConfig extends NFeConfig {

    private final DFUnidadeFederativa uf;
    private final DFAmbiente ambiente;
    private final CertificadoDigitalProvider.CertificadoA1 certificado;
    private final CadeiaCertificadosProvider.CadeiaCertificados cadeia;

    CactusNFeConfig(DFUnidadeFederativa uf, DFAmbiente ambiente, CertificadoDigitalProvider.CertificadoA1 certificado,
                    CadeiaCertificadosProvider.CadeiaCertificados cadeia) {
        this.uf = uf;
        this.ambiente = ambiente;
        this.certificado = certificado;
        this.cadeia = cadeia;
    }

    @Override
    public DFUnidadeFederativa getCUF() {
        return uf;
    }

    @Override
    public DFAmbiente getAmbiente() {
        return ambiente;
    }

    @Override
    public KeyStore getCertificadoKeyStore() {
        return certificado.keyStore();
    }

    @Override
    public String getCertificadoAlias() {
        return certificado.aliasChave();
    }

    @Override
    public String getCertificadoSenha() {
        return certificado.senha();
    }

    @Override
    public KeyStore getCadeiaCertificadosKeyStore() {
        return cadeia.keyStore();
    }

    @Override
    public String getCadeiaCertificadosSenha() {
        return cadeia.senha();
    }

    /** Horário oficial de Brasília — o padrão da biblioteca é o fuso da máquina, que num container costuma ser UTC. */
    @Override
    public TimeZone getTimeZone() {
        return DFConfig.getTimezoneSP();
    }
}
