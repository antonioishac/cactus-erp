package br.com.cactusdigital.erp.financeiro.entity;

/**
 * {@code ABERTA} (sem nenhuma baixa) → {@code PARCIAL} (baixa parcial registrada) →
 * {@code PAGA} (saldo zerado). {@code CANCELADA} a partir de {@code ABERTA}/
 * {@code PARCIAL}. Vencido NÃO é um valor desta enum — é calculado na resposta
 * ({@code dataVencimento < hoje} e situação ainda aberta/parcial), pra não precisar de
 * um job agendado só pra virar o estado.
 * <p>
 * {@code RENEGOCIADA} (2026-09-06, fecha gap contra {@code FIN_STATUS_PARCELA} do
 * {@code fenix.sql}, que prevê "05 = Renegociado") — terminal como {@code CANCELADA}
 * (sem baixa possível depois), mas semanticamente distinta: o saldo não sumiu, foi
 * substituído por título(s) novo(s) via {@code renegociar()} (ver
 * {@code ContaPagarService}/{@code ContaReceberService}), rastreável pelo novo título
 * ter {@code origemTipo=RENEGOCIACAO}/{@code origemId} apontando pro título original.
 */
public enum SituacaoTitulo {
    ABERTA,
    PARCIAL,
    PAGA,
    CANCELADA,
    RENEGOCIADA
}
