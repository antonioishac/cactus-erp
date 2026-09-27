-- Financeiro (Contas a Receber/Contas a Pagar, fechamento de pendência da Fase 7) —
-- novo módulo financeiro-service. Módulo novo, mesmo padrão de V9/V11/V12/V13/V14 —
-- insere modulo + funcionalidades + grants explícitos de ADMIN/GERENTE/SUPERVISOR/
-- OPERADOR no mesmo arquivo (nunca assumir grant "automático" de uma migration futura).

INSERT INTO modulo (codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES ('FINANCEIRO', 'Financeiro', 'A', NOW(6), NOW(6), 'seed', 'seed');

INSERT INTO funcionalidade (modulo_id, codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT m.id, f.codigo, f.nome, 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM modulo m
JOIN (
  SELECT 'FINANCEIRO_CONSULTAR' AS codigo, 'Consultar contas a receber e a pagar' AS nome UNION ALL
  SELECT 'FINANCEIRO_LANCAR',               'Criar título manual, gerar de venda, registrar baixa' UNION ALL
  SELECT 'FINANCEIRO_GERENCIAR',            'Cancelar título'
) f ON TRUE
WHERE m.codigo = 'FINANCEIRO';

-- ADMIN/GERENTE: tudo.
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo LIKE 'FINANCEIRO_%'
WHERE p.nome IN ('ADMIN', 'GERENTE');

-- SUPERVISOR/OPERADOR: consulta+lança (registrar baixa é rotina, mesmo critério de
-- COMPRAS_RECEBER) — cancelar título fica em nível gerencial (mesmo critério de
-- COMPRAS_APROVAR).
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f
  ON f.codigo IN ('FINANCEIRO_CONSULTAR', 'FINANCEIRO_LANCAR')
WHERE p.nome IN ('SUPERVISOR', 'OPERADOR');

-- ENTREGADOR não ganha acesso (mesmo critério de sempre: acesso mínimo, só cliente).
