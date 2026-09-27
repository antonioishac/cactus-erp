package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.config.SessaoPdv;
import br.com.cactuserp.pdv.config.util.HashCalculator;
import br.com.cactuserp.pdv.entity.Cliente;
import br.com.cactuserp.pdv.entity.PdvMovimento;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.PdvVendaDetalhe;
import br.com.cactuserp.pdv.entity.PdvVendaDetalheHistorico;
import br.com.cactuserp.pdv.entity.Produto;
import br.com.cactuserp.pdv.entity.SimNao;
import br.com.cactuserp.pdv.entity.StatusVenda;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.repository.PdvVendaCabecalhoRepository;
import br.com.cactuserp.pdv.repository.PdvVendaDetalheHistoricoRepository;
import br.com.cactuserp.pdv.repository.PdvVendaDetalheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import br.com.cactuserp.pdv.entity.PdvTotalTipoPagamento;
import br.com.cactuserp.pdv.entity.TipoPagamento;
import br.com.cactuserp.pdv.repository.PdvTotalTipoPagamentoRepository;

/**
 * Serviço de operações da venda (cupom) no PDV.
 *
 * <h3>Funcionalidades</h3>
 * <ul>
 *   <li><b>Agrupamento automático</b>: bipa COCA pela 2ª vez? Soma na linha
 *       existente em vez de criar duplicada. (Padrão atacarejo).</li>
 *   <li><b>Faixa de preço por quantidade</b>: aplica preço de atacado
 *       automaticamente quando a quantidade total atinge o piso.</li>
 *   <li><b>Auditoria fiscal completa</b>: cada alteração gera registro em
 *       PDV_VENDA_DETALHE_HISTORICO com estado anterior + novo + hashes.</li>
 *   <li><b>Atomicidade</b>: tudo em transação curta.</li>
 *   <li><b>Imutabilidade auditada</b>: cancelamento via flag, nunca delete.</li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
public class VendaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VendaService.class);
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    /** Tipos de alteração registrados no histórico. */
    public static final String TIPO_ADICIONADO = "A";
    public static final String TIPO_AGRUPADO   = "G";
    public static final String TIPO_CANCELADO  = "C";
    public static final String TIPO_FAIXA      = "F";
    public static final String TIPO_DESCONTO   = "D";

    private final PdvVendaCabecalhoRepository cabecalhoRepository;
    private final PdvVendaDetalheRepository detalheRepository;
    private final PdvVendaDetalheHistoricoRepository historicoRepository;
    private final SessaoPdv sessaoPdv;
    private final DispositivoInfoService dispositivoInfo;
    private final FaixaPrecoService faixaPrecoService;
    private final PdvTotalTipoPagamentoRepository totalPagamentoRepository;
    private final EstoqueMovimentoService estoqueMovimentoService;
    private final FiadoService fiadoService;
    private final CalculoTributarioService calculoTributarioService;

    public VendaService(PdvVendaCabecalhoRepository cabecalhoRepository,
                        PdvVendaDetalheRepository detalheRepository,
                        PdvVendaDetalheHistoricoRepository historicoRepository,
                        PdvTotalTipoPagamentoRepository totalPagamentoRepository,
                        SessaoPdv sessaoPdv,
                        DispositivoInfoService dispositivoInfo,
                        FaixaPrecoService faixaPrecoService,
                        EstoqueMovimentoService estoqueMovimentoService,
                        FiadoService fiadoService,
                        CalculoTributarioService calculoTributarioService) {
        this.cabecalhoRepository = cabecalhoRepository;
        this.detalheRepository = detalheRepository;
        this.historicoRepository = historicoRepository;
        this.totalPagamentoRepository = totalPagamentoRepository;
        this.sessaoPdv = sessaoPdv;
        this.dispositivoInfo = dispositivoInfo;
        this.faixaPrecoService = faixaPrecoService;
        this.estoqueMovimentoService = estoqueMovimentoService;
        this.fiadoService = fiadoService;
        this.calculoTributarioService = calculoTributarioService;
    }

    // ====================================================================
    //  CONSULTAS
    // ====================================================================

    public Optional<PdvVendaCabecalho> buscarCupomAberto(PdvMovimento movimento) {
        if (movimento == null) return Optional.empty();
        return cabecalhoRepository
                .findFirstByPdvMovimentoAndStatusVendaOrderByIdDesc(movimento, StatusVenda.A);
    }

    public List<PdvVendaDetalhe> listarDetalhes(PdvVendaCabecalho cupom) {
        if (cupom == null) return List.of();
        return detalheRepository.buscarDetalhesComProduto(cupom);
    }

    // ====================================================================
    //  ESCRITA — abrir cupom
    // ====================================================================

    @Transactional
    public PdvVendaCabecalho abrirCupomSeNaoExistir(PdvMovimento movimento) {
        if (movimento == null) {
            throw new IllegalArgumentException("Movimento é obrigatório para abrir cupom.");
        }

        Optional<PdvVendaCabecalho> existente = buscarCupomAberto(movimento);
        if (existente.isPresent()) {
            LOGGER.debug("Cupom em aberto reutilizado: ID={}", existente.get().getId());
            return existente.get();
        }

        PdvVendaCabecalho cupom = new PdvVendaCabecalho();
        cupom.setPdvMovimento(movimento);
        cupom.setColaborador(sessaoPdv.getOperadorAtual().getColaborador());
        cupom.setDataVenda(LocalDate.now());
        cupom.setHoraVenda(LocalTime.now().format(FMT_HORA));
        cupom.setStatusVenda(StatusVenda.A);
        cupom.setCupomCancelado(SimNao.N);

        cupom.setValorVenda(BigDecimal.ZERO);
        cupom.setValorTotalProdutos(BigDecimal.ZERO);
        cupom.setValorTotalDocumento(BigDecimal.ZERO);
        cupom.setValorDesconto(BigDecimal.ZERO);
        cupom.setValorAcrescimo(BigDecimal.ZERO);
        cupom.setValorCancelado(BigDecimal.ZERO);

        cupom.setIdDispositivo(dispositivoInfo.getHostname());

        cupom = cabecalhoRepository.save(cupom);
        cupom.setHashRegistro(HashCalculator.hashDoCabecalho(cupom));
        cupom = cabecalhoRepository.save(cupom);

        LOGGER.info("Cupom aberto: ID={}, movimento={}", cupom.getId(), movimento.getId());
        return cupom;
    }

    /**
     * Associa (ou remove, se {@code cliente} for null) o cliente identificado
     * na venda ao cupom em aberto — inclui os campos desnormalizados
     * {@code NOME_CLIENTE}/{@code CPF_CNPJ_CLIENTE} usados na impressão do
     * cupom e na nota fiscal ("CPF na nota").
     *
     * <p>Sem isso, a seleção de cliente feita na tela do PDV
     * ({@code Pdv4Controller}) nunca chegava a ser persistida — o campo
     * ficava só em memória na UI e se perdia ao finalizar a venda.
     *
     * <p>Não recalcula {@code HASH_REGISTRO}: cliente não faz parte da
     * fórmula do hash fiscal (ver {@code HashCalculator}), que é
     * intencionalmente restrita aos campos de valor/status da venda.
     */
    @Transactional
    public PdvVendaCabecalho atualizarCliente(PdvVendaCabecalho cupom, Cliente cliente) {
        if (cupom == null) {
            throw new IllegalArgumentException("Cupom é obrigatório.");
        }

        PdvVendaCabecalho gerenciado = cabecalhoRepository.findById(cupom.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Cupom #" + cupom.getId() + " não encontrado."));

        if (!gerenciado.estaAberto()) {
            throw new IllegalStateException(
                    "Só é possível alterar o cliente de um cupom em aberto. Status atual: "
                            + gerenciado.getStatusVenda());
        }

        gerenciado.setCliente(cliente);
        gerenciado.setNomeCliente(cliente != null ? cliente.getNome() : null);
        gerenciado.setCpfCnpjCliente(cliente != null ? cliente.getCpfCnpj() : null);

        gerenciado = cabecalhoRepository.save(gerenciado);
        LOGGER.info("Cliente atualizado no cupom {}: {}", gerenciado.getId(),
                cliente != null ? cliente.getNome() : "(removido)");
        return gerenciado;
    }

    // ====================================================================
    //  ESCRITA — adicionar item (COM AGRUPAMENTO + FAIXA + HISTÓRICO)
    // ====================================================================

    /**
     * Adiciona um produto ao cupom. Comportamento:
     * <ol>
     *   <li>Se o produto JÁ existe no cupom (não cancelado): <b>AGRUPA</b>
     *       — soma a quantidade, recalcula faixa, atualiza item.</li>
     *   <li>Senão: INSERT novo item, aplica faixa de preço pela quantidade.</li>
     * </ol>
     *
     * <p>Em qualquer caso, grava histórico com tipo A (adicionado) ou
     * G (agrupado). O retorno é o item resultante (novo ou agrupado),
     * que o caller usa pra decidir atualizar/inserir na UI.
     *
     * @return resultado contendo o detalhe + flag indicando se foi agrupado
     */
    @Transactional
    public ResultadoAdicaoItem adicionarItem(PdvVendaCabecalho cupom,
                                             Produto produto,
                                             BigDecimal quantidade) {
        validarAdicaoDeItem(cupom, produto, quantidade);

        Optional<PdvVendaDetalhe> existente = detalheRepository
                .findByPdvVendaCabecalhoAndProdutoAndCancelado(cupom, produto, SimNao.N);

        if (existente.isPresent()) {
            return agruparItemExistente(cupom, existente.get(), quantidade);
        }
        return inserirItemNovo(cupom, produto, quantidade);
    }

    /**
     * Caminho NOVO: insere item inédito no cupom. Aplica faixa de preço
     * já no insert (quantidade pode vir grande via multiplicador 6*COCA
     * e cair direto no atacado).
     */
    private ResultadoAdicaoItem inserirItemNovo(PdvVendaCabecalho cupom,
                                                Produto produto,
                                                BigDecimal quantidade) {
        FaixaPrecoService.ResultadoFaixa faixa =
                faixaPrecoService.calcular(produto, quantidade);
        BigDecimal valorUnitario = faixa.getPrecoUnitario();
        BigDecimal valorTotal = valorUnitario.multiply(quantidade).setScale(2, RoundingMode.DOWN);

        int proximoItem = (int) detalheRepository.countByPdvVendaCabecalho(cupom) + 1;

        PdvVendaDetalhe detalhe = new PdvVendaDetalhe();
        detalhe.setPdvVendaCabecalho(cupom);
        detalhe.setProduto(produto);
        detalhe.setItem(proximoItem);
        detalhe.setVersao(1);
        detalhe.setGtin(produto.getGtin());
        detalhe.setQuantidade(quantidade);
        detalhe.setValorUnitario(valorUnitario);
        detalhe.setValorTotal(valorTotal);
        detalhe.setValorTotalItem(valorTotal);
        detalhe.setCancelado(SimNao.N);
        detalhe.setMovimentaEstoque(SimNao.S);
        detalhe.setIdDispositivo(dispositivoInfo.getHostname());
        calculoTributarioService.aplicarEm(detalhe, produto, valorTotal);

        detalhe = detalheRepository.save(detalhe);
        detalhe.setHashRegistro(HashCalculator.hashDoDetalhe(detalhe));
        detalhe = detalheRepository.save(detalhe);

        String motivo = faixa.temFaixaAplicada()
                ? "Item adicionado já na faixa " + faixa.getRotulo()
                : "Item adicionado (preço varejo)";
        gravarHistoricoInsert(detalhe, motivo);

        ajustarCabecalhoSomandoDelta(cupom, valorTotal);

        LOGGER.info("Item NOVO adicionado: cupom={}, produto={}, qtd={}, vlrUnit={}, total={}",
                cupom.getId(), produto.getId(), quantidade, valorUnitario, valorTotal);

        return new ResultadoAdicaoItem(detalhe, false, faixa);
    }

    /**
     * Caminho AGRUPADO: produto já existe no cupom. Soma a nova quantidade
     * à existente, recalcula faixa, atualiza o item, grava histórico,
     * ajusta totalizadores do cabeçalho pelo DELTA.
     */
    private ResultadoAdicaoItem agruparItemExistente(PdvVendaCabecalho cupom,
                                                     PdvVendaDetalhe atual,
                                                     BigDecimal quantidadeAdicional) {
        BigDecimal qtdAnterior   = atual.getQuantidade();
        BigDecimal vUnitAnterior = atual.getValorUnitario();
        BigDecimal vTotAnterior  = atual.getValorTotal();
        String hashAnterior      = atual.getHashRegistro();
        Integer versaoAnterior   = atual.getVersao() != null ? atual.getVersao() : 1;

        BigDecimal qtdNova = qtdAnterior.add(quantidadeAdicional);
        FaixaPrecoService.ResultadoFaixa faixa =
                faixaPrecoService.calcular(atual.getProduto(), qtdNova);
        BigDecimal vUnitNovo = faixa.getPrecoUnitario();
        BigDecimal vTotNovo  = vUnitNovo.multiply(qtdNova).setScale(2, RoundingMode.DOWN);

        atual.setQuantidade(qtdNova);
        atual.setValorUnitario(vUnitNovo);
        atual.setValorTotal(vTotNovo);
        atual.setValorTotalItem(vTotNovo);
        atual.setVersao(versaoAnterior + 1);
        calculoTributarioService.aplicarEm(atual, atual.getProduto(), vTotNovo);

        atual.setHashRegistro(HashCalculator.hashDoDetalhe(atual));
        atual = detalheRepository.save(atual);

        boolean mudouFaixa = vUnitNovo.compareTo(vUnitAnterior) != 0;
        String motivo;
        if (mudouFaixa && faixa.temFaixaAplicada()) {
            motivo = "Agrupado +" + quantidadeAdicional.stripTrailingZeros().toPlainString()
                    + " unid · faixa aplicada: " + faixa.getRotulo()
                    + " (preço " + vUnitAnterior + " → " + vUnitNovo + ")";
        } else if (mudouFaixa) {
            motivo = "Agrupado +" + quantidadeAdicional.stripTrailingZeros().toPlainString()
                    + " unid · voltou ao varejo (preço " + vUnitAnterior + " → " + vUnitNovo + ")";
        } else {
            motivo = "Agrupado +" + quantidadeAdicional.stripTrailingZeros().toPlainString()
                    + " unid (mesma faixa)";
        }

        PdvVendaDetalheHistorico h = novoHistoricoBase(atual, TIPO_AGRUPADO, motivo);
        h.setQuantidadeAnterior(qtdAnterior);
        h.setValorUnitarioAnterior(vUnitAnterior);
        h.setValorTotalAnterior(vTotAnterior);
        h.setCanceladoAnterior("N");
        h.setHashAnterior(hashAnterior);
        h.setQuantidadeNova(qtdNova);
        h.setValorUnitarioNovo(vUnitNovo);
        h.setValorTotalNovo(vTotNovo);
        h.setCanceladoNovo("N");
        h.setHashNovo(atual.getHashRegistro());
        historicoRepository.save(h);

        BigDecimal delta = vTotNovo.subtract(vTotAnterior);
        ajustarCabecalhoSomandoDelta(cupom, delta);

        LOGGER.info("Item AGRUPADO: detalhe={}, qtd {} → {}, vlrUnit {} → {}, delta total={}",
                atual.getId(), qtdAnterior, qtdNova, vUnitAnterior, vUnitNovo, delta);

        return new ResultadoAdicaoItem(atual, true, faixa);
    }

    // ====================================================================
    //  ESCRITA — cancelar item
    // ====================================================================

    /**
     * Cancela um item específico do cupom. Marca como cancelado, atualiza
     * cabeçalho e grava histórico com tipo C.
     */
    @Transactional
    public PdvVendaDetalhe cancelarItem(PdvVendaDetalhe detalhe) {
        if (detalhe == null) {
            throw new IllegalArgumentException("Detalhe é obrigatório.");
        }
        if (detalhe.getCancelado() == SimNao.S) {
            throw new IllegalStateException("Item já está cancelado.");
        }

        Long detalheId = detalhe.getId();
        PdvVendaDetalhe gerenciado = detalheRepository.findById(detalheId)
                .orElseThrow(() -> new IllegalStateException(
                        "Detalhe #" + detalheId + " não encontrado."));

        Long cabecalhoId = gerenciado.getPdvVendaCabecalho().getId();
        PdvVendaCabecalho cupom = cabecalhoRepository.findById(cabecalhoId)
                .orElseThrow(() -> new IllegalStateException(
                        "Cupom #" + cabecalhoId + " não encontrado."));

        if (!cupom.estaAberto()) {
            throw new IllegalStateException(
                    "Não é possível cancelar item de cupom com status " + cupom.getStatusVenda());
        }

        // Snapshot ANTERIOR
        BigDecimal valorItem = nullSafe(gerenciado.getValorTotal());
        String hashAnterior  = gerenciado.getHashRegistro();
        Integer versaoAnterior = gerenciado.getVersao() != null ? gerenciado.getVersao() : 1;

        // Aplica cancelamento
        gerenciado.setCancelado(SimNao.S);
        gerenciado.setVersao(versaoAnterior + 1);
        gerenciado.setHashRegistro(HashCalculator.hashDoDetalhe(gerenciado));
        gerenciado = detalheRepository.save(gerenciado);

        // Histórico
        PdvVendaDetalheHistorico h = novoHistoricoBase(gerenciado, TIPO_CANCELADO,
                "Item cancelado pelo operador (F1)");
        h.setQuantidadeAnterior(gerenciado.getQuantidade());
        h.setValorUnitarioAnterior(gerenciado.getValorUnitario());
        h.setValorTotalAnterior(valorItem);
        h.setCanceladoAnterior("N");
        h.setHashAnterior(hashAnterior);
        h.setQuantidadeNova(gerenciado.getQuantidade());
        h.setValorUnitarioNovo(gerenciado.getValorUnitario());
        h.setValorTotalNovo(valorItem);
        h.setCanceladoNovo("S");
        h.setHashNovo(gerenciado.getHashRegistro());
        historicoRepository.save(h);

        // Atualiza cabeçalho: subtrai valor do item e acumula em valorCancelado
        subtrairItemDoCabecalho(cupom, valorItem);

        LOGGER.info("Item cancelado: detalhe={}, valor={}, cupom={}",
                gerenciado.getId(), valorItem, cupom.getId());
        return gerenciado;
    }

    // ====================================================================
    //  ESCRITA — cancelar cupom inteiro
    // ====================================================================

    @Transactional
    public PdvVendaCabecalho cancelarCupom(PdvVendaCabecalho cupom) {
        if (cupom == null) {
            throw new IllegalArgumentException("Cupom é obrigatório.");
        }
        if (!cupom.estaAberto()) {
            throw new IllegalStateException(
                    "Apenas cupons em aberto podem ser cancelados. Status atual: "
                            + cupom.getStatusVenda());
        }

        // Cancela todos os itens não-cancelados (com histórico)
        List<PdvVendaDetalhe> itens = detalheRepository.buscarDetalhesComProduto(cupom)
                .stream()
                .filter(d -> d.getCancelado() != SimNao.S)
                .collect(java.util.stream.Collectors.toList());

        for (PdvVendaDetalhe item : itens) {
            // Snapshot pra histórico (sem chamar cancelarItem pra evitar
            // múltiplos ajustes incrementais no cabeçalho)
            String hashAnterior = item.getHashRegistro();
            Integer versaoAnterior = item.getVersao() != null ? item.getVersao() : 1;

            item.setCancelado(SimNao.S);
            item.setVersao(versaoAnterior + 1);
            item.setHashRegistro(HashCalculator.hashDoDetalhe(item));
            detalheRepository.save(item);

            PdvVendaDetalheHistorico h = novoHistoricoBase(item, TIPO_CANCELADO,
                    "Cancelado em massa (cancelamento do cupom inteiro)");
            h.setQuantidadeAnterior(item.getQuantidade());
            h.setValorUnitarioAnterior(item.getValorUnitario());
            h.setValorTotalAnterior(item.getValorTotal());
            h.setCanceladoAnterior("N");
            h.setHashAnterior(hashAnterior);
            h.setQuantidadeNova(item.getQuantidade());
            h.setValorUnitarioNovo(item.getValorUnitario());
            h.setValorTotalNovo(item.getValorTotal());
            h.setCanceladoNovo("S");
            h.setHashNovo(item.getHashRegistro());
            historicoRepository.save(h);
        }

        BigDecimal totalAcumulado = nullSafe(cupom.getValorTotalDocumento());
        cupom.setStatusVenda(StatusVenda.C);
        cupom.setCupomCancelado(SimNao.S);
        cupom.setValorCancelado(totalAcumulado);

        cupom.setHashRegistro(HashCalculator.hashDoCabecalho(cupom));
        cupom = cabecalhoRepository.save(cupom);

        LOGGER.info("Cupom cancelado: ID={}, valor cancelado={}, itens cancelados={}",
                cupom.getId(), totalAcumulado, itens.size());
        return cupom;
    }

    /**
     * Finaliza uma venda em aberto:
     * <ol>
     *   <li>Grava 1 linha em PDV_TOTAL_TIPO_PAGAMENTO por pagamento lançado</li>
     *   <li>Calcula totalRecebido, troco e valorFinal no cabeçalho</li>
     *   <li>Muda STATUS_VENDA de A para F (Finalizada)</li>
     *   <li>Recalcula HASH_REGISTRO do cabeçalho final</li>
     * </ol>
     *
     * <p>Tudo numa transação atômica. Se falhar em qualquer ponto, faz
     * rollback completo — pagamentos somem, cupom continua em aberto.
     *
     * <p>O tipo de operação ({@code tipoOperacao}) é setado como "REC"
     * (Recibo) por padrão. Quando a integração NFC-e/SAT for adicionada,
     * o valor muda para "NFC" ou "SAT" antes de gravar.
     *
     * @param cupom cupom a finalizar (deve estar com status A)
     * @param pagamentos lista de pagamentos lançados (não pode ser vazia)
     * @return o cupom atualizado, agora com status F
     * @throws IllegalArgumentException se argumentos forem inválidos
     * @throws IllegalStateException se cupom não estiver em aberto, ou
     *         se a soma dos pagamentos for menor que o valor total
     */
    @Transactional
    public PdvVendaCabecalho finalizarVenda(PdvVendaCabecalho cupom,
                                            List<PagamentoLancado> pagamentos) {
        // ====== Validações ======
        if (cupom == null) {
            throw new IllegalArgumentException("Cupom é obrigatório.");
        }
        if (pagamentos == null || pagamentos.isEmpty()) {
            throw new IllegalArgumentException("Deve haver pelo menos um pagamento.");
        }

        // Recarrega o cupom na sessão atual (evita LazyInit + verifica estado)
        Long cabecalhoId = cupom.getId();
        PdvVendaCabecalho gerenciado = cabecalhoRepository.findById(cabecalhoId)
                .orElseThrow(() -> new IllegalStateException(
                        "Cupom #" + cabecalhoId + " não encontrado."));

        if (!gerenciado.estaAberto()) {
            throw new IllegalStateException(
                    "Apenas cupons em aberto podem ser finalizados. Status atual: "
                            + gerenciado.getStatusVenda());
        }

        // Soma de pagamentos vs total a pagar
        BigDecimal totalRecebido = pagamentos.stream()
                .map(PagamentoLancado::getValor)
                .filter(v -> v != null && v.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDocumento = nullSafe(gerenciado.getValorTotalDocumento());
        if (totalRecebido.compareTo(totalDocumento) < 0) {
            throw new IllegalStateException(
                    "Total recebido (" + totalRecebido
                            + ") menor que total da venda (" + totalDocumento + ")");
        }

        // ====== Validação de fiado (se houver pagamento com CODIGO_FIADO) ======
        // Feita ANTES de gravar qualquer coisa — falha aqui não deve deixar
        // rastro de pagamento gravado.
        BigDecimal totalFiado = pagamentos.stream()
                .filter(p -> p.getTipoPagamento() != null
                        && FiadoService.CODIGO_FIADO.equals(p.getTipoPagamento().getCodigo()))
                .map(PagamentoLancado::getValor)
                .filter(v -> v != null && v.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalFiado.compareTo(BigDecimal.ZERO) > 0) {
            if (gerenciado.getCliente() == null) {
                throw new IllegalStateException(
                        "Venda fiado exige cliente identificado. Selecione o cliente antes de finalizar.");
            }
            if (!fiadoService.dentroDoLimite(gerenciado.getCliente(), totalFiado)) {
                throw new IllegalStateException(
                        "Venda fiado ultrapassa o limite de crédito do cliente "
                                + gerenciado.getCliente().getNome() + ".");
            }
        }

        // ====== 1. Grava cada pagamento ======
        LocalDate hoje = LocalDate.now();
        String horaAgora = LocalTime.now().format(FMT_HORA);
        String hostname = dispositivoInfo.getHostname();

        for (PagamentoLancado p : pagamentos) {
            if (p.getValor() == null || p.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                continue; // pula valores inválidos
            }
            if (p.getTipoPagamento() == null) {
                throw new IllegalArgumentException(
                        "Pagamento sem tipo informado.");
            }

            PdvTotalTipoPagamento pagto = new PdvTotalTipoPagamento();
            pagto.setIdPdvVendaCabecalho(gerenciado.getId());
            pagto.setPdvTipoPagamento(p.getTipoPagamento());
            pagto.setDataVenda(hoje);
            pagto.setHoraVenda(horaAgora);
            pagto.setValor(p.getValor());
            pagto.setEstorno("N");
            pagto.setNsu(p.getNsu());
            pagto.setRede(p.getRede());
            pagto.setCartaoDc(p.getCartaoDc());
            pagto.setIdDispositivo(hostname);

            // Salva primeiro (pra ter ID), depois calcula hash, depois salva de novo
            pagto = totalPagamentoRepository.save(pagto);
            pagto.setHashRegistro(HashCalculator.hashDoPagamento(pagto));
            totalPagamentoRepository.save(pagto);
        }

        // ====== 2. Calcula troco e atualiza cabeçalho ======
        BigDecimal troco = totalRecebido.subtract(totalDocumento);
        if (troco.compareTo(BigDecimal.ZERO) < 0) {
            troco = BigDecimal.ZERO; // garantia (não deveria acontecer)
        }

        gerenciado.setValorRecebido(totalRecebido);
        gerenciado.setValorTroco(troco);
        gerenciado.setValorFinal(totalDocumento);
        gerenciado.setStatusVenda(StatusVenda.F);
        gerenciado.setCupomCancelado(SimNao.N);

        if (gerenciado.getTipoOperacao() == null) {
            gerenciado.setTipoOperacao("REC"); // Recibo (default; muda quando integrar NFC-e/SAT)
        }

        // ====== 3. Agrega tributos dos itens no cabeçalho ======
        // (VALOR_BASE_ICMS/VALOR_ICMS/etc do item já foram calculados na
        // inclusão/agrupamento — aqui só somamos pro total do documento.)
        PdvOperador operadorAtual = sessaoPdv.getOperadorAtual();
        List<PdvVendaDetalhe> itensAtivos = detalheRepository
                .findByPdvVendaCabecalhoAndCanceladoOrderByItemAsc(gerenciado, SimNao.N);
        agregarTributosNoCabecalho(gerenciado, itensAtivos);

        // ====== 4. Recalcula hash final do cabeçalho ======
        gerenciado.setHashRegistro(HashCalculator.hashDoCabecalho(gerenciado));
        gerenciado = cabecalhoRepository.save(gerenciado);

        // ====== 5. Baixa de estoque (fase 5 — livro-razão) ======
        // Só itens ativos (não cancelados) e marcados pra movimentar estoque.
        // Falha na baixa de um item não deve derrubar a venda inteira — já
        // foi paga; registramos o erro e seguimos, pra correção manual depois.
        for (PdvVendaDetalhe itemAtivo : itensAtivos) {
            if (itemAtivo.getMovimentaEstoque() != SimNao.S) continue;
            try {
                estoqueMovimentoService.registrarSaidaPorVenda(itemAtivo, operadorAtual);
            } catch (Exception ex) {
                LOGGER.error("Falha ao baixar estoque do item #{} (produto {}) do cupom {}: {}",
                        itemAtivo.getId(), itemAtivo.getProduto().getId(), gerenciado.getId(), ex.getMessage(), ex);
            }
        }

        // ====== 6. Lançamento de fiado (fase 8), se houver pagamento nesse tipo ======
        // Validado no início do método — aqui só registra o lançamento.
        if (totalFiado.compareTo(BigDecimal.ZERO) > 0) {
            try {
                fiadoService.registrarCompra(gerenciado.getCliente(), totalFiado, gerenciado, operadorAtual);
            } catch (Exception ex) {
                LOGGER.error("Falha ao registrar lançamento de fiado do cupom {}: {}",
                        gerenciado.getId(), ex.getMessage(), ex);
            }
        }

        LOGGER.info("Venda finalizada: cupom={}, totalDocumento={}, totalRecebido={}, troco={}, pagamentos={}",
                gerenciado.getId(), totalDocumento, totalRecebido, troco, pagamentos.size());

        return gerenciado;
    }

    // ====================================================================
    //  Helpers — Histórico
    // ====================================================================

    /**
     * Cria um histórico pre-preenchido com campos comuns. Caller
     * preenche os campos específicos (anterior/novo) conforme o tipo.
     */
    private PdvVendaDetalheHistorico novoHistoricoBase(PdvVendaDetalhe detalhe,
                                                       String tipo,
                                                       String motivo) {
        PdvVendaDetalheHistorico h = new PdvVendaDetalheHistorico();
        h.setIdPdvVendaDetalhe(detalhe.getId());
        h.setVersao(detalhe.getVersao() != null ? detalhe.getVersao() : 1);
        h.setDataHora(LocalDateTime.now());
        h.setTipoAlteracao(tipo);
        h.setIdDispositivo(dispositivoInfo.getHostname());
        h.setMotivo(motivo);
        return h;
    }

    /**
     * Grava histórico de INSERT inicial (sem estado anterior, só novo).
     */
    private void gravarHistoricoInsert(PdvVendaDetalhe detalhe, String motivo) {
        PdvVendaDetalheHistorico h = novoHistoricoBase(detalhe, TIPO_ADICIONADO, motivo);
        // Sem estado anterior (insert)
        h.setQuantidadeNova(detalhe.getQuantidade());
        h.setValorUnitarioNovo(detalhe.getValorUnitario());
        h.setValorTotalNovo(detalhe.getValorTotal());
        h.setCanceladoNovo("N");
        h.setHashNovo(detalhe.getHashRegistro());
        historicoRepository.save(h);
    }

    // ====================================================================
    //  Helpers — Cabeçalho
    // ====================================================================

    /**
     * Soma os tributos já calculados por item — ver
     * {@link CalculoTributarioService#aplicarEm} (chamado na inclusão/
     * agrupamento do item) — nos totalizadores do cabeçalho. Só considera
     * itens ativos: cancelados não entram no documento fiscal.
     */
    private void agregarTributosNoCabecalho(PdvVendaCabecalho cupom, List<PdvVendaDetalhe> itensAtivos) {
        BigDecimal baseIcms = BigDecimal.ZERO;
        BigDecimal icms = BigDecimal.ZERO;
        BigDecimal pis = BigDecimal.ZERO;
        BigDecimal cofins = BigDecimal.ZERO;
        BigDecimal issqn = BigDecimal.ZERO;

        for (PdvVendaDetalhe item : itensAtivos) {
            baseIcms = baseIcms.add(nullSafe(item.getValorBaseIcms()));
            icms = icms.add(nullSafe(item.getValorIcms()));
            pis = pis.add(nullSafe(item.getValorPis()));
            cofins = cofins.add(nullSafe(item.getValorCofins()));
            issqn = issqn.add(nullSafe(item.getValorIssqn()));
        }

        cupom.setValorBaseIcms(baseIcms);
        cupom.setValorIcms(icms);
        cupom.setValorPis(pis);
        cupom.setValorCofins(cofins);
        cupom.setValorIssqn(issqn);
    }

    private void validarAdicaoDeItem(PdvVendaCabecalho cupom,
                                     Produto produto,
                                     BigDecimal quantidade) {
        if (cupom == null) {
            throw new IllegalArgumentException("Cupom é obrigatório.");
        }
        if (!cupom.estaAberto()) {
            throw new IllegalStateException(
                    "Não é possível adicionar item a cupom com status " + cupom.getStatusVenda());
        }
        if (produto == null) {
            throw new IllegalArgumentException("Produto é obrigatório.");
        }
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        if (produto.getValorVenda() == null
                || produto.getValorVenda().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException(
                    "Produto " + produto.getId() + " sem valor de venda válido.");
        }
    }

    /**
     * Ajusta os totalizadores do cabeçalho somando um delta.
     * O delta pode ser positivo (novo item ou agrupamento aumentou valor)
     * ou negativo (agrupamento reduziu valor por faixa melhor).
     */
    private void ajustarCabecalhoSomandoDelta(PdvVendaCabecalho cupom, BigDecimal delta) {
        BigDecimal totalProdutos = nullSafe(cupom.getValorTotalProdutos()).add(delta);

        // Proteção contra negativo por erro de cálculo
        if (totalProdutos.compareTo(BigDecimal.ZERO) < 0) {
            totalProdutos = BigDecimal.ZERO;
        }

        cupom.setValorTotalProdutos(totalProdutos);
        cupom.setValorVenda(totalProdutos);
        cupom.setValorTotalDocumento(totalProdutos);

        cupom.setHashRegistro(HashCalculator.hashDoCabecalho(cupom));
        cabecalhoRepository.save(cupom);
    }

    /**
     * Subtrai o valor do item cancelado dos totais do cabeçalho e
     * acumula em VALOR_CANCELADO. Recalcula o hash.
     */
    private void subtrairItemDoCabecalho(PdvVendaCabecalho cupom, BigDecimal valorItem) {
        BigDecimal totalProdutos = nullSafe(cupom.getValorTotalProdutos()).subtract(valorItem);
        BigDecimal valorCancelado = nullSafe(cupom.getValorCancelado()).add(valorItem);

        if (totalProdutos.compareTo(BigDecimal.ZERO) < 0) {
            totalProdutos = BigDecimal.ZERO;
        }

        cupom.setValorTotalProdutos(totalProdutos);
        cupom.setValorVenda(totalProdutos);
        cupom.setValorTotalDocumento(totalProdutos);
        cupom.setValorCancelado(valorCancelado);

        cupom.setHashRegistro(HashCalculator.hashDoCabecalho(cupom));
        cabecalhoRepository.save(cupom);
    }

    private BigDecimal nullSafe(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    // ====================================================================
    //  Result type — retorno de adicionarItem
    // ====================================================================

    /**
     * Resultado de {@link #adicionarItem(PdvVendaCabecalho, Produto, BigDecimal)}.
     * Contém o detalhe persistido, flag de agrupamento, e o resultado da
     * faixa aplicada (pra UI mostrar o rótulo "ATACADO" e calcular economia).
     */
    public static class ResultadoAdicaoItem {
        private final PdvVendaDetalhe detalhe;
        private final boolean agrupado;
        private final FaixaPrecoService.ResultadoFaixa faixa;

        public ResultadoAdicaoItem(PdvVendaDetalhe detalhe,
                                   boolean agrupado,
                                   FaixaPrecoService.ResultadoFaixa faixa) {
            this.detalhe = detalhe;
            this.agrupado = agrupado;
            this.faixa = faixa;
        }

        public PdvVendaDetalhe getDetalhe() { return detalhe; }
        public boolean isAgrupado() { return agrupado; }
        public FaixaPrecoService.ResultadoFaixa getFaixa() { return faixa; }
    }

    /**
     * DTO de entrada do método {@link #finalizarVenda}.
     * Representa um pagamento individual lançado pelo operador na tela
     * de encerramento. Carrega o tipo (forma de pagamento) e o valor.
     *
     * <p>Campos opcionais (NSU, rede, cartaoDc) são preenchidos quando
     * o pagamento for por TEF. Por enquanto, ficam null.
     */
    public static class PagamentoLancado {
        private final TipoPagamento tipoPagamento;
        private final BigDecimal valor;
        private final String nsu;
        private final String rede;
        private final String cartaoDc;

        public PagamentoLancado(TipoPagamento tipoPagamento, BigDecimal valor) {
            this(tipoPagamento, valor, null, null, null);
        }

        public PagamentoLancado(TipoPagamento tipoPagamento,
                                BigDecimal valor,
                                String nsu,
                                String rede,
                                String cartaoDc) {
            this.tipoPagamento = tipoPagamento;
            this.valor = valor;
            this.nsu = nsu;
            this.rede = rede;
            this.cartaoDc = cartaoDc;
        }

        public TipoPagamento getTipoPagamento() { return tipoPagamento; }
        public BigDecimal getValor() { return valor; }
        public String getNsu() { return nsu; }
        public String getRede() { return rede; }
        public String getCartaoDc() { return cartaoDc; }
    }
}