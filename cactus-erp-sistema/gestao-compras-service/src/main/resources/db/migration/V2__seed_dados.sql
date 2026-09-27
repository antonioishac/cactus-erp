-- Configuração padrão de compras pra empresa demo (empresa_id=1, mesma premissa já usada
-- em outros seeds do monorepo: a empresa demo semeada pelo cadastro-base-service é
-- sempre a primeira linha). Limite de aprovação de R$ 5.000,00 — acima disso, o pedido de
-- compra exige aprovação nominal (ver PedidoCompra.situacao=AGUARDANDO_APROVACAO).
INSERT INTO configuracao_compras_empresa (empresa_id, limite_aprovacao_pedido, data_criacao, data_atualizacao, criado_por)
VALUES (1, 5000.00, NOW(6), NOW(6), 'seed');
