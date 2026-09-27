package br.com.cactuserp.pdv.entity;

/**
 * Enum genérico para mapear colunas {@code CHAR(1)} com valores 'S' (Sim) ou 'N' (Não).
 *
 * <p>Usado em campos como {@code CANCELADO}, {@code CUPOM_CANCELADO},
 * {@code MOVIMENTA_ESTOQUE}, {@code ESTORNO}, etc.
 *
 * <p>Para usar em uma entidade JPA:
 * <pre>
 * {@literal @}Enumerated(EnumType.STRING)
 * {@literal @}Column(name = "CANCELADO", length = 1, columnDefinition = "CHAR(1)")
 * private SimNao cancelado;
 * </pre>
 */
public enum SimNao {

    S, // Sim
    N; // Não

    /** Converte um boolean Java para o enum (true → S, false → N). */
    public static SimNao de(boolean valor) {
        return valor ? S : N;
    }

    /** Indica se o valor representa "Sim". */
    public boolean ehSim() {
        return this == S;
    }

    /** Indica se o valor representa "Não" (ou seja, NÃO foi marcado como Sim). */
    public boolean ehNao() {
        return this == N;
    }
}