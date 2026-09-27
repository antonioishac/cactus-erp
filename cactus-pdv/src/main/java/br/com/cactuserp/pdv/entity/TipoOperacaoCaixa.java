package br.com.cactuserp.pdv.entity;

/**
 * Tipo de operação de movimentação de caixa.
 *
 * <p>Usado para parametrizar a tela e o service que tratam sangria e
 * suprimento de forma compartilhada (operações irmãs com fluxos quase
 * idênticos).
 *
 * <ul>
 *   <li>{@link #SUPRIMENTO} — entrada de dinheiro no caixa (reforço de troco).
 *       Aumenta o saldo esperado. Baixo risco.</li>
 *   <li>{@link #SANGRIA} — saída de dinheiro do caixa (depósito, cofre).
 *       Reduz o saldo esperado. Operação sensível, pode exigir autorização
 *       gerencial conforme valor.</li>
 * </ul>
 */
public enum TipoOperacaoCaixa {

    SUPRIMENTO("Suprimento", "Adicionar dinheiro ao caixa"),
    SANGRIA("Sangria", "Retirar dinheiro do caixa");

    private final String rotulo;
    private final String descricao;

    TipoOperacaoCaixa(String rotulo, String descricao) {
        this.rotulo = rotulo;
        this.descricao = descricao;
    }

    public String getRotulo() {
        return rotulo;
    }

    public String getDescricao() {
        return descricao;
    }
}