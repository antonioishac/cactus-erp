package br.com.cactusdigital.erp.vendas.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Campos que {@code VendaOrcamento} não carrega e {@code VendaCabecalho} exige — o
 * orçamento cobre condição de pagamento/vendedor/cliente/transportadora/frete/itens, mas
 * não o tipo de NF nem a forma de pagamento de fato usada no fechamento.
 */
public record GerarVendaDeOrcamentoRequest(
        @NotNull Long empresaId,
        @NotNull Long notaFiscalTipoId,
        @NotNull Long formaPagamentoId,
        LocalDate dataVenda,
        LocalDate dataSaida,
        String horaSaida,
        Integer numeroFatura) {
}
