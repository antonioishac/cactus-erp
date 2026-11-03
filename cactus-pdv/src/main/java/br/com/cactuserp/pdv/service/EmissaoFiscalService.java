package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.LojaConfiguracao;
import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvConfiguracao;
import br.com.cactuserp.pdv.entity.PdvNfceNumeroPendente;
import br.com.cactuserp.pdv.entity.PdvNfceXml;
import br.com.cactuserp.pdv.entity.PdvTotalTipoPagamento;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.PdvVendaDetalhe;
import br.com.cactuserp.pdv.entity.ProdutoUnidade;
import br.com.cactuserp.pdv.entity.SimNao;
import br.com.cactuserp.pdv.entity.StatusFiscal;
import br.com.cactuserp.pdv.repository.LojaConfiguracaoRepository;
import br.com.cactuserp.pdv.repository.PdvConfiguracaoRepository;
import br.com.cactuserp.pdv.repository.PdvNfceNumeroPendenteRepository;
import br.com.cactuserp.pdv.repository.PdvNfceXmlRepository;
import br.com.cactuserp.pdv.repository.PdvTotalTipoPagamentoRepository;
import br.com.cactuserp.pdv.repository.PdvVendaCabecalhoRepository;
import br.com.cactuserp.pdv.repository.PdvVendaDetalheRepository;
import br.com.cactuserp.pdv.repository.ProdutoUnidadeRepository;
import br.com.cactuserp.pdv.service.dto.ResultadoEmissaoFiscal;
import br.com.cactuserp.pdv.service.fiscal.AssinadorNfce;
import br.com.cactuserp.pdv.service.fiscal.ContingenciaNfceService;
import br.com.cactuserp.pdv.service.fiscal.FalhaComunicacaoNfceException;
import br.com.cactuserp.pdv.service.fiscal.FiscalPdvProperties;
import br.com.cactuserp.pdv.service.fiscal.NfceConfigFactory;
import br.com.cactuserp.pdv.service.fiscal.NfceConfiguracaoException;
import br.com.cactuserp.pdv.service.fiscal.NfceDocumentoBuilder;
import br.com.cactuserp.pdv.service.fiscal.NumeracaoNfceService;
import br.com.cactuserp.pdv.service.fiscal.TransmissorNfce;
import com.fincatto.documentofiscal.nfe.NFTipoEmissao;
import com.fincatto.documentofiscal.nfe.NFeConfig;
import com.fincatto.documentofiscal.nfe400.classes.lote.envio.NFLoteEnvio;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNota;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Emissão da NFC-e de uma venda finalizada direto na SEFAZ (sem ACBr Monitor), com contingência offline no padrão
 * dos PDVs de mercado. <b>Nunca desfaz a venda</b>: o pagamento já foi recebido; o que não der certo fica registrado
 * no cupom ({@link StatusFiscal}) e o {@code ReprocessamentoFiscalService} continua o trabalho.
 *
 * <h3>Fluxo de {@link #emitir}</h3>
 * <ol>
 *   <li>Terminal sem NFC-e habilitada: recibo ({@code N}).</li>
 *   <li>Reserva o número na série do terminal ({@link NumeracaoNfceService}, transação própria: o cupom nunca perde o
 *       número reservado, mesmo se o processo cair).</li>
 *   <li>Terminal em contingência ({@link ContingenciaNfceService}): emite direto offline, sem esperar a SEFAZ.</li>
 *   <li>Senão: monta, assina (QR Code v3), valida no XSD e transmite (síncrono, timeout curto).
 *       Autorizada → {@code E}; denegada → {@code D}; rejeitada → {@code R}; duplicidade → consulta a chave.</li>
 *   <li>Sem resposta: entra em contingência e emite offline ({@code O}). Se a conexão nem abriu, com o MESMO número;
 *       se a NFC-e pode ter chegado (timeout), com OUTRO número — e a tentativa vai para
 *       {@code PDV_NFCE_NUMERO_PENDENTE} para ser cancelada por substituição (se autorizada) ou inutilizada.</li>
 *   <li>Erro de configuração/cadastro: {@code P}, com a mensagem para correção.</li>
 * </ol>
 */
@Service
public class EmissaoFiscalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmissaoFiscalService.class);
    private static final ZoneId FUSO_BRASILIA = ZoneId.of("America/Sao_Paulo");
    private static final String TIPO_NORMAL = "1";
    private static final String TIPO_OFFLINE = "9";
    private static final String TIPO_OPERACAO_NFCE = "NFC";

    private final PdvConfiguracaoRepository configuracaoRepository;
    private final LojaConfiguracaoRepository lojaConfiguracaoRepository;
    private final PdvVendaDetalheRepository detalheRepository;
    private final PdvTotalTipoPagamentoRepository pagamentoRepository;
    private final PdvVendaCabecalhoRepository cabecalhoRepository;
    private final ProdutoUnidadeRepository unidadeRepository;
    private final PdvNfceXmlRepository xmlRepository;
    private final PdvNfceNumeroPendenteRepository numeroPendenteRepository;
    private final NumeracaoNfceService numeracaoService;
    private final ContingenciaNfceService contingencia;
    private final NfceDocumentoBuilder documentoBuilder;
    private final AssinadorNfce assinador;
    private final NfceConfigFactory configFactory;
    private final TransmissorNfce transmissor;
    private final FiscalPdvProperties properties;
    private final TransactionTemplate novaTransacao;

    public EmissaoFiscalService(PdvConfiguracaoRepository configuracaoRepository,
                                LojaConfiguracaoRepository lojaConfiguracaoRepository,
                                PdvVendaDetalheRepository detalheRepository,
                                PdvTotalTipoPagamentoRepository pagamentoRepository,
                                PdvVendaCabecalhoRepository cabecalhoRepository,
                                ProdutoUnidadeRepository unidadeRepository,
                                PdvNfceXmlRepository xmlRepository,
                                PdvNfceNumeroPendenteRepository numeroPendenteRepository,
                                NumeracaoNfceService numeracaoService,
                                ContingenciaNfceService contingencia,
                                NfceDocumentoBuilder documentoBuilder,
                                AssinadorNfce assinador,
                                NfceConfigFactory configFactory,
                                TransmissorNfce transmissor,
                                FiscalPdvProperties properties,
                                PlatformTransactionManager transactionManager) {
        this.configuracaoRepository = configuracaoRepository;
        this.lojaConfiguracaoRepository = lojaConfiguracaoRepository;
        this.detalheRepository = detalheRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.cabecalhoRepository = cabecalhoRepository;
        this.unidadeRepository = unidadeRepository;
        this.xmlRepository = xmlRepository;
        this.numeroPendenteRepository = numeroPendenteRepository;
        this.numeracaoService = numeracaoService;
        this.contingencia = contingencia;
        this.documentoBuilder = documentoBuilder;
        this.assinador = assinador;
        this.configFactory = configFactory;
        this.transmissor = transmissor;
        this.properties = properties;
        this.novaTransacao = new TransactionTemplate(transactionManager);
        this.novaTransacao.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    /**
     * Emite a NFC-e de um cupom finalizado. Nunca lança exceção: qualquer falha fica registrada no cupom.
     *
     * <p><b>Sem transação externa de propósito</b>: cada passo de banco roda numa transação curta e própria (sempre
     * relendo o cupom) e as chamadas à SEFAZ ficam fora de transação. Assim a reserva do número ({@code REQUIRES_NEW},
     * com lock no terminal) nunca disputa a linha do cupom com uma transação aberta, e nenhuma conexão de banco fica
     * presa durante o timeout da SEFAZ.</p>
     */
    public ResultadoEmissaoFiscal emitir(PdvCaixa caixa, PdvVendaCabecalho cupomFinalizado) {
        if (caixa == null || cupomFinalizado == null) {
            throw new IllegalArgumentException("Caixa e cupom são obrigatórios.");
        }
        Long cupomId = cupomFinalizado.getId();
        try {
            Preparo preparo = emTransacao(() -> preparar(caixa, cupomId));
            if (preparo.resultadoFinal() != null) {
                return preparo.resultadoFinal();
            }
            LojaConfiguracao loja = preparo.loja();
            boolean producao = preparo.producao();
            if (preparo.semNumero()) {
                reservarNumero(caixa, cupomId, TIPO_NORMAL);
            }
            if (contingencia.ativa() || preparo.jaOffline()) {
                return emTransacao(() -> emitirOffline(cupomId, loja, producao, motivoContingencia()));
            }

            AssinadorNfce.DocumentoNfceAssinado documento =
                    emTransacao(() -> montarEAssinar(cupomId, loja, producao, NFTipoEmissao.EMISSAO_NORMAL, null, null));
            TransmissorNfce.RetornoAutorizacao retorno;
            try {
                NFeConfig config = configFactory.paraTransmissao(loja, producao, NFTipoEmissao.EMISSAO_NORMAL);
                retorno = transmissor.autorizar(documento.xmlLote(), documento.nota(), config);
                if (retorno.resultado() == TransmissorNfce.Resultado.DUPLICIDADE) {
                    retorno = reconciliarDuplicidade(documento.chaveAcesso(), documento.nota(), config, retorno);
                }
            } catch (FalhaComunicacaoNfceException semResposta) {
                contingencia.entrar(semResposta.getMessage());
                if (semResposta.isPodeTerSidoRecebida()) {
                    emTransacao(() -> registrarNumeroPendente(caixa.getId(), cupomId, producao, documento.chaveAcesso(),
                            "Transmitida sem resposta da SEFAZ; substituída por NFC-e em contingência offline com outro número"));
                    reservarNumero(caixa, cupomId, TIPO_OFFLINE);
                }
                String motivo = "Falha de comunicacao com a SEFAZ: " + semResposta.getMessage();
                return emTransacao(() -> emitirOffline(cupomId, loja, producao, motivo));
            }
            TransmissorNfce.RetornoAutorizacao efetivo = retorno;
            return emTransacao(() -> aplicarAutorizacao(cupomId, efetivo));
        } catch (NfceConfiguracaoException ex) {
            return emTransacao(() -> pendente(cupomId, ex.getMessage()));
        } catch (RuntimeException ex) {
            LOGGER.error("Falha inesperada na emissão da NFC-e do cupom #{}", cupomId, ex);
            return emTransacao(() -> pendente(cupomId, "Falha inesperada na emissão da NFC-e: " + ex.getMessage()));
        }
    }

    /**
     * Transmite uma NFC-e emitida em contingência offline, exatamente como foi assinada e impressa. Falha de
     * comunicação mantém {@code O} (o job tenta de novo); retorno fiscal é aplicado como na emissão normal.
     *
     * @return {@code true} se saiu de {@code O}
     */
    @Transactional
    public boolean transmitirContingencia(Long cupomId) {
        PdvVendaCabecalho cupom = cabecalhoRepository.findById(cupomId).orElse(null);
        if (cupom == null || !StatusFiscal.CONTINGENCIA_OFFLINE.equals(cupom.getStatusFiscal())) {
            return false;
        }
        PdvNfceXml xml = xmlRepository.findById(cupomId).orElse(null);
        if (xml == null) {
            LOGGER.error("Cupom #{} em contingência offline sem o XML assinado guardado — exige ação manual.", cupomId);
            return false;
        }
        LojaConfiguracao loja = lojaConfiguracaoRepository.buscarConfiguracao().orElse(null);
        boolean producao = cupom.getChaveAcessoFiscal() != null && ambienteDaChave(xml).equals("1");
        try {
            NFeConfig config = configFactory.paraTransmissao(loja, producao, NFTipoEmissao.CONTIGENCIA_OFFLINE);
            NFNota nota = config.getPersister().read(NFLoteEnvio.class, xml.getXmlLoteAssinado()).getNotas().get(0);
            TransmissorNfce.RetornoAutorizacao retorno = transmissor.autorizar(xml.getXmlLoteAssinado(), nota, config);
            if (retorno.resultado() == TransmissorNfce.Resultado.DUPLICIDADE) {
                retorno = reconciliarDuplicidade(cupom.getChaveAcessoFiscal(), nota, config, retorno);
            }
            aplicarAutorizacao(cupom.getId(), retorno);
            return true;
        } catch (FalhaComunicacaoNfceException ex) {
            contingencia.entrar(ex.getMessage());
            LOGGER.info("NFC-e offline do cupom #{} ainda não transmitida: {}", cupomId, ex.getMessage());
            return false;
        } catch (NfceConfiguracaoException ex) {
            cupom.setMensagemFiscal(truncar("Contingência offline aguardando transmissão: " + ex.getMessage()));
            cabecalhoRepository.save(cupom);
            return false;
        } catch (Exception ex) {
            LOGGER.error("Falha ao transmitir a NFC-e offline do cupom #{}", cupomId, ex);
            return false;
        }
    }

    /**
     * Cancela na SEFAZ (evento 110111) a NFC-e de um cupom. NFC-e em contingência ainda não transmitida é transmitida
     * antes (só se cancela documento autorizado). <b>Lança exceção</b> em qualquer falha: sem a confirmação da SEFAZ a
     * venda não pode ser cancelada localmente (ficaria cancelada no PDV e válida no fisco).
     *
     * @return protocolo do evento de cancelamento
     */
    @Transactional
    public String cancelarNfce(PdvCaixa caixa, PdvVendaCabecalho cupomRef, String justificativa) {
        if (StatusFiscal.CONTINGENCIA_OFFLINE.equals(cupomRef.getStatusFiscal()) && !transmitirContingencia(cupomRef.getId())) {
            throw new IllegalStateException("NFC-e emitida em contingência offline ainda não foi transmitida à SEFAZ — "
                    + "o cancelamento é possível assim que ela for autorizada (verifique a conexão).");
        }
        PdvVendaCabecalho cupom = cabecalhoRepository.findById(cupomRef.getId())
                .orElseThrow(() -> new IllegalStateException("Cupom #" + cupomRef.getId() + " não encontrado."));
        if (!StatusFiscal.AUTORIZADA.equals(cupom.getStatusFiscal()) || cupom.getChaveAcessoFiscal() == null) {
            throw new IllegalStateException("Só NFC-e autorizada pode ser cancelada (situação fiscal atual: " + cupom.getStatusFiscal() + ").");
        }
        LojaConfiguracao loja = lojaConfiguracaoRepository.buscarConfiguracao()
                .orElseThrow(() -> new IllegalStateException("Configuração da loja (CNPJ do emitente) não encontrada."));
        if (loja.getCnpj() == null || loja.getCnpj().isBlank()) {
            throw new IllegalStateException("CNPJ do emitente não configurado na loja — a SEFAZ exige no cancelamento da NFC-e.");
        }
        PdvConfiguracao terminal = caixa == null ? null : configuracaoRepository.findFirstByCaixaId(caixa.getId()).orElse(null);
        boolean producao = terminal != null && terminal.nfceEmProducao();

        TransmissorNfce.RetornoEvento retorno;
        try {
            retorno = transmissor.cancelar(cupom.getChaveAcessoFiscal(), cupom.getProtocoloAutorizacao(), justificativa,
                    configFactory.paraTransmissao(loja, producao, NFTipoEmissao.EMISSAO_NORMAL));
        } catch (FalhaComunicacaoNfceException ex) {
            throw new IllegalStateException("Sem comunicação com a SEFAZ para cancelar a NFC-e: " + ex.getMessage(), ex);
        } catch (NfceConfiguracaoException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
        if (!retorno.homologado()) {
            throw new IllegalStateException("SEFAZ não homologou o cancelamento (CStat=" + retorno.codigoStatus() + "): " + retorno.motivo());
        }
        xmlRepository.findById(cupom.getId()).ifPresent(x -> {
            x.setXmlCancelamento(retorno.xml());
            xmlRepository.save(x);
        });
        cupom.setCodigoStatusSefaz(retorno.codigoStatus());
        cabecalhoRepository.save(cupom);
        return retorno.protocolo();
    }

    // ================================================================ internos

    /** Primeiro passo (transação própria): decide se há o que emitir e marca o cupom como NFC-e. */
    private Preparo preparar(PdvCaixa caixa, Long cupomId) {
        PdvVendaCabecalho cupom = cupom(cupomId);
        String status = cupom.getStatusFiscal();
        if (StatusFiscal.AUTORIZADA.equals(status) || StatusFiscal.DENEGADA.equals(status) || StatusFiscal.CONTINGENCIA_OFFLINE.equals(status)) {
            return Preparo.finalizado(resultadoAtual(cupom));
        }
        PdvConfiguracao terminal = configuracaoRepository.findFirstByCaixaId(caixa.getId()).orElse(null);
        if (terminal == null || !terminal.emiteNfce()) {
            cupom.setStatusFiscal(StatusFiscal.NAO_HABILITADA);
            cabecalhoRepository.save(cupom);
            return Preparo.finalizado(ResultadoEmissaoFiscal.naoConfigurado());
        }
        cupom.setTipoOperacao(TIPO_OPERACAO_NFCE);
        cabecalhoRepository.save(cupom);
        return new Preparo(null, lojaConfiguracaoRepository.buscarConfiguracao().orElse(null), terminal.nfceEmProducao(),
                cupom.getNfceNumero() == null, TIPO_OFFLINE.equals(cupom.getNfceTipoEmissao()));
    }

    /** Reserva fora de qualquer transação aberta sobre o cupom (ver {@link NumeracaoNfceService}). */
    private void reservarNumero(PdvCaixa caixa, Long cupomId, String tipoEmissao) {
        numeracaoService.reservarParaCupom(caixa.getId(), cupomId, tipoEmissao, LocalDateTime.now(FUSO_BRASILIA).withNano(0));
    }

    private ResultadoEmissaoFiscal emitirOffline(Long cupomId, LojaConfiguracao loja, boolean producao, String motivo) {
        PdvVendaCabecalho cupom = cupom(cupomId);
        cupom.setNfceTipoEmissao(TIPO_OFFLINE);
        cabecalhoRepository.save(cupom);
        LocalDateTime entrada = contingencia.ativaDesde().orElse(LocalDateTime.now(FUSO_BRASILIA)).withNano(0);
        AssinadorNfce.DocumentoNfceAssinado documento =
                montarEAssinar(cupomId, loja, producao, NFTipoEmissao.CONTIGENCIA_OFFLINE, entrada, justificativa(motivo));
        cupom = cupom(cupomId);
        cupom.setStatusFiscal(StatusFiscal.CONTINGENCIA_OFFLINE);
        cupom.setCodigoStatusSefaz(null);
        cupom.setMensagemFiscal(truncar("NFC-e emitida em contingência offline (" + motivo + "). Transmissão automática assim que a "
                + "SEFAZ responder — prazo legal: até o 1º dia útil seguinte / 24h."));
        cabecalhoRepository.save(cupom);
        LOGGER.warn("Cupom #{}: NFC-e {} série {} emitida em CONTINGÊNCIA OFFLINE, chave {}", cupomId, cupom.getNfceNumero(),
                cupom.getNfceSerie(), documento.chaveAcesso());
        return ResultadoEmissaoFiscal.contingencia(cupom.getMensagemFiscal());
    }

    /** Monta, assina, aplica o QR Code e guarda o XML (o documento guardado é sempre o último assinado). */
    private AssinadorNfce.DocumentoNfceAssinado montarEAssinar(Long cupomId, LojaConfiguracao loja, boolean producao, NFTipoEmissao tipo,
                                                              LocalDateTime dataHoraContingencia, String justificativaContingencia) {
        PdvVendaCabecalho cupom = cupom(cupomId);
        List<PdvVendaDetalhe> itens = detalheRepository.findByPdvVendaCabecalhoAndCanceladoOrderByItemAsc(cupom, SimNao.N);
        List<PdvTotalTipoPagamento> pagamentos = pagamentoRepository.findByIdPdvVendaCabecalho(cupomId);
        NfceDocumentoBuilder.DadosNfce dados = new NfceDocumentoBuilder.DadosNfce(cupom, loja, itens, pagamentos, siglas(itens),
                cupom.getNfceSerie(), cupom.getNfceNumero(), cupom.getNfceDataHoraEmissao(), producao, tipo,
                dataHoraContingencia, justificativaContingencia, properties.responsavelTecnico());
        NFNota nota = documentoBuilder.construir(dados);
        AssinadorNfce.DocumentoNfceAssinado documento = assinador.assinar(nota, configFactory.paraAssinatura(loja, producao, tipo), cupomId);

        PdvNfceXml xml = xmlRepository.findById(cupomId).orElse(null);
        if (xml == null) {
            xml = new PdvNfceXml(cupomId, documento.xmlLote(), documento.qrCode(), documento.urlConsulta());
        } else {
            xml.substituirDocumento(documento.xmlLote(), documento.qrCode(), documento.urlConsulta());
        }
        xmlRepository.save(xml);
        cupom.setChaveAcessoFiscal(documento.chaveAcesso());
        cabecalhoRepository.save(cupom);
        return documento;
    }

    private ResultadoEmissaoFiscal aplicarAutorizacao(Long cupomId, TransmissorNfce.RetornoAutorizacao retorno) {
        PdvVendaCabecalho cupom = cupom(cupomId);
        cupom.setCodigoStatusSefaz(retorno.codigoStatus());
        String mensagem = truncar(retorno.codigoStatus() + " - " + retorno.motivo());
        cupom.setMensagemFiscal(mensagem);
        switch (retorno.resultado()) {
            case AUTORIZADA, DENEGADA -> {
                cupom.setStatusFiscal(retorno.resultado() == TransmissorNfce.Resultado.AUTORIZADA ? StatusFiscal.AUTORIZADA : StatusFiscal.DENEGADA);
                cupom.setProtocoloAutorizacao(retorno.protocolo());
                xmlRepository.findById(cupomId).ifPresent(x -> {
                    x.setXmlProcessado(retorno.xmlProcessado());
                    xmlRepository.save(x);
                });
            }
            default -> cupom.setStatusFiscal(StatusFiscal.REJEITADA);
        }
        cabecalhoRepository.save(cupom);
        LOGGER.info("Cupom #{}: NFC-e {} — {}", cupomId, cupom.getStatusFiscal(), mensagem);
        return switch (cupom.getStatusFiscal()) {
            case StatusFiscal.AUTORIZADA -> ResultadoEmissaoFiscal.autorizada(mensagem);
            case StatusFiscal.DENEGADA -> ResultadoEmissaoFiscal.denegada(mensagem);
            default -> ResultadoEmissaoFiscal.rejeitada(mensagem);
        };
    }

    /**
     * 204/539: a SEFAZ já recebeu este número. O documento é determinístico (cNF e data de emissão fixos no cupom),
     * então a consulta pela chave diz o que aconteceu com a tentativa anterior cuja resposta se perdeu.
     */
    private TransmissorNfce.RetornoAutorizacao reconciliarDuplicidade(String chave, NFNota notaAssinada, NFeConfig config,
                                                                     TransmissorNfce.RetornoAutorizacao duplicidade) {
        TransmissorNfce.RetornoConsulta consulta = transmissor.consultar(chave, notaAssinada, config);
        return switch (consulta.situacao()) {
            case AUTORIZADA -> new TransmissorNfce.RetornoAutorizacao(TransmissorNfce.Resultado.AUTORIZADA, consulta.codigoStatus(),
                    consulta.motivo(), consulta.protocolo(), consulta.versaoAplicativo(), consulta.xmlProcessado());
            case DENEGADA -> new TransmissorNfce.RetornoAutorizacao(TransmissorNfce.Resultado.DENEGADA, consulta.codigoStatus(),
                    consulta.motivo(), consulta.protocolo(), consulta.versaoAplicativo(), consulta.xmlProcessado());
            default -> new TransmissorNfce.RetornoAutorizacao(TransmissorNfce.Resultado.REJEITADA, duplicidade.codigoStatus(),
                    duplicidade.motivo() + " | consulta da chave: " + consulta.codigoStatus() + " " + consulta.motivo(), null, null, null);
        };
    }

    private Void registrarNumeroPendente(Long caixaId, Long cupomId, boolean producao, String chave, String motivo) {
        PdvVendaCabecalho cupom = cupom(cupomId);
        if (cupom.getNfceNumero() != null
                && !numeroPendenteRepository.existsByIdPdvCaixaAndSerieAndNumero(caixaId, cupom.getNfceSerie(), cupom.getNfceNumero())) {
            numeroPendenteRepository.save(new PdvNfceNumeroPendente(caixaId, cupomId, producao ? "1" : "2",
                    cupom.getNfceSerie(), cupom.getNfceNumero(), chave, motivo));
        }
        return null;
    }

    private ResultadoEmissaoFiscal pendente(Long cupomId, String mensagem) {
        LOGGER.warn("Cupom #{}: NFC-e pendente — {}", cupomId, mensagem);
        PdvVendaCabecalho cupom = cupom(cupomId);
        cupom.setStatusFiscal(StatusFiscal.PENDENTE);
        cupom.setMensagemFiscal(truncar(mensagem));
        cabecalhoRepository.save(cupom);
        return ResultadoEmissaoFiscal.pendente(mensagem);
    }

    private ResultadoEmissaoFiscal resultadoAtual(PdvVendaCabecalho cupom) {
        return switch (cupom.getStatusFiscal()) {
            case StatusFiscal.AUTORIZADA -> ResultadoEmissaoFiscal.autorizada(cupom.getMensagemFiscal());
            case StatusFiscal.CONTINGENCIA_OFFLINE -> ResultadoEmissaoFiscal.contingencia(cupom.getMensagemFiscal());
            default -> ResultadoEmissaoFiscal.denegada(cupom.getMensagemFiscal());
        };
    }

    private PdvVendaCabecalho cupom(Long cupomId) {
        return cabecalhoRepository.findById(cupomId).orElseThrow(() -> new IllegalStateException("Cupom #" + cupomId + " não encontrado."));
    }

    private <T> T emTransacao(java.util.function.Supplier<T> passo) {
        return novaTransacao.execute(status -> passo.get());
    }

    private Map<Long, String> siglas(List<PdvVendaDetalhe> itens) {
        List<Long> ids = itens.stream().map(i -> i.getProduto() != null ? i.getProduto().getProdutoUnidade() : null)
                .filter(Objects::nonNull).distinct().toList();
        Map<Long, String> siglas = new HashMap<>();
        for (ProdutoUnidade unidade : unidadeRepository.findAllById(ids)) {
            if (unidade.getSigla() != null && !unidade.getSigla().isBlank()) {
                siglas.put(unidade.getId(), unidade.getSigla().trim());
            }
        }
        return siglas;
    }

    private String motivoContingencia() {
        String motivo = contingencia.motivo();
        return motivo == null ? "SEFAZ indisponivel" : motivo;
    }

    /** xJust: 15 a 256 caracteres. */
    private static String justificativa(String motivo) {
        String texto = ("Contingencia offline: " + (motivo == null ? "SEFAZ indisponivel" : motivo)).replaceAll("\\s+", " ").trim();
        return texto.length() > 256 ? texto.substring(0, 256) : texto;
    }

    private static String ambienteDaChave(PdvNfceXml xml) {
        int inicio = xml.getXmlLoteAssinado().indexOf("<tpAmb>");
        return inicio < 0 ? "2" : xml.getXmlLoteAssinado().substring(inicio + 7, inicio + 8);
    }

    private static String truncar(String texto) {
        return texto == null || texto.length() <= 500 ? texto : texto.substring(0, 500);
    }

    /** Resultado do primeiro passo: ou já há um resultado final, ou o contexto para emitir. */
    private record Preparo(ResultadoEmissaoFiscal resultadoFinal, LojaConfiguracao loja, boolean producao, boolean semNumero,
                           boolean jaOffline) {

        static Preparo finalizado(ResultadoEmissaoFiscal resultado) {
            return new Preparo(resultado, null, false, false, false);
        }
    }
}
