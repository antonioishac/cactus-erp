package br.com.cactuserp.pdv.service.fiscal;

import com.fincatto.documentofiscal.DFModelo;
import com.fincatto.documentofiscal.nfe.NFeConfig;
import com.fincatto.documentofiscal.nfe400.classes.NFProtocolo;
import com.fincatto.documentofiscal.nfe400.classes.NFProtocoloInfo;
import com.fincatto.documentofiscal.nfe400.classes.evento.NFEnviaEventoRetorno;
import com.fincatto.documentofiscal.nfe400.classes.evento.NFEventoRetorno;
import com.fincatto.documentofiscal.nfe400.classes.evento.NFInfoEventoRetorno;
import com.fincatto.documentofiscal.nfe400.classes.evento.inutilizacao.NFRetornoEventoInutilizacao;
import com.fincatto.documentofiscal.nfe400.classes.evento.inutilizacao.NFRetornoEventoInutilizacaoDados;
import com.fincatto.documentofiscal.nfe400.classes.lote.envio.NFCancelamentoRetornoDados;
import com.fincatto.documentofiscal.nfe400.classes.lote.envio.NFLoteEnvioRetorno;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNota;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaProcessada;
import com.fincatto.documentofiscal.nfe400.classes.nota.consulta.NFNotaConsultaRetorno;
import com.fincatto.documentofiscal.nfe400.classes.statusservico.consulta.NFStatusServicoConsultaRetorno;
import com.fincatto.documentofiscal.nfe400.webservices.WSFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * {@link TransmissorNfce} real, pela {@code wmixvideo/nfe}. Só traduz chamadas e cStat; nenhuma regra de negócio.
 */
@Component
public class SefazTransmissorNfce implements TransmissorNfce {

    private static final Logger LOGGER = LoggerFactory.getLogger(SefazTransmissorNfce.class);
    private static final String VERSAO = "4.00";
    private static final String LOTE_PROCESSADO = "104";
    private static final Set<String> AUTORIZADA = Set.of("100", "150");
    private static final Set<String> DENEGADA = Set.of("110", "301", "302", "303");
    private static final Set<String> DUPLICIDADE = Set.of("204", "539");
    private static final Set<String> CANCELADA = Set.of("101", "151", "155");
    private static final int LOTE_EVENTO_PROCESSADO = 128;
    private static final Set<Integer> EVENTO_REGISTRADO = Set.of(135, 155);

    @Override
    public RetornoAutorizacao autorizar(String xmlLote, NFNota notaAssinada, NFeConfig config) {
        NFLoteEnvioRetorno retorno = executar(config, facade -> facade.enviaLoteAssinado(xmlLote, DFModelo.NFCE));
        if (!LOTE_PROCESSADO.equals(retorno.getStatus())) {
            String status = retorno.getStatus();
            return new RetornoAutorizacao(DUPLICIDADE.contains(status) ? Resultado.DUPLICIDADE : Resultado.REJEITADA,
                    status, retorno.getMotivo(), null, null, null);
        }
        NFProtocoloInfo protocolo = retorno.getProtocoloInfo();
        String status = protocolo.getStatus();
        if (AUTORIZADA.contains(status) || DENEGADA.contains(status)) {
            return new RetornoAutorizacao(AUTORIZADA.contains(status) ? Resultado.AUTORIZADA : Resultado.DENEGADA, status,
                    protocolo.getMotivo(), protocolo.getNumeroProtocolo(), protocolo.getVersaoAplicacao(), nfeProc(notaAssinada, protocolo));
        }
        return new RetornoAutorizacao(DUPLICIDADE.contains(status) ? Resultado.DUPLICIDADE : Resultado.REJEITADA,
                status, protocolo.getMotivo(), null, null, null);
    }

    @Override
    public RetornoConsulta consultar(String chaveAcesso, NFNota notaAssinada, NFeConfig config) {
        NFNotaConsultaRetorno retorno = executar(config, facade -> facade.consultaNota(chaveAcesso));
        NFProtocoloInfo protocolo = retorno.getProtocolo() != null ? retorno.getProtocolo().getProtocoloInfo() : null;
        String status = retorno.getStatus();
        SituacaoConsulta situacao;
        if (AUTORIZADA.contains(status)) {
            situacao = SituacaoConsulta.AUTORIZADA;
        } else if (DENEGADA.contains(status)) {
            situacao = SituacaoConsulta.DENEGADA;
        } else if (CANCELADA.contains(status)) {
            situacao = SituacaoConsulta.CANCELADA;
        } else if ("217".equals(status)) {
            situacao = SituacaoConsulta.NAO_ENCONTRADA;
        } else {
            situacao = SituacaoConsulta.OUTRA;
        }
        String xml = (situacao == SituacaoConsulta.AUTORIZADA || situacao == SituacaoConsulta.DENEGADA) && notaAssinada != null && protocolo != null
                ? nfeProc(notaAssinada, protocolo) : null;
        return new RetornoConsulta(situacao, status, retorno.getMotivo(),
                protocolo != null ? protocolo.getNumeroProtocolo() : null,
                protocolo != null ? protocolo.getVersaoAplicacao() : null, xml);
    }

