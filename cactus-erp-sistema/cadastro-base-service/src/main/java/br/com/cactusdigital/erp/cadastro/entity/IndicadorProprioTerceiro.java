package br.com.cactusdigital.erp.cadastro.entity;

/** {@code IPPT} do {@code PRODUTO} do cactus-pdv — mercadoria própria ou de terceiro (consignação). */
public enum IndicadorProprioTerceiro {
    PROPRIO('P'),
    TERCEIRO('T');

    private final char codigo;

    IndicadorProprioTerceiro(char codigo) {
        this.codigo = codigo;
    }

    public char getCodigo() {
        return codigo;
    }

    public static IndicadorProprioTerceiro fromCodigo(char codigo) {
        for (IndicadorProprioTerceiro indicador : values()) {
            if (indicador.codigo == codigo) {
                return indicador;
            }
        }
        throw new IllegalArgumentException("Código de IPPT inválido: " + codigo);
    }
}
