package br.com.cactusdigital.erp.common.domain;

/**
 * Situação (ativo/inativo) de um registro cadastral.
 * <p>
 * Substitui o padrão legado de {@code CHAR(1)} solto sem tipagem usado no cactus-pdv
 * (ex: {@code SITUACAO char(1) COMMENT 'A=ATIVO / I=INATIVO'}) por um enum Java forte,
 * mantendo a mesma representação compacta no banco via {@link SituacaoConverter}.
 */
public enum Situacao {

    ATIVO('A'),
    INATIVO('I');

    private final char codigo;

    Situacao(char codigo) {
        this.codigo = codigo;
    }

    public char getCodigo() {
        return codigo;
    }

    public static Situacao fromCodigo(char codigo) {
        for (Situacao situacao : values()) {
            if (situacao.codigo == codigo) {
                return situacao;
            }
        }
        throw new IllegalArgumentException("Código de situação inválido: " + codigo);
    }
}
