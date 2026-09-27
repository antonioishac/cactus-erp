package br.com.cactusdigital.erp.financeiro.dto;

import java.util.List;

/** Os dois lados da conciliação, lado a lado — {@code itensExtratoPendentes} (o banco diz
 * que aconteceu, ainda sem lançamento interno correspondente encontrado) e
 * {@code movimentosPendentes} (lançamos internamente, ainda sem confirmação do extrato
 * real do banco). */
public record PendenciasConciliacaoResponse(
        List<ExtratoBancarioItemResponse> itensExtratoPendentes,
        List<MovimentoCaixaBancoResponse> movimentosPendentes) {
}
