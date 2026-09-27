package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.config.SessaoPdv;
import br.com.cactuserp.pdv.entity.ContaAberta;
import br.com.cactuserp.pdv.entity.ContaAbertaItem;
import br.com.cactuserp.pdv.entity.OrigemVenda;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.PdvVendaDetalhe;
import br.com.cactuserp.pdv.entity.SimNao;
import br.com.cactuserp.pdv.entity.SituacaoItemConta;
import br.com.cactuserp.pdv.entity.TipoContaAberta;
import br.com.cactuserp.pdv.repository.PdvVendaCabecalhoRepository;
import br.com.cactuserp.pdv.repository.PdvVendaDetalheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Orquestra a importação de uma {@link ContaAberta} (mesa/comanda/delivery)
 * para o cupom de venda do PDV.
 *
 * <p>Esta classe coordena 4 services existentes para fazer o trabalho:
 * <ul>
 *   <li>{@link VendaService} — abre cupom + adiciona itens</li>
 *   <li>{@link ContaAbertaService} — muda status da conta + vincula</li>
 *   <li>{@link SessaoPdv} — guarda referência do cupom ativo</li>
 *   <li>Repositórios — atualizam os campos de rastreabilidade no cupom</li>
 * </ul>
 *
 * <h3>Ciclo completo</h3>
 * <pre>
 *  importarParaCupom(conta):
 *    1. abre/reusa cupom no PDV
 *    2. seta origem (M/C/D) + idContaAberta no cupom
 *    3. pra cada item ativo da conta, chama vendaService.adicionarItem
 *    4. ENRIQUECE cada PdvVendaDetalhe com idContaAbertaItem
 *    5. muda conta pra status E
 *
 *  liberarSeImportada(cupom):
 *    - chamado quando cupom é cancelado
 *    - se cupom tem contaAberta vinculada, libera (E → A)
 *
 *  vincularAposFinalizar(cupom):
 *    - chamado após VendaService.finalizarVenda
 *    - se cupom tem contaAberta vinculada, marca como F
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class IntegracaoContaPdvService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegracaoContaPdvService.class);

    private final VendaService vendaService;
    private final ContaAbertaService contaService;
    private final SessaoPdv sessaoPdv;
    private final PdvVendaCabecalhoRepository cabecalhoRepository;
    private final PdvVendaDetalheRepository detalheRepository;

    public IntegracaoContaPdvService(VendaService vendaService,
                                     ContaAbertaService contaService,
                                     SessaoPdv sessaoPdv,
                                     PdvVendaCabecalhoRepository cabecalhoRepository,
                                     PdvVendaDetalheRepository detalheRepository) {
        this.vendaService = vendaService;
        this.contaService = contaService;
        this.sessaoPdv = sessaoPdv;
        this.cabecalhoRepository = cabecalhoRepository;
        this.detalheRepository = detalheRepository;
    }

    // ====================================================================
    //  IMPORTAR — chamado quando operador confirma F8 na tela de busca
    // ====================================================================

    /**
     * Importa todos os itens ATIVOS da conta para o cupom de venda atual.
     *
     * <p>Se não há cupom aberto, abre um novo. Os itens são adicionados
     * via {@link VendaService#adicionarItem} (que já trata snapshot,
     * agrupamento, faixa de preço, histórico).
     *
     * <p>Após adicionar todos os itens, enriquece o PDV_VENDA_DETALHE
     * com o ID original do CONTA_ABERTA_ITEM (rastreabilidade granular)
     * e marca a conta como E (em atendimento).
     *
     * @param contaId id da conta a importar
     * @return cupom resultante (com os itens importados)
     * @throws IllegalStateException se conta não pode ser importada
     */
    @Transactional
    public PdvVendaCabecalho importarParaCupom(Long contaId) {
        // 1. Carrega a conta com itens
        ContaAberta conta = contaService.buscarComItens(contaId)
                .orElseThrow(() -> new IllegalStateException(
                        "Conta #" + contaId + " não encontrada."));

        if (!conta.estaAberta()) {
            throw new IllegalStateException(
                    "Conta #" + contaId + " não está aberta (status: "
                            + conta.getStatus() + ").");
        }

        // 2. Filtra itens ativos
        List<ContaAbertaItem> itensAtivos = new ArrayList<>();
        for (ContaAbertaItem it : conta.getItens()) {
            if (it.getSituacao() == SituacaoItemConta.A) {
                itensAtivos.add(it);
            }
        }

        if (itensAtivos.isEmpty()) {
            throw new IllegalStateException(
                    "Conta #" + contaId + " não possui itens ativos para importar.");
        }

        // 3. Garante cupom aberto no PDV
        if (!sessaoPdv.movimentoEstaAberto()) {
            throw new IllegalStateException(
                    "Não há movimento de caixa aberto. Abra o movimento antes de importar.");
        }

        PdvVendaCabecalho cupom = sessaoPdv.getCupomAtivoOuNulo();
        if (cupom == null) {
            cupom = vendaService.abrirCupomSeNaoExistir(sessaoPdv.getMovimentoAtivo());
            sessaoPdv.setCupomAtivo(cupom);
        }

        // 4. Marca origem + vincula conta no cupom (recarrega do banco
        //    pra evitar entity desatualizado)
        Long cupomId = cupom.getId();
        cupom = cabecalhoRepository.findById(cupomId)
                .orElseThrow(() -> new IllegalStateException("Cupom #" + cupomId + " sumiu."));
        cupom.setOrigemVenda(toOrigemVenda(conta.getTipo()));
        cupom.setContaAberta(conta);
        cupom = cabecalhoRepository.save(cupom);

        // 5. Adiciona itens via VendaService (reusa toda a lógica:
        //    snapshot, agrupamento, faixa, hash, histórico)
        for (ContaAbertaItem itemOrigem : itensAtivos) {
            VendaService.ResultadoAdicaoItem result = vendaService.adicionarItem(
                    cupom, itemOrigem.getProduto(), itemOrigem.getQuantidade());

            // 6. Enriquece o PdvVendaDetalhe com o vínculo ao item original
            PdvVendaDetalhe detalhe = result.getDetalhe();
            if (detalhe.getContaAbertaItem() == null) {
                // Recarrega pra evitar conflito de versão e atualiza só o vínculo
                PdvVendaDetalhe gerenciado = detalheRepository.findById(detalhe.getId())
                        .orElse(null);
                if (gerenciado != null) {
                    gerenciado.setContaAbertaItem(itemOrigem);
                    detalheRepository.save(gerenciado);
                }
            }
        }

        // 7. Muda conta pra E (em atendimento) — bloqueia novos lançamentos no salão
        contaService.marcarEmAtendimento(conta);

        // Atualiza referência na sessão (cupom agora tem origem + contaAberta)
        sessaoPdv.setCupomAtivo(cupom);

        LOGGER.info("Conta #{} ({}) importada para cupom #{} — {} item(ns)",
                conta.getId(), conta.getTipo(), cupom.getId(), itensAtivos.size());
        return cupom;
    }

    // ====================================================================
    //  HOOKS — chamados pelos fluxos existentes do PDV
    // ====================================================================

    /**
     * Chamado quando o cupom é cancelado (F2) ou operador desiste do
     * pagamento. Se o cupom veio de uma conta importada, libera a conta
     * de volta pra A (Aberta) — garçom pode continuar lançando itens.
     *
     * <p>Idempotente: se cupom não tem conta vinculada, não faz nada.
     */
    @Transactional
    public void liberarSeImportada(PdvVendaCabecalho cupom) {
        if (cupom == null) return;
        ContaAberta conta = cupom.getContaAberta();
        if (conta == null) return;
        contaService.liberarConta(conta);
        LOGGER.info("Conta #{} liberada (cupom #{} cancelado)",
                conta.getId(), cupom.getId());
    }

    /**
     * Chamado após VendaService.finalizarVenda. Se o cupom veio de conta
     * importada, marca a conta como F (Fechada) e vincula.
     *
     * <p>Idempotente: se cupom não tem conta vinculada, não faz nada.
     */
    @Transactional
    public void vincularAposFinalizar(PdvVendaCabecalho cupom) {
        if (cupom == null) return;
        ContaAberta conta = cupom.getContaAberta();
        if (conta == null) return;
        contaService.vincularVendaConcluida(conta, cupom.getId(),
                sessaoPdv.getOperadorAtual());
        LOGGER.info("Conta #{} fechada (vinculada à venda #{})",
                conta.getId(), cupom.getId());
    }

    // ====================================================================
    //  Helpers
    // ====================================================================

    private OrigemVenda toOrigemVenda(TipoContaAberta tipo) {
        switch (tipo) {
            case M: return OrigemVenda.M;
            case C: return OrigemVenda.C;
            case D: return OrigemVenda.D;
            default: throw new IllegalArgumentException("Tipo desconhecido: " + tipo);
        }
    }
}