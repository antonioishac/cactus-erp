package br.com.cactuserp.pdv.entity;

/**
 * Origem da venda — rastreia de onde veio.
 *
 * <p>Cabeçalho de venda ({@link PdvVendaCabecalho}) sempre tem uma origem.
 * Quando origem ≠ {@link #B}, há vínculo com uma {@link ContaAberta}.
 */
public enum OrigemVenda {

    /** Cliente direto no balcão — bipou no caixa. */
    B,
    /** Importada de mesa. */
    M,
    /** Importada de comanda. */
    C,
    /** Importada de pedido delivery. */
    D,
    /** Externa — app, e-commerce, integração futura. */
    E;

    public String getRotulo() {
        switch (this) {
            case B: return "Balcão";
            case M: return "Mesa";
            case C: return "Comanda";
            case D: return "Delivery";
            case E: return "Externa";
            default: return name();
        }
    }
}