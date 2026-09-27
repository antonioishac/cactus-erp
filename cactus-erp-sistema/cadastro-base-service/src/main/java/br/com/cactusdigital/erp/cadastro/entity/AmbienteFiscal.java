package br.com.cactusdigital.erp.cadastro.entity;

/** Ambiente da SEFAZ para emissão fiscal (NFe/NFCe/NFSe) — nunca emitir em PRODUCAO sem confirmação explícita. */
public enum AmbienteFiscal {
    HOMOLOGACAO,
    PRODUCAO
}
