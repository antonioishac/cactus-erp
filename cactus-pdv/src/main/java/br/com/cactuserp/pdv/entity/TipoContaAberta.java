package br.com.cactuserp.pdv.entity;

/**
 * Tipo de conta aberta.
 *
 * <p>Define qual fluxo se aplica e quais campos do
 * {@link ContaAberta} são obrigatórios.
 */
public enum TipoContaAberta {
    /** Mesa de restaurante / pizzaria — vincula a {@link Mesa}. */
    M,
    /** Comanda (padaria / bar) — usa código físico ou virtual. */
    C,
    /** Pedido de delivery — exige endereço e geralmente cliente. */
    D;

    public String getRotulo() {
        switch (this) {
            case M: return "Mesa";
            case C: return "Comanda";
            case D: return "Delivery";
            default: return name();
        }
    }
}