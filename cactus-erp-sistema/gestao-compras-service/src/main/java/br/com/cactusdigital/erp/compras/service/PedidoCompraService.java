package br.com.cactusdigital.erp.compras.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.compras.client.FinanceiroClient;
import br.com.cactusdigital.erp.compras.client.GerarContasPagarClientRequest;
import br.com.cactusdigital.erp.compras.client.ParcelaFinanceiroClientRequest;
import br.com.cactusdigital.erp.compras.dto.GerarPedidoDeCotacaoRequest;
import br.com.cactusdigital.erp.compras.dto.PedidoCompraItemRequest;
import br.com.cactusdigital.erp.compras.dto.PedidoCompraItemResponse;
import br.com.cactusdigital.erp.compras.dto.PedidoCompraRequest;
import br.com.cactusdigital.erp.compras.dto.PedidoCompraResponse;
import br.com.cactusdigital.erp.compras.entity.CotacaoCompra;
import br.com.cactusdigital.erp.compras.entity.FornecedorCotacao;
import br.com.cactusdigital.erp.compras.entity.FornecedorCotacaoItem;
import br.com.cactusdigital.erp.compras.entity.OrigemPedidoCompra;
import br.com.cactusdigital.erp.compras.entity.PedidoCompra;
import br.com.cactusdigital.erp.compras.entity.PedidoCompraItem;
import br.com.cactusdigital.erp.compras.entity.SituacaoPedidoCompra;
import br.com.cactusdigital.erp.compras.exception.ComprasErrorCode;
import br.com.cactusdigital.erp.compras.mapper.ComprasMapper;
import br.com.cactusdigital.erp.compras.repository.PedidoCompraItemRepository;
import br.com.cactusdigital.erp.compras.repository.PedidoCompraRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Máquina de estados do pedido de compra: {@code RASCUNHO} → (enviar) →
 * {@code AGUARDANDO_APROVACAO} (só se {@code valorTotal} excede
 * {@code ConfiguracaoComprasEmpresa.limiteAprovacaoPedido}) ou direto {@code ENVIADO} →
 * (recebimento parcial/total, ver {@code RecebimentoCompraService}) →
 * {@code PARCIALMENTE_RECEBIDO}/{@code RECEBIDO_TOTAL}. {@code CANCELADO} alcançável de
 * qualquer situação anterior a receber algo.
 * <p>
 * {@code aprovadorId}/{@code dataAprovacao} são setados a partir do JWT de quem chama
 * {@link #aprovar} — nunca vêm do corpo da requisição (mais seguro que o
 * {@code aprovadorId} client-supplied do {@code EstoqueMovimentoRequest} no
 * controle-estoque-service; não retrofita o Estoque agora, só não repete o padrão aqui).
 */
@Service
@RequiredArgsConstructor
public class PedidoCompraService {

    private final PedidoCompraRepository repository;
    private final PedidoCompraItemRepository itemRepository;
    private final RequisicaoCompraService requisicaoCompraService;
    private final CotacaoCompraService cotacaoCompraService;
    private final ConfiguracaoComprasEmpresaService configuracaoService;
    private final FinanceiroClient financeiroClient;
    private final ComprasMapper mapper;

    @Transactional(readOnly = true)
    public Page<PedidoCompraResponse> listar(Long fornecedorId, Pageable pageable) {
        Page<PedidoCompra> pagina = fornecedorId != null
                ? repository.findByFornecedorId(fornecedorId, pageable)
                : repository.findAll(pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, PedidoCompra::getId, repository::buscarComItens, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PedidoCompraResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public PedidoCompraResponse criar(PedidoCompraRequest request) {
        PedidoCompra pedido = PedidoCompra.builder()
                .empresaId(request.empresaId())
                .tipo(request.tipo())
                .fornecedorId(request.fornecedorId())
                .colaboradorId(request.colaboradorId())
                .origemTipo(request.origemTipo())
                .origemId(request.origemId())
                .dataPedido(request.dataPedido())
                .dataPrevistaEntrega(request.dataPrevistaEntrega())
                .dataPrevisaoPagamento(request.dataPrevisaoPagamento())
                .localEntrega(request.localEntrega())
                .localCobranca(request.localCobranca())
                .contato(request.contato())
                .tipoFrete(request.tipoFrete())
                .indicadorPagamento(request.indicadorPagamento())
                .quantidadeParcelas(request.quantidadeParcelas())
                .diaPrimeiroVencimento(request.diaPrimeiroVencimento())
                .intervaloEntreParcelas(request.intervaloEntreParcelas())
                .diaFixoParcela(request.diaFixoParcela())
                .valorFrete(request.valorFrete())
                .valorSeguro(request.valorSeguro())
                .valorOutrasDespesas(request.valorOutrasDespesas())
                .situacao(SituacaoPedidoCompra.RASCUNHO)
                .observacao(request.observacao())
                .build();
        pedido = repository.save(pedido);

        marcarOrigemComoPedidoGerada(request.origemTipo(), request.origemId());
        return mapper.toResponse(pedido);
    }

    /** Copia os itens do fornecedor vencedor de uma cotação pra um pedido novo (em
     * RASCUNHO) — a cotação vira FINALIZADA e a requisição de origem vira PEDIDO_GERADO. */
    @Transactional
    public PedidoCompraResponse gerarAPartirDeCotacao(Long cotacaoId, Long fornecedorCotacaoId, GerarPedidoDeCotacaoRequest request) {
        FornecedorCotacao fornecedorCotacao = cotacaoCompraService.buscarFornecedorCotacao(cotacaoId, fornecedorCotacaoId);
        if (fornecedorCotacao.getItens().isEmpty()) {
            throw new BusinessException(ComprasErrorCode.COTACAO_SEM_ITENS);
        }
        CotacaoCompra cotacao = fornecedorCotacao.getCotacao();

        PedidoCompra pedido = PedidoCompra.builder()
                .empresaId(request.empresaId())
                .tipo(request.tipo())
                .fornecedorId(fornecedorCotacao.getFornecedorId())
                .colaboradorId(cotacao.getRequisicao().getColaboradorId())
                .origemTipo(OrigemPedidoCompra.COTACAO)
                .origemId(cotacaoId)
                .situacao(SituacaoPedidoCompra.RASCUNHO)
                .build();

        for (FornecedorCotacaoItem item : fornecedorCotacao.getItens()) {
            pedido.getItens().add(PedidoCompraItem.builder()
                    .pedido(pedido)
                    .produtoId(item.getProdutoId())
                    .quantidade(item.getQuantidade())
                    .valorUnitario(item.getValorUnitario())
                    .taxaDesconto(item.getTaxaDesconto())
                    .valorDesconto(item.getValorDesconto())
                    .valorTotal(item.getValorTotal())
                    .build());
        }
        recalcularTotais(pedido);
        pedido = repository.save(pedido);

        cotacaoCompraService.finalizar(cotacaoId);
        requisicaoCompraService.marcarPedidoGerado(cotacao.getRequisicao().getId());
        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoCompraItemResponse adicionarItem(Long pedidoId, PedidoCompraItemRequest request) {
        PedidoCompra pedido = buscarEntidade(pedidoId);
        exigirSituacao(pedido, SituacaoPedidoCompra.RASCUNHO, ComprasErrorCode.PEDIDO_ITEM_APENAS_EM_RASCUNHO);

        BigDecimal subtotalItem = request.quantidade().multiply(request.valorUnitario());
        BigDecimal descontoItem = calcularValorDesconto(subtotalItem, request.taxaDesconto());

        PedidoCompraItem item = PedidoCompraItem.builder()
                .pedido(pedido)
                .produtoId(request.produtoId())
                .unidadeCompraId(request.unidadeCompraId())
                .fatorConversaoEstoque(request.fatorConversaoEstoque() != null ? request.fatorConversaoEstoque() : BigDecimal.ONE)
                .quantidade(request.quantidade())
                .valorUnitario(request.valorUnitario())
                .taxaDesconto(request.taxaDesconto())
                .valorDesconto(descontoItem)
                .valorTotal(subtotalItem.subtract(descontoItem))
                .build();
        // itemRepository.save(...) explícito, não só cascade+dirty-checking — achado
        // real ao validar o vendas-service ao vivo (Fase 7, mesmo formato de código):
        // com GenerationType.IDENTITY (BaseEntity), o id só é atribuído no INSERT de
        // verdade, que só acontece no flush (fim da transação) quando o pai já é uma
        // entidade gerenciada — sem save explícito, a resposta deste POST viria com
        // item.id=null (o dado grava certo, só a resposta imediata vinha errada).
        item = itemRepository.save(item);
        pedido.getItens().add(item);
        recalcularTotais(pedido);
        return mapper.toResponse(item);
    }

    @Transactional
    public void removerItem(Long pedidoId, Long itemId) {
        PedidoCompra pedido = buscarEntidade(pedidoId);
        exigirSituacao(pedido, SituacaoPedidoCompra.RASCUNHO, ComprasErrorCode.PEDIDO_ITEM_APENAS_EM_RASCUNHO);
        PedidoCompraItem item = buscarItemEntidade(pedidoId, itemId);
        pedido.getItens().remove(item);
        recalcularTotais(pedido);
    }

    @Transactional
    public PedidoCompraResponse enviar(Long id) {
        PedidoCompra pedido = buscarEntidade(id);
        exigirSituacao(pedido, SituacaoPedidoCompra.RASCUNHO, ComprasErrorCode.PEDIDO_SITUACAO_INVALIDA);
        if (pedido.getItens().isEmpty()) {
            throw new BusinessException(ComprasErrorCode.PEDIDO_SEM_ITENS);
        }

        BigDecimal limite = configuracaoService.limiteAprovacaoDaEmpresa(pedido.getEmpresaId());
        boolean exigeAprovacao = limite != null && pedido.getValorTotal().compareTo(limite) > 0;
        pedido.setSituacao(exigeAprovacao ? SituacaoPedidoCompra.AGUARDANDO_APROVACAO : SituacaoPedidoCompra.ENVIADO);
        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoCompraResponse aprovar(Long id, Long aprovadorId) {
        PedidoCompra pedido = buscarEntidade(id);
        exigirSituacao(pedido, SituacaoPedidoCompra.AGUARDANDO_APROVACAO, ComprasErrorCode.PEDIDO_SITUACAO_INVALIDA);
        pedido.setAprovadorId(aprovadorId);
        pedido.setDataAprovacao(Instant.now());
        pedido.setSituacao(SituacaoPedidoCompra.ENVIADO);
        return mapper.toResponse(pedido);
    }

    @Transactional
    public PedidoCompraResponse cancelar(Long id) {
        PedidoCompra pedido = buscarEntidade(id);
        boolean jaRecebeuAlgo = pedido.getSituacao() == SituacaoPedidoCompra.PARCIALMENTE_RECEBIDO
                || pedido.getSituacao() == SituacaoPedidoCompra.RECEBIDO_TOTAL;
        if (jaRecebeuAlgo || pedido.getSituacao() == SituacaoPedidoCompra.CANCELADO) {
            throw new ConflictException(ComprasErrorCode.PEDIDO_SITUACAO_INVALIDA);
        }
        pedido.setSituacao(SituacaoPedidoCompra.CANCELADO);
        return mapper.toResponse(pedido);
    }

    /**
     * Chamado pelo {@code RecebimentoCompraService} depois de gravar um recebimento —
     * recalcula se o pedido virou RECEBIDO_TOTAL ou continua PARCIALMENTE_RECEBIDO (só
     * mutação LOCAL, sem chamada cross-serviço — ver Javadoc da classe pro porquê disso
     * ter mudado). Devolve {@code true} só na transição pra RECEBIDO_TOTAL a primeira vez
     * (situação anterior != RECEBIDO_TOTAL) — o chamador usa esse booleano pra decidir se
     * inclui o passo "gerar-contas-a-pagar" na saga orquestrada
     * ({@code common.saga.SagaOrchestrator}, ver {@code RecebimentoCompraService#registrar}).
     * <p>
     * <b>Achado de refatoração (2026-09-13, piloto de Saga aplicado aqui)</b>: antes, este
     * método chamava {@link #gerarContasPagar} direto — mas isso deixava a chamada
     * cross-serviço FORA de qualquer mecanismo de compensação, o mesmo gap que motivou o
     * piloto de saga em {@code vendas-service.confirmar}. Agora {@link #gerarContasPagar}
     * só é chamado pelo passo da saga (executado no momento certo, com compensação real
     * via {@code FinanceiroClient#estornarContasDeCompra} se um passo futuro vier depois
     * dele e falhar).
     */
    @Transactional
    public boolean verificarTransicaoParaRecebidoTotal(Long pedidoId) {
        PedidoCompra pedido = buscarEntidade(pedidoId);
        boolean tudoRecebido = pedido.getItens().stream()
                .allMatch(item -> item.getQuantidadeRecebida().compareTo(item.getQuantidade()) >= 0);
        boolean transicaoParaRecebidoTotal = tudoRecebido && pedido.getSituacao() != SituacaoPedidoCompra.RECEBIDO_TOTAL;
        pedido.setSituacao(tudoRecebido ? SituacaoPedidoCompra.RECEBIDO_TOTAL : SituacaoPedidoCompra.PARCIALMENTE_RECEBIDO);
        return transicaoParaRecebidoTotal;
    }

    /**
     * Gera 1 {@code ContaPagar} por parcela no {@code financeiro-service} — fecha o gap
     * registrado desde a criação daquele módulo ("{@code ContaPagar} só {@code MANUAL}").
     * Usa {@code quantidadeParcelas}/{@code diaPrimeiroVencimento}/
     * {@code intervaloEntreParcelas}/{@code diaFixoParcela} do próprio pedido quando
     * informados (ver {@link #calcularVencimentoParcela}); sem eles, gera 1 parcela única.
     * Valor dividido IGUALMENTE entre as parcelas (última absorve o resto da divisão) —
     * mesmo critério já usado em {@code vendas-service.VendaService#gerarContasReceber},
     * mesmo motivo (sem um campo de peso de distribuição confiável por parcela).
     */
    void gerarContasPagar(PedidoCompra pedido) {
        LocalDate dataBase = pedido.getDataPedido() != null ? pedido.getDataPedido() : LocalDate.now();
        int quantidade = pedido.getQuantidadeParcelas() != null && pedido.getQuantidadeParcelas() > 0
                ? pedido.getQuantidadeParcelas() : 1;

        BigDecimal valorParcela = pedido.getValorTotal().divide(BigDecimal.valueOf(quantidade), 6, RoundingMode.DOWN);
        BigDecimal somaParcelas = valorParcela.multiply(BigDecimal.valueOf(quantidade));
        BigDecimal ajusteUltimaParcela = pedido.getValorTotal().subtract(somaParcelas);

        List<ParcelaFinanceiroClientRequest> parcelas = new ArrayList<>();
        for (int i = 1; i <= quantidade; i++) {
            BigDecimal valor = (i == quantidade) ? valorParcela.add(ajusteUltimaParcela) : valorParcela;
            parcelas.add(new ParcelaFinanceiroClientRequest(i, calcularVencimentoParcela(pedido, dataBase, i), valor));
        }

        financeiroClient.gerarContasPagar(new GerarContasPagarClientRequest(
                pedido.getId(), pedido.getEmpresaId(), pedido.getFornecedorId(), dataBase, parcelas));
    }

    /**
     * Sem {@code quantidadeParcelas} informado: parcela única, vencimento em
     * {@code dataPrevisaoPagamento} quando existir, senão {@code dataBase + 30 dias}.
     * Com parcelamento: {@code dataBase + diaPrimeiroVencimento + (n-1)*intervaloEntreParcelas}
     * dias (defaults 30/30 quando não informados), com {@code diaFixoParcela} ajustando o
     * dia-do-mês do resultado quando presente (clamped ao último dia do mês, pra meses
     * mais curtos que o dia fixo configurado).
     */
    private LocalDate calcularVencimentoParcela(PedidoCompra pedido, LocalDate dataBase, int numeroParcela) {
        if (pedido.getQuantidadeParcelas() == null || pedido.getQuantidadeParcelas() <= 0) {
            return pedido.getDataPrevisaoPagamento() != null ? pedido.getDataPrevisaoPagamento() : dataBase.plusDays(30);
        }
        int diaPrimeiroVencimento = pedido.getDiaPrimeiroVencimento() != null ? pedido.getDiaPrimeiroVencimento() : 30;
        int intervalo = pedido.getIntervaloEntreParcelas() != null ? pedido.getIntervaloEntreParcelas() : 30;
        LocalDate vencimento = dataBase.plusDays(diaPrimeiroVencimento + (long) (numeroParcela - 1) * intervalo);
        if (pedido.getDiaFixoParcela() != null) {
            int diaFixo = Math.min(pedido.getDiaFixoParcela(), vencimento.lengthOfMonth());
            vencimento = vencimento.withDayOfMonth(diaFixo);
        }
        return vencimento;
    }

    private void marcarOrigemComoPedidoGerada(OrigemPedidoCompra origemTipo, Long origemId) {
        if (origemId == null) {
            return;
        }
        switch (origemTipo) {
            case REQUISICAO -> requisicaoCompraService.marcarPedidoGerado(origemId);
            case COTACAO -> requisicaoCompraService.marcarPedidoGerado(
                    cotacaoCompraService.buscarEntidade(origemId).getRequisicao().getId());
            case AVULSO -> { /* sem origem a marcar */ }
        }
    }

    private void recalcularTotais(PedidoCompra pedido) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal desconto = BigDecimal.ZERO;
        for (PedidoCompraItem item : pedido.getItens()) {
            subtotal = subtotal.add(item.getQuantidade().multiply(item.getValorUnitario()));
            desconto = desconto.add(item.getValorDesconto());
        }
        BigDecimal frete = coalesce(pedido.getValorFrete());
        BigDecimal seguro = coalesce(pedido.getValorSeguro());
        BigDecimal outrasDespesas = coalesce(pedido.getValorOutrasDespesas());

        pedido.setValorSubtotal(subtotal);
        pedido.setValorDesconto(desconto);
        pedido.setValorTotal(subtotal.subtract(desconto).add(frete).add(seguro).add(outrasDespesas));
    }

    private BigDecimal coalesce(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    private BigDecimal calcularValorDesconto(BigDecimal valorSubtotal, BigDecimal taxaDesconto) {
        if (taxaDesconto == null || taxaDesconto.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return valorSubtotal.multiply(taxaDesconto).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
    }

    private void exigirSituacao(PedidoCompra pedido, SituacaoPedidoCompra esperada, ComprasErrorCode erro) {
        if (pedido.getSituacao() != esperada) {
            throw new ConflictException(erro);
        }
    }

    PedidoCompraItem buscarItemEntidade(Long pedidoId, Long itemId) {
        return itemRepository.findById(itemId)
                .filter(candidato -> candidato.getPedido().getId().equals(pedidoId))
                .orElseThrow(() -> NotFoundException.of("Item de pedido de compra", itemId));
    }

    PedidoCompra buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Pedido de compra", id));
    }
}
