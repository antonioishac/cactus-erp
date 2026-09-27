package br.com.cactusdigital.erp.fiscal.client.dto;

import java.math.BigDecimal;

/** Espelha o subconjunto do {@code ItemPedidoResponse} (loja-virtual-service) necessário pra montar o item da NFC-e — cópia deliberada, não dependência Java (limite de serviço). */
public record ItemPedidoLojaClienteDTO(
        Long id,
        Long varianteProdutoLojaId,
        String skuSnapshot,
        String nomeSnapshot,
        String ncmSnapshot,
        String csosnSnapshot,
        BigDecimal quantidade,
        BigDecimal precoUnitario,
        BigDecimal valorTotal) {
}
