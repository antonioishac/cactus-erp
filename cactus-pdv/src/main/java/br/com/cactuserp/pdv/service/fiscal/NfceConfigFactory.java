package br.com.cactuserp.pdv.service.fiscal;

import br.com.cactuserp.pdv.entity.LojaConfiguracao;
import com.fincatto.documentofiscal.DFAmbiente;
import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.nfe.NFTipoEmissao;
import com.fincatto.documentofiscal.nfe.NFeConfig;
import org.springframework.stereotype.Component;

/**
 * Único ponto que decide UF/ambiente/certificado/cadeia de uma operação de NFC-e e aplica a trava de produção.
 *
 * <p>{@link #paraAssinatura} não exige a cadeia TLS: assinar e gerar o QR Code offline não usam rede — é o que permite
 * a contingência offline funcionar mesmo num terminal cuja cadeia ainda não foi instalada.</p>
 */
@Component
public class NfceConfigFactory {

    private final FiscalPdvProperties properties;
    private final CertificadosNfceProvider certificados;

    public NfceConfigFactory(FiscalPdvProperties properties, CertificadosNfceProvider certificados) {
        this.properties = properties;
        this.certificados = certificados;
    }

    /** Para operações que falam com a SEFAZ (autorização, consulta, eventos, inutilização). */
    public NFeConfig paraTransmissao(LojaConfiguracao loja, boolean producao, NFTipoEmissao tipoEmissao) {
        validarAmbiente(producao);
        return new PdvNfceConfig(uf(loja), ambiente(producao), tipoEmissao, certificados.certificado(),
                certificados.cadeia(producao), properties.timeoutMs());
    }

    /** Para montar/assinar o documento e o QR Code (sem rede). */
    public NFeConfig paraAssinatura(LojaConfiguracao loja, boolean producao, NFTipoEmissao tipoEmissao) {
        validarAmbiente(producao);
        return new PdvNfceConfig(uf(loja), ambiente(producao), tipoEmissao, certificados.certificado(), null, properties.timeoutMs());
    }

    private void validarAmbiente(boolean producao) {
        if (producao && !properties.permitirProducao()) {
            throw new NfceConfiguracaoException("Terminal configurado em PRODUÇÃO, mas a emissão em produção está desligada "
                    + "neste computador (cactus.pdv.fiscal.permitir-producao=false).");
        }
    }

    private static DFUnidadeFederativa uf(LojaConfiguracao loja) {
        if (loja == null || loja.getUf() == null || loja.getUf().isBlank()) {
            throw new NfceConfiguracaoException("UF da loja não configurada (LOJA_CONFIGURACAO.UF).");
        }
        return DFUnidadeFederativa.valueOfCodigo(loja.getUf());
    }

    private static DFAmbiente ambiente(boolean producao) {
        return producao ? DFAmbiente.PRODUCAO : DFAmbiente.HOMOLOGACAO;
    }
}
