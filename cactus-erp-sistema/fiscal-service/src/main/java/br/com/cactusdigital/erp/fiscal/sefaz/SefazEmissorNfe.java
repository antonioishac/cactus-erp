package br.com.cactusdigital.erp.fiscal.sefaz;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.fiscal.client.dto.ClienteFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaConfiguracaoFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronica;
import br.com.cactusdigital.erp.fiscal.exception.FiscalErrorCode;
import com.fincatto.documentofiscal.DFModelo;
import com.fincatto.documentofiscal.nfe.NFeConfig;
import com.fincatto.documentofiscal.nfe400.classes.NFProtocolo;
import com.fincatto.documentofiscal.nfe400.classes.NFProtocoloInfo;
import com.fincatto.documentofiscal.nfe400.classes.lote.envio.NFLoteEnvio;
import com.fincatto.documentofiscal.nfe400.classes.lote.envio.NFLoteEnvioRetorno;
import com.fincatto.documentofiscal.nfe400.classes.lote.envio.NFLoteIndicadorProcessamento;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNota;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaProcessada;
import com.fincatto.documentofiscal.nfe400.classes.nota.consulta.NFNotaConsultaRetorno;
import com.fincatto.documentofiscal.nfe400.webservices.WSFacade;
import com.fincatto.documentofiscal.validadores.DFXMLValidador;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

/**
 * Emissão de NF-e direto no autorizador da SEFAZ, pela biblioteca {@code com.github.wmixvideo:nfe} (sem ACBr Monitor).
 *
 * <p>Fluxo: monta ({@link NfeDocumentoBuilder}) → assina (chave/DV calculados pela biblioteca) → valida no XSD
 * <b>antes</b> de qualquer rede (erro de montagem é 422, nada enviado, número intacto) → transmite em modo síncrono
 * (um documento por lote, o protocolo volta na mesma resposta) → interpreta o cStat.</p>
 *
 * <p>Duplicidade (cStat 204): acontece quando uma transmissão anterior da mesma nota foi autorizada mas a resposta se
 * perdeu. Como a chave é determinística ({@link NfeDocumentoBuilder#codigoNumerico}) e a nota em {@code ERRO} mantém a
 * data de emissão, a retransmissão produz o mesmo documento: a NF-e é consultada pela chave e, se autorizada, o
 * {@code nfeProc} é montado com o protocolo original.</p>
 */
@Component
class SefazEmissorNfe implements EmissorNfe {

    private static final Logger log = LoggerFactory.getLogger(SefazEmissorNfe.class);
    private static final String VERSAO = "4.00";
    private static final String LOTE_PROCESSADO = "104";
    private static final String DUPLICIDADE = "204";
    private static final Set<String> AUTORIZADO = Set.of("100", "150");
    private static final Set<String> DENEGADO = Set.of("110", "301", "302", "303");

    private final NfeConfigFactory configFactory;
    private final NfeDocumentoBuilder documentoBuilder;

    SefazEmissorNfe(NfeConfigFactory configFactory, NfeDocumentoBuilder documentoBuilder) {
        this.configFactory = configFactory;
        this.documentoBuilder = documentoBuilder;
    }

    @Override
    public RetornoSefazNfe autorizar(NotaFiscalEletronica nota, EmpresaFiscalDTO emitente, ClienteFiscalDTO destinatario,
                                     ClienteFiscalDTO transportadora, EmpresaConfiguracaoFiscalDTO configuracao) {
        NFeConfig config = configFactory.criar(nota.getAmbiente(), emitente, configuracao);
        NFNota documento = montar(nota, emitente, destinatario, transportadora);

        try (WSFacade facade = new WSFacade(config)) {
            NFLoteEnvio loteAssinado = assinarEValidar(facade, nota, documento);
            NFNota notaAssinada = loteAssinado.getNotas().get(0);
            String chave = notaAssinada.getInfo().getChaveAcesso();

            NFLoteEnvioRetorno retorno = transmitir(facade, loteAssinado, chave);
            if (!LOTE_PROCESSADO.equals(retorno.getStatus())) {
                return rejeitada(chave, retorno.getStatus(), retorno.getMotivo());
            }
            NFProtocoloInfo protocolo = retorno.getProtocoloInfo();
            if (DUPLICIDADE.equals(protocolo.getStatus())) {
                return reconciliarDuplicidade(facade, notaAssinada, chave, protocolo);
            }
            return interpretar(notaAssinada, chave, protocolo);
        } catch (BusinessException | FalhaComunicacaoSefazException ex) {
            throw ex;
        } catch (Exception ex) {
            // Construtor/close do WSFacade (contexto TLS do certificado/cadeia).
            throw new BusinessException(FiscalErrorCode.CERTIFICADO_INVALIDO, "Não foi possível preparar a conexão com a SEFAZ: " + ex.getMessage());
        }
    }

