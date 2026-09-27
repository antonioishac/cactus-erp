package br.com.cactusdigital.erp.financeiro.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.financeiro.dto.AtualizarClassificacaoTituloRequest;
import br.com.cactusdigital.erp.financeiro.dto.BaixaRequest;
import br.com.cactusdigital.erp.financeiro.dto.CancelarTituloRequest;
import br.com.cactusdigital.erp.financeiro.dto.ContaPagarRequest;
import br.com.cactusdigital.erp.financeiro.dto.ContaPagarResponse;
import br.com.cactusdigital.erp.financeiro.dto.EstornarBaixaRequest;
import br.com.cactusdigital.erp.financeiro.dto.GerarContaPagarParceladaRequest;
import br.com.cactusdigital.erp.financeiro.dto.GerarContasPagarDeCompraRequest;
import br.com.cactusdigital.erp.financeiro.dto.NovaParcelaRenegociacaoRequest;
import br.com.cactusdigital.erp.financeiro.dto.RenegociarTituloRequest;
import br.com.cactusdigital.erp.financeiro.entity.BaixaContaPagar;
import br.com.cactusdigital.erp.financeiro.entity.ContaPagar;
import br.com.cactusdigital.erp.financeiro.entity.OrigemContaPagar;
import br.com.cactusdigital.erp.financeiro.entity.OrigemMovimentoCaixa;
import br.com.cactusdigital.erp.financeiro.entity.SituacaoTitulo;
import br.com.cactusdigital.erp.financeiro.entity.TipoMovimentoCaixa;
import br.com.cactusdigital.erp.financeiro.exception.FinanceiroErrorCode;
import br.com.cactusdigital.erp.financeiro.mapper.FinanceiroMapper;
import br.com.cactusdigital.erp.financeiro.repository.BaixaContaPagarRepository;
import br.com.cactusdigital.erp.financeiro.repository.ContaPagarRepository;
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
 * Contas a Pagar — mesmo desenho de {@link ContaReceberService}, espelhado pro lado de
 * fornecedor. {@link #gerarDeCompra} é chamado pelo {@code gestao-compras-service} quando
 * um {@code PedidoCompra} vira {@code RECEBIDO_TOTAL} (novo client cross-serviço, mesmo
 * padrão de {@code EstoqueClient}) — idempotente por {@code pedidoCompraId}
 * ({@code origemTipo=COMPRA}, {@code origemId=pedidoCompraId}): uma segunda chamada pro
 * mesmo pedido é rejeitada ({@link FinanceiroErrorCode#COMPRA_JA_GEROU_CONTAS_PAGAR}), não
 * gera duplicata. {@code MANUAL} continua sendo o único caminho de criação direta pela
 * API (ver {@link #criar}).
 * <p>
 * Desde 2026-09-06 toda {@link #registrarBaixa} gera um {@code MovimentoCaixaBanco} real
 * ({@code SAIDA}) via {@link MovimentoCaixaBancoService} — ver Javadoc de {@code BaixaContaPagar}.
 */
@Service
@RequiredArgsConstructor
public class ContaPagarService {

    private final ContaPagarRepository repository;
    private final BaixaContaPagarRepository baixaRepository;
    private final ConfiguracaoFinanceiroEmpresaService configuracaoService;
    private final MovimentoCaixaBancoService movimentoService;
    private final FinanceiroMapper mapper;

    @Transactional(readOnly = true)
    public Page<ContaPagarResponse> listar(Long empresaId, Long fornecedorId, Pageable pageable) {
        Page<ContaPagar> pagina;
        if (fornecedorId != null) {
            pagina = repository.findByFornecedorId(fornecedorId, pageable);
        } else if (empresaId != null) {
            pagina = repository.findByEmpresaId(empresaId, pageable);
        } else {
            pagina = repository.findAll(pageable);
        }
        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, ContaPagar::getId, repository::buscarComBaixas, this::mapComJurosMulta);
    }

    @Transactional(readOnly = true)
    public ContaPagarResponse buscarPorId(Long id) {
        return mapComJurosMulta(buscarEntidade(id));
    }

    @Transactional
    public ContaPagarResponse criar(ContaPagarRequest request) {
        ContaPagar conta = ContaPagar.builder()
                .empresaId(request.empresaId())
                .fornecedorId(request.fornecedorId())
                .origemTipo(OrigemContaPagar.MANUAL)
                .naturezaFinanceiraId(request.naturezaFinanceiraId())
                .tipoDocumentoId(request.tipoDocumentoId())
                .contaBancariaId(request.contaBancariaId())
                .numeroDocumento(request.numeroDocumento())
                .descricao(request.descricao())
                .dataEmissao(request.dataEmissao())
                .dataVencimento(request.dataVencimento())
                .descontoAte(request.descontoAte())
                .percentualDesconto(request.percentualDesconto())
                .percentualMultaAtraso(request.percentualMultaAtraso())
                .percentualJurosDiaAtraso(request.percentualJurosDiaAtraso())
                .valorOriginal(request.valorOriginal())
                .valorSaldo(request.valorOriginal())
                .build();
        return mapComJurosMulta(repository.save(conta));
    }

    /** Espelho de {@code ContaReceberService#gerarParcelado} — ver Javadoc lá. */
    @Transactional
    public List<ContaPagarResponse> gerarParcelado(GerarContaPagarParceladaRequest request) {
        int quantidade = request.quantidadeParcelas();
        BigDecimal valorPorParcela = request.valorTotal().divide(BigDecimal.valueOf(quantidade), 6, RoundingMode.DOWN);
        BigDecimal valorUltimaParcela = request.valorTotal().subtract(valorPorParcela.multiply(BigDecimal.valueOf(quantidade - 1)));

        List<ContaPagar> criadas = new ArrayList<>();
        Long loteId = null;
        for (int i = 0; i < quantidade; i++) {
            int numeroParcela = i + 1;
            BigDecimal valorParcela = numeroParcela == quantidade ? valorUltimaParcela : valorPorParcela;
            LocalDate vencimento = request.primeiroVencimento().plusDays((long) request.intervaloEntreParcelasDias() * i);
            ContaPagar conta = ContaPagar.builder()
                    .empresaId(request.empresaId())
                    .fornecedorId(request.fornecedorId())
                    .origemTipo(OrigemContaPagar.MANUAL)
                    .naturezaFinanceiraId(request.naturezaFinanceiraId())
                    .tipoDocumentoId(request.tipoDocumentoId())
                    .contaBancariaId(request.contaBancariaId())
                    .numeroDocumento(numeroDocumentoParcela(request.numeroDocumento(), numeroParcela, quantidade))
                    .descricao(request.descricao())
                    .dataEmissao(request.dataEmissao())
                    .dataVencimento(vencimento)
                    .descontoAte(request.descontoAte())
                    .percentualDesconto(request.percentualDesconto())
                    .percentualMultaAtraso(request.percentualMultaAtraso())
                    .percentualJurosDiaAtraso(request.percentualJurosDiaAtraso())
                    .valorOriginal(valorParcela)
                    .valorSaldo(valorParcela)
                    .build();
            conta = repository.save(conta);
            if (loteId == null) {
                loteId = conta.getId();
            }
            conta.setLoteId(loteId);
            criadas.add(conta);
        }
        return criadas.stream().map(this::mapComJurosMulta).toList();
    }

    /** 1 {@link ContaPagar} por parcela — chamado pelo {@code gestao-compras-service}. */
    @Transactional
    public List<ContaPagarResponse> gerarDeCompra(GerarContasPagarDeCompraRequest request) {
        if (repository.existsByOrigemTipoAndOrigemId(OrigemContaPagar.COMPRA, request.pedidoCompraId())) {
            throw new ConflictException(FinanceiroErrorCode.COMPRA_JA_GEROU_CONTAS_PAGAR);
        }

        return request.parcelas().stream()
                .map(parcela -> {
                    ContaPagar conta = ContaPagar.builder()
                            .empresaId(request.empresaId())
                            .fornecedorId(request.fornecedorId())
                            .origemTipo(OrigemContaPagar.COMPRA)
                            .origemId(request.pedidoCompraId())
                            // "Compra #<id> - Parcela <n>", não "Pedido de compra #..." — mesmo
                            // orçamento de tamanho que ContaReceberService usa pra "Venda #<id>
                            // - Parcela <n>" (numero_documento é VARCHAR(30); a versão verbosa
                            // estourava a coluna — achado real ao validar ao vivo, 2026-09-05).
                            .numeroDocumento("Compra #" + request.pedidoCompraId() + " - Parcela " + parcela.numeroParcela())
                            .descricao("Parcela " + parcela.numeroParcela() + " do pedido de compra #" + request.pedidoCompraId())
                            .dataEmissao(request.dataEmissao())
                            .dataVencimento(parcela.dataVencimento())
                            .valorOriginal(parcela.valor())
                            .valorSaldo(parcela.valor())
                            .build();
                    return mapComJurosMulta(repository.save(conta));
                })
                .toList();
    }

    /** Espelho de {@code ContaReceberService#estornarPorVenda} — ver Javadoc lá. Compensação de {@link #gerarDeCompra}, chamada pela saga orquestrada do {@code gestao-compras-service} (ver {@code common.saga.SagaOrchestrator}/{@code RecebimentoCompraService#registrar}). */
    @Transactional
    public int estornarPorCompra(Long pedidoCompraId) {
        List<ContaPagar> contas = repository.findByOrigemTipoAndOrigemId(OrigemContaPagar.COMPRA, pedidoCompraId);
        int canceladas = 0;
        for (ContaPagar conta : contas) {
            boolean cancelavel = conta.getSituacao() != SituacaoTitulo.CANCELADA
                    && conta.getSituacao() != SituacaoTitulo.RENEGOCIADA
                    && conta.getBaixas().stream().noneMatch(b -> !b.isEstornado());
            if (cancelavel) {
                conta.setSituacao(SituacaoTitulo.CANCELADA);
                conta.setMotivoEncerramento("Compensação de saga — falha ao confirmar o recebimento do pedido de compra #" + pedidoCompraId + " no gestao-compras-service");
                canceladas++;
            }
        }
        return canceladas;
    }

    /** Espelho de {@code ContaReceberService#atualizarClassificacao} — ver Javadoc lá. */
    @Transactional
    public ContaPagarResponse atualizarClassificacao(Long id, AtualizarClassificacaoTituloRequest request) {
        ContaPagar conta = buscarEntidade(id);
        conta.setNaturezaFinanceiraId(request.naturezaFinanceiraId());
        conta.setTipoDocumentoId(request.tipoDocumentoId());
        conta.setContaBancariaId(request.contaBancariaId());
        conta.setDescontoAte(request.descontoAte());
        conta.setPercentualDesconto(request.percentualDesconto());
        conta.setPercentualMultaAtraso(request.percentualMultaAtraso());
        conta.setPercentualJurosDiaAtraso(request.percentualJurosDiaAtraso());
        return mapComJurosMulta(conta);
    }

    /** Espelho de {@code ContaReceberService#registrarBaixa} — ver Javadoc lá (aqui gera
     * {@code SAIDA}, não {@code ENTRADA}). */
    @Transactional
    public ContaPagarResponse registrarBaixa(Long id, BaixaRequest request) {
        ContaPagar conta = buscarEntidade(id);
        exigirTituloBaixavel(conta);

        LocalDate dataBaixa = request.dataBaixa() != null ? request.dataBaixa() : LocalDate.now();
        BigDecimal valorDesconto = valorDescontoValidado(conta, request, dataBaixa);

        BaixaContaPagar baixa = baixaRepository.save(BaixaContaPagar.builder()
                .contaPagar(conta)
                .dataBaixa(dataBaixa)
                .valorBaixa(request.valorBaixa())
                .valorDesconto(valorDesconto)
                .formaPagamentoId(request.formaPagamentoId())
                .contaBancariaId(request.contaBancariaId())
                .chequeEmitidoId(request.chequeEmitidoId())
                .observacao(request.observacao())
                .usuarioId(request.usuarioId())
                .build());
        conta.getBaixas().add(baixa);

        movimentoService.registrar(request.contaBancariaId(), TipoMovimentoCaixa.SAIDA, OrigemMovimentoCaixa.BAIXA_CONTA_PAGAR,
                baixa.getId(), request.valorBaixa(), dataBaixa, historicoBaixa(conta), request.usuarioId());

        recalcularTotais(conta);
        return mapComJurosMulta(conta);
    }

    /** Espelho de {@code ContaReceberService#estornarBaixa} — ver Javadoc lá (gera
     * {@code ENTRADA} compensatória, não {@code SAIDA}). */
    @Transactional
    public ContaPagarResponse estornarBaixa(Long id, Long baixaId, EstornarBaixaRequest request) {
        ContaPagar conta = buscarEntidade(id);
        BaixaContaPagar baixa = conta.getBaixas().stream()
                .filter(b -> b.getId().equals(baixaId))
                .findFirst()
                .orElseThrow(() -> NotFoundException.of("Baixa de conta a pagar", baixaId));
        if (baixa.isEstornado()) {
            throw new ConflictException(FinanceiroErrorCode.BAIXA_JA_ESTORNADA);
        }

        baixa.setEstornado(true);
        baixa.setDataEstorno(LocalDate.now());
        baixa.setMotivoEstorno(request.motivo());

        if (baixa.getContaBancariaId() != null) {
            movimentoService.registrar(baixa.getContaBancariaId(), TipoMovimentoCaixa.ENTRADA, OrigemMovimentoCaixa.ESTORNO_BAIXA_PAGAR,
                    baixa.getId(), baixa.getValorBaixa(), LocalDate.now(),
                    "Estorno da baixa #" + baixa.getId() + " do título #" + conta.getId(), baixa.getUsuarioId());
        }

        recalcularTotais(conta);
        return mapComJurosMulta(conta);
    }

    @Transactional
    public ContaPagarResponse cancelar(Long id, CancelarTituloRequest request) {
        ContaPagar conta = buscarEntidade(id);
        if (conta.getSituacao() == SituacaoTitulo.CANCELADA) {
            throw new ConflictException(FinanceiroErrorCode.TITULO_CANCELADO);
        }
        if (conta.getSituacao() == SituacaoTitulo.RENEGOCIADA) {
            throw new ConflictException(FinanceiroErrorCode.TITULO_RENEGOCIADO);
        }
        boolean temBaixaAtiva = conta.getBaixas().stream().anyMatch(b -> !b.isEstornado());
        if (temBaixaAtiva) {
            throw new ConflictException(FinanceiroErrorCode.TITULO_JA_TEM_BAIXA_PARA_CANCELAR);
        }
        conta.setSituacao(SituacaoTitulo.CANCELADA);
        conta.setMotivoEncerramento(request.motivo());
        return mapComJurosMulta(conta);
    }

    /** Espelho de {@code ContaReceberService#renegociar} — ver Javadoc lá. */
    @Transactional
    public ContaPagarResponse renegociar(Long id, RenegociarTituloRequest request) {
        ContaPagar original = buscarEntidade(id);
        if (original.getSituacao() != SituacaoTitulo.ABERTA && original.getSituacao() != SituacaoTitulo.PARCIAL) {
            throw new ConflictException(FinanceiroErrorCode.TITULO_NAO_PODE_SER_RENEGOCIADO);
        }
        BigDecimal somaNovas = request.novasParcelas().stream()
                .map(NovaParcelaRenegociacaoRequest::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (somaNovas.compareTo(original.getValorSaldo()) != 0) {
            throw new BusinessException(FinanceiroErrorCode.SOMA_NOVAS_PARCELAS_DIVERGE_DO_SALDO);
        }

        Long loteId = null;
        int numero = 1;
        for (NovaParcelaRenegociacaoRequest parcela : request.novasParcelas()) {
            ContaPagar nova = ContaPagar.builder()
                    .empresaId(original.getEmpresaId())
                    .fornecedorId(original.getFornecedorId())
                    .origemTipo(OrigemContaPagar.RENEGOCIACAO)
                    .origemId(original.getId())
                    .naturezaFinanceiraId(original.getNaturezaFinanceiraId())
                    .tipoDocumentoId(original.getTipoDocumentoId())
                    .contaBancariaId(original.getContaBancariaId())
                    .numeroDocumento("Renegociação #" + original.getId() + " - Parcela " + numero)
                    .descricao("Renegociação do título #" + original.getId() + ": " + request.motivo())
                    .dataEmissao(LocalDate.now())
                    .dataVencimento(parcela.dataVencimento())
                    .valorOriginal(parcela.valor())
                    .valorSaldo(parcela.valor())
                    .build();
            nova = repository.save(nova);
            if (loteId == null) {
                loteId = nova.getId();
            }
            nova.setLoteId(loteId);
            numero++;
        }

        original.setSituacao(SituacaoTitulo.RENEGOCIADA);
        original.setMotivoEncerramento(request.motivo());
        return mapComJurosMulta(original);
    }

    private void exigirTituloBaixavel(ContaPagar conta) {
        if (conta.getSituacao() == SituacaoTitulo.PAGA) {
            throw new ConflictException(FinanceiroErrorCode.TITULO_JA_QUITADO);
        }
        if (conta.getSituacao() == SituacaoTitulo.CANCELADA) {
            throw new ConflictException(FinanceiroErrorCode.TITULO_CANCELADO);
        }
        if (conta.getSituacao() == SituacaoTitulo.RENEGOCIADA) {
            throw new ConflictException(FinanceiroErrorCode.TITULO_RENEGOCIADO);
        }
    }

    /** Espelho de {@code ContaReceberService#valorDescontoValidado} — ver Javadoc lá. */
    private BigDecimal valorDescontoValidado(ContaPagar conta, BaixaRequest request, LocalDate dataBaixa) {
        BigDecimal valorDesconto = request.valorDesconto() != null ? request.valorDesconto() : BigDecimal.ZERO;
        if (valorDesconto.compareTo(BigDecimal.ZERO) > 0
                && (conta.getDescontoAte() == null || dataBaixa.isAfter(conta.getDescontoAte()))) {
            throw new BusinessException(FinanceiroErrorCode.DESCONTO_FORA_DO_PRAZO);
        }
        BigDecimal totalAbatido = request.valorBaixa().add(valorDesconto);
        if (totalAbatido.compareTo(conta.getValorSaldo()) > 0) {
            FinanceiroErrorCode erro = valorDesconto.compareTo(BigDecimal.ZERO) > 0
                    ? FinanceiroErrorCode.VALOR_BAIXA_MAIS_DESCONTO_MAIOR_QUE_SALDO
                    : FinanceiroErrorCode.VALOR_BAIXA_MAIOR_QUE_SALDO;
            throw new BusinessException(erro);
        }
        return valorDesconto;
    }

    private String historicoBaixa(ContaPagar conta) {
        return "Pagamento título #" + conta.getId() + (conta.getNumeroDocumento() != null ? " (" + conta.getNumeroDocumento() + ")" : "");
    }

    private String numeroDocumentoParcela(String base, int numeroParcela, int totalParcelas) {
        if (base == null || base.isBlank()) {
            return "Parcela " + numeroParcela + "/" + totalParcelas;
        }
        return base + " - Parcela " + numeroParcela + "/" + totalParcelas;
    }

    /** Resolve {@code ConfiguracaoFinanceiroEmpresa} da empresa do título e decora a
     * resposta com juros/multa calculados — mesmo desenho de
     * {@code ContaReceberService#mapComJurosMulta}. Usa o override do próprio título
     * quando presente, senão cai pro default da empresa. */
    private ContaPagarResponse mapComJurosMulta(ContaPagar conta) {
        BigDecimal percentualMulta = conta.getPercentualMultaAtraso() != null
                ? conta.getPercentualMultaAtraso() : configuracaoService.percentualMultaDaEmpresa(conta.getEmpresaId());
        BigDecimal percentualJurosDia = conta.getPercentualJurosDiaAtraso() != null
                ? conta.getPercentualJurosDiaAtraso() : configuracaoService.percentualJurosDiaDaEmpresa(conta.getEmpresaId());
        return mapper.toResponse(conta, percentualMulta, percentualJurosDia);
    }

    /** Mesmo desenho de {@code ContaReceberService#recalcularTotais} — ver Javadoc lá. */
    private void recalcularTotais(ContaPagar conta) {
        BigDecimal valorPago = BigDecimal.ZERO;
        BigDecimal valorDescontoTotal = BigDecimal.ZERO;
        for (BaixaContaPagar baixa : conta.getBaixas()) {
            if (!baixa.isEstornado()) {
                valorPago = valorPago.add(baixa.getValorBaixa());
                valorDescontoTotal = valorDescontoTotal.add(baixa.getValorDesconto());
            }
        }
        conta.setValorPago(valorPago);
        conta.setValorDesconto(valorDescontoTotal);
        BigDecimal totalAbatido = valorPago.add(valorDescontoTotal);
        conta.setValorSaldo(conta.getValorOriginal().subtract(totalAbatido));
        if (conta.getSituacao() != SituacaoTitulo.CANCELADA && conta.getSituacao() != SituacaoTitulo.RENEGOCIADA) {
            SituacaoTitulo novaSituacao;
            if (conta.getValorSaldo().compareTo(BigDecimal.ZERO) <= 0) {
                novaSituacao = SituacaoTitulo.PAGA;
            } else if (totalAbatido.compareTo(BigDecimal.ZERO) > 0) {
                novaSituacao = SituacaoTitulo.PARCIAL;
            } else {
                novaSituacao = SituacaoTitulo.ABERTA;
            }
            conta.setSituacao(novaSituacao);
        }
    }

    ContaPagar buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Conta a pagar", id));
    }
}
