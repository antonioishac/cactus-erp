package br.com.cactusdigital.erp.loja.dto;

import br.com.cactusdigital.erp.loja.entity.FormaPagamentoLoja;
import br.com.cactusdigital.erp.loja.entity.SituacaoPedido;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PedidoResponse(
        Long id,
        Long lojaId,
        Long clienteLojaId,
        String clienteNome,
        String clienteEmail,
        String clienteCpf,
        Long carrinhoId,
        String logradouroEntrega,
        String numeroEntrega,
        String complementoEntrega,
        String bairroEntrega,
        String cepEntrega,
        String municipioEntrega,
        String ufEntrega,
        FormaPagamentoLoja formaPagamento,
        BigDecimal valorSubtotal,
        BigDecimal valorFrete,
        BigDecimal valorTotal,
        SituacaoPedido situacao,
        Instant dataPagamento,
        String motivoCancelamento,
        List<ItemPedidoResponse> itens,
        Instant dataCriacao) {
}