    private NFNota montar(NotaFiscalEletronica nota, EmpresaFiscalDTO emitente, ClienteFiscalDTO destinatario,
                          ClienteFiscalDTO transportadora) {
        try {
            return documentoBuilder.construir(nota, emitente, destinatario, transportadora);
        } catch (BusinessException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            // Os setters da biblioteca validam tamanho/formato de cada campo do leiaute.
            throw new BusinessException(FiscalErrorCode.DOCUMENTO_FISCAL_INVALIDO, ex.getMessage());
        }
    }

    private NFLoteEnvio assinarEValidar(WSFacade facade, NotaFiscalEletronica nota, NFNota documento) {
        NFLoteEnvio lote = new NFLoteEnvio();
        lote.setIdLote(String.valueOf(nota.getId()));
        lote.setVersao(VERSAO);
        lote.setIndicadorProcessamento(NFLoteIndicadorProcessamento.PROCESSAMENTO_SINCRONO);
        lote.setNotas(List.of(documento));
        try {
            NFLoteEnvio assinado = facade.getLoteAssinado(lote);
            DFXMLValidador.validaLote400(assinado.toString());
            return assinado;
        } catch (SAXException ex) {
            throw new BusinessException(FiscalErrorCode.DOCUMENTO_FISCAL_INVALIDO, "XSD: " + ex.getMessage());
        } catch (Exception ex) {
            throw new BusinessException(FiscalErrorCode.DOCUMENTO_FISCAL_INVALIDO, "Falha ao gerar/assinar o XML: " + ex.getMessage());
        }
    }

    private NFLoteEnvioRetorno transmitir(WSFacade facade, NFLoteEnvio loteAssinado, String chave) {
        try {
            return facade.enviaLoteAssinado(loteAssinado.toString(), DFModelo.NFE);
        } catch (Exception ex) {
            log.warn("NF-e {}: transmissão sem resposta utilizável", chave, ex);
            throw new FalhaComunicacaoSefazException(chave, ex);
        }
    }

    private RetornoSefazNfe reconciliarDuplicidade(WSFacade facade, NFNota notaAssinada, String chave, NFProtocoloInfo duplicidade) {
        NFNotaConsultaRetorno consulta;
        try {
            consulta = facade.consultaNota(chave);
        } catch (Exception ex) {
            throw new FalhaComunicacaoSefazException(chave, ex);
        }
        if (consulta.getProtocolo() == null || consulta.getProtocolo().getProtocoloInfo() == null) {
            return rejeitada(chave, duplicidade.getStatus(), duplicidade.getMotivo() + " | consulta: " + consulta.getStatus() + " " + consulta.getMotivo());
        }
        log.info("NF-e {}: duplicidade reconciliada pela consulta (cStat {})", chave, consulta.getProtocolo().getProtocoloInfo().getStatus());
        return interpretar(notaAssinada, chave, consulta.getProtocolo().getProtocoloInfo());
    }

    private RetornoSefazNfe interpretar(NFNota notaAssinada, String chave, NFProtocoloInfo protocolo) {
        String status = protocolo.getStatus();
        if (AUTORIZADO.contains(status)) {
            return new RetornoSefazNfe(RetornoSefazNfe.Resultado.AUTORIZADA, chave, protocolo.getNumeroProtocolo(), status,
                    protocolo.getMotivo(), nfeProc(notaAssinada, protocolo));
        }
        if (DENEGADO.contains(status)) {
            return new RetornoSefazNfe(RetornoSefazNfe.Resultado.DENEGADA, chave, protocolo.getNumeroProtocolo(), status,
                    protocolo.getMotivo(), nfeProc(notaAssinada, protocolo));
        }
        return rejeitada(chave, status, protocolo.getMotivo());
    }

    private static RetornoSefazNfe rejeitada(String chave, String status, String motivo) {
        return new RetornoSefazNfe(RetornoSefazNfe.Resultado.REJEITADA, chave, null, status, motivo, null);
    }

    private static String nfeProc(NFNota notaAssinada, NFProtocoloInfo protocoloInfo) {
        NFProtocolo protocolo = new NFProtocolo();
        protocolo.setVersao(VERSAO);
        protocolo.setProtocoloInfo(protocoloInfo);
        NFNotaProcessada processada = new NFNotaProcessada();
        processada.setVersao(new BigDecimal(VERSAO));
        processada.setNota(notaAssinada);
        processada.setProtocolo(protocolo);
        return processada.toString();
    }
}
