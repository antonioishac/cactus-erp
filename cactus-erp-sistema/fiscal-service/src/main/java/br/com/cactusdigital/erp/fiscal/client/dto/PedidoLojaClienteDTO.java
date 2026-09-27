package br.com.cactusdigital.erp.fiscal.client.dto;

import java.math.BigDecimal;
import java.util.List;

/** Espelha o subconjunto do {@code PedidoResponse} (loja-virtual-service) necessário pra montar a NFC-e. */
public record PedidoLojaClienteDTO(
        Long id,
        Long lojaId,
        Long clienteLojaId,
        String clienteNome,
        String clienteEmail,
        String clienteCpf,
        String logradouroEntrega,
        String numeroEntrega,
        String complementoEntrega,
        String bairroEntrega,
        String cepEntrega,
        String municipioEntrega,
        String ufEntrega,
        String formaPagamento,
        BigDecimal valorSubtotal,
        BigDecimal valorFrete,
        BigDecimal valorTotal,
        String situacao,
        List<ItemPedidoLojaClienteDTO> itens) {
}
