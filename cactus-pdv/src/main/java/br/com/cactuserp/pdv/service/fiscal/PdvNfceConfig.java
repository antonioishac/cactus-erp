package br.com.cactuserp.pdv.service.fiscal;

import com.fincatto.documentofiscal.DFAmbiente;
import com.fincatto.documentofiscal.DFConfig;
import com.fincatto.documentofiscal.DFModelo;
import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.nfe.NFTipoEmissao;
import com.fincatto.documentofiscal.nfe.NFeConfig;

import java.security.KeyStore;
import java.util.TimeZone;

/**
 * {@link NFeConfig} de uma operação de NFC-e: UF da loja, ambiente do terminal, tipo de emissão, certificado e cadeia.
 * Imutável, montado por operação por {@link NfceConfigFactory}. Sem CSC: o QR Code v3 (NT 2025.001) não usa mais.
 */
final class PdvNfceConfig extends NFeConfig {

    private final DFUnidadeFederativa uf;
    private final DFAmbiente ambiente;
    private final NFTipoEmissao tipoEmissao;
    private final CertificadosNfceProvider.CertificadoA1 certificado;
    private final CertificadosNfceProvider.CadeiaTls cadeia;
    private final int timeoutMs;

    PdvNfceConfig(DFUnidadeFederativa uf, DFAmbiente ambiente, NFTipoEmissao tipoEmissao,
                  CertificadosNfceProvider.CertificadoA1 certificado, CertificadosNfceProvider.CadeiaTls cadeia, int timeoutMs) {
        this.uf = uf;
        this.ambiente = ambiente;
        this.tipoEmissao = tipoEmissao;
        this.certificado = certificado;
        this.cadeia = cadeia;
        this.timeoutMs = timeoutMs;
    }

    @Override
    public DFModelo getModelo() {
        return DFModelo.NFCE;
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
    public NFTipoEmissao getTipoEmissao() {
        return tipoEmissao;
    }

    @Override
    public KeyStore getCertificadoKeyStore() {
        return certificado.keyStore();
    }

    @Override
    public String getCertificadoAlias() {
        return certificado.alias();
    }

    @Override
    public String getCertificadoSenha() {
        return certificado.senha();
    }

    @Override
    public KeyStore getCadeiaCertificadosKeyStore() {
        return cadeia == null ? null : cadeia.keyStore();
    }

    @Override
    public String getCadeiaCertificadosSenha() {
        return cadeia == null ? "" : cadeia.senha();
    }

    @Override
    public int getTimeoutRequisicaoEmMillis() {
        return timeoutMs;
    }

    @Override
    public int getSoTimeoutEmMillis() {
        return timeoutMs;
    }

    @Override
    public int getTimeoutFilaConexaoEmMillis() {
        return timeoutMs;
    }

    /** Horário de Brasília — o padrão da biblioteca é o fuso da máquina do terminal. */
    @Override
    public TimeZone getTimeZone() {
        return DFConfig.getTimezoneSP();
    }
}
