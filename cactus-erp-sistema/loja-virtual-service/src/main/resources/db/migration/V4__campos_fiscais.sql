-- Fase 13, sub-fase 3 — campos mínimos pra emitir NFC-e a partir de um Pedido pago.
-- empresa_id vincula o tenant a uma Empresa/EmpresaConfiguracaoFiscal do
-- cadastro-base-service; ncm/csosn assumem Simples Nacional (o regime da esmagadora
-- maioria dos pequenos lojistas de e-commerce) — ver Javadoc de VarianteProdutoLoja.

ALTER TABLE loja
    ADD COLUMN empresa_id BIGINT NULL COMMENT 'FK solta pro Empresa do cadastro-base-service' AFTER cnpj;

ALTER TABLE variante_produto_loja
    ADD COLUMN ncm   VARCHAR(8) NULL AFTER peso_kg,
    ADD COLUMN csosn VARCHAR(3) NULL AFTER ncm;

ALTER TABLE item_pedido
    ADD COLUMN ncm_snapshot   VARCHAR(8) NULL AFTER nome_snapshot,
    ADD COLUMN csosn_snapshot VARCHAR(3) NULL AFTER ncm_snapshot;
