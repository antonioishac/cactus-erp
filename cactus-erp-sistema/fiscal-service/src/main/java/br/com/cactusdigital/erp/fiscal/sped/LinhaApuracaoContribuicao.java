package br.com.cactusdigital.erp.fiscal.sped;

import java.math.BigDecimal;

/** Uma linha agregada dos registros de detalhamento de apuração ({@code M210}/{@code M610}) da EFD Contribuições — agrupada por CST, somando base de cálculo e valor da contribuição (PIS/COFINS) de todos os itens do período que caem nesse CST. */
record LinhaApuracaoContribuicao(String cst, BigDecimal baseCalculo, BigDecimal valorContribuicao) {
}
