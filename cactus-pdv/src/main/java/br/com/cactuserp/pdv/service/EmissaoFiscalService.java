package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.LojaConfiguracao;
import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvConfiguracao;
import br.com.cactuserp.pdv.entity.PdvTotalTipoPagamento;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.PdvVendaDetalhe;
import br.com.cactuserp.pdv.entity.SimNao;
import br.com.cactuserp.pdv.repository.LojaConfiguracaoRepository;
import br.com.cactuserp.pdv.repository.PdvConfiguracaoRepository;
import br.com.cactuserp.pdv.repository.PdvTotalTipoPagamentoRepository;
import br.com.cactuserp.pdv.repository.PdvVendaCabecalhoRepository;
import br.com.cactuserp.pdv.repository.PdvVendaDetalheRepository;
import br.com.cactuserp.pdv.service.dto.ResultadoEmissaoFiscal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Orquestra a emissão do documento fiscal (NFC-e/SAT/MFE) de uma venda já
 * finalizada, via ACBr Monitor.
 *
 * <h3>Fluxo</h3>
 * <ol>
 *   <li>Busca a {@link PdvConfiguracao} do caixa — se não houver endereço/
 *       porta/módulo principal configurados, retorna
 *       {@link ResultadoEmissaoFiscal.Status#NAO_CONFIGURADO} sem erro
 *       (loja ainda não fez essa etapa de setup, opera só com recibo).</li>
 *   <li>Tenta emitir pelo módulo principal ({@code MODULO_FISCAL_PRINCIPAL}).</li>
 *   <li>Se falhar, tenta o módulo de contingência
 *       ({@code MODULO_FISCAL_CONTINGENCIA}), se configurado.</li>
 *   <li>Se ambos falharem, retorna
 *       {@link ResultadoEmissaoFiscal.Status#PENDENTE_REPROCESSAMENTO} —
 *       <b>nunca lança exceção que desfaça a venda</b>: o pagamento já foi
 *       recebido, a venda já está fechada; {@code ReprocessamentoFiscalService}
 *       tenta de novo periodicamente.</li>
 * </ol>
 *
 * <p><b>Estado desta implementação (2026-08-22):</b> protocolo de
 * transporte e formato do comando ({@code NFe.CriarEnviarNFe} + arquivo
 * INI) validados contra documentação oficial do projeto ACBr — ver
 * {@link AcbrMonitorClient} e {@link AcbrComandoFiscalBuilder}. Chave de
 * acesso e protocolo de autorização agora são extraídos da resposta real
 * (campos {@code ChDFe}/{@code NProt}). <b>Ainda não testado contra uma
 * instância rodando de verdade</b> — nada aqui foi confirmado
 * empiricamente, só contra documentação.
 */
@Service
@Transactional(readOnly = true)
public class EmissaoFiscalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmissaoFiscalService.class);

    /** Extrai "ChDFe=..." (aceita espaços e quebra por fim de linha). */
    private static final Pattern PADRAO_CHAVE_ACESSO =
            Pattern.compile("(?im)^\\s*ChDFe\\s*=\\s*(.+?)\\s*$");
    /** Extrai "CStat=..." (status da SEFAZ). */
    private static final Pattern PADRAO_CSTAT =
            Pattern.compile("(?im)^\\s*CStat\\s*=\\s*(\\d+)\\s*$");
    /** Extrai "XMotivo=...". */
    private static final Pattern PADRAO_XMOTIVO =
            Pattern.compile("(?im)^\\s*XMotivo\\s*=\\s*(.+?)\\s*$");
    /**
     * CStat de cancelamento aceito: 135 (evento registrado e vinculado),
     * 155 (cancelamento homologado fora de prazo) e 101 (cancelamento
     * homologado, código do exemplo da documentação do ACBr). O ACBr responde
     * {@code OK:} quando a comunicação funcionou, não quando a SEFAZ aceitou
     * — por isso o CStat é conferido explicitamente.
     */
    private static final java.util.Set<String> CSTAT_CANCELAMENTO_ACEITO = java.util.Set.of("135", "155", "101");

    /** Extrai "NProt=...". */
    private static final Pattern PADRAO_PROTOCOLO =
            Pattern.compile("(?im)^\\s*NProt\\s*=\\s*(.+?)\\s*$");

    private final PdvConfiguracaoRepository configuracaoRepository;
    private final LojaConfiguracaoRepository lojaConfiguracaoRepository;
    private final PdvVendaDetalheRepository detalheRepository;
    private final PdvTotalTipoPagamentoRepository pagamentoRepository;
    private final PdvVendaCabecalhoRepository cabecalhoRepository;
    private final AcbrMonitorClient acbrClient;
    private final AcbrComandoFiscalBuilder comandoBuilder;

    public EmissaoFiscalService(PdvConfiguracaoRepository configuracaoRepository,
                                LojaConfiguracaoRepository lojaConfiguracaoRepository,
                                PdvVendaDetalheRepository detalheRepository,
                                PdvTotalTipoPagamentoRepository pagamentoRepository,
                                PdvVendaCabecalhoRepository cabecalhoRepository,
                                AcbrMonitorClient acbrClient,
                                AcbrComandoFiscalBuilder comandoBuilder) {
        this.configuracaoRepository = configuracaoRepository;
        this.lojaConfiguracaoRepository = lojaConfiguracaoRepository;
        this.detalheRepository = detalheRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.cabecalhoRepository = cabecalhoRepository;
        this.acbrClient = acbrClient;
        this.comandoBuilder = comandoBuilder;
    }

    /**
     * Emite o documento fiscal de uma venda finalizada. Nunca lança exceção
     * — qualquer falha vira {@link ResultadoEmissaoFiscal.Status#PENDENTE_REPROCESSAMENTO}.
     *
     * @param caixa           caixa onde a venda ocorreu (pra achar a configuração fiscal)
     * @param cupomFinalizado cupom já com {@code StatusVenda.F}
     */
    @Transactional
    public ResultadoEmissaoFiscal emitir(PdvCaixa caixa, PdvVendaCabecalho cupomFinalizado) {
        if (caixa == null || cupomFinalizado == null) {
            throw new IllegalArgumentException("Caixa e cupom são obrigatórios.");
        }

        PdvConfiguracao config = configuracaoRepository.findFirstByCaixaId(caixa.getId()).orElse(null);
        if (config == null
                || isBlank(config.getAcbrMonitorEndereco())
                || config.getAcbrMonitorPorta() == null
                || isBlank(config.getModuloFiscalPrincipal())) {
            LOGGER.info("ACBr Monitor não configurado para o caixa {} — cupom {} segue como recibo.",
                    caixa.getNome(), cupomFinalizado.getId());
            return concluirNaoConfigurado(cupomFinalizado);
        }

        LojaConfiguracao loja = lojaConfiguracaoRepository.buscarConfiguracao().orElse(null);
        List<PdvVendaDetalhe> itens = detalheRepository
                .findByPdvVendaCabecalhoAndCanceladoOrderByItemAsc(cupomFinalizado, SimNao.N);
        List<PdvTotalTipoPagamento> pagamentos =
                pagamentoRepository.findByIdPdvVendaCabecalho(cupomFinalizado.getId());

        String host = config.getAcbrMonitorEndereco();
        int porta = config.getAcbrMonitorPorta();

        try {
            String resposta = executarEmissao(config.getModuloFiscalPrincipal(), host, porta,
                    cupomFinalizado, loja, itens, pagamentos);
            return concluirComSucesso(cupomFinalizado, config.getModuloFiscalPrincipal(), resposta);
        } catch (Exception principalFalhou) {
            LOGGER.error("Falha ao emitir via módulo principal ({}) para o cupom {}: {}",
                    config.getModuloFiscalPrincipal(), cupomFinalizado.getId(), principalFalhou.getMessage(), principalFalhou);

            String contingencia = config.getModuloFiscalContingencia();
            if (isBlank(contingencia)) {
                return concluirPendente(cupomFinalizado,
                        "Falha na emissão (" + config.getModuloFiscalPrincipal() + "): "
                                + principalFalhou.getMessage() + ". Sem contingência configurada.");
            }

            try {
                String resposta = executarEmissao(contingencia, host, porta, cupomFinalizado, loja, itens, pagamentos);
                return concluirComSucesso(cupomFinalizado, contingencia, resposta);
            } catch (Exception contingenciaFalhou) {
                LOGGER.error("Falha também na contingência ({}) para o cupom {}: {}",
                        contingencia, cupomFinalizado.getId(), contingenciaFalhou.getMessage(), contingenciaFalhou);
                return concluirPendente(cupomFinalizado,
                        "Falha na emissão principal (" + principalFalhou.getMessage() + ") e na "
                                + "contingência (" + contingenciaFalhou.getMessage() + "). "
                                + "Venda concluída; emissão fiscal precisa ser reprocessada manualmente.");
            }
        }
    }

    /**
     * Cancela na SEFAZ a NFC-e de um cupom já autorizado. Diferente de
     * {@link #emitir}, <b>lança exceção</b> em qualquer falha: sem a
     * confirmação da SEFAZ a venda não pode ser cancelada localmente
     * (ficaria cancelada no PDV e válida no fisco).
     *
     * @return protocolo do evento de cancelamento
     */
    public String cancelarNfce(PdvCaixa caixa, PdvVendaCabecalho cupom, String justificativa) {
        PdvConfiguracao config = caixa == null ? null
                : configuracaoRepository.findFirstByCaixaId(caixa.getId()).orElse(null);
        if (config == null || isBlank(config.getAcbrMonitorEndereco()) || config.getAcbrMonitorPorta() == null) {
            throw new IllegalStateException("ACBr Monitor não configurado neste caixa — não é possível cancelar a NFC-e.");
        }
        LojaConfiguracao loja = lojaConfiguracaoRepository.buscarConfiguracao()
                .orElseThrow(() -> new IllegalStateException("Configuração da loja (CNPJ do emitente) não encontrada."));
        if (isBlank(loja.getCnpj())) {
            throw new IllegalStateException("CNPJ do emitente não configurado na loja — a SEFAZ exige no cancelamento da NFC-e.");
        }
        String comando = comandoBuilder.construirComandoCancelamentoNfce(
                cupom.getChaveAcessoFiscal(), justificativa, loja.getCnpj());

        String resposta;
        try {
            resposta = acbrClient.enviarComando(config.getAcbrMonitorEndereco(), config.getAcbrMonitorPorta(), comando);
        } catch (Exception ex) {
            throw new IllegalStateException("Falha de comunicação com o ACBr Monitor: " + ex.getMessage(), ex);
        }
        return interpretarRespostaCancelamento(resposta);
    }

    /** Package-private e estático para teste unitário direto ({@code EmissaoFiscalServiceTest}). */
    static String interpretarRespostaCancelamento(String resposta) {
        if (resposta == null || resposta.isBlank()) {
            throw new IllegalStateException("ACBr Monitor não respondeu ao cancelamento.");
        }
        if (resposta.trim().toUpperCase(Locale.ROOT).startsWith("ERRO")) {
            throw new IllegalStateException("ACBr Monitor recusou o cancelamento: " + resposta.trim());
        }
        String cStat = extrair(PADRAO_CSTAT, resposta, 3);
        if (cStat == null || !CSTAT_CANCELAMENTO_ACEITO.contains(cStat)) {
            String motivo = extrair(PADRAO_XMOTIVO, resposta, 255);
            throw new IllegalStateException("SEFAZ não homologou o cancelamento (CStat=" + cStat + "): "
                    + (motivo != null ? motivo : resposta.trim()));
        }
        return extrairProtocoloAutorizacao(resposta);
    }

    /**
     * Monta o comando (INI + {@code NFe.CriarEnviarNFe}) e executa via
     * {@link AcbrMonitorClient}. Retorna a resposta bruta em caso de
     * sucesso — quem chama extrai chave/protocolo dela.
     */
    private String executarEmissao(String modulo, String host, int porta,
                                    PdvVendaCabecalho cupom, LojaConfiguracao loja,
                                    List<PdvVendaDetalhe> itens,
                                    List<PdvTotalTipoPagamento> pagamentos) throws Exception {
        String comando = comandoBuilder.construirComando(
                modulo, cupom, loja, itens, pagamentos, /* lote */ 1);
        String resposta = acbrClient.enviarComando(host, porta, comando);
        if (acbrClient.respostaComErro(resposta)) {
            throw new IllegalStateException("ACBr Monitor retornou erro: " + resposta);
        }
        return resposta;
    }

    /** Status persistidos em PDV_VENDA_CABECALHO.STATUS_FISCAL. */
    private static final String STATUS_FISCAL_EMITIDO = "E";
    private static final String STATUS_FISCAL_PENDENTE = "P";
    private static final String STATUS_FISCAL_NAO_CONFIGURADO = "N";

    /**
     * Atualiza {@code TIPO_OPERACAO}/{@code STATUS_FISCAL} do cupom, e
     * extrai {@code CHAVE_ACESSO_FISCAL}/{@code PROTOCOLO_AUTORIZACAO} da
     * resposta real do ACBr (campos {@code ChDFe}/{@code NProt} — ver
     * {@code PADRAO_CHAVE_ACESSO}/{@code PADRAO_PROTOCOLO}).
     */
    private ResultadoEmissaoFiscal concluirComSucesso(PdvVendaCabecalho cupom, String modulo, String respostaAcbr) {
        cupom.setTipoOperacao(tipoOperacaoDoModulo(modulo));
        cupom.setStatusFiscal(STATUS_FISCAL_EMITIDO);
        cupom.setMensagemFiscal(truncar("Emitido via " + modulo + ".", 500));
        cupom.setChaveAcessoFiscal(extrairChaveAcesso(respostaAcbr));
        cupom.setProtocoloAutorizacao(extrairProtocoloAutorizacao(respostaAcbr));
        cabecalhoRepository.save(cupom);
        LOGGER.info("Emissão fiscal concluída: cupom={}, módulo={}, chave={}, protocolo={}",
                cupom.getId(), modulo, cupom.getChaveAcessoFiscal(), cupom.getProtocoloAutorizacao());
        return ResultadoEmissaoFiscal.emitido(modulo,
                "Documento fiscal emitido com sucesso via " + modulo + ".");
    }

    /** Loja sem ACBr Monitor configurado — venda segue como recibo simples. */
    private ResultadoEmissaoFiscal concluirNaoConfigurado(PdvVendaCabecalho cupom) {
        cupom.setStatusFiscal(STATUS_FISCAL_NAO_CONFIGURADO);
        cabecalhoRepository.save(cupom);
        return ResultadoEmissaoFiscal.naoConfigurado();
    }

    /** Falha em todos os módulos disponíveis — marca pra reprocessamento futuro. */
    private ResultadoEmissaoFiscal concluirPendente(PdvVendaCabecalho cupom, String mensagem) {
        cupom.setStatusFiscal(STATUS_FISCAL_PENDENTE);
        cupom.setMensagemFiscal(truncar(mensagem, 500));
        cabecalhoRepository.save(cupom);
        return ResultadoEmissaoFiscal.pendente(mensagem);
    }

    /**
     * Extrai a chave de acesso ({@code ChDFe=...}) de uma resposta do ACBr
     * Monitor. Estático e package-private só pra permitir teste unitário
     * direto ({@code EmissaoFiscalServiceTest}) sem subir contexto Spring —
     * ver a ressalva no javadoc da classe: o formato exato do campo em uma
     * resposta de SUCESSO real segue não confirmado contra uma emissão de
     * verdade (só o caminho de erro foi validado até 2026-08-23).
     */
    static String extrairChaveAcesso(String respostaAcbr) {
        return extrair(PADRAO_CHAVE_ACESSO, respostaAcbr, 44);
    }

    /** Extrai o protocolo de autorização ({@code NProt=...}) — mesma ressalva de {@link #extrairChaveAcesso}. */
    static String extrairProtocoloAutorizacao(String respostaAcbr) {
        return extrair(PADRAO_PROTOCOLO, respostaAcbr, 30);
    }

    private static String extrair(Pattern padrao, String texto, int maxLength) {
        if (texto == null) return null;
        Matcher m = padrao.matcher(texto);
        return m.find() ? truncar(m.group(1), maxLength) : null;
    }

    private static String truncar(String texto, int max) {
        if (texto == null) return null;
        return texto.length() <= max ? texto : texto.substring(0, max);
    }

    private String tipoOperacaoDoModulo(String modulo) {
        return switch (modulo.toUpperCase(Locale.ROOT)) {
            case AcbrComandoFiscalBuilder.MODULO_NFCE -> "NFC";
            case AcbrComandoFiscalBuilder.MODULO_SAT -> "SAT";
            case AcbrComandoFiscalBuilder.MODULO_MFE -> "MFE";
            default -> "REC";
        };
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
