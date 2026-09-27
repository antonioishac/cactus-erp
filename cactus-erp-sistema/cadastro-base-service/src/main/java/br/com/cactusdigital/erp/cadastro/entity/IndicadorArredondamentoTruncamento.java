package br.com.cactusdigital.erp.cadastro.entity;

/**
 * {@code IAT} do {@code PRODUTO} do cactus-pdv — como o valor unitário fracionado (ex:
 * preço por grama de um item vendido a granel) é arredondado na hora de compor o total
 * do cupom.
 */
public enum IndicadorArredondamentoTruncamento {
    ARREDONDAMENTO('A'),
    TRUNCAMENTO('T');

    private final char codigo;

    IndicadorArredondamentoTruncamento(char codigo) {
        this.codigo = codigo;
    }

    public char getCodigo() {
        return codigo;
    }

    public static IndicadorArredondamentoTruncamento fromCodigo(char codigo) {
        for (IndicadorArredondamentoTruncamento indicador : values()) {
            if (indicador.codigo == codigo) {
                return indicador;
            }
        }
        throw new IllegalArgumentException("Código de IAT inválido: " + codigo);
    }
}
