-- Caixa e Bancos (2026-09-06) — expansão do financeiro-service, mesmo módulo FINANCEIRO
-- já seedado em V15 (não cria modulo novo — Caixa/Tesouraria/Conciliação/EDI são o
-- mesmo domínio financeiro, só prefixo de authority diferente por área, mesmo critério
-- de CADASTRO_CLIENTE_*/CADASTRO_PRODUTO_* dentro do módulo CADASTRO_BASE).

INSERT INTO funcionalidade (modulo_id, codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT m.id, f.codigo, f.nome, 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM modulo m
JOIN (
  SELECT 'CAIXA_CONSULTAR' AS codigo, 'Consultar contas bancárias, movimentos e cheques' AS nome UNION ALL
  SELECT 'CAIXA_MOVIMENTAR',          'Registrar movimento manual, cheque, depósito/compensação/desconto' UNION ALL
  SELECT 'CAIXA_GERENCIAR',           'Criar/editar/inativar conta bancária, cancelar cheque emitido'
) f ON TRUE
WHERE m.codigo = 'FINANCEIRO';

-- ADMIN/GERENTE: tudo.
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo LIKE 'CAIXA_%'
WHERE p.nome IN ('ADMIN', 'GERENTE');

-- SUPERVISOR/OPERADOR: consulta+movimenta (mesmo critério de FINANCEIRO_CONSULTAR/LANCAR)
-- — criar/editar conta bancária e cancelar cheque emitido ficam em nível gerencial.
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f
  ON f.codigo IN ('CAIXA_CONSULTAR', 'CAIXA_MOVIMENTAR')
WHERE p.nome IN ('SUPERVISOR', 'OPERADOR');

-- ENTREGADOR não ganha acesso (mesmo critério de sempre).
