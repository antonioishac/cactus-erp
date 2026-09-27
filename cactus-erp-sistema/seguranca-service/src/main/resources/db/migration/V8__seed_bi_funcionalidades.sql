-- Funcionalidades do business-intelligence-service — módulo BI já existia semeado
-- (V3__seed_dados.sql) desde a extração deste serviço, só sem funcionalidades ainda.
-- Grants de ADMIN/GERENTE/OPERADOR/SUPERVISOR explícitos no mesmo arquivo (lição fixada
-- desde a Fase 3/V6 — nunca assumir grant "automático" de uma migration futura).

INSERT INTO funcionalidade (modulo_id, codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT m.id, f.codigo, f.nome, 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM modulo m
JOIN (
  SELECT 'BI_CONSULTAR' AS codigo, 'Consultar indicadores de BI (valor de estoque, curva ABC, compras por fornecedor)' AS nome UNION ALL
  SELECT 'BI_GERENCIAR',              'Disparar execução manual do ETL do BI'
) f ON TRUE
WHERE m.codigo = 'BI';

-- ADMIN: tudo.
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo LIKE 'BI_%'
WHERE p.nome = 'ADMIN';

-- GERENTE: tudo (nenhum código de BI é SEGURANCA_GERENCIAR, mesmo critério da V3).
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo LIKE 'BI_%'
WHERE p.nome = 'GERENTE';

-- SUPERVISOR: só consulta indicadores — disparar ETL manual fica em nível gerencial.
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo = 'BI_CONSULTAR'
WHERE p.nome = 'SUPERVISOR';

-- OPERADOR: só consulta (mesmo critério '%_CONSULTAR' da V3).
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo = 'BI_CONSULTAR'
WHERE p.nome = 'OPERADOR';

-- ENTREGADOR não ganha acesso a BI (mesmo critério da V3: acesso mínimo, só cliente).
