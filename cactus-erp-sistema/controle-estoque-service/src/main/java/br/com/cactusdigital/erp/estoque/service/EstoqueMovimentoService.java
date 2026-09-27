package br.com.cactusdigital.erp.estoque.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.estoque.dto.EstoqueMovimentoRequest;
import br.com.cactusdigital.erp.estoque.dto.EstoqueMovimentoResponse;
import br.com.cactusdigital.erp.estoque.entity.CamadaCustoFifo;
import br.com.cactusdigital.erp.estoque.entity.ConfiguracaoEstoqueEmpresa;
import br.com.cactusdigital.erp.estoque.entity.Deposito;
import br.com.cactusdigital.erp.estoque.entity.DirecaoAjuste;
import br.com.cactusdigital.erp.estoque.entity.EstoqueMovimento;
import br.com.cactusdigital.erp.estoque.entity.Lote;
import br.com.cactusdigital.erp.estoque.entity.MetodoCusteio;
import br.com.cactusdigital.erp.estoque.entity.NumeroSerie;
import br.com.cactusdigital.erp.estoque.entity.SaldoEstoque;
import br.com.cactusdigital.erp.estoque.entity.SaldoEstoqueLote;
import br.com.cactusdigital.erp.estoque.entity.TipoMovimentoEstoque;
import br.com.cactusdigital.erp.estoque.exception.EstoqueErrorCode;
import br.com.cactusdigital.erp.estoque.mapper.EstoqueMapper;
import br.com.cactusdigital.erp.estoque.repository.CamadaCustoFifoRepository;
import br.com.cactusdigital.erp.estoque.repository.ConfiguracaoEstoqueEmpresaRepository;
import br.com.cactusdigital.erp.estoque.repository.EstoqueMovimentoRepository;
import br.com.cactusdigital.erp.estoque.repository.LoteRepository;
import br.com.cactusdigital.erp.estoque.repository.NumeroSerieRepository;
import br.com.cactusdigital.erp.estoque.repository.SaldoEstoqueLoteRepository;
import br.com.cactusdigital.erp.estoque.repository.SaldoEstoqueRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dono exclusivo da escrita de {@link SaldoEstoque}/{@link SaldoEstoqueLote} — nenhum
 * outro service escreve saldo diretamente; toda mudança acontece dentro da mesma
 * transação que insere um {@link EstoqueMovimento} (ledger append-only, nunca
 * {@code UPDATE} direto de saldo "solto").
 * <p>
 * {@code quantidade} do request é sempre positiva (magnitude); o efeito no saldo
 * (soma/subtração) é derivado de {@code tipoMovimento} — e de {@code direcaoAjuste}
 * especificamente pra {@code AJUSTE}, que pode ir nos dois sentidos.
 */
@Service
@RequiredArgsConstructor
public class EstoqueMovimentoService {

    private final EstoqueMovimentoRepository movimentoRepository;
    private final SaldoEstoqueRepository saldoRepository;
    private final SaldoEstoqueLoteRepository saldoLoteRepository;
    private final DepositoService depositoService;
    private final LoteRepository loteRepository;
    private final NumeroSerieRepository numeroSerieRepository;
    private final ConfiguracaoEstoqueEmpresaRepository configuracaoRepository;
    private final CamadaCustoFifoRepository camadaFifoRepository;
    private final EstoqueMapper mapper;

    @Transactional(readOnly = true)
    public Page<EstoqueMovimentoResponse> listarPorProdutoEDeposito(Long produtoId, Long depositoId, Pageable pageable) {
        Page<EstoqueMovimento> pagina = produtoId != null && depositoId != null
                ? movimentoRepository.findByProdutoIdAndDepositoId(produtoId, depositoId, pageable)
                : produtoId != null
                        ? movimentoRepository.findByProdutoId(produtoId, pageable)
                        : depositoId != null
                                ? movimentoRepository.findByDepositoId(depositoId, pageable)
                                : movimentoRepository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public EstoqueMovimentoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public EstoqueMovimentoResponse registrar(EstoqueMovimentoRequest request, Long usuarioId) {
        if (request.tipoMovimento() == TipoMovimentoEstoque.AJUSTE && request.direcaoAjuste() == null) {
            throw new BusinessException(EstoqueErrorCode.AJUSTE_DIRECAO_OBRIGATORIA);
        }

        Deposito deposito = depositoService.buscarEntidade(request.depositoId());
        Lote lote = buscarLoteOpcional(request.loteId());
        NumeroSerie numeroSerie = buscarNumeroSerieOpcional(request.numeroSerieId());

        SaldoEstoque saldo = buscarOuCriarSaldo(request.produtoId(), deposito);
        BigDecimal quantidadeAnterior = saldo.getQuantidade();

        int sinal = sinalDoMovimento(request.tipoMovimento(), request.direcaoAjuste());
        BigDecimal delta = request.quantidade().multiply(BigDecimal.valueOf(sinal));
        BigDecimal quantidadeNova = quantidadeAnterior.add(delta);

        if (quantidadeNova.signum() < 0) {
            throw new BusinessException(EstoqueErrorCode.SALDO_INSUFICIENTE);
        }

        verificarAprovacaoSeNecessario(request, deposito, saldo);

        MetodoCusteio metodoCusteio = metodoCusteioDaEmpresa(deposito.getEmpresaId());
        // TRANSFERENCIA_ENTRADA conta como entrada pra fins de custeio — é valor chegando
        // no depósito, igual uma compra; sem isso o depósito destino ficaria com custo
        // médio ponderado zerado depois de receber transferência (achado testando).
        boolean isEntrada = request.tipoMovimento() == TipoMovimentoEstoque.ENTRADA
                || request.tipoMovimento() == TipoMovimentoEstoque.TRANSFERENCIA_ENTRADA;

        // custoUnitario efetivo do movimento: o informado (ENTRADA/TRANSFERENCIA_ENTRADA)
        // ou, pros demais tipos, a base de valorização real — camadas FIFO consumidas em
        // ordem (se a empresa usa FIFO) ou o custo médio ponderado vigente (default) — é
        // o que relatórios (curva ABC) usam pra saber "quanto valia" toda saída/ajuste/
        // perda, não só as entradas.
        BigDecimal custoUnitarioEfetivo = request.custoUnitario();
        if (isEntrada && request.custoUnitario() != null) {
            saldo.setCustoMedioPonderado(recalcularCustoMedioPonderado(saldo, quantidadeAnterior, request.quantidade(), request.custoUnitario(), quantidadeNova));
        } else if (custoUnitarioEfetivo == null) {
            custoUnitarioEfetivo = metodoCusteio == MetodoCusteio.FIFO
                    ? consumirCamadasFifo(request.produtoId(), deposito, request.quantidade(), saldo.getCustoMedioPonderado())
                    : saldo.getCustoMedioPonderado();
        }
        saldo.setQuantidade(quantidadeNova);
        saldo.setDataUltimaMovimentacao(Instant.now());
        saldoRepository.save(saldo);

        if (lote != null) {
            atualizarSaldoLote(request.produtoId(), deposito, lote, delta);
        }

        EstoqueMovimento movimento = EstoqueMovimento.builder()
                .produtoId(request.produtoId())
                .deposito(deposito)
                .lote(lote)
                .numeroSerie(numeroSerie)
                .tipoMovimento(request.tipoMovimento())
                .direcaoAjuste(request.direcaoAjuste())
                .quantidade(request.quantidade())
                .quantidadeAnterior(quantidadeAnterior)
                .quantidadeNova(quantidadeNova)
                .custoUnitario(custoUnitarioEfetivo)
                .origemTipo(request.origemTipo())
                .origemId(request.origemId())
                .usuarioId(usuarioId)
                .aprovadorId(request.aprovadorId())
                .observacao(request.observacao())
                .dataMovimento(Instant.now())
                .build();
        movimento = movimentoRepository.save(movimento);

        if (isEntrada && metodoCusteio == MetodoCusteio.FIFO && request.custoUnitario() != null) {
            criarCamadaFifo(request.produtoId(), deposito, lote, request.quantidade(), request.custoUnitario(), movimento);
        }

        return mapper.toResponse(movimento);
    }

    private MetodoCusteio metodoCusteioDaEmpresa(Long empresaId) {
        return configuracaoRepository.findByEmpresaId(empresaId)
                .map(ConfiguracaoEstoqueEmpresa::getMetodoCusteio)
                .orElse(MetodoCusteio.MEDIA_PONDERADA);
    }

    private void criarCamadaFifo(Long produtoId, Deposito deposito, Lote lote, BigDecimal quantidade, BigDecimal custoUnitario, EstoqueMovimento movimentoEntrada) {
        CamadaCustoFifo camada = CamadaCustoFifo.builder()
                .produtoId(produtoId)
                .deposito(deposito)
                .lote(lote)
                .quantidadeRestante(quantidade)
                .custoUnitario(custoUnitario)
                .dataEntrada(Instant.now())
                .movimentoEntrada(movimentoEntrada)
                .build();
        camadaFifoRepository.save(camada);
    }

    /** Consome camadas mais antigas primeiro até cobrir {@code quantidadeNecessaria}; devolve
     * o custo médio das camadas realmente consumidas, ou {@code custoFallback} se não
     * houver camada nenhuma (produto nunca teve ENTRADA registrada sob FIFO). */
    private BigDecimal consumirCamadasFifo(Long produtoId, Deposito deposito, BigDecimal quantidadeNecessaria, BigDecimal custoFallback) {
        List<CamadaCustoFifo> camadas = camadaFifoRepository
                .findByProdutoIdAndDepositoIdAndQuantidadeRestanteGreaterThanOrderByDataEntradaAsc(produtoId, deposito.getId(), BigDecimal.ZERO);

        BigDecimal restante = quantidadeNecessaria;
        BigDecimal valorConsumido = BigDecimal.ZERO;
        for (CamadaCustoFifo camada : camadas) {
            if (restante.signum() <= 0) {
                break;
            }
            BigDecimal consumidaDestaCamada = camada.getQuantidadeRestante().min(restante);
            valorConsumido = valorConsumido.add(consumidaDestaCamada.multiply(camada.getCustoUnitario()));
            camada.setQuantidadeRestante(camada.getQuantidadeRestante().subtract(consumidaDestaCamada));
            restante = restante.subtract(consumidaDestaCamada);
        }
        BigDecimal quantidadeConsumida = quantidadeNecessaria.subtract(restante);
        return quantidadeConsumida.signum() > 0 ? valorConsumido.divide(quantidadeConsumida, 6, RoundingMode.HALF_UP) : custoFallback;
    }

    private void verificarAprovacaoSeNecessario(EstoqueMovimentoRequest request, Deposito deposito, SaldoEstoque saldo) {
        boolean exigeAprovacaoPorTipo = request.tipoMovimento() == TipoMovimentoEstoque.AJUSTE
                || request.tipoMovimento() == TipoMovimentoEstoque.PERDA;
        if (!exigeAprovacaoPorTipo || request.aprovadorId() != null) {
            return;
        }
        configuracaoRepository.findByEmpresaId(deposito.getEmpresaId()).ifPresent(config -> {
            BigDecimal custoReferencia = request.custoUnitario() != null ? request.custoUnitario() : saldo.getCustoMedioPonderado();
            BigDecimal valorMovimento = request.quantidade().multiply(custoReferencia);
            if (valorMovimento.compareTo(config.getLimiteAprovacaoAjuste()) > 0) {
                throw new BusinessException(EstoqueErrorCode.APROVADOR_OBRIGATORIO);
            }
        });
    }

    private BigDecimal recalcularCustoMedioPonderado(
            SaldoEstoque saldo, BigDecimal quantidadeAnterior, BigDecimal quantidadeEntrada, BigDecimal custoUnitarioEntrada, BigDecimal quantidadeNova) {
        if (quantidadeAnterior.signum() <= 0) {
            return custoUnitarioEntrada;
        }
        BigDecimal valorAnterior = quantidadeAnterior.multiply(saldo.getCustoMedioPonderado());
        BigDecimal valorEntrada = quantidadeEntrada.multiply(custoUnitarioEntrada);
        return valorAnterior.add(valorEntrada).divide(quantidadeNova, 6, java.math.RoundingMode.HALF_UP);
    }

    private SaldoEstoque buscarOuCriarSaldo(Long produtoId, Deposito deposito) {
        return saldoRepository.findByProdutoIdAndDepositoId(produtoId, deposito.getId())
                .orElseGet(() -> SaldoEstoque.builder()
                        .produtoId(produtoId)
                        .deposito(deposito)
                        .quantidade(BigDecimal.ZERO)
                        .quantidadeReservada(BigDecimal.ZERO)
                        .custoMedioPonderado(BigDecimal.ZERO)
                        .build());
    }

    private void atualizarSaldoLote(Long produtoId, Deposito deposito, Lote lote, BigDecimal delta) {
        SaldoEstoqueLote saldoLote = saldoLoteRepository.findByProdutoIdAndDepositoIdAndLoteId(produtoId, deposito.getId(), lote.getId())
                .orElseGet(() -> SaldoEstoqueLote.builder()
                        .produtoId(produtoId)
                        .deposito(deposito)
                        .lote(lote)
                        .quantidade(BigDecimal.ZERO)
                        .build());
        saldoLote.setQuantidade(saldoLote.getQuantidade().add(delta));
        saldoLoteRepository.save(saldoLote);
    }

    private int sinalDoMovimento(TipoMovimentoEstoque tipo, DirecaoAjuste direcaoAjuste) {
        return switch (tipo) {
            case ENTRADA, DEVOLUCAO, TRANSFERENCIA_ENTRADA -> 1;
            case SAIDA, PERDA, TRANSFERENCIA_SAIDA -> -1;
            case AJUSTE -> direcaoAjuste == DirecaoAjuste.ACRESCIMO ? 1 : -1;
        };
    }

    private Lote buscarLoteOpcional(Long loteId) {
        if (loteId == null) {
            return null;
        }
        return loteRepository.findById(loteId).orElseThrow(() -> NotFoundException.of("Lote", loteId));
    }

    private NumeroSerie buscarNumeroSerieOpcional(Long numeroSerieId) {
        if (numeroSerieId == null) {
            return null;
        }
        return numeroSerieRepository.findById(numeroSerieId).orElseThrow(() -> NotFoundException.of("Número de série", numeroSerieId));
    }

    EstoqueMovimento buscarEntidade(Long id) {
        return movimentoRepository.findById(id).orElseThrow(() -> NotFoundException.of("Movimento de estoque", id));
    }
}
