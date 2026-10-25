package br.com.cactusdigital.erp.fiscal.entity;

/**
 * {@code RASCUNHO} (composta a partir da Venda, ainda não enviada) → (emitir) →
 * {@code ENVIADA} (comando disparado, aguardando retorno síncrono do ACBr Monitor) →
 * {@code AUTORIZADA}/{@code REJEITADA} (retorno da SEFAZ, via ACBr)/{@code ERRO} (falha de
 * transporte/comunicação, não uma rejeição fiscal de verdade) → {@code CANCELADA} (fora de
 * escopo desta fase, ver Javadoc de {@code NotaFiscalEletronicaService}).
 */
public enum SituacaoNotaFiscal {
    RASCUNHO,
    ENVIADA,
    AUTORIZADA,
    REJEITADA,
    ERRO,
    CANCELADA,
    /** Só MDF-e: viagem concluída, evento de Encerramento (110112) registrado na SEFAZ. Terminal. */
    ENCERRADA
}
