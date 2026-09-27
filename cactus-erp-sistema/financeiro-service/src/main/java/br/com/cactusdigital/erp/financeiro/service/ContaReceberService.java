package br.com.cactusdigital.erp.financeiro.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.financeiro.dto.AtualizarClassificacaoTituloRequest;
import br.com.cactusdigital.erp.financeiro.dto.BaixaRequest;
import br.com.cactusdigital.erp.financeiro.dto.CancelarTituloRequest;
import br.com.cactusdigital.erp.financeiro.dto.ContaReceberRequest;
import br.com.cactusdigital.erp.financeiro.dto.ContaReceberResponse;
import br.com.cactusdigital.erp.financeiro.dto.EstornarBaixaRequest;
import br.com.cactusdigital.erp.financeiro.dto.GerarContaReceberParceladaRequest;
import br.com.cactusdigital.erp.financeiro.dto.GerarContasReceberDeVendaRequest;
import br.com.cactusdigital.erp.financeiro.dto.NovaParcelaRenegociacaoRequest;
import br.com.cactusdigital.erp.financeiro.dto.RenegociarTituloRequest;
import br.com.cactusdigital.erp.financeiro.entity.BaixaContaReceber;
import br.com.cactusdigital.erp.financeiro.entity.ContaReceber;
import br.com.cactusdigital.erp.financeiro.entity.OrigemContaReceber;
import br.com.cactusdigital.erp.financeiro.entity.OrigemMovimentoCaixa;
import br.com.cactusdigital.erp.financeiro.entity.SituacaoTitulo;
import br.com.cactusdigital.erp.financeiro.entity.TipoMovimentoCaixa;
import br.com.cactusdigital.erp.financeiro.exception.FinanceiroErrorCode;
import br.com.cactusdigital.erp.financeiro.mapper.FinanceiroMapper;
import br.com.cactusdigital.erp.financeiro.repository.BaixaContaReceberRepository;
import br.com.cactusdigital.erp.financeiro.repository.ContaReceberRepository;
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
 * Contas a Receber — fecha o gap registrado desde a Fase 7, ver Javadoc de
 * {@link ContaReceber}. {@link #gerarDeVenda} é chamado pelo {@code vendas-service} ao
 * confirmar uma venda (novo client cross-serviço, mesmo padrão de
 * {@code EstoqueClient}) — idempotente por {@code vendaId} ({@code origemTipo=VENDA},
 * {@code origemId=vendaId}): uma segunda chamada pra mesma venda é rejeitada
 * ({@link FinanceiroErrorCode#VENDA_JA_GEROU_CONTAS_RECEBER}), não gera duplicata.
 * <p>
 * Desde 2026-09-06 toda {@link #registrarBaixa} gera um {@code MovimentoCaixaBanco} real
 * via {@link MovimentoCaixaBancoService} — ver Javadoc de {@code BaixaContaReceber}.
 */
@Service
@RequiredArgsConstructor
public class ContaReceberService {

    private final ContaReceberRepository repository;
    private final BaixaContaReceberRepository baixaRepository;
    private final ConfiguracaoFinanceiroEmpresaService configuracaoService;
    private final MovimentoCaixaBancoService movimentoService;
    private final FinanceiroMapper mapper;

    @Transactional(readOnly = true)
    public Page<ContaReceberResponse> listar(Long empresaId, Long clienteId, Pageable pageable) {
        Page<ContaReceber> pagina;
        if (clienteId != null) {
            pagina = repository.findByClienteId(clienteId, pageable);
        } else if (empresaId != null) {
            pagina = repository.findByEmpresaId(empresaId, pageable);
        } else {
            pagina = repository.findAll(pageable);
        }
        return PaginatedFetchSupport.comColecoesCarregadas(
                pagina, ContaReceber::getId, repository::buscarComBaixas, this::mapComJurosMulta);
    }

    @Transactional(readOnly = true)
    public ContaReceberResponse buscarPorId(Long id) {
        return mapComJurosMulta(buscarEntidade(id));
    }

    @Transactional
    public ContaReceberResponse criar(ContaReceberRequest request) {
        ContaReceber conta = ContaReceber.builder()
                .empresaId(request.empresaId())
                .clienteId(request.clienteId())
                .origemTipo(OrigemContaReceber.MANUAL)
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

    /**
     * Parcelamento nativo pra lançamento manual — fecha o gap contra
     * {@code FIN_LANCAMENTO_RECEBER} do fenix.sql (cabeçalho com quantidade/primeiro
     * vencimento/intervalo). Valor dividido igualmente, última parcela absorve o resto
     * da divisão (mesmo critério de {@link #gerarDeVenda}/{@code ContaPagarService#gerarDeCompra}).
     * As N parcelas compartilham {@code loteId} (aponta pro id da primeira, inclusive nela mesma).
     */
    @Transactional
    public List<ContaReceberResponse> gerarParcelado(GerarContaReceberParceladaRequest request) {
        int quantidade = request.quantidadeParcelas();
        BigDecimal valorPorParcela = request.valorTotal().divide(BigDecimal.valueOf(quantidade), 6, RoundingMode.DOWN);
        BigDecimal valorUltimaParcela = request.valorTotal().subtract(valorPorParcela.multiply(BigDecimal.valueOf(quantidade - 1)));

        List<ContaReceber> criadas = new ArrayList<>();
        Long loteId = null;
        for (int i = 0; i < quantidade; i++) {
            int numeroParcela = i + 1;
            BigDecimal valorParcela = numeroParcela == quantidade ? valorUltimaParcela : valorPorParcela;
            LocalDate vencimento = request.primeiroVencimento().plusDays((long) request.intervaloEntreParcelasDias() * i);
            ContaReceber conta = ContaReceber.builder()
                    .empresaId(request.empresaId())
                    .clienteId(request.clienteId())
                    .origemTipo(OrigemContaReceber.MANUAL)
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

    /** 1 {@link ContaReceber} por parcela — chamado pelo {@code vendas-service}. */
    @Transactional
    public List<ContaReceberResponse> gerarDeVenda(GerarContasReceberDeVendaRequest request) {
        if (repository.existsByOrigemTipoAndOrigemId(OrigemContaReceber.VENDA, request.vendaId())) {
            throw new ConflictException(FinanceiroErrorCode.VENDA_JA_GEROU_CONTAS_RECEBER);
        }

        return request.parcelas().stream()
                .map(parcela -> {
                    ContaReceber conta = ContaReceber.builder()
                            .empresaId(request.empresaId())
                            .clienteId(request.clienteId())
                            .origemTipo(OrigemContaReceber.VENDA)
                            .origemId(request.vendaId())
                            .numeroDocumento("Venda #" + request.vendaId() + " - Parcela " + parcela.numeroParcela())
                            .descricao("Parcela " + parcela.numeroParcela() + " da venda #" + request.vendaId())
                            .dataEmissao(request.dataEmissao())
                            .dataVencimento(parcela.dataVencimento())
                            .valorOriginal(parcela.valor())
                            .valorSaldo(parcela.valor())
                            .build();
                    return mapComJurosMulta(repository.save(conta));
                })
                .toList();
    }

    /** Fecha a lacuna de títulos nascidos de integração (VENDA), que não tem como informar
     * natureza/tipo de documento no momento da geração automática. Nunca mexe em
     * valor/vencimento/situação. */
    @Transactional
    public ContaReceberResponse atualizarClassificacao(Long id, AtualizarClassificacaoTituloRequest request) {
        ContaReceber conta = buscarEntidade(id);
        conta.setNaturezaFinanceiraId(request.naturezaFinanceiraId());
        conta.setTipoDocumentoId(request.tipoDocumentoId());
        conta.setContaBancariaId(request.contaBancariaId());
        conta.setDescontoAte(request.descontoAte());
        conta.setPercentualDesconto(request.percentualDesconto());
        conta.setPercentualMultaAtraso(request.percentualMultaAtraso());
        conta.setPercentualJurosDiaAtraso(request.percentualJurosDiaAtraso());
        return mapComJurosMulta(conta);
    }

    /** Registra recebimento parcial/total — recalcula {@code valorPago}/{@code valorSaldo}/
     * {@code situacao} a partir das baixas ativas (ver {@link #recalcularTotais}) e gera um
     * {@code MovimentoCaixaBanco} real ({@code ENTRADA}) na conta bancária informada. */
    @Transactional
    public ContaReceberResponse registrarBaixa(Long id, BaixaRequest request) {
        ContaReceber conta = buscarEntidade(id);
        exigirTituloBaixavel(conta);

        LocalDate dataBaixa = request.dataBaixa() != null ? request.dataBaixa() : LocalDate.now();
        BigDecimal valorDesconto = valorDescontoValidado(conta, request, dataBaixa);

        BaixaContaReceber baixa = baixaRepository.save(BaixaContaReceber.builder()
                .contaReceber(conta)
                .dataBaixa(dataBaixa)
                .valorBaixa(request.valorBaixa())
                .valorDesconto(valorDesconto)
                .formaPagamentoId(request.formaPagamentoId())
                .contaBancariaId(request.contaBancariaId())
                .chequeRecebidoId(request.chequeRecebidoId())
                .observacao(request.observacao())
                .usuarioId(request.usuarioId())
                .build());
        conta.getBaixas().add(baixa);

        movimentoService.registrar(request.contaBancariaId(), TipoMovimentoCaixa.ENTRADA, OrigemMovimentoCaixa.BAIXA_CONTA_RECEBER,
                baixa.getId(), request.valorBaixa(), dataBaixa, historicoBaixa(conta), request.usuarioId());

        recalcularTotais(conta);
        return mapComJurosMulta(conta);
    }

    /**
     * Estorna uma baixa já registrada — a linha original NUNCA é apagada nem tem seu
     * valor/data/forma de pagamento alterados (auditoria), só ganha {@code estornado=true}
     * (+ data/motivo do estorno) e deixa de contar pro saldo do título a partir daqui.
     * Gera a saída compensatória no {@code MovimentoCaixaBanco} (2026-09-06) — nunca apaga
     * o lançamento original do ledger, é lançamento novo. É o que destrava {@link #cancelar}
     * de um título que já tinha baixa registrada.
     */
    @Transactional
    public ContaReceberResponse estornarBaixa(Long id, Long baixaId, EstornarBaixaRequest request) {
        ContaReceber conta = buscarEntidade(id);
        BaixaContaReceber baixa = conta.getBaixas().stream()
                .filter(b -> b.getId().equals(baixaId))
                .findFirst()
                .orElseThrow(() -> NotFoundException.of("Baixa de conta a receber", baixaId));
        if (baixa.isEstornado()) {
            throw new ConflictException(FinanceiroErrorCode.BAIXA_JA_ESTORNADA);
        }

        baixa.setEstornado(true);
        baixa.setDataEstorno(LocalDate.now());
        baixa.setMotivoEstorno(request.motivo());

        if (baixa.getContaBancariaId() != null) {
            movimentoService.registrar(baixa.getContaBancariaId(), TipoMovimentoCaixa.SAIDA, OrigemMovimentoCaixa.ESTORNO_BAIXA_RECEBER,
                    baixa.getId(), baixa.getValorBaixa(), LocalDate.now(),
                    "Estorno da baixa #" + baixa.getId() + " do título #" + conta.getId(), baixa.getUsuarioId());
        }

        recalcularTotais(conta);
        return mapComJurosMulta(conta);
    }

    @Transactional
    public ContaReceberResponse cancelar(Long id, CancelarTituloRequest request) {
        ContaReceber conta = buscarEntidade(id);
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

    /**
     * Compensação de {@link #gerarDeVenda}, chamada pela saga orquestrada do
     * {@code vendas-service} (ver {@code common.saga.SagaOrchestrator}/
     * {@code VendaService#confirmar}) quando um passo POSTERIOR à geração das contas a
     * receber falha. <b>Idempotente e tolerante</b> — se a venda nunca gerou contas
     * (nenhuma linha encontrada) ou algumas já têm baixa/estão renegociadas (não deveriam,
     * já que são recém-criadas, mas nunca é seguro assumir isso numa rotina de
     * compensação), o método segue adiante sem lançar exceção: cancela o que PODE cancelar
     * e devolve quantos títulos foram de fato cancelados — best-effort, mesmo espírito do
     * lado da compensação em {@code SagaOrchestrator#compensarEmOrdemReversa}.
     */
    @Transactional
    public int estornarPorVenda(Long vendaId) {
        List<ContaReceber> contas = repository.findByOrigemTipoAndOrigemId(OrigemContaReceber.VENDA, vendaId);
        int canceladas = 0;
        for (ContaReceber conta : contas) {
            boolean cancelavel = conta.getSituacao() != SituacaoTitulo.CANCELADA
                    && conta.getSituacao() != SituacaoTitulo.RENEGOCIADA
                    && conta.getBaixas().stream().noneMatch(b -> !b.isEstornado());
            if (cancelavel) {
                conta.setSituacao(SituacaoTitulo.CANCELADA);
                conta.setMotivoEncerramento("Compensação de saga — falha ao confirmar a venda #" + vendaId + " no vendas-service");
                canceladas++;
            }
        }
        return canceladas;
    }

    /**
     * Substitui o saldo em aberto do título original por N título(s) novo(s)
     * ({@code origemTipo=RENEGOCIACAO}) — fecha o gap contra {@code FIN_STATUS_PARCELA}
     * do fenix.sql ("05 = Renegociado"). Só permitido com saldo em aberto
     * ({@code ABERTA}/{@code PARCIAL}) e soma das novas parcelas batendo exatamente com
     * {@code valorSaldo} do original. Título original vira {@code RENEGOCIADA} — terminal,
     * sem baixa/cancelamento possível depois (ver {@link #exigirTituloBaixavel}/{@link #cancelar}).
     */
    @Transactional
    public ContaReceberResponse renegociar(Long id, RenegociarTituloRequest request) {
        ContaReceber original = buscarEntidade(id);
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
            ContaReceber nova = ContaReceber.builder()
                    .empresaId(original.getEmpresaId())
                    .clienteId(original.getClienteId())
                    .origemTipo(OrigemContaReceber.RENEGOCIACAO)
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

    private void exigirTituloBaixavel(ContaReceber conta) {
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

    /** Valida desconto por antecipação (só aceito até {@code descontoAte}) e que
     * baixa+desconto não ultrapasse o saldo — devolve o {@code valorDesconto} efetivo
     * (zero se o request não informou nenhum). */
    private BigDecimal valorDescontoValidado(ContaReceber conta, BaixaRequest request, LocalDate dataBaixa) {
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

    private String historicoBaixa(ContaReceber conta) {
        return "Recebimento título #" + conta.getId() + (conta.getNumeroDocumento() != null ? " (" + conta.getNumeroDocumento() + ")" : "");
    }

    private String numeroDocumentoParcela(String base, int numeroParcela, int totalParcelas) {
        if (base == null || base.isBlank()) {
            return "Parcela " + numeroParcela + "/" + totalParcelas;
        }
        return base + " - Parcela " + numeroParcela + "/" + totalParcelas;
    }

    /** Resolve {@code ConfiguracaoFinanceiroEmpresa} da empresa do título e decora a
     * resposta com juros/multa calculados — ver Javadoc de {@code FinanceiroMapper}. Usa o
     * override do próprio título quando presente, senão cai pro default da empresa. */
    private ContaReceberResponse mapComJurosMulta(ContaReceber conta) {
        BigDecimal percentualMulta = conta.getPercentualMultaAtraso() != null
                ? conta.getPercentualMultaAtraso() : configuracaoService.percentualMultaDaEmpresa(conta.getEmpresaId());
        BigDecimal percentualJurosDia = conta.getPercentualJurosDiaAtraso() != null
                ? conta.getPercentualJurosDiaAtraso() : configuracaoService.percentualJurosDiaDaEmpresa(conta.getEmpresaId());
        return mapper.toResponse(conta, percentualMulta, percentualJurosDia);
    }

    /** {@code valorPago}/{@code valorDesconto} = soma das baixas NÃO estornadas;
     * {@code situacao} nunca é mexida aqui se já {@code CANCELADA}/{@code RENEGOCIADA}
     * (são ações à parte, ver {@link #cancelar}/{@link #renegociar}). */
    private void recalcularTotais(ContaReceber conta) {
        BigDecimal valorPago = BigDecimal.ZERO;
        BigDecimal valorDescontoTotal = BigDecimal.ZERO;
        for (BaixaContaReceber baixa : conta.getBaixas()) {
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

    ContaReceber buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> NotFoundException.of("Conta a receber", id));
    }
}
