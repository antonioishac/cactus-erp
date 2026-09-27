package br.com.cactuserp.pdv.entity;

/**
 * Identifica se a operação é um desconto (reduz valor) ou
 * acréscimo (aumenta valor — juros, taxa, etc).
 */
public enum TipoOperacaoDescontoAcrescimo {
    DESCONTO("Desconto"),
    ACRESCIMO("Acréscimo");

    private final String rotulo;

    TipoOperacaoDescontoAcrescimo(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}