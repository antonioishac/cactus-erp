package br.com.cactuserp.pdv.service.fiscal;

import com.fincatto.documentofiscal.nfe.NFTipoEmissao;
import com.fincatto.documentofiscal.nfe.NFeConfig;
import com.fincatto.documentofiscal.nfe400.classes.lote.envio.NFLoteEnvio;
import com.fincatto.documentofiscal.nfe400.classes.lote.envio.NFLoteIndicadorProcessamento;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNota;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoSuplementar;
import com.fincatto.documentofiscal.nfe400.utils.NFGeraChave;
import com.fincatto.documentofiscal.nfe400.utils.qrcode30.NFGeraQRCode30;
import com.fincatto.documentofiscal.nfe400.utils.qrcode30.NFGeraQRCodeContingenciaOffline30;
import com.fincatto.documentofiscal.nfe400.utils.qrcode30.NFGeraQRCodeEmissaoNormal30;
import com.fincatto.documentofiscal.utils.DFAssinaturaDigital;
import com.fincatto.documentofiscal.validadores.DFXMLValidador;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.util.List;

/**
 * Assina a NFC-e e aplica o QR Code <b>v3</b> (NT 2025.001), sem rede. Necessário porque o {@code getLoteAssinado} da
 * biblioteca força o QR Code v2 (com CSC) para NFC-e. Mesmo passo a passo dela: chave/DV → assinatura XML-DSig do
 * {@code infNFe} → {@code infNFeSupl} (QR Code + URL de consulta) depois de assinado, porque o grupo fica fora do
 * trecho assinado.
 *
 * <ul>
 *   <li>Emissão normal: {@code chave|3|tpAmb} — sem CSC nem hash.</li>
 *   <li>Contingência offline: {@code chave|3|tpAmb|dia|vNF|tpIdDest|idDest|assinatura}, assinatura com o certificado
 *       (rejeições 474/583 se ausente ou divergente).</li>
 * </ul>
 * Valida o lote final no XSD oficial: erro aqui é de montagem/cadastro ({@link NfceConfiguracaoException}), nada é
 * enviado.
 */
@Component
public class AssinadorNfce {

    public DocumentoNfceAssinado assinar(NFNota nota, NFeConfig config, long idLote) {
        try {
            NFGeraChave geraChave = new NFGeraChave(nota);
            nota.getInfo().getIdentificacao().setDigitoVerificador(geraChave.getDV());
            nota.getInfo().setIdentificador(geraChave.getChaveAcesso());

            NFLoteEnvio lote = new NFLoteEnvio();
            lote.setIdLote(String.valueOf(idLote));
            lote.setVersao("4.00");
            lote.setIndicadorProcessamento(NFLoteIndicadorProcessamento.PROCESSAMENTO_SINCRONO);
            lote.setNotas(List.of(nota));

            String assinado = new DFAssinaturaDigital(config).assinarDocumento(lote.toString());
            NFLoteEnvio loteAssinado = config.getPersister().read(NFLoteEnvio.class, assinado);
            NFNota notaAssinada = loteAssinado.getNotas().get(0);

            NFGeraQRCode30 geraQrCode = nota.getInfo().getIdentificacao().getTipoEmissao() == NFTipoEmissao.CONTIGENCIA_OFFLINE
                    ? new NFGeraQRCodeContingenciaOffline30(notaAssinada, config)
                    : new NFGeraQRCodeEmissaoNormal30(notaAssinada, config);
            NFNotaInfoSuplementar suplementar = new NFNotaInfoSuplementar();
            String qrCode = geraQrCode.getQRCode();
            String urlConsulta = geraQrCode.urlConsultaChaveAcesso();
            suplementar.setQrCode(qrCode);
            suplementar.setUrlConsultaChaveAcesso(urlConsulta);
            notaAssinada.setInfoSuplementar(suplementar);

            String xmlLote = loteAssinado.toString();
            DFXMLValidador.validaLote400(xmlLote);
            return new DocumentoNfceAssinado(xmlLote, notaAssinada.getInfo().getChaveAcesso(), qrCode, urlConsulta, notaAssinada);
        } catch (NfceConfiguracaoException ex) {
            throw ex;
        } catch (SAXException ex) {
            throw new NfceConfiguracaoException("NFC-e fora do leiaute (XSD): " + ex.getMessage());
        } catch (Exception ex) {
            throw new NfceConfiguracaoException("Falha ao gerar/assinar a NFC-e: " + ex.getMessage());
        }
    }

    /**
     * @param xmlLote {@code enviNFe} assinado, exatamente como será transmitido (e guardado).
     * @param nota a NFC-e assinada, para montar o {@code nfeProc} depois da autorização.
     */
    public record DocumentoNfceAssinado(String xmlLote, String chaveAcesso, String qrCode, String urlConsulta, NFNota nota) {
    }
}
