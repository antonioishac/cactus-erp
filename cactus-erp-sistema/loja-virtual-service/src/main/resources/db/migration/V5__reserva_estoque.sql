-- Fase 13, sub-fase 4 — reserva de estoque real, substitui o "soft check" da sub-fase 2
-- (ver Javadoc de VarianteProdutoLoja/CarrinhoService). estoque_reservado é a quantidade
-- comprometida por carrinho ABERTO ou Pedido ainda não cancelado/entregue; disponível =
-- estoque_quantidade - estoque_reservado (calculado, nunca persistido).

ALTER TABLE variante_produto_loja
    ADD COLUMN estoque_reservado DECIMAL(18,6) NOT NULL DEFAULT 0
        COMMENT 'Quantidade comprometida por carrinho ABERTO ou Pedido AGUARDANDO_PAGAMENTO/PAGO/EM_SEPARACAO'
        AFTER estoque_quantidade;
