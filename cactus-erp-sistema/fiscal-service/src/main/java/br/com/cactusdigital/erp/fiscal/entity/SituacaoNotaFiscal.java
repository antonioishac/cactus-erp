package br.com.cactusdigital.erp.fiscal.entity;

/**
 * {@code RASCUNHO} (composta a partir da Venda, ainda não enviada) → (emitir) →
 * {@code ENVIADA} (transmitida, aguardando o retorno síncrono da SEFAZ) →
 * {@code AUTORIZADA}/{@code DENEGADA}/{@code REJEITADA} (retorno da SEFAZ)/{@code ERRO} (falha de
 * transporte/comunicação, não uma rejeição fiscal de verdade) → {@code CANCELADA} (fora de
 * escopo desta fase, ver Javadoc de {@code NotaFiscalEletronicaService}).
 */
public enum SituacaoNotaFiscal {
    RASCUNHO,
    ENVIADA,
    AUTORIZADA,
    /** NF-e: uso denegado pela SEFAZ (cStat 110/301/302/303 — irregularidade fiscal de emitente ou destinatário). O número
     * fica consumido e o XML denegado tem que ser guardado como o autorizado. Terminal. */
    DENEGADA,
    REJEITADA,
    ERRO,
    CANCELADA,
    /** Só MDF-e: viagem concluída, evento de Encerramento (110112) registrado na SEFAZ. Terminal. */
    ENCERRADA
}
