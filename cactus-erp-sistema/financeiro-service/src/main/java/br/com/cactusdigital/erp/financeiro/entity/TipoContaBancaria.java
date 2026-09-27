package br.com.cactusdigital.erp.financeiro.entity;

/**
 * Domínio de valores de mercado (não específico de nenhuma fonte legada) — diferente de
 * {@code pafPSt}/{@code tipoCobertura} (outros módulos), que ficaram como string crua por
 * falta de confirmação, "conta corrente/poupança/caixa interno/aplicação" é terminologia
 * bancária universal, segura para modelar como enum fechado.
 */
public enum TipoContaBancaria {

    /** Conta corrente em banco real (tem {@code bancoId}). */
    CORRENTE,

    /** Conta poupança em banco real. */
    POUPANCA,

    /** Caixa físico/dinheiro em espécie — sem banco associado, {@code bancoId} nulo. */
    CAIXA_INTERNO,

    /** Conta de aplicação financeira (CDB, fundo, Tesouro Direto etc.) — ver {@code AplicacaoFinanceira}. */
    APLICACAO
}
