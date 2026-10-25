package br.com.cactusdigital.erp.financeiro.entity;

/** Por que o {@link MovimentoCaixaBanco} existe — {@code origemId} aponta pro id da
 * entidade correspondente quando fizer sentido (baixa, transferência, aplicação, cheque). */
public enum OrigemMovimentoCaixa {
    /** {@code origemId} = {@code BaixaContaReceber.id}. */
    BAIXA_CONTA_RECEBER,
    /** {@code origemId} = {@code BaixaContaPagar.id}. */
    BAIXA_CONTA_PAGAR,
    /** {@code origemId} = {@code BaixaContaReceber.id} estornada. */
    ESTORNO_BAIXA_RECEBER,
    /** {@code origemId} = {@code BaixaContaPagar.id} estornada. */
    ESTORNO_BAIXA_PAGAR,
    /** {@code origemId} = {@code TransferenciaBancaria.id}. */
    TRANSFERENCIA,
    /** {@code origemId} = {@code AplicacaoFinanceira.id}. */
    APLICACAO_FINANCEIRA,
    RESGATE_APLICACAO,
    /** {@code origemId} = {@code ChequeEmitido.id} ou {@code ChequeRecebido.id}. */
    CHEQUE_COMPENSADO,
    CHEQUE_DEVOLVIDO,
    /** Lançamento manual de ajuste de saldo (tarifa, IOF, correção) — sem origem, {@code origemId} nulo. */
    AJUSTE,
    /** Gerado a partir de uma linha de extrato bancário sem lançamento interno
     * correspondente (tarifa cobrada pelo banco sem aviso prévio, etc) — {@code origemId}
     * = {@code ExtratoBancarioItem.id}. Ver {@code ConciliacaoBancariaService#gerarAjuste}. */
    CONCILIACAO,
    /** Fase 6-B (PDV → ERP): {@code origemId} = {@code RegistroSincronizacao.id} no sincronizacao-service
     * (a chave de idempotência do envio). Sempre na conta {@code CAIXA_INTERNO} do terminal. */
    SANGRIA_PDV,
    SUPRIMENTO_PDV,
    /** Fundo de troco com que o terminal abre o turno ({@code PDV_MOVIMENTO.VALOR_FUNDO_INICIAL}). */
    ABERTURA_CAIXA_PDV,
    /** Sobra (ENTRADA) ou falta (SAIDA) apurada na conferência de caixa do terminal. */
    CONFERENCIA_CAIXA_PDV
}
