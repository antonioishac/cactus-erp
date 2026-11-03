package br.com.cactusdigital.erp.fiscal.sefaz;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaConfiguracaoFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronica;
import br.com.cactusdigital.erp.fiscal.exception.FiscalErrorCode;
import com.fincatto.documentofiscal.DFConfig;
import com.fincatto.documentofiscal.DFModelo;
import com.fincatto.documentofiscal.nfe.NFeConfig;
import com.fincatto.documentofiscal.nfe400.classes.evento.NFEnviaEventoRetorno;
import com.fincatto.documentofiscal.nfe400.classes.evento.NFEventoRetorno;
import com.fincatto.documentofiscal.nfe400.classes.evento.NFInfoEventoRetorno;
import com.fincatto.documentofiscal.nfe400.classes.evento.inutilizacao.NFRetornoEventoInutilizacao;
import com.fincatto.documentofiscal.nfe400.classes.evento.inutilizacao.NFRetornoEventoInutilizacaoDados;
import com.fincatto.documentofiscal.nfe400.classes.lote.envio.NFCancelamentoRetornoDados;
import com.fincatto.documentofiscal.nfe400.webservices.WSFacade;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Cancelamento (evento 110111) e inutilização de NF-e direto na SEFAZ, pela {@code wmixvideo/nfe}. A biblioteca monta,
 * assina e envia; aqui só se interpreta o cStat.
 *
 * <p>Prazo de cancelamento: <b>não</b> é verificado localmente. A regra geral é 24h da autorização, mas há UFs que
 * aceitam cancelamento extemporâneo (cStat 155); quem decide é a SEFAZ, e a rejeição (501) volta com o motivo.</p>
 */
@Component
class SefazEventosNfe implements EventosNfe {

    private static final Logger log = LoggerFactory.getLogger(SefazEventosNfe.class);
    private static final int LOTE_EVENTO_PROCESSADO = 128;
    /** 135 = evento registrado e vinculado; 155 = cancelamento homologado fora de prazo. */
    private static final Set<Integer> CANCELAMENTO_HOMOLOGADO = Set.of(135, 155);
    private static final String INUTILIZACAO_HOMOLOGADA = "102";

    private final NfeConfigFactory configFactory;

    SefazEventosNfe(NfeConfigFactory configFactory) {
        this.configFactory = configFactory;
    }

    @Override
    public RetornoEventoSefaz cancelar(NotaFiscalEletronica nota, EmpresaFiscalDTO emitente, EmpresaConfiguracaoFiscalDTO configuracao,
                                       String justificativa) {
        NFeConfig config = configFactory.criar(nota.getAmbiente(), emitente, configuracao);
        NFCancelamentoRetornoDados dados = executar(config, nota.getChaveAcesso(),
                facade -> facade.cancelaNota(nota.getChaveAcesso(), nota.getProtocoloAutorizacao(), justificativa));

        NFEnviaEventoRetorno lote = dados.getRetorno();
        if (lote.getCodigoStatusReposta() == null || lote.getCodigoStatusReposta() != LOTE_EVENTO_PROCESSADO) {
            return new RetornoEventoSefaz(false, String.valueOf(lote.getCodigoStatusReposta()), lote.getMotivo(), null, null, null);
        }
        List<NFEventoRetorno> eventos = lote.getEventoRetorno();
        NFInfoEventoRetorno evento = eventos == null || eventos.isEmpty() ? null : eventos.get(0).getInfoEventoRetorno();
        if (evento == null) {
            return new RetornoEventoSefaz(false, String.valueOf(lote.getCodigoStatusReposta()), "Lote processado sem retorno do evento", null, null, null);
        }
        boolean homologado = CANCELAMENTO_HOMOLOGADO.contains(evento.getCodigoStatus());
        return new RetornoEventoSefaz(homologado, String.valueOf(evento.getCodigoStatus()), evento.getMotivo(),
                evento.getNumeroProtocolo(), instante(evento.getDataHoraRegistro()),
                homologado ? dados.getProtocolo().toString() : null);
    }

    @Override
    public RetornoEventoSefaz inutilizar(AmbienteFiscal ambiente, EmpresaFiscalDTO emitente, EmpresaConfiguracaoFiscalDTO configuracao,
                                         String serie, long numeroInicial, long numeroFinal, int ano, String justificativa) {
        NFeConfig config = configFactory.criar(ambiente, emitente, configuracao);
        String cnpj = emitente.cnpj() == null ? "" : emitente.cnpj().replaceAll("\\D", "");
        NFRetornoEventoInutilizacao retorno = executar(config, "inutilização " + serie + "/" + numeroInicial + "-" + numeroFinal,
                facade -> facade.inutilizaNota(ano, cnpj, serie, String.valueOf(numeroInicial), String.valueOf(numeroFinal),
                        justificativa, DFModelo.NFE));
        NFRetornoEventoInutilizacaoDados dados = retorno.getDados();
        boolean homologado = INUTILIZACAO_HOMOLOGADA.equals(dados.getStatus());
        return new RetornoEventoSefaz(homologado, dados.getStatus(), dados.getMotivo(), dados.getNumeroProtocolo(),
                instante(dados.getDatahoraRecebimento()), retorno.toString());
    }

    private <T> T executar(NFeConfig config, String referencia, Chamada<T> chamada) {
        WSFacade facade;
        try {
            facade = new WSFacade(config);
        } catch (Exception ex) {
            throw new BusinessException(FiscalErrorCode.CERTIFICADO_INVALIDO, "Não foi possível preparar a conexão com a SEFAZ: " + ex.getMessage());
        }
        try (facade) {
            return chamada.executar(facade);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            // Validadores de leiaute da biblioteca (justificativa, chave, protocolo): nada foi enviado.
            throw new BusinessException(FiscalErrorCode.DOCUMENTO_FISCAL_INVALIDO, ex.getMessage());
        } catch (Exception ex) {
            log.warn("SEFAZ sem resposta utilizável ({})", referencia, ex);
            throw new FalhaComunicacaoSefazException(null, ex);
        }
    }

    private static Instant instante(ZonedDateTime dataHora) {
        return dataHora == null ? Instant.now() : dataHora.toInstant();
    }

    private static Instant instante(LocalDateTime dataHora) {
        return dataHora == null ? Instant.now() : dataHora.atZone(DFConfig.getTimezoneSP().toZoneId()).toInstant();
    }

    @FunctionalInterface
    private interface Chamada<T> {
        T executar(WSFacade facade) throws Exception;
    }
}
