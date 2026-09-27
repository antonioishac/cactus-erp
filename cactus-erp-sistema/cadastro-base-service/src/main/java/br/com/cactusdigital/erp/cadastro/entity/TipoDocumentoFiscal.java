package br.com.cactusdigital.erp.cadastro.entity;

/** Qual das 4 séries numéricas de {@link EmpresaConfiguracaoFiscal} — usado por {@link NumeroFiscalPulado} pra identificar qual contador teve um número pulado. */
public enum TipoDocumentoFiscal {
    NFE,
    NFCE,
    NFSE,
    CTE
}
