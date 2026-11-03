package br.com.cactuserp.pdv.entity;

import java.util.Set;

/**
 * Valores de {@code PDV_VENDA_CABECALHO.STATUS_FISCAL} — fonte única (antes repetidos como strings soltas em
 * {@code EmissaoFiscalService}, {@code ReprocessamentoFiscalService} e {@code CancelamentoVendaService}). Coluna
 * {@code CHAR(1)} mantida como {@code String} na entidade para não quebrar linhas antigas ({@code NULL}).
 */
public final class StatusFiscal {

    /** Terminal sem NFC-e habilitada: a venda fica com recibo. */
    public static final String NAO_HABILITADA = "N";
    /** Autorizada pela SEFAZ (emissão normal, ou contingência offline já transmitida). */
    public static final String AUTORIZADA = "E";
    /** Emitida em contingência offline (tpEmis 9), impressa, aguardando transmissão — prazo legal de 24h. */
    public static final String CONTINGENCIA_OFFLINE = "O";
    /** A SEFAZ rejeitou (online ou na transmissão da offline): precisa de correção antes de reenviar. */
    public static final String REJEITADA = "R";
    /** Uso denegado (110/301/302/303). Número consumido, XML guardado. Terminal. */
    public static final String DENEGADA = "D";
    /** Falha antes de assinar (certificado, cadastro, leiaute): o reprocessamento tenta de novo. */
    public static final String PENDENTE = "P";
    /** Venda cancelada antes de ter documento fiscal. */
    public static final String CANCELADA_SEM_EMISSAO = "C";

    /** Situações em que o job ainda tem algo a fazer pela venda. */
    public static final Set<String> A_PROCESSAR = Set.of(PENDENTE, CONTINGENCIA_OFFLINE);

    private StatusFiscal() {
    }
}