    @Override
    public boolean servicoEmOperacao(NFeConfig config) {
        NFStatusServicoConsultaRetorno retorno = executar(config, facade -> facade.consultaStatus(config.getCUF(), DFModelo.NFCE));
        return "107".equals(retorno.getStatus());
    }

    @Override
    public RetornoEvento cancelar(String chaveAcesso, String protocoloAutorizacao, String justificativa, NFeConfig config) {
        NFCancelamentoRetornoDados dados = executar(config, facade -> facade.cancelaNota(chaveAcesso, protocoloAutorizacao, justificativa));
        RetornoEvento retorno = interpretar(dados.getRetorno(), null);
        return retorno.homologado()
                ? new RetornoEvento(true, retorno.codigoStatus(), retorno.motivo(), retorno.protocolo(), dados.getProtocolo().toString())
                : retorno;
    }

    @Override
    public RetornoEvento cancelarPorSubstituicao(String chaveAcesso, String protocoloAutorizacao, String justificativa,
                                                 String versaoAplicativoSubstituta, String chaveSubstituta, NFeConfig config) {
        NFEnviaEventoRetorno retorno = executar(config, facade -> facade.cancelaNotaPorSubstituicao(chaveAcesso, protocoloAutorizacao,
                justificativa, versaoAplicativoSubstituta, chaveSubstituta));
        return interpretar(retorno, retorno.toString());
    }

    @Override
    public RetornoEvento inutilizar(int ano, String cnpj, int serie, long numero, String justificativa, NFeConfig config) {
        NFRetornoEventoInutilizacao retorno = executar(config, facade -> facade.inutilizaNota(ano, cnpj, String.valueOf(serie),
                String.valueOf(numero), String.valueOf(numero), justificativa, DFModelo.NFCE));
        NFRetornoEventoInutilizacaoDados dados = retorno.getDados();
        return new RetornoEvento("102".equals(dados.getStatus()), dados.getStatus(), dados.getMotivo(), dados.getNumeroProtocolo(), retorno.toString());
    }

    private static RetornoEvento interpretar(NFEnviaEventoRetorno lote, String xml) {
        Integer statusLote = lote.getCodigoStatusReposta();
        if (statusLote == null || statusLote != LOTE_EVENTO_PROCESSADO) {
            return new RetornoEvento(false, String.valueOf(statusLote), lote.getMotivo(), null, null);
        }
        List<NFEventoRetorno> eventos = lote.getEventoRetorno();
        NFInfoEventoRetorno evento = eventos == null || eventos.isEmpty() ? null : eventos.get(0).getInfoEventoRetorno();
        if (evento == null) {
            return new RetornoEvento(false, String.valueOf(statusLote), "Lote processado sem retorno do evento", null, null);
        }
        boolean registrado = EVENTO_REGISTRADO.contains(evento.getCodigoStatus());
        return new RetornoEvento(registrado, String.valueOf(evento.getCodigoStatus()), evento.getMotivo(), evento.getNumeroProtocolo(),
                registrado ? xml : null);
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

    private static <T> T executar(NFeConfig config, Chamada<T> chamada) {
        WSFacade facade;
        try {
            facade = new WSFacade(config);
        } catch (Exception ex) {
            throw new NfceConfiguracaoException("Não foi possível preparar a conexão com a SEFAZ (certificado/cadeia): " + ex.getMessage());
        }
        try (facade) {
            return chamada.executar(facade);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            // Validadores de leiaute da biblioteca: nada foi enviado.
            throw new NfceConfiguracaoException(ex.getMessage());
        } catch (Exception ex) {
            LOGGER.warn("SEFAZ sem resposta utilizável: {}", ex.getMessage());
            throw new FalhaComunicacaoNfceException(ex);
        }
    }

    @FunctionalInterface
    private interface Chamada<T> {
        T executar(WSFacade facade) throws Exception;
    }
}
