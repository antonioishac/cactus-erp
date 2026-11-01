package br.com.cactusdigital.erp.fiscal.sefaz;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaConfiguracaoFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.exception.FiscalErrorCode;
import com.fincatto.documentofiscal.DFAmbiente;
import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.nfe.NFeConfig;
import org.springframework.stereotype.Component;

/**
 * Monta o {@link NFeConfig} de uma emissão e aplica a trava de produção ({@link FiscalProperties#permitirProducao()}).
 * Único ponto que decide ambiente/certificado/cadeia — o resto da camada recebe a configuração pronta.
 */
@Component
class NfeConfigFactory {

    private final FiscalProperties properties;
    private final CertificadoDigitalProvider certificadoProvider;
    private final CadeiaCertificadosProvider cadeiaProvider;

    NfeConfigFactory(FiscalProperties properties, CertificadoDigitalProvider certificadoProvider,
                     CadeiaCertificadosProvider cadeiaProvider) {
        this.properties = properties;
        this.certificadoProvider = certificadoProvider;
        this.cadeiaProvider = cadeiaProvider;
    }

    NFeConfig criar(AmbienteFiscal ambiente, EmpresaFiscalDTO emitente, EmpresaConfiguracaoFiscalDTO configuracao) {
        if (ambiente == AmbienteFiscal.PRODUCAO && !properties.permitirProducao()) {
            throw new BusinessException(FiscalErrorCode.EMISSAO_PRODUCAO_DESABILITADA);
        }
        return new CactusNFeConfig(
                DFUnidadeFederativa.valueOfCodigo(emitente.uf()),
                ambiente == AmbienteFiscal.PRODUCAO ? DFAmbiente.PRODUCAO : DFAmbiente.HOMOLOGACAO,
                certificadoProvider.obter(configuracao),
                cadeiaProvider.obter(ambiente));
    }
}
