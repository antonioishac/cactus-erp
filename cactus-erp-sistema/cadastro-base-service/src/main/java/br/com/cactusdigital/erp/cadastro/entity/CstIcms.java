package br.com.cactusdigital.erp.cadastro.entity;

/**
 * Situação Tributária do ICMS — Regime Normal (Convênio s/nº 70/1997, Ajuste SINIEF
 * 07/2001). Tabela pequena e fixa da legislação — modelada como enum (não tabela),
 * igual {@link br.com.cactusdigital.erp.common.domain.Situacao}.
 */
public enum CstIcms {
    CST_00("00", "Tributada integralmente"),
    CST_10("10", "Tributada e com cobrança do ICMS por substituição tributária"),
    CST_20("20", "Com redução de base de cálculo"),
    CST_30("30", "Isenta ou não tributada e com cobrança do ICMS por substituição tributária"),
    CST_40("40", "Isenta"),
    CST_41("41", "Não tributada"),
    CST_50("50", "Suspensão"),
    CST_51("51", "Diferimento"),
    CST_60("60", "ICMS cobrado anteriormente por substituição tributária"),
    CST_70("70", "Com redução de base de cálculo e cobrança do ICMS por substituição tributária"),
    CST_90("90", "Outras");

    private final String codigo;
    private final String descricao;

    CstIcms(String codigo, String descricao) {
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
