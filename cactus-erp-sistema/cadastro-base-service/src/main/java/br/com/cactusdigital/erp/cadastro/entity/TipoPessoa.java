package br.com.cactusdigital.erp.cadastro.entity;

public enum TipoPessoa {
    FISICA('F'),
    JURIDICA('J');

    private final char codigo;

    TipoPessoa(char codigo) {
        this.codigo = codigo;
    }

    public char getCodigo() {
        return codigo;
    }

    public static TipoPessoa fromCodigo(char codigo) {
        for (TipoPessoa tipo : values()) {
            if (tipo.codigo == codigo) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código de tipo de pessoa inválido: " + codigo);
    }
}
