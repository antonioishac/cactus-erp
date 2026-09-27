package br.com.cactusdigital.erp.financeiro.ofx;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Uma linha {@code <STMTTRN>} do arquivo OFX, já com os tipos certos (data/valor). Ver
 * {@link OfxParser} pro parsing em si.
 *
 * @param tipo    {@code TRNTYPE} cru (DEBIT/CREDIT/CHECK/XFER/PAYMENT/FEE/INT/OTHER...) —
 *                não convertido pra enum de propósito, o domínio de valores do OFX é
 *                maior do que vale a pena fechar num enum próprio agora.
 * @param data    {@code DTPOSTED}, só a parte de data (hora ignorada).
 * @param valor   {@code TRNAMT} — SINAL PRESERVADO como vem no arquivo (negativo = saída,
 *                positivo = entrada), diferente do resto do sistema onde valor de
 *                {@code MovimentoCaixaBanco} é sempre positivo. Convertido na hora de
 *                gerar um ajuste (ver {@code ConciliacaoBancariaService#gerarAjuste}).
 * @param fitId   {@code FITID} — identificador único da transação dentro do extrato do
 *                banco, usado como chave de dedup entre importações.
 * @param memo    {@code MEMO} (ou {@code NAME} quando MEMO ausente) — histórico textual.
 */
public record OfxTransacao(String tipo, LocalDate data, BigDecimal valor, String fitId, String memo) {
}
