package br.com.cactusdigital.erp.cadastro.entity;

/** Código de Situação da Operação — Simples Nacional. Usado em vez de {@link CstIcms} quando CRT=1/2. */
public enum Csosn {
    CSOSN_101("101", "Tributada pelo Simples Nacional com permissão de crédito"),
    CSOSN_102("102", "Tributada pelo Simples Nacional sem permissão de crédito"),
    CSOSN_103("103", "Isenção do ICMS no Simples Nacional para faixa de receita bruta"),
    CSOSN_201("201", "Tributada pelo Simples Nacional com permissão de crédito e com cobrança do ICMS por ST"),
    CSOSN_202("202", "Tributada pelo Simples Nacional sem permissão de crédito e com cobrança do ICMS por ST"),
    CSOSN_203("203", "Isenção do ICMS no Simples Nacional para faixa de receita bruta e com cobrança do ICMS por ST"),
    CSOSN_300("300", "Imune"),
    CSOSN_400("400", "Não tributada pelo Simples Nacional"),
    CSOSN_500("500", "ICMS cobrado anteriormente por substituição tributária ou por antecipação"),
    CSOSN_900("900", "Outros");

    private final String codigo;
    private final String descricao;

    Csosn(String codigo, String descricao) {
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
