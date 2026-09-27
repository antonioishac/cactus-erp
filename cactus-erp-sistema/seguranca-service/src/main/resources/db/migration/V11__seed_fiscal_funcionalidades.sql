-- Fase 8 (Módulo Comercial) — novo módulo FISCAL (fiscal-service), NF-e a partir de
-- Venda via ACBr Monitor. Módulo novo (não existia em nenhuma migration anterior),
-- mesmo padrão de V9__seed_sincronizacao_funcionalidades.sql — insere modulo +
-- funcionalidades + grants explícitos de ADMIN/GERENTE/SUPERVISOR/OPERADOR no mesmo
-- arquivo (nunca assumir grant "automático" de uma migration futura).

INSERT INTO modulo (codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES ('FISCAL', 'Fiscal (NF-e/NFC-e/CT-e)', 'A', NOW(6), NOW(6), 'seed', 'seed');

INSERT INTO funcionalidade (modulo_id, codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT m.id, f.codigo, f.nome, 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM modulo m
JOIN (
  SELECT 'FISCAL_CONSULTAR' AS codigo, 'Consultar notas fiscais eletrônicas geradas' AS nome UNION ALL
  SELECT 'FISCAL_EMITIR',              'Gerar nota fiscal a partir de uma venda e emitir via ACBr' UNION ALL
  SELECT 'FISCAL_GERENCIAR',           'Reemitir nota fiscal em erro e demais ações administrativas'
) f ON TRUE
WHERE m.codigo = 'FISCAL';

-- ADMIN/GERENTE: tudo.
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo LIKE 'FISCAL_%'
WHERE p.nome IN ('ADMIN', 'GERENTE');

-- SUPERVISOR: consulta e emite — mesmo critério de VENDAS_VENDER (operação de risco
-- baixo/médio no dia a dia), mas não faz gerência administrativa (reemissão em erro).
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f
  ON f.codigo IN ('FISCAL_CONSULTAR', 'FISCAL_EMITIR')
WHERE p.nome = 'SUPERVISOR';

-- OPERADOR: só consulta (mesmo critério '%_CONSULTAR' de sempre).
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo = 'FISCAL_CONSULTAR'
WHERE p.nome = 'OPERADOR';

-- ENTREGADOR não ganha acesso (mesmo critério de sempre: acesso mínimo, só cliente).
