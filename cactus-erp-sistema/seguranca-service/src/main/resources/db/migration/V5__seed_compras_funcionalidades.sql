-- Funcionalidades do gestao-compras-service — módulo COMPRAS já existia semeado
-- (V3__seed_dados.sql) desde a extração deste serviço, só sem funcionalidades ainda.
-- Catálogo central de RBAC mora aqui mesmo protegendo endpoint de outro serviço — ver
-- CLAUDE.md, seção da extração do seguranca-service.
--
-- Ao contrário de V4__seed_estoque_funcionalidades.sql (que assumiu, errado, que ADMIN/
-- GERENTE/OPERADOR pegariam essas funcionalidades novas "automaticamente" — não pegam,
-- ver V6__backfill_estoque_papel_funcionalidade.sql), esta migration já insere os grants
-- de ADMIN/GERENTE/OPERADOR explicitamente, no mesmo arquivo que cria as funcionalidades.

INSERT INTO funcionalidade (modulo_id, codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT m.id, f.codigo, f.nome, 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM modulo m
JOIN (
  SELECT 'COMPRAS_CONSULTAR'  AS codigo, 'Consultar requisições, cotações, pedidos e recebimentos de compra' AS nome UNION ALL
  SELECT 'COMPRAS_REQUISITAR',              'Criar e cancelar requisição de compra'                                    UNION ALL
  SELECT 'COMPRAS_COTAR',                   'Criar cotação, adicionar fornecedor/item e gerar pedido a partir dela'    UNION ALL
  SELECT 'COMPRAS_APROVAR',                 'Aprovar pedido de compra acima do limite configurado'                     UNION ALL
  SELECT 'COMPRAS_RECEBER',                 'Registrar recebimento de pedido de compra'                                UNION ALL
  SELECT 'COMPRAS_GERENCIAR',               'Gerenciar pedidos de compra e configuração de compras'
) f ON TRUE
WHERE m.codigo = 'COMPRAS';

-- ADMIN: tudo.
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo LIKE 'COMPRAS_%'
WHERE p.nome = 'ADMIN';

-- GERENTE: tudo (nenhum código de COMPRAS é SEGURANCA_GERENCIAR, mesmo critério da V3).
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo LIKE 'COMPRAS_%'
WHERE p.nome = 'GERENTE';

-- SUPERVISOR: requisita, cota e recebe no dia a dia — mas não aprova pedido acima do
-- limiar (fica em nível gerencial) nem gerencia pedido/configuração livremente.
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f
  ON f.codigo IN ('COMPRAS_REQUISITAR', 'COMPRAS_COTAR', 'COMPRAS_RECEBER')
WHERE p.nome = 'SUPERVISOR';

-- OPERADOR: só consulta (mesmo critério '%_CONSULTAR' da V3).
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo = 'COMPRAS_CONSULTAR'
WHERE p.nome = 'OPERADOR';

-- ENTREGADOR não ganha acesso a compras (mesmo critério da V3: acesso mínimo, só cliente).
