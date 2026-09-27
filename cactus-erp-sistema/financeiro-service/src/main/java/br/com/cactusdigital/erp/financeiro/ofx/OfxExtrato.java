package br.com.cactusdigital.erp.financeiro.ofx;

import java.util.List;

/** Resultado do parsing de um arquivo OFX — {@code bancoId}/{@code agencia}/{@code conta}
 * são só informativos (do bloco {@code <BANKACCTFROM>}, podem vir nulos), a conta bancária
 * de destino da importação é sempre a informada explicitamente pelo operador no upload,
 * nunca inferida do arquivo (evita importar pro lugar errado por um dado incorreto do
 * próprio banco). */
public record OfxExtrato(String bancoId, String agencia, String conta, List<OfxTransacao> transacoes) {
}
