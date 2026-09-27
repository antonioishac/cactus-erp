-- Fase 14 — novo módulo Contábil (contabilidade-service): Plano de Contas → Centro de
-- Custo → Lançamento Contábil (partida dobrada) → Balancete/Razão/SPED ECD. Mesmo padrão
-- de V9/V11/V12/V13 — insere modulo + funcionalidades + grants explícitos de
-- ADMIN/GERENTE/SUPERVISOR/OPERADOR no mesmo arquivo (nunca assumir grant "automático"
-- de uma migration futura).
--
-- Diferente do FINANCEIRO_CONSULTAR/LANCAR (onde OPERADOR também lança — registrar uma
-- baixa é rotina de qualquer operador), lançamento contábil exige conhecimento contábil
-- especializado — decisão deliberada de deixar CONTABIL_LANCAR só com SUPERVISOR/GERENTE/
-- ADMIN, OPERADOR só consulta.

INSERT INTO modulo (codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES ('CONTABIL', 'Contabilidade', 'A', NOW(6), NOW(6), 'seed', 'seed');

INSERT INTO funcionalidade (modulo_id, codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT m.id, f.codigo, f.nome, 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM modulo m
JOIN (
  SELECT 'CONTABIL_CONSULTAR' AS codigo, 'Consultar plano de contas, centros de custo, lançamentos, balancete/razão e export SPED ECD' AS nome UNION ALL
  SELECT 'CONTABIL_LANCAR',              'Lançar e estornar lançamentos contábeis' UNION ALL
  SELECT 'CONTABIL_GERENCIAR',           'Gerenciar plano de contas e centros de custo'
) f ON TRUE
WHERE m.codigo = 'CONTABIL';

-- ADMIN/GERENTE: tudo.
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo LIKE 'CONTABIL_%'
WHERE p.nome IN ('ADMIN', 'GERENTE');

-- SUPERVISOR: consulta + lança (não gerencia a estrutura do plano de contas).
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f
  ON f.codigo IN ('CONTABIL_CONSULTAR', 'CONTABIL_LANCAR')
WHERE p.nome = 'SUPERVISOR';

-- OPERADOR: só consulta — lançamento contábil exige conhecimento especializado, diferente
-- de rotina operacional (baixa financeira, movimento de estoque).
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f
  ON f.codigo = 'CONTABIL_CONSULTAR'
WHERE p.nome = 'OPERADOR';

-- ENTREGADOR não ganha acesso (mesmo critério de sempre: acesso mínimo, só cliente).
