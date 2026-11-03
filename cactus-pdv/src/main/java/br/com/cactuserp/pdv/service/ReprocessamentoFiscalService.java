package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.LojaConfiguracao;
import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvConfiguracao;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.StatusFiscal;
import br.com.cactuserp.pdv.entity.StatusVenda;
import br.com.cactuserp.pdv.repository.LojaConfiguracaoRepository;
import br.com.cactuserp.pdv.repository.PdvConfiguracaoRepository;
import br.com.cactuserp.pdv.repository.PdvVendaCabecalhoRepository;
import br.com.cactuserp.pdv.service.fiscal.ContingenciaNfceService;
import br.com.cactuserp.pdv.service.fiscal.FalhaComunicacaoNfceException;
import br.com.cactuserp.pdv.service.fiscal.NfceConfigFactory;
import br.com.cactuserp.pdv.service.fiscal.TransmissorNfce;
import com.fincatto.documentofiscal.nfe.NFTipoEmissao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Job fiscal do terminal (roda enquanto o PDV está aberto; ao reabrir, retoma de onde parou):
 * <ol>
 *   <li><b>Contingência</b>: se o terminal está em contingência offline, consulta o status do serviço de autorização;
 *       com cStat 107, sai da contingência — as próximas vendas voltam a ser emitidas online.</li>
 *   <li><b>Transmissão das offline</b> ({@code O}), da mais antiga para a mais nova (o prazo legal — até o 1º dia útil
 *       seguinte/24h — corre por emissão). Avisa no log quando alguma passa de {@link #ALERTA_PRAZO}.</li>
 *   <li><b>Pendentes</b> ({@code P}): tenta emitir de novo (ex.: o certificado foi instalado, o cadastro corrigido).</li>
 * </ol>
 * Cupom de venda cancelada não é reprocessado. Cada cupom em transação própria: uma falha não para os demais.
 */
@Service
public class ReprocessamentoFiscalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReprocessamentoFiscalService.class);
    private static final ZoneId FUSO_BRASILIA = ZoneId.of("America/Sao_Paulo");
    static final Duration ALERTA_PRAZO = Duration.ofHours(20);

    private final PdvVendaCabecalhoRepository cabecalhoRepository;
    private final EmissaoFiscalService emissaoFiscalService;
    private final ContingenciaNfceService contingencia;
    private final TransmissorNfce transmissor;
    private final NfceConfigFactory configFactory;
    private final LojaConfiguracaoRepository lojaConfiguracaoRepository;
    private final PdvConfiguracaoRepository configuracaoRepository;

    public ReprocessamentoFiscalService(PdvVendaCabecalhoRepository cabecalhoRepository,
                                        EmissaoFiscalService emissaoFiscalService,
                                        ContingenciaNfceService contingencia,
                                        TransmissorNfce transmissor,
                                        NfceConfigFactory configFactory,
                                        LojaConfiguracaoRepository lojaConfiguracaoRepository,
                                        PdvConfiguracaoRepository configuracaoRepository) {
        this.cabecalhoRepository = cabecalhoRepository;
        this.emissaoFiscalService = emissaoFiscalService;
        this.contingencia = contingencia;
        this.transmissor = transmissor;
        this.configFactory = configFactory;
        this.lojaConfiguracaoRepository = lojaConfiguracaoRepository;
        this.configuracaoRepository = configuracaoRepository;
    }

    /**
     * Intervalo {@code cactus.pdv.fiscal.reprocessamento.intervalo-ms} (padrão 1 minuto: a contingência precisa
     * terminar assim que a SEFAZ voltar). Conta a partir do fim da execução anterior — nunca empilha.
     */
    @Scheduled(fixedDelayString = "${cactus.pdv.fiscal.reprocessamento.intervalo-ms:60000}")
    public void executar() {
        if (contingencia.ativa() && !servicoDisponivel()) {
            return;
        }
        contingencia.sair();
        transmitirContingencias();
        reprocessarPendentes();
    }

    void transmitirContingencias() {
        List<PdvVendaCabecalho> offline = cabecalhoRepository.findByStatusFiscalOrderByNfceDataHoraEmissaoAsc(StatusFiscal.CONTINGENCIA_OFFLINE);
        LocalDateTime agora = LocalDateTime.now(FUSO_BRASILIA);
        for (PdvVendaCabecalho cupom : offline) {
            if (cupom.getNfceDataHoraEmissao() != null && Duration.between(cupom.getNfceDataHoraEmissao(), agora).compareTo(ALERTA_PRAZO) > 0) {
                LOGGER.warn("ATENÇÃO: NFC-e offline do cupom #{} emitida em {} ainda não transmitida — prazo legal se esgotando.",
                        cupom.getId(), cupom.getNfceDataHoraEmissao());
            }
            try {
                emissaoFiscalService.transmitirContingencia(cupom.getId());
            } catch (Exception ex) {
                LOGGER.error("Falha ao transmitir a NFC-e offline do cupom #{}", cupom.getId(), ex);
            }
            if (contingencia.ativa()) {
                break; // a SEFAZ parou de responder de novo: a próxima rodada continua (não martela cupom a cupom)
            }
        }
    }

    void reprocessarPendentes() {
        for (PdvVendaCabecalho cupom : cabecalhoRepository.findByStatusFiscal(StatusFiscal.PENDENTE)) {
            reprocessarUm(cupom.getId());
        }
    }

    /** Reemite um cupom pendente. Sem transação aqui: {@link EmissaoFiscalService#emitir} controla as suas. */
    public void reprocessarUm(Long idCupom) {
        try {
            PdvVendaCabecalho cupom = cabecalhoRepository.buscarComMovimentoECaixa(idCupom).orElse(null);
            if (cupom == null || cupom.getStatusVenda() != StatusVenda.F) {
                return; // cancelada depois de ficar pendente: emitir geraria documento de venda que não existe mais
            }
            PdvCaixa caixa = cupom.getPdvMovimento() != null ? cupom.getPdvMovimento().getCaixa() : null;
            if (caixa == null) {
                LOGGER.warn("Cupom #{} pendente sem movimento/caixa associado — não é possível reprocessar.", idCupom);
                return;
            }
            emissaoFiscalService.emitir(caixa, cupom);
        } catch (Exception ex) {
            LOGGER.error("Falha ao reprocessar a NFC-e do cupom #{}", idCupom, ex);
        }
    }

    private boolean servicoDisponivel() {
        try {
            LojaConfiguracao loja = lojaConfiguracaoRepository.buscarConfiguracao().orElse(null);
            boolean producao = configuracaoRepository.findAll().stream().filter(PdvConfiguracao::emiteNfce).anyMatch(PdvConfiguracao::nfceEmProducao);
            return transmissor.servicoEmOperacao(configFactory.paraTransmissao(loja, producao, NFTipoEmissao.EMISSAO_NORMAL));
        } catch (FalhaComunicacaoNfceException ex) {
            return false;
        } catch (Exception ex) {
            LOGGER.warn("Não foi possível consultar o status da SEFAZ: {}", ex.getMessage());
            return false;
        }
    }
}
