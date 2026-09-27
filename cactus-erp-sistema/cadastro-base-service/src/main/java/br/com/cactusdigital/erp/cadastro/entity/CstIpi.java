package br.com.cactusdigital.erp.cadastro.entity;

/** Situação Tributária do IPI. Só os códigos de SAÍDA (venda) — entrada (compra) fica pro módulo de Compras. */
public enum CstIpi {
    CST_50("50", "Saída tributada"),
    CST_51("51", "Saída tributada com alíquota zero"),
    CST_52("52", "Saída isenta"),
    CST_53("53", "Saída não-tributada"),
    CST_54("54", "Saída imune"),
    CST_55("55", "Saída com suspensão"),
    CST_99("99", "Outras saídas");

    private final String codigo;
    private final String descricao;

    CstIpi(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }
}
