package br.com.cactusdigital.erp.vendas.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.common.saga.SagaOrchestrator;
import br.com.cactusdigital.erp.common.saga.SagaStep;
import br.com.cactusdigital.erp.vendas.client.EstoqueClient;
import br.com.cactusdigital.erp.vendas.client.EstoqueMovimentoClientRequest;
import br.com.cactusdigital.erp.vendas.client.FinanceiroClient;
import br.com.cactusdigital.erp.vendas.client.GerarContasReceberClientRequest;
import br.com.cactusdigital.erp.vendas.client.ParcelaFinanceiroClientRequest;
import br.com.cactusdigital.erp.vendas.dto.GerarVendaDeOrcamentoRequest;
import br.com.cactusdigital.erp.vendas.dto.VendaFreteRequest;
import br.com.cactusdigital.erp.vendas.dto.VendaFreteResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaItemRequest;
import br.com.cactusdigital.erp.vendas.dto.VendaItemResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaRequest;
import br.com.cactusdigital.erp.vendas.dto.VendaResponse;
import br.com.cactusdigital.erp.vendas.entity.ComissaoObjetivo;
import br.com.cactusdigital.erp.vendas.entity.NotaFiscalTipo;
import br.com.cactusdigital.erp.vendas.entity.SituacaoOrcamento;
import br.com.cactusdigital.erp.vendas.entity.SituacaoVenda;
import br.com.cactusdigital.erp.vendas.entity.TipoPagamentoVenda;
import br.com.cactusdigital.erp.vendas.entity.VendaCabecalho;
import br.com.cactusdigital.erp.vendas.entity.VendaCondicoesPagamento;
import br.com.cactusdigital.erp.vendas.entity.VendaCondicoesParcela;
import br.com.cactusdigital.erp.vendas.entity.VendaComissao;
import br.com.cactusdigital.erp.vendas.entity.VendaFrete;
import br.com.cactusdigital.erp.vendas.entity.VendaItem;
import br.com.cactusdigital.erp.vendas.entity.VendaOrcamento;
import br.com.cactusdigital.erp.vendas.entity.VendaOrcamentoItem;
import br.com.cactusdigital.erp.vendas.entity.Vendedor;
import br.com.cactusdigital.erp.vendas.exception.VendasErrorCode;
import br.com.cactusdigital.erp.vendas.mapper.VendasMapper;
import br.com.cactusdigital.erp.vendas.repository.ComissaoObjetivoRepository;
import br.com.cactusdigital.erp.vendas.repository.VendaCabecalhoRepository;
import br.com.cactusdigital.erp.vendas.repository.VendaComissaoRepository;
import br.com.cactusdigital.erp.vendas.repository.VendaFreteRepository;
import br.com.cactusdigital.erp.vendas.repository.VendaItemRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Máquina de estados da venda: {@code RASCUNHO} → (confirmar) → {@code CONFIRMADA}
 * (gera {@link VendaComissao} e chama {@link EstoqueClient} — {@code SAIDA} por item no
 * controle-estoque-service, mesmo padrão exato de
 * {@code RecebimentoCompraService}/{@code EstoqueClient} da Fase 3) → {@code CANCELADA}.
 * <p>
 * <b>Cancelamento só permitido em RASCUNHO</b> — depois de confirmada, o estoque e a
 * comissão já foram gerados de verdade no controle-estoque-service; reverter isso exige
 * um fluxo de devolução com o mesmo rigor de auditoria da venda original, que só faz
 * sentido construir quando a Fase 8 (NF-e/emissão fiscal) existir — uma devolução real
 * também precisa cancelar/complementar o documento fiscal, não é só um estorno de saldo.
 */
@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaCabecalhoRepository repository;
    private final VendaItemRepository itemRepository;
    private final VendaFreteRepository freteRepository;
    private final VendaComissaoRepository comissaoRepository;
    private final ComissaoObjetivoRepository comissaoObjetivoRepository;
    private final VendaCondicoesPagamentoService condicoesPagamentoService;
    private final NotaFiscalTipoService notaFiscalTipoService;
    private final VendedorService vendedorService;
    private final VendaOrcamentoService orcamentoService;
    private final EstoqueClient estoqueClient;
    private final FinanceiroClient financeiroClient;
    private final SagaOrchestrator sagaOrchestrator;
    private final SagaAuditoriaService sagaAuditoriaService;
    private final VendasMapper mapper;

    @Transactional(readOnly = true)
    public Page<VendaResponse> listar(Long clienteId, Pageable pageable) {
        Page<VendaCabecalho> pagina = clienteId != null
                ? repository.findByClienteId(clienteId, pageable)
                : repository.findAll(pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, VendaCabecalho::getId, repository::buscarComItens, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public VendaResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public VendaResponse criar(VendaRequest request) {
        VendaCabecalho venda = montarCabecalho(request);
        return mapper.toResponse(repository.save(venda));
    }

    /** Copia os itens do orçamento pra uma venda nova (em RASCUNHO) — o orçamento vira CONVERTIDO. */
    @Transactional
    public VendaResponse gerarAPartirDeOrcamento(Long orcamentoId, GerarVendaDeOrcamentoRequest request) {
        VendaOrcamento orcamento = orcamentoService.buscarEntidade(orcamentoId);
        if (orcamento.getSituacao() != SituacaoOrcamento.APROVADO) {
            throw new ConflictException(VendasErrorCode.ORCAMENTO_SITUACAO_INVALIDA);
        }
        if (orcamento.getItens().isEmpty()) {
            throw new BusinessException(VendasErrorCode.ORCAMENTO_SEM_ITENS);
        }

        NotaFiscalTipo notaFiscalTipo = notaFiscalTipoService.buscarEntidade(request.notaFiscalTipoId());
        VendaCabecalho venda = VendaCabecalho.builder()
                .empresaId(request.empresaId())
                .orcamento(orcamento)
                .condicoesPagamento(orcamento.getCondicoesPagamento())
                .notaFiscalTipo(notaFiscalTipo)
                .vendedor(orcamento.getVendedor())
                .clienteId(orcamento.getClienteId())
                .transportadoraId(orcamento.getTransportadoraId())
                .formaPagamentoId(request.formaPagamentoId())
                .dataVenda(request.dataVenda() != null ? request.dataVenda() : LocalDate.now())
                .dataSaida(request.dataSaida())
                .horaSaida(request.horaSaida())
                .numeroFatura(request.numeroFatura())
                .taxaComissao(orcamento.getTaxaComissao())
                .taxaDesconto(orcamento.getTaxaDesconto())
                .tipoFrete(orcamento.getTipoFrete())
                .valorFrete(orcamento.getValorFrete())
                .observacao(orcamento.getObservacao())
                .situacao(SituacaoVenda.RASCUNHO)
                .build();

        // deposito_id não vem do orçamento (que não conhece estoque) — precisa ser
        // definido item a item depois, via PUT/PATCH de item ou recriação; nesta fase o
        // depósito default é resolvido só quando o item é adicionado avulso (ver
        // adicionarItem). Itens copiados do orçamento entram SEM depósito definido e
        // precisam ser complementados antes de confirmar — ver VENDA_SEM_ITENS/validação
        // de confirmar().
        for (VendaOrcamentoItem origem : orcamento.getItens()) {
            venda.getItens().add(VendaItem.builder()
                    .venda(venda)
                    .produtoId(origem.getProdutoId())
                    .depositoId(null)
                    .quantidade(origem.getQuantidade())
                    .valorUnitario(origem.getValorUnitario())
                    .taxaDesconto(origem.getTaxaDesconto())
                    .valorSubtotal(origem.getValorSubtotal())
                    .valorDesconto(origem.getValorDesconto())
                    .valorTotal(origem.getValorTotal())
                    .build());
        }
        recalcularTotais(venda);
        venda = repository.save(venda);

        orcamentoService.marcarConvertido(orcamentoId);
        return mapper.toResponse(venda);
    }

    @Transactional
    public VendaItemResponse adicionarItem(Long vendaId, VendaItemRequest request) {
        VendaCabecalho venda = buscarEntidade(vendaId);
        exigirSituacao(venda, SituacaoVenda.RASCUNHO, VendasErrorCode.VENDA_ITEM_APENAS_EM_RASCUNHO);

        BigDecimal subtotalItem = request.quantidade().multiply(request.valorUnitario());
        BigDecimal descontoItem = calcularValorDesconto(subtotalItem, request.taxaDesconto());

        VendaItem item = VendaItem.builder()
                .venda(venda)
                .produtoId(request.produtoId())
                .depositoId(request.depositoId())
                .quantidade(request.quantidade())
                .valorUnitario(request.valorUnitario())
                .taxaDesconto(request.taxaDesconto())
                .valorSubtotal(subtotalItem)
                .valorDesconto(descontoItem)
                .valorTotal(subtotalItem.subtract(descontoItem))
                .build();
        // itemRepository.save(...) explícito, não só cascade — mesmo achado real de
        // VendaOrcamentoService#adicionarItem (GenerationType.IDENTITY só atribui o id
        // no INSERT de verdade, cascade+dirty-checking deixaria null até o commit).
        item = itemRepository.save(item);
        venda.getItens().add(item);
        recalcularTotais(venda);
        return mapper.toResponse(item);
    }

    @Transactional
    public void removerItem(Long vendaId, Long itemId) {
        VendaCabecalho venda = buscarEntidade(vendaId);
        exigirSituacao(venda, SituacaoVenda.RASCUNHO, VendasErrorCode.VENDA_ITEM_APENAS_EM_RASCUNHO);
        VendaItem item = buscarItemEntidade(vendaId, itemId);
        venda.getItens().remove(item);
        recalcularTotais(venda);
    }

    /**
     * Substitui os dados de um item já existente — principal uso real: definir o
     * {@code depositoId} de um item copiado de {@link VendaOrcamento} (que não conhece
     * estoque/depósito, ver {@link #gerarAPartirDeOrcamento}), sem precisar
     * remover+readicionar. Só permitido em RASCUNHO.
     */
    @Transactional
    public VendaItemResponse atualizarItem(Long vendaId, Long itemId, VendaItemRequest request) {
        VendaCabecalho venda = buscarEntidade(vendaId);
        exigirSituacao(venda, SituacaoVenda.RASCUNHO, VendasErrorCode.VENDA_ITEM_APENAS_EM_RASCUNHO);
        VendaItem item = buscarItemEntidade(vendaId, itemId);

        BigDecimal subtotalItem = request.quantidade().multiply(request.valorUnitario());
        BigDecimal descontoItem = calcularValorDesconto(subtotalItem, request.taxaDesconto());

        item.setProdutoId(request.produtoId());
        item.setDepositoId(request.depositoId());
        item.setQuantidade(request.quantidade());
        item.setValorUnitario(request.valorUnitario());
        item.setTaxaDesconto(request.taxaDesconto());
        item.setValorSubtotal(subtotalItem);
        item.setValorDesconto(descontoItem);
        item.setValorTotal(subtotalItem.subtract(descontoItem));

        recalcularTotais(venda);
        return mapper.toResponse(item);
    }

    @Transactional
    public VendaFreteResponse definirFrete(Long vendaId, VendaFreteRequest request) {
        VendaCabecalho venda = buscarEntidade(vendaId);
        exigirSituacao(venda, SituacaoVenda.RASCUNHO, VendasErrorCode.VENDA_ITEM_APENAS_EM_RASCUNHO);

        VendaFrete frete = freteRepository.findByVendaId(vendaId).orElseGet(() -> VendaFrete.builder().venda(venda).build());
        frete.setTransportadoraId(request.transportadoraId());
        frete.setConhecimento(request.conhecimento());
        frete.setResponsavel(request.responsavel());
        frete.setPlaca(request.placa());
        frete.setUfPlaca(request.ufPlaca());
        frete.setSeloFiscal(request.seloFiscal());
        frete.setQuantidadeVolume(request.quantidadeVolume());
        frete.setMarcaVolume(request.marcaVolume());
        frete.setEspecieVolume(request.especieVolume());
        frete.setPesoBruto(request.pesoBruto());
        frete.setPesoLiquido(request.pesoLiquido());
        return mapper.toResponse(freteRepository.save(frete));
    }

    @Transactional(readOnly = true)
    public VendaFreteResponse buscarFrete(Long vendaId) {
        return freteRepository.findByVendaId(vendaId)
                .map(mapper::toResponse)
                .orElseThrow(() -> NotFoundException.of("Frete da venda", vendaId));
    }

    /**
     * Confirma a venda: gera {@link VendaComissao} (taxa do {@link Vendedor} ou, se
     * existir {@link ComissaoObjetivo} pro produto no perfil do vendedor, a taxa/valor
     * específico) e orquestra, via {@link SagaOrchestrator}, o débito de estoque
     * ({@link EstoqueClient#registrarSaida}, um passo por item) seguido da geração de
     * contas a receber ({@link FinanceiroClient#gerarContasReceber}).
     * <p>
     * <b>Saga orquestrada (piloto deste padrão no monorepo)</b> — se um passo falhar, os
     * passos JÁ executados com sucesso são compensados em ordem reversa
     * ({@link EstoqueClient#registrarEntrada} desfaz cada {@code SAIDA} já debitada) antes
     * da exceção original ser relançada; toda a execução é auditada em
     * {@code SagaExecucao}/{@code SagaExecucaoPasso} (via {@link VendaSagaAuditor}),
     * consultável em {@code GET /api/v1/sagas}. Ver Javadoc de {@link SagaOrchestrator}
     * pro que isso garante e pro que continua sendo uma limitação aceita (compensação é
     * best-effort, não uma transação distribuída de verdade). {@code gerarComissao} fica
     * FORA da saga, de propósito — é escrita local (mesmo banco/transação da própria
     * {@code VendaCabecalho}), já coberta pelo rollback natural do {@code @Transactional}
     * se algo falhar antes dela, sem precisar de compensação cross-serviço.
     */
    @Transactional
    public VendaResponse confirmar(Long id) {
        VendaCabecalho venda = buscarEntidade(id);
        exigirSituacao(venda, SituacaoVenda.RASCUNHO, VendasErrorCode.VENDA_SITUACAO_INVALIDA);
        if (venda.getItens().isEmpty()) {
            throw new BusinessException(VendasErrorCode.VENDA_SEM_ITENS);
        }
        for (VendaItem item : venda.getItens()) {
            if (item.getDepositoId() == null) {
                throw new BusinessException(VendasErrorCode.VENDA_ITEM_SEM_DEPOSITO,
                        "Item de produto " + item.getProdutoId() + " sem depósito definido — não é possível confirmar a venda");
            }
        }

        gerarComissao(venda);
        sagaOrchestrator.executar("CONFIRMAR_VENDA", venda, construirPassosConfirmacao(venda),
                new VendaSagaAuditor(sagaAuditoriaService));

        venda.setSituacao(SituacaoVenda.CONFIRMADA);
        return mapper.toResponse(venda);
    }

    private List<SagaStep<VendaCabecalho>> construirPassosConfirmacao(VendaCabecalho venda) {
        List<SagaStep<VendaCabecalho>> passos = new ArrayList<>();
        for (VendaItem item : venda.getItens()) {
            passos.add(new SagaStep<VendaCabecalho>() {
                @Override
                public String nome() {
                    return "debitar-estoque-item-" + item.getId();
                }

                @Override
                public void executar(VendaCabecalho v) {
                    estoqueClient.registrarSaida(new EstoqueMovimentoClientRequest(
                            item.getProdutoId(), item.getDepositoId(), null, null,
                            "SAIDA", null, item.getQuantidade(), item.getValorUnitario(),
                            "VENDA", v.getId(), null, "Venda #" + v.getId()));
                }

                @Override
                public void compensar(VendaCabecalho v) {
                    estoqueClient.registrarEntrada(new EstoqueMovimentoClientRequest(
                            item.getProdutoId(), item.getDepositoId(), null, null,
                            "ENTRADA", null, item.getQuantidade(), item.getValorUnitario(),
                            "ESTORNO_VENDA", v.getId(), null,
                            "Estorno da venda #" + v.getId() + " (compensação de saga)"));
                }
            });
        }
        passos.add(new SagaStep<VendaCabecalho>() {
            @Override
            public String nome() {
                return "gerar-contas-a-receber";
            }

            @Override
            public void executar(VendaCabecalho v) {
                gerarContasReceber(v);
            }

            @Override
            public void compensar(VendaCabecalho v) {
                financeiroClient.estornarContasDeVenda(v.getId());
            }
        });
        return passos;
    }

    /**
     * Gera 1 {@code ContaReceber} por parcela no {@code financeiro-service} — fecha o
     * gap registrado desde a criação desta fase (condição de pagamento/parcelas nunca
     * geravam lançamento financeiro de verdade). Divide {@code valorTotal}
     * IGUALMENTE entre as parcelas (ver Javadoc de {@link VendaCondicoesParcela} pro
     * porquê de não usar {@code taxa} como peso de distribuição) — a última parcela
     * absorve o resto da divisão pra garantir que a soma bata exatamente com
     * {@code valorTotal}, sem sobra/falta por arredondamento.
     */
    private void gerarContasReceber(VendaCabecalho venda) {
        List<VendaCondicoesParcela> parcelas = venda.getCondicoesPagamento().getParcelas();
        int quantidade = parcelas.size();
        BigDecimal valorParcela = venda.getValorTotal().divide(BigDecimal.valueOf(quantidade), 6, RoundingMode.DOWN);
        BigDecimal somaParcelas = valorParcela.multiply(BigDecimal.valueOf(quantidade));
        BigDecimal ajusteUltimaParcela = venda.getValorTotal().subtract(somaParcelas);

        List<ParcelaFinanceiroClientRequest> parcelasRequest = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            VendaCondicoesParcela parcela = parcelas.get(i);
            BigDecimal valor = (i == quantidade - 1) ? valorParcela.add(ajusteUltimaParcela) : valorParcela;
            LocalDate dataVencimento = venda.getDataVenda().plusDays(parcela.getDias() != null ? parcela.getDias() : 0);
            parcelasRequest.add(new ParcelaFinanceiroClientRequest(parcela.getParcela(), dataVencimento, valor));
        }

        financeiroClient.gerarContasReceber(new GerarContasReceberClientRequest(
                venda.getId(), venda.getEmpresaId(), venda.getClienteId(), venda.getDataVenda(), parcelasRequest));
    }

    @Transactional
    public VendaResponse cancelar(Long id) {
        VendaCabecalho venda = buscarEntidade(id);
        exigirSituacao(venda, SituacaoVenda.RASCUNHO, VendasErrorCode.VENDA_CANCELAMENTO_APENAS_EM_RASCUNHO);
        venda.setSituacao(SituacaoVenda.CANCELADA);
        return mapper.toResponse(venda);
    }

    private void gerarComissao(VendaCabecalho venda) {
        Vendedor vendedor = venda.getVendedor();
        BigDecimal valorComissaoTotal = BigDecimal.ZERO;
        Long comissaoPerfilId = vendedor.getComissaoPerfil() != null ? vendedor.getComissaoPerfil().getId() : null;

        for (VendaItem item : venda.getItens()) {
            BigDecimal valorItem = item.getValorTotal();
            ComissaoObjetivo objetivo = comissaoPerfilId != null
                    ? comissaoObjetivoRepository.findByComissaoPerfilIdAndProdutoId(comissaoPerfilId, item.getProdutoId()).orElse(null)
                    : null;

            BigDecimal comissaoItem;
            if (objetivo != null && objetivo.getTaxaPagamento() != null) {
                comissaoItem = valorItem.multiply(objetivo.getTaxaPagamento()).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
            } else if (objetivo != null && objetivo.getValorPagamento() != null) {
                comissaoItem = objetivo.getValorPagamento().multiply(item.getQuantidade());
            } else if (vendedor.getComissaoPercentual() != null) {
                comissaoItem = valorItem.multiply(vendedor.getComissaoPercentual()).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
            } else {
                comissaoItem = BigDecimal.ZERO;
            }
            valorComissaoTotal = valorComissaoTotal.add(comissaoItem);
        }

        venda.setValorComissao(valorComissaoTotal);

        if (valorComissaoTotal.signum() > 0) {
            VendaComissao comissao = VendaComissao.builder()
                    .venda(venda)
                    .vendedor(vendedor)
                    .valorVenda(venda.getValorTotal())
                    .tipoContabil(TipoPagamentoVenda.VISTA)
                    .valorComissao(valorComissaoTotal)
                    .dataLancamento(LocalDate.now())
                    .build();
            comissaoRepository.save(comissao);
        }
    }

    private VendaCabecalho montarCabecalho(VendaRequest request) {
        VendaCondicoesPagamento condicoes = condicoesPagamentoService.buscarEntidade(request.condicoesPagamentoId());
        NotaFiscalTipo notaFiscalTipo = notaFiscalTipoService.buscarEntidade(request.notaFiscalTipoId());
        Vendedor vendedor = vendedorService.buscarEntidade(request.vendedorId());

        return VendaCabecalho.builder()
                .empresaId(request.empresaId())
                .condicoesPagamento(condicoes)
                .notaFiscalTipo(notaFiscalTipo)
                .vendedor(vendedor)
                .clienteId(request.clienteId())
                .transportadoraId(request.transportadoraId())
                .formaPagamentoId(request.formaPagamentoId())
                .dataVenda(request.dataVenda() != null ? request.dataVenda() : LocalDate.now())
                .dataSaida(request.dataSaida())
                .horaSaida(request.horaSaida())
                .numeroFatura(request.numeroFatura())
                .localEntrega(request.localEntrega())
                .localCobranca(request.localCobranca())
                .tipoFrete(request.tipoFrete())
                .taxaComissao(request.taxaComissao())
                .taxaDesconto(request.taxaDesconto())
                .valorFrete(request.valorFrete())
                .valorSeguro(request.valorSeguro())
                .diaFixoParcela(request.diaFixoParcela())
                .observacao(request.observacao())
                .situacao(SituacaoVenda.RASCUNHO)
                .build();
    }

    private void recalcularTotais(VendaCabecalho venda) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal desconto = BigDecimal.ZERO;
        for (VendaItem item : venda.getItens()) {
            subtotal = subtotal.add(item.getQuantidade().multiply(item.getValorUnitario()));
            desconto = desconto.add(coalesce(item.getValorDesconto()));
        }
        BigDecimal frete = coalesce(venda.getValorFrete());
        BigDecimal seguro = coalesce(venda.getValorSeguro());

        venda.setValorSubtotal(subtotal);
        venda.setValorDesconto(desconto);
        venda.setValorTotal(subtotal.subtract(desconto).add(frete).add(seguro));
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

    private void exigirSituacao(VendaCabecalho venda, SituacaoVenda esperada, VendasErrorCode erro) {
        if (venda.getSituacao() != esperada) {
            throw new ConflictException(erro);
        }
    }

    VendaItem buscarItemEntidade(Long vendaId, Long itemId) {
        return itemRepository.findById(itemId)
                .filter(candidato -> candidato.getVenda().getId().equals(vendaId))
                .orElseThrow(() -> NotFoundException.of("Item de venda", itemId));
    }

    VendaCabecalho buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Venda", id));
    }
}
