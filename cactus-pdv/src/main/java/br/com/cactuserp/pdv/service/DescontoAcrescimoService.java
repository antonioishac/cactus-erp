package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.config.util.HashCalculator;
import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvConfiguracao;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.PdvVendaDetalhe;
import br.com.cactuserp.pdv.entity.SimNao;
import br.com.cactuserp.pdv.entity.TipoEntradaValor;
import br.com.cactuserp.pdv.entity.TipoOperacaoDescontoAcrescimo;
import br.com.cactuserp.pdv.repository.PdvConfiguracaoRepository;
import br.com.cactuserp.pdv.repository.PdvVendaCabecalhoRepository;
import br.com.cactuserp.pdv.repository.PdvVendaDetalheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço de aplicação de desconto e acréscimo, com regras fiscais
 * conformes ao PAF-NFC-e.
 *
 * <h3>Níveis de aplicação</h3>
 * <ul>
 *   <li><b>Em item (F6/F4)</b>: aplicado diretamente pelo operador num item.
 *       Grava em {@code VALOR_DESCONTO_ITEM} / {@code VALOR_ACRESCIMO_ITEM}.</li>
 *   <li><b>Na venda total (F6/F4 no encerramento)</b>: redistribuído
 *       proporcionalmente entre os itens. Grava em
 *       {@code VALOR_DESCONTO_RATEIO} / {@code VALOR_ACRESCIMO_RATEIO}.</li>
 * </ul>
 *
 * <p>Os campos {@code VALOR_DESCONTO} / {@code VALOR_ACRESCIMO} armazenam
 * a soma dos dois níveis (compatibilidade com NFC-e e relatórios existentes).
 *
 * <h3>Regras fiscais implementadas</h3>
 * <ul>
 *   <li><b>Dupla gravação</b> (R$ + %)</li>
 *   <li><b>Redistribuição proporcional</b> sobre o líquido pós-F6</li>
 *   <li><b>Ajuste de centavo</b>: último item absorve diferenças de arredondamento</li>
 *   <li><b>Limites configuráveis</b> via PdvConfiguracao</li>
 *   <li><b>Não permite descontar mais que o valor base</b></li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
public class DescontoAcrescimoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DescontoAcrescimoService.class);
    private static final BigDecimal CEM = new BigDecimal("100");

    private final PdvVendaDetalheRepository detalheRepository;
    private final PdvVendaCabecalhoRepository cabecalhoRepository;
    private final PdvConfiguracaoRepository configuracaoRepository;
    private final CalculoTributarioService calculoTributarioService;

    public DescontoAcrescimoService(PdvVendaDetalheRepository detalheRepository,
                                    PdvVendaCabecalhoRepository cabecalhoRepository,
                                    PdvConfiguracaoRepository configuracaoRepository,
                                    CalculoTributarioService calculoTributarioService) {
        this.detalheRepository = detalheRepository;
        this.cabecalhoRepository = cabecalhoRepository;
        this.configuracaoRepository = configuracaoRepository;
        this.calculoTributarioService = calculoTributarioService;
    }

    // ====================================================================
    //  Consulta de configuração
    // ====================================================================

    public PdvConfiguracao obterConfiguracao(PdvCaixa caixa) {
        if (caixa == null) {
            return new PdvConfiguracao();
        }
        return configuracaoRepository.findFirstByCaixaId(caixa.getId())
                .orElseGet(() -> {
                    LOGGER.warn("Caixa {} sem PDV_CONFIGURACAO. Usando defaults do mercado.",
                            caixa.getId());
                    return new PdvConfiguracao();
                });
    }

    // ====================================================================
    //  Verificação de autorização — ITEM
    // ====================================================================

    public boolean exigeAutorizacaoGerencialEmItem(TipoOperacaoDescontoAcrescimo tipo,
                                                   BigDecimal percentual,
                                                   PdvConfiguracao config) {
        if (percentual == null) return false;
        BigDecimal limite = (tipo == TipoOperacaoDescontoAcrescimo.DESCONTO)
                ? config.limiteDescontoItemPercentualOuPadrao()
                : config.limiteAcrescimoItemPercentualOuPadrao();
        return percentual.compareTo(limite) > 0;
    }

    public boolean proibidoMesmoComGerente(TipoOperacaoDescontoAcrescimo tipo,
                                           BigDecimal percentual,
                                           PdvConfiguracao config) {
        if (tipo != TipoOperacaoDescontoAcrescimo.DESCONTO) return false;
        if (config.permiteAcimaLimiteComGerente()) return false;
        return exigeAutorizacaoGerencialEmItem(tipo, percentual, config);
    }

    // ====================================================================
    //  Verificação de autorização — VENDA TOTAL
    // ====================================================================

    /**
     * Verifica se a operação na venda total ultrapassa o limite e exige
     * autorização gerencial. Considera tanto limite percentual quanto valor.
     */
    public boolean exigeAutorizacaoGerencialEmVenda(TipoOperacaoDescontoAcrescimo tipo,
                                                    BigDecimal valorRS,
                                                    BigDecimal percentual,
                                                    PdvConfiguracao config) {
        if (tipo == TipoOperacaoDescontoAcrescimo.DESCONTO) {
            BigDecimal limitePct  = config.limiteDescontoVendaPercentualOuPadrao();
            BigDecimal limiteRS   = config.limiteDescontoVendaValorOuPadrao();
            boolean excedePct    = percentual != null && percentual.compareTo(limitePct) > 0;
            boolean excedeValor  = valorRS != null && valorRS.compareTo(limiteRS) > 0;
            return excedePct || excedeValor;
        } else {
            BigDecimal limitePct = config.limiteAcrescimoVendaPercentualOuPadrao();
            return percentual != null && percentual.compareTo(limitePct) > 0;
        }
    }

    public boolean proibidoMesmoComGerenteVenda(TipoOperacaoDescontoAcrescimo tipo,
                                                BigDecimal valorRS,
                                                BigDecimal percentual,
                                                PdvConfiguracao config) {
        if (tipo != TipoOperacaoDescontoAcrescimo.DESCONTO) return false;
        if (config.permiteAcimaLimiteComGerente()) return false;
        return exigeAutorizacaoGerencialEmVenda(tipo, valorRS, percentual, config);
    }

    // ====================================================================
    //  Cálculos
    // ====================================================================

    public ResultadoCalculo calcular(BigDecimal valorBase,
                                     BigDecimal valorEntrada,
                                     TipoEntradaValor tipoEntrada) {
        if (valorBase == null || valorBase.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor base inválido para cálculo.");
        }
        if (valorEntrada == null || valorEntrada.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da operação deve ser maior que zero.");
        }

        BigDecimal valorRS;
        BigDecimal percentual;

        if (tipoEntrada == TipoEntradaValor.VALOR_RS) {
            valorRS = valorEntrada.setScale(2, RoundingMode.HALF_UP);
            percentual = valorEntrada
                    .multiply(CEM)
                    .divide(valorBase, 4, RoundingMode.HALF_UP);
        } else {
            percentual = valorEntrada.setScale(2, RoundingMode.HALF_UP);
            valorRS = valorBase
                    .multiply(valorEntrada)
                    .divide(CEM, 2, RoundingMode.HALF_UP);
        }

        return new ResultadoCalculo(valorRS, percentual);
    }

    // ====================================================================
    //  Aplicação em ITEM
    // ====================================================================

    @Transactional
    public PdvVendaDetalhe aplicarEmItem(PdvVendaDetalhe detalhe,
                                         TipoOperacaoDescontoAcrescimo tipo,
                                         BigDecimal valorEntrada,
                                         TipoEntradaValor tipoEntrada,
                                         PdvOperador autorizador) {

        if (detalhe == null || detalhe.getId() == null) {
            throw new IllegalArgumentException("Item de venda inválido.");
        }
        if (detalhe.getCancelado() == SimNao.S) {
            throw new IllegalStateException("Item cancelado não pode receber desconto/acréscimo.");
        }

        PdvVendaDetalhe d = detalheRepository.findById(detalhe.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Item #" + detalhe.getId() + " não encontrado."));

        BigDecimal valorBase = d.getValorUnitario().multiply(d.getQuantidade());

        ResultadoCalculo calc = calcular(valorBase, valorEntrada, tipoEntrada);

        if (tipo == TipoOperacaoDescontoAcrescimo.DESCONTO
                && calc.getValorRS().compareTo(valorBase) >= 0) {
            throw new IllegalStateException(
                    "Desconto não pode ser maior ou igual ao valor do item.");
        }

        // Guarda nos campos "_ITEM" — origem é o F6/F4 direto no item
        if (tipo == TipoOperacaoDescontoAcrescimo.DESCONTO) {
            d.setValorDescontoItem(calc.getValorRS());
            d.setTaxaDesconto(calc.getPercentual());
            d.setValorAcrescimoItem(BigDecimal.ZERO);
            d.setTaxaAcrescimo(BigDecimal.ZERO);
        } else {
            d.setValorAcrescimoItem(calc.getValorRS());
            d.setTaxaAcrescimo(calc.getPercentual());
            d.setValorDescontoItem(BigDecimal.ZERO);
            d.setTaxaDesconto(BigDecimal.ZERO);
        }

        // Atualiza campos consolidados (soma item + rateio existente)
        consolidarCamposDeItem(d);

        d = detalheRepository.save(d);

        recalcularTotaisDoCupom(d.getPdvVendaCabecalho());

        LOGGER.info("Item #{} recebeu {} de R$ {} ({}%). Autorizador: {}",
                d.getId(),
                tipo,
                calc.getValorRS(),
                calc.getPercentual(),
                autorizador != null ? autorizador.getId() : "—");

        return d;
    }

    // ====================================================================
    //  Aplicação na VENDA TOTAL (com redistribuição proporcional)
    // ====================================================================

    /**
     * Aplica desconto/acréscimo na venda total, redistribuindo
     * proporcionalmente entre os itens ativos.
     *
     * <p>A distribuição opera sobre o <b>valor líquido pós-desconto-de-item</b>:
     * itens que já receberam F6/F4 entram no rateio com seu valor líquido
     * (bruto - descontoItem + acrescimoItem).
     *
     * <p>O último item recebe o ajuste de centavo para garantir que a soma
     * dos rateios bata exatamente com o valor total da operação.
     *
     * @param cabecalho     cupom alvo
     * @param tipo          DESCONTO ou ACRESCIMO
     * @param valorEntrada  valor digitado pelo operador
     * @param tipoEntrada   R$ ou %
     * @param autorizador   gerente que autorizou (pode ser null se sob limite)
     */
    @Transactional
    public void aplicarEmVendaTotal(PdvVendaCabecalho cabecalho,
                                    TipoOperacaoDescontoAcrescimo tipo,
                                    BigDecimal valorEntrada,
                                    TipoEntradaValor tipoEntrada,
                                    PdvOperador autorizador) {

        if (cabecalho == null || cabecalho.getId() == null) {
            throw new IllegalArgumentException("Cupom inválido.");
        }

        // Primeiro: REMOVE rateio anterior (caso operador re-aplique).
        // Isso permite o operador editar/refazer o desconto da venda
        // sem precisar cancelar a venda inteira.
        removerRateioDaVenda(cabecalho, tipo);

        // Coleta itens ativos com seus valores líquidos pós-F6/F4
        List<PdvVendaDetalhe> itensAtivos = new ArrayList<>();
        BigDecimal subtotalLiquido = BigDecimal.ZERO;

        for (PdvVendaDetalhe item : detalheRepository.buscarDetalhesComProduto(cabecalho)) {
            if (item.getCancelado() == SimNao.S) continue;
            itensAtivos.add(item);
            BigDecimal liquido = calcularValorLiquidoPosItem(item);
            subtotalLiquido = subtotalLiquido.add(liquido);
        }

        if (itensAtivos.isEmpty()) {
            throw new IllegalStateException("Cupom sem itens ativos para aplicar operação.");
        }

        if (subtotalLiquido.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Subtotal líquido inválido para rateio.");
        }

        // Calcula valor total da operação
        ResultadoCalculo calc = calcular(subtotalLiquido, valorEntrada, tipoEntrada);
        BigDecimal valorOperacao = calc.getValorRS();

        // Validação fiscal: desconto não pode zerar/inverter a venda
        if (tipo == TipoOperacaoDescontoAcrescimo.DESCONTO
                && valorOperacao.compareTo(subtotalLiquido) >= 0) {
            throw new IllegalStateException(
                    "Desconto não pode ser maior ou igual ao subtotal da venda.");
        }

        // Distribui proporcionalmente
        BigDecimal somaDistribuida = BigDecimal.ZERO;
        int ultimoIndice = itensAtivos.size() - 1;

        for (int i = 0; i < itensAtivos.size(); i++) {
            PdvVendaDetalhe item = itensAtivos.get(i);
            BigDecimal liquidoItem = calcularValorLiquidoPosItem(item);

            BigDecimal rateio;
            if (i == ultimoIndice) {
                // Último item: absorve a diferença de arredondamento
                rateio = valorOperacao.subtract(somaDistribuida);
            } else {
                // Proporcional: (liquidoItem / subtotalLiquido) * valorOperacao
                rateio = liquidoItem
                        .multiply(valorOperacao)
                        .divide(subtotalLiquido, 2, RoundingMode.HALF_UP);
            }

            if (tipo == TipoOperacaoDescontoAcrescimo.DESCONTO) {
                item.setValorDescontoRateio(rateio);
                // Acréscimo de rateio fica zerado pra evitar conflito
                item.setValorAcrescimoRateio(BigDecimal.ZERO);
            } else {
                item.setValorAcrescimoRateio(rateio);
                item.setValorDescontoRateio(BigDecimal.ZERO);
            }

            consolidarCamposDeItem(item);
            detalheRepository.save(item);
            somaDistribuida = somaDistribuida.add(rateio);
        }

        recalcularTotaisDoCupom(cabecalho);

        LOGGER.info("Cupom #{} recebeu {} TOTAL de R$ {} ({}%) redistribuído entre {} itens. " +
                        "Autorizador: {}",
                cabecalho.getId(),
                tipo,
                valorOperacao,
                calc.getPercentual(),
                itensAtivos.size(),
                autorizador != null ? autorizador.getId() : "—");
    }

    /**
     * Remove o rateio (desconto OU acréscimo da venda) de todos os itens
     * do cupom. Usado antes de re-aplicar (o operador pode refazer).
     */
    @Transactional
    public void removerRateioDaVenda(PdvVendaCabecalho cabecalho,
                                     TipoOperacaoDescontoAcrescimo tipo) {
        for (PdvVendaDetalhe item : detalheRepository.buscarDetalhesComProduto(cabecalho)) {
            if (item.getCancelado() == SimNao.S) continue;

            if (tipo == TipoOperacaoDescontoAcrescimo.DESCONTO) {
                item.setValorDescontoRateio(BigDecimal.ZERO);
            } else {
                item.setValorAcrescimoRateio(BigDecimal.ZERO);
            }
            consolidarCamposDeItem(item);
            detalheRepository.save(item);
        }
    }

    // ====================================================================
    //  Helpers — consolidação e cálculo de valores
    // ====================================================================

    /**
     * Após mexer em qualquer _ITEM ou _RATEIO, atualiza os campos
     * consolidados ({@code VALOR_DESCONTO}, {@code VALOR_ACRESCIMO},
     * {@code VALOR_TOTAL}) que são lidos pela NFC-e e relatórios.
     *
     * <p>Também recalcula os tributos do item (ver
     * {@link CalculoTributarioService#aplicarEm}) sobre o novo
     * {@code VALOR_TOTAL} — a base de cálculo de ICMS/PIS/COFINS/ISSQN
     * muda junto com desconto/acréscimo, e este é o único ponto onde
     * {@code VALOR_TOTAL} do item é recalculado, então é aqui que o
     * tributo precisa acompanhar.
     */
    private void consolidarCamposDeItem(PdvVendaDetalhe d) {
        BigDecimal descItem    = nz(d.getValorDescontoItem());
        BigDecimal descRateio  = nz(d.getValorDescontoRateio());
        BigDecimal acrItem     = nz(d.getValorAcrescimoItem());
        BigDecimal acrRateio   = nz(d.getValorAcrescimoRateio());

        d.setValorDesconto(descItem.add(descRateio));
        d.setValorAcrescimo(acrItem.add(acrRateio));

        BigDecimal bruto = d.getValorUnitario().multiply(d.getQuantidade());
        BigDecimal liquido = bruto
                .subtract(d.getValorDesconto())
                .add(d.getValorAcrescimo());
        d.setValorTotal(liquido.setScale(2, RoundingMode.HALF_UP));

        calculoTributarioService.aplicarEm(d, d.getProduto(), d.getValorTotal());
    }

    /**
     * Valor líquido do item considerando APENAS o desconto/acréscimo
     * de item (sem rateio da venda).
     *
     * <p>Esse é o valor sobre o qual a redistribuição opera.
     */
    private BigDecimal calcularValorLiquidoPosItem(PdvVendaDetalhe item) {
        BigDecimal bruto = item.getValorUnitario().multiply(item.getQuantidade());
        BigDecimal descItem = nz(item.getValorDescontoItem());
        BigDecimal acrItem  = nz(item.getValorAcrescimoItem());
        return bruto.subtract(descItem).add(acrItem);
    }

    // ====================================================================
    //  Recálculo de totais do cupom (cabeçalho)
    // ====================================================================

    void recalcularTotaisDoCupom(PdvVendaCabecalho cabecalho) {
        if (cabecalho == null || cabecalho.getId() == null) return;

        List<PdvVendaDetalhe> itens = detalheRepository.buscarDetalhesComProduto(cabecalho);

        BigDecimal somaDesconto  = BigDecimal.ZERO;
        BigDecimal somaAcrescimo = BigDecimal.ZERO;
        BigDecimal somaCancelado = BigDecimal.ZERO;
        BigDecimal somaBruto     = BigDecimal.ZERO;
        BigDecimal somaLiquido   = BigDecimal.ZERO;

        for (PdvVendaDetalhe item : itens) {
            BigDecimal bruto = item.getValorUnitario().multiply(item.getQuantidade());

            if (item.getCancelado() == SimNao.S) {
                somaCancelado = somaCancelado.add(bruto);
                continue;
            }

            somaBruto = somaBruto.add(bruto);
            somaDesconto  = somaDesconto.add(nz(item.getValorDesconto()));
            somaAcrescimo = somaAcrescimo.add(nz(item.getValorAcrescimo()));
            somaLiquido   = somaLiquido.add(nz(item.getValorTotal()));
        }

        PdvVendaCabecalho c = cabecalhoRepository.findById(cabecalho.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Cupom #" + cabecalho.getId() + " não encontrado."));

        c.setValorDesconto(somaDesconto);
        c.setValorAcrescimo(somaAcrescimo);
        c.setValorCancelado(somaCancelado);
        c.setValorTotalProdutos(somaBruto);
        c.setValorTotalDocumento(somaLiquido);

        try {
            String hash = HashCalculator.sha256(
                    c.getId() + "|" + c.getValorTotalDocumento() + "|" + c.getValorDesconto());
            c.setHashRegistro(hash);
        } catch (Exception e) {
            LOGGER.warn("Falha ao recalcular hash do cupom #{}", c.getId(), e);
        }

        cabecalhoRepository.save(c);
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    // ====================================================================
    //  DTO simples para retorno do cálculo
    // ====================================================================

    public static class ResultadoCalculo {
        private final BigDecimal valorRS;
        private final BigDecimal percentual;

        public ResultadoCalculo(BigDecimal valorRS, BigDecimal percentual) {
            this.valorRS = valorRS;
            this.percentual = percentual;
        }

        public BigDecimal getValorRS() { return valorRS; }
        public BigDecimal getPercentual() { return percentual; }
    }
}