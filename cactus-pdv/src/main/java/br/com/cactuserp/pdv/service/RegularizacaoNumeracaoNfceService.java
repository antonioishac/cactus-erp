package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.LojaConfiguracao;
import br.com.cactuserp.pdv.entity.PdvNfceNumeroPendente;
import br.com.cactuserp.pdv.entity.PdvNfceXml;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.SituacaoNumeroPendente;
import br.com.cactuserp.pdv.entity.StatusFiscal;
import br.com.cactuserp.pdv.repository.LojaConfiguracaoRepository;
import br.com.cactuserp.pdv.repository.PdvNfceNumeroPendenteRepository;
import br.com.cactuserp.pdv.repository.PdvNfceXmlRepository;
import br.com.cactuserp.pdv.repository.PdvVendaCabecalhoRepository;
import br.com.cactuserp.pdv.service.fiscal.ContingenciaNfceService;
import br.com.cactuserp.pdv.service.fiscal.FalhaComunicacaoNfceException;
import br.com.cactuserp.pdv.service.fiscal.NfceConfigFactory;
import br.com.cactuserp.pdv.service.fiscal.NfceConfiguracaoException;
import br.com.cactuserp.pdv.service.fiscal.TransmissorNfce;
import com.fincatto.documentofiscal.nfe.NFTipoEmissao;
import com.fincatto.documentofiscal.nfe.NFeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regulariza na SEFAZ os números de NFC-e que não ficaram com o cupom ({@code PDV_NFCE_NUMERO_PENDENTE}), como exige a
 * homologação de PDV com contingência offline:
 * <ul>
 *   <li><b>Tentativa com chave</b> (transmitida sem resposta e substituída por uma NFC-e offline): consulta a chave.
 *       Autorizada → cancelamento por substituição (110112) referenciando a NFC-e substituta já autorizada; ainda não
 *       autorizada a substituta → espera a próxima rodada. Não consta (217) → inutiliza.</li>
 *   <li><b>Sem chave</b> (número reservado e nunca transmitido): inutiliza.</li>
 *   <li><b>UFs sem pedido de inutilização</b> ({@link #UFS_SEM_INUTILIZACAO}, Ajuste SINIEF 38/2026 a partir de
 *       {@link #INICIO_DISPENSA}): o número só é registrado como dispensado; a SEFAZ nem tem o pedido.</li>
 * </ul>
 * Só roda fora da contingência (precisa da SEFAZ). Depois de {@link #MAXIMO_TENTATIVAS} recusas, o número vira
 * {@code E} para ação do responsável fiscal (nunca fica tentando para sempre).
 */
@Service
public class RegularizacaoNumeracaoNfceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegularizacaoNumeracaoNfceService.class);
    private static final ZoneId FUSO_BRASILIA = ZoneId.of("America/Sao_Paulo");
    static final Set<String> UFS_SEM_INUTILIZACAO = Set.of("AC", "BA", "SP");
    static final LocalDate INICIO_DISPENSA = LocalDate.of(2026, 11, 1);
    static final int MAXIMO_TENTATIVAS = 5;
    private static final Pattern VERSAO_APLICATIVO = Pattern.compile("<protNFe.*?<verAplic>([^<]+)</verAplic>", Pattern.DOTALL);
    private static final String JUSTIFICATIVA_SUBSTITUICAO = "NFC-e substituida por emissao em contingencia offline";
    private static final String JUSTIFICATIVA_INUTILIZACAO = "Numero de NFC-e nao utilizado: falha de comunicacao com a SEFAZ no PDV";

    private final PdvNfceNumeroPendenteRepository pendenteRepository;
    private final PdvVendaCabecalhoRepository cabecalhoRepository;
    private final PdvNfceXmlRepository xmlRepository;
    private final LojaConfiguracaoRepository lojaConfiguracaoRepository;
    private final TransmissorNfce transmissor;
    private final NfceConfigFactory configFactory;
    private final ContingenciaNfceService contingencia;

    public RegularizacaoNumeracaoNfceService(PdvNfceNumeroPendenteRepository pendenteRepository,
                                             PdvVendaCabecalhoRepository cabecalhoRepository,
                                             PdvNfceXmlRepository xmlRepository,
                                             LojaConfiguracaoRepository lojaConfiguracaoRepository,
                                             TransmissorNfce transmissor,
                                             NfceConfigFactory configFactory,
                                             ContingenciaNfceService contingencia) {
        this.pendenteRepository = pendenteRepository;
        this.cabecalhoRepository = cabecalhoRepository;
        this.xmlRepository = xmlRepository;
        this.lojaConfiguracaoRepository = lojaConfiguracaoRepository;
        this.transmissor = transmissor;
        this.configFactory = configFactory;
        this.contingencia = contingencia;
    }

    @Scheduled(fixedDelayString = "${cactus.pdv.fiscal.regularizacao.intervalo-ms:600000}", initialDelay = 120_000)
    public void executar() {
        if (contingencia.ativa()) {
            return;
        }
        for (PdvNfceNumeroPendente pendente : pendenteRepository.findBySituacaoOrderByDataRegistroAsc(SituacaoNumeroPendente.V)) {
            try {
                regularizar(pendente.getId());
            } catch (FalhaComunicacaoNfceException ex) {
                LOGGER.info("Regularização de numeração interrompida: SEFAZ sem resposta ({})", ex.getMessage());
                return;
            } catch (Exception ex) {
                LOGGER.error("Falha ao regularizar o número pendente #{}", pendente.getId(), ex);
            }
        }
    }

    public void regularizar(Long pendenteId) {
        PdvNfceNumeroPendente pendente = pendenteRepository.findById(pendenteId).orElse(null);
        if (pendente == null || pendente.getSituacao() != SituacaoNumeroPendente.V) {
            return;
        }
        LojaConfiguracao loja = lojaConfiguracaoRepository.buscarConfiguracao()
                .orElseThrow(() -> new NfceConfiguracaoException("Configuração da loja não cadastrada."));
        boolean producao = "1".equals(pendente.getAmbiente());
        NFeConfig config = configFactory.paraTransmissao(loja, producao, NFTipoEmissao.EMISSAO_NORMAL);

        if (pendente.getChaveAcesso() != null) {
            TransmissorNfce.RetornoConsulta consulta = transmissor.consultar(pendente.getChaveAcesso(), null, config);
            switch (consulta.situacao()) {
                case AUTORIZADA -> {
                    cancelarPorSubstituicao(pendente, consulta, config);
                    return;
                }
                case CANCELADA -> {
                    pendente.resolver(SituacaoNumeroPendente.S, consulta.protocolo(), "Já cancelada na SEFAZ (" + consulta.codigoStatus() + ")");
                    pendenteRepository.save(pendente);
                    return;
                }
                case NAO_ENCONTRADA -> {
                    // Não chegou à SEFAZ: o número é inutilizado como qualquer outro abandonado.
                }
                default -> {
                    tentativaSemSucesso(pendente, "Consulta da tentativa: " + consulta.codigoStatus() + " " + consulta.motivo());
                    return;
                }
            }
        }
        inutilizar(pendente, loja, config);
    }

    private void cancelarPorSubstituicao(PdvNfceNumeroPendente pendente, TransmissorNfce.RetornoConsulta consulta, NFeConfig config) {
        PdvVendaCabecalho substituta = pendente.getIdPdvVendaCabecalho() == null ? null
                : cabecalhoRepository.findById(pendente.getIdPdvVendaCabecalho()).orElse(null);
        if (substituta == null) {
            pendente.registrarTentativa("NFC-e da tentativa está AUTORIZADA e não há NFC-e substituta registrada: cancele-a "
                    + "manualmente (chave " + pendente.getChaveAcesso() + ")", true);
            pendenteRepository.save(pendente);
            return;
        }
        if (!StatusFiscal.AUTORIZADA.equals(substituta.getStatusFiscal())) {
            LOGGER.info("Número pendente #{}: aguardando a autorização da NFC-e substituta (cupom #{})", pendente.getId(), substituta.getId());
            return;
        }
        String versaoAplicativo = xmlRepository.findById(substituta.getId()).map(PdvNfceXml::getXmlProcessado)
                .map(RegularizacaoNumeracaoNfceService::versaoAplicativo).orElse(null);
        if (versaoAplicativo == null) {
            tentativaSemSucesso(pendente, "NFC-e substituta sem nfeProc guardado (versão do aplicativo autorizador desconhecida)");
            return;
        }
        TransmissorNfce.RetornoEvento evento = transmissor.cancelarPorSubstituicao(pendente.getChaveAcesso(), consulta.protocolo(),
                JUSTIFICATIVA_SUBSTITUICAO, versaoAplicativo, substituta.getChaveAcessoFiscal(), config);
        if (evento.homologado()) {
            pendente.resolver(SituacaoNumeroPendente.S, evento.protocolo(), evento.codigoStatus() + " - " + evento.motivo());
            pendenteRepository.save(pendente);
            LOGGER.info("NFC-e {} cancelada por substituição pela {} (protocolo {})", pendente.getChaveAcesso(),
                    substituta.getChaveAcessoFiscal(), evento.protocolo());
        } else {
            tentativaSemSucesso(pendente, "Cancelamento por substituição recusado: " + evento.codigoStatus() + " " + evento.motivo());
        }
    }

    private void inutilizar(PdvNfceNumeroPendente pendente, LojaConfiguracao loja, NFeConfig config) {
        if (dispensaInutilizacao(loja.getUf(), LocalDate.now(FUSO_BRASILIA))) {
            pendente.resolver(SituacaoNumeroPendente.D, "AJ.SINIEF 38/2026",
                    "Inutilização dispensada em " + loja.getUf() + " (Ajuste SINIEF 38/2026): salto de numeração apenas registrado");
            pendenteRepository.save(pendente);
            return;
        }
        int ano = pendente.getDataRegistro().getYear() % 100;
        TransmissorNfce.RetornoEvento evento = transmissor.inutilizar(ano, loja.getCnpj().replaceAll("\\D", ""), pendente.getSerie(),
                pendente.getNumero(), JUSTIFICATIVA_INUTILIZACAO, config);
        if (evento.homologado()) {
            pendente.resolver(SituacaoNumeroPendente.I, evento.protocolo(), evento.codigoStatus() + " - " + evento.motivo());
            pendenteRepository.save(pendente);
        } else {
            tentativaSemSucesso(pendente, "Inutilização recusada: " + evento.codigoStatus() + " " + evento.motivo());
        }
    }

    private void tentativaSemSucesso(PdvNfceNumeroPendente pendente, String mensagem) {
        boolean desistir = pendente.getTentativas() + 1 >= MAXIMO_TENTATIVAS;
        pendente.registrarTentativa(mensagem, desistir);
        pendenteRepository.save(pendente);
        if (desistir) {
            LOGGER.error("Número de NFC-e {}/{} exige ação do responsável fiscal: {}", pendente.getSerie(), pendente.getNumero(), mensagem);
        }
    }

    /** Package-private para teste. */
    static boolean dispensaInutilizacao(String uf, LocalDate hoje) {
        return uf != null && UFS_SEM_INUTILIZACAO.contains(uf.trim().toUpperCase()) && !hoje.isBefore(INICIO_DISPENSA);
    }

    static String versaoAplicativo(String nfeProc) {
        if (nfeProc == null) {
            return null;
        }
        Matcher m = VERSAO_APLICATIVO.matcher(nfeProc);
        return m.find() ? m.group(1).trim() : null;
    }
}
