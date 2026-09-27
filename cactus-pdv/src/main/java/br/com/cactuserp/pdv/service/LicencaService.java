package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.LicencaCache;
import br.com.cactuserp.pdv.entity.OrigemLicencaCache;
import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvConfiguracao;
import br.com.cactuserp.pdv.entity.PlanoSituacao;
import br.com.cactuserp.pdv.repository.LicencaCacheRepository;
import br.com.cactuserp.pdv.repository.PdvConfiguracaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Trava de segurança (mensalidade) do PDV.
 *
 * <p>Desenho completo — estados, fluxo de verificação online/offline,
 * janela de tolerância, vínculo por dispositivo — documentado em
 * {@code LICENCIAMENTO.md} na raiz do projeto.
 *
 * <h3>Fluxo de verificação (implementado 2026-08-22)</h3>
 * <ol>
 *   <li>Se {@link RetaguardaLicencaClient} está configurado, tenta
 *       consulta remota. Sucesso → grava em {@link LicencaCache} (origem
 *       R) e retorna.</li>
 *   <li>Se a consulta remota falhar (ou não estiver configurada), cai pro
 *       cache local: se a última verificação bem-sucedida está dentro da
 *       janela de tolerância ({@code cactus.pdv.licenca.tolerancia-dias},
 *       padrão 5 dias), reusa aquela situação.</li>
 *   <li>Se a tolerância expirou, força {@link PlanoSituacao#B} por
 *       segurança (evita burlar a trava desligando a internet).</li>
 *   <li>Se NUNCA houve verificação remota bem-sucedida (instalação sem
 *       retaguarda configurada — cenário mais comum hoje, já que o
 *       endpoint da retaguarda ainda não existe), opera 100% em modo
 *       local: lê {@code PDV_CONFIGURACAO.PLANO_SITUACAO} diretamente,
 *       fail-open pra {@link PlanoSituacao#A} se não configurado.</li>
 * </ol>
 *
 * <h3>Auditoria de transição de estado (implementado 2026-08-23)</h3>
 * <p>Toda vez que a situação apurada difere da última conhecida (qualquer
 * origem), {@link LicencaCache} ganha uma nova linha com
 * {@code SITUACAO_ANTERIOR}/{@code PLANO_SITUACAO}/{@code DISPOSITIVO}
 * (ver {@code LICENCIAMENTO.md}, item 5). Verificação remota bem-sucedida
 * (origem {@code R}) sempre grava — mesmo sem mudança de situação — porque
 * é também o que sustenta {@code DATA_HORA_VERIFICACAO} usada pela janela
 * de tolerância; bloqueio forçado (origem {@code F}) e leitura local
 * (origem {@code L}) só gravam quando a situação realmente muda, pra não
 * inundar a tabela a cada consulta idêntica.</p>
 */
// Não é readOnly: consultarSituacao pode gravar em LicencaCache
// (gravarCache é chamado por auto-invocação, que ignora anotações
// @Transactional próprias — a transação gravável precisa vir da entrada
// pública, então a classe inteira não pode ser readOnly).
@Service
@Transactional
public class LicencaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LicencaService.class);

    @Value("${cactus.pdv.licenca.tolerancia-dias:5}")
    private long toleranciaDias;

    private final PdvConfiguracaoRepository configuracaoRepository;
    private final LicencaCacheRepository cacheRepository;
    private final RetaguardaLicencaClient retaguardaClient;
    private final DispositivoInfoService dispositivoInfo;

    public LicencaService(PdvConfiguracaoRepository configuracaoRepository,
                          LicencaCacheRepository cacheRepository,
                          RetaguardaLicencaClient retaguardaClient,
                          DispositivoInfoService dispositivoInfo) {
        this.configuracaoRepository = configuracaoRepository;
        this.cacheRepository = cacheRepository;
        this.retaguardaClient = retaguardaClient;
        this.dispositivoInfo = dispositivoInfo;
    }

    /**
     * Situação atual do plano. Ver fluxo completo no javadoc da classe.
     */
    public PlanoSituacao consultarSituacao(PdvCaixa caixa) {
        // 1) Tenta remoto, se configurado
        if (retaguardaClient.estaConfigurado()) {
            Optional<PlanoSituacao> remoto = retaguardaClient.consultarSituacao(dispositivoInfo.getHostname());
            if (remoto.isPresent()) {
                gravarCache(remoto.get(), OrigemLicencaCache.R, /* sempreGravar */ true);
                return remoto.get();
            }
            LOGGER.debug("Consulta remota de licença falhou/indisponível — caindo pro cache local.");
        }

        // 2) Cache local (última verificação remota bem-sucedida ou bloqueio
        //    forçado) — leituras locais (origem L, passo 3) não contam aqui:
        //    não podem "resetar" a contagem de dias sem contato remoto.
        Optional<LicencaCache> cache = cacheRepository.findFirstByOrigemInOrderByIdDesc(
                List.of(OrigemLicencaCache.R, OrigemLicencaCache.F));
        if (cache.isPresent()) {
            long diasSemContato = ChronoUnit.DAYS.between(cache.get().getDataHoraVerificacao(), LocalDateTime.now());
            if (diasSemContato <= toleranciaDias) {
                return cache.get().getPlanoSituacao();
            }
            LOGGER.warn("Janela de tolerância offline expirada ({} dias sem contato com a retaguarda, "
                    + "limite {}) — forçando bloqueio.", diasSemContato, toleranciaDias);
            gravarCache(PlanoSituacao.B, OrigemLicencaCache.F, /* sempreGravar */ true);
            return PlanoSituacao.B;
        }

        // 3) Nunca houve verificação remota — modo 100% local (comportamento
        //    original, preservado pra não travar instalação sem retaguarda).
        PlanoSituacao situacaoLocal;
        if (caixa == null || caixa.getId() == null) {
            situacaoLocal = PlanoSituacao.A;
        } else {
            situacaoLocal = configuracaoRepository.findFirstByCaixaId(caixa.getId())
                    .map(PdvConfiguracao::planoSituacaoOuAtivo)
                    .orElse(PlanoSituacao.A);
        }
        gravarCache(situacaoLocal, OrigemLicencaCache.L, /* sempreGravar */ false);
        return situacaoLocal;
    }

    /**
     * {@code true} se o caixa pode abrir um NOVO movimento.
     * Bloqueia apenas em situação {@link PlanoSituacao#B} — inadimplente
     * (I) não bloqueia, só avisa (ver {@link #mensagemAvisoOuNull}).
     */
    public boolean podeAbrirCaixa(PdvCaixa caixa) {
        boolean pode = consultarSituacao(caixa) != PlanoSituacao.B;
        if (!pode) {
            LOGGER.warn("Abertura de movimento bloqueada por inadimplência — caixa={}",
                    caixa != null ? caixa.getNome() : "?");
        }
        return pode;
    }

    /**
     * Mensagem de aviso a exibir quando o plano está inadimplente (I).
     * {@code null} quando não há nada a avisar (situação A ou B — B já é
     * bloqueado em outro ponto do fluxo).
     */
    public String mensagemAvisoOuNull(PdvCaixa caixa) {
        if (consultarSituacao(caixa) == PlanoSituacao.I) {
            return "Pagamento da mensalidade pendente — regularize para evitar o "
                    + "bloqueio da abertura de novos caixas.";
        }
        return null;
    }

    /**
     * Grava uma linha de auditoria em {@link LicencaCache} — chamado só por
     * auto-invocação a partir de {@link #consultarSituacao}, que já garante
     * transação gravável (sem {@code @Transactional} próprio; seria ignorado
     * pelo Spring AOP em chamada interna mesmo assim).
     *
     * <p>{@code situacaoAnterior} é sempre derivada da última linha conhecida
     * (qualquer origem), nunca recebida por parâmetro, pra garantir que a
     * trilha reflita a sequência real independente de qual origem gravou
     * por último.</p>
     *
     * @param sempreGravar {@code true} pra verificação remota bem-sucedida
     *                     (origem R) — precisa gravar mesmo sem mudança de
     *                     situação, pois também é o que sustenta
     *                     {@code DATA_HORA_VERIFICACAO} da janela de
     *                     tolerância. {@code false} pra bloqueio forçado (F)
     *                     e leitura local (L) — só grava se a situação
     *                     realmente mudou, pra não inundar a tabela.
     */
    private void gravarCache(PlanoSituacao situacaoNova, OrigemLicencaCache origem, boolean sempreGravar) {
        Optional<LicencaCache> ultimo = cacheRepository.findFirstByOrderByIdDesc();
        PlanoSituacao situacaoAnterior = ultimo.map(LicencaCache::getPlanoSituacao).orElse(null);
        if (!sempreGravar && situacaoAnterior == situacaoNova) {
            return;
        }
        LicencaCache registro = new LicencaCache();
        registro.setPlanoSituacao(situacaoNova);
        registro.setSituacaoAnterior(situacaoAnterior);
        registro.setDataHoraVerificacao(LocalDateTime.now());
        registro.setOrigem(origem);
        registro.setDispositivo(dispositivoInfo.getHostname());
        cacheRepository.save(registro);
    }
}
