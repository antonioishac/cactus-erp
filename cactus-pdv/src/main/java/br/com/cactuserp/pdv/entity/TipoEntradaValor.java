package br.com.cactuserp.pdv.entity;

/**
 * Identifica como o operador entrou com o valor do desconto/acréscimo:
 * <ul>
 *   <li>{@code VALOR_RS}: valor absoluto em reais (ex: R$ 5,00)</li>
 *   <li>{@code PERCENTUAL}: percentual sobre o valor base (ex: 10%)</li>
 * </ul>
 *
 * <p>Independentemente de qual o operador escolha, o sistema calcula a
 * outra grandeza automaticamente e grava ambas no banco (exigência fiscal).
 */
public enum TipoEntradaValor {
    VALOR_RS("R$"),
    PERCENTUAL("%");

    private final String simbolo;

    TipoEntradaValor(String simbolo) {
        this.simbolo = simbolo;
    }

    public String getSimbolo() {
        return simbolo;
    }
}