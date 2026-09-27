package br.com.cactuserp.pdv.service.dto;

import java.math.BigDecimal;

/**
 * DTO usado pela UI do PDV para representar uma linha (item) da venda
 * em andamento na {@code TableView} do {@code Pdv4Controller}.
 *
 * <p>Não é entidade JPA. Reflete o estado visível do item no momento.
 *
 * <p>Os campos {@code valorDesconto} e {@code valorAcrescimo} permitem
 * que o controller calcule os totalizadores agregados do painel de
 * resumo (Subtotal bruto, Desconto, Acréscimo, Total líquido) sem
 * precisar reconsultar o banco a cada operação.
 *
 * <p>Os campos {@code rotuloFaixa} e {@code precoVarejoOriginal} são
 * usados para o modelo ATACAREJO:
 * <ul>
 *   <li>{@code rotuloFaixa} = "ATACADO", "PACK 6+", etc. Null = preço varejo.</li>
 *   <li>{@code precoVarejoOriginal} = preço de tabela (Produto.VALOR_VENDA)
 *       antes da faixa. Usado para calcular economia visualmente.</li>
 * </ul>
 */
public class ProdutoVenda {

    private String item;
    private String codigo;
    private String descricao;
    private BigDecimal qtde;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private BigDecimal valorDesconto = BigDecimal.ZERO;
    private BigDecimal valorAcrescimo = BigDecimal.ZERO;
    private boolean cancelado;

    // === Campos novos do modelo atacarejo ===
    /** Rótulo da faixa aplicada (ex: "ATACADO"). Null se preço varejo. */
    private String rotuloFaixa;

    /** Preço de varejo original (Produto.VALOR_VENDA), pra calcular economia. */
    private BigDecimal precoVarejoOriginal;

    public ProdutoVenda(String item,
                        String codigo,
                        String descricao,
                        BigDecimal qtde,
                        BigDecimal valorUnitario,
                        BigDecimal valorTotal,
                        boolean cancelado) {
        this.item = item;
        this.codigo = codigo;
        this.descricao = descricao;
        this.qtde = qtde;
        this.valorUnitario = valorUnitario;
        this.valorTotal = valorTotal;
        this.cancelado = cancelado;
    }

    // ====================================================================
    //  Helpers de negócio
    // ====================================================================

    /** True se há faixa de atacado aplicada neste item. */
    public boolean estaEmFaixaAtacado() {
        return rotuloFaixa != null && !rotuloFaixa.isBlank();
    }

    /**
     * Economia gerada pela faixa de atacado neste item:
     * {@code qtde * (precoVarejoOriginal - valorUnitario)}.
     * Retorna ZERO se não há faixa aplicada ou faltam dados.
     */
    public BigDecimal calcularEconomia() {
        if (!estaEmFaixaAtacado()) return BigDecimal.ZERO;
        if (precoVarejoOriginal == null || valorUnitario == null || qtde == null) return BigDecimal.ZERO;

        BigDecimal diferencaUnit = precoVarejoOriginal.subtract(valorUnitario);
        if (diferencaUnit.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        return diferencaUnit.multiply(qtde);
    }

    // ====================================================================
    //  Getters / Setters
    // ====================================================================

    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getQtde() { return qtde; }
    public void setQtde(BigDecimal qtde) { this.qtde = qtde; }

    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public BigDecimal getValorDesconto() {
        return valorDesconto == null ? BigDecimal.ZERO : valorDesconto;
    }
    public void setValorDesconto(BigDecimal valorDesconto) { this.valorDesconto = valorDesconto; }

    public BigDecimal getValorAcrescimo() {
        return valorAcrescimo == null ? BigDecimal.ZERO : valorAcrescimo;
    }
    public void setValorAcrescimo(BigDecimal valorAcrescimo) { this.valorAcrescimo = valorAcrescimo; }

    public boolean isCancelado() { return cancelado; }
    public void setCancelado(boolean cancelado) { this.cancelado = cancelado; }

    public String getRotuloFaixa() { return rotuloFaixa; }
    public void setRotuloFaixa(String rotuloFaixa) { this.rotuloFaixa = rotuloFaixa; }

    public BigDecimal getPrecoVarejoOriginal() { return precoVarejoOriginal; }
    public void setPrecoVarejoOriginal(BigDecimal precoVarejoOriginal) {
        this.precoVarejoOriginal = precoVarejoOriginal;
    }
}