-- Fase 12 (Módulo Comercial) — novo módulo CRM (crm-service): Lead → Oportunidade
-- (funil) → Atividade/Interação. Módulo novo (não existia em nenhuma migration
-- anterior), mesmo padrão de V9/V11/V12 — insere modulo + funcionalidades + grants
-- explícitos de ADMIN/GERENTE/SUPERVISOR/OPERADOR no mesmo arquivo (nunca assumir
-- grant "automático" de uma migration futura).

INSERT INTO modulo (codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES ('CRM', 'CRM', 'A', NOW(6), NOW(6), 'seed', 'seed');

INSERT INTO funcionalidade (modulo_id, codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT m.id, f.codigo, f.nome, 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM modulo m
JOIN (
  SELECT 'CRM_CONSULTAR' AS codigo, 'Consultar leads, oportunidades, atividades e interações' AS nome UNION ALL
  SELECT 'CRM_PROSPECTAR',           'Criar/editar lead e oportunidade, qualificar/desqualificar/converter lead, mover estágio, registrar atividade/interação' UNION ALL
  SELECT 'CRM_GERENCIAR',            'Gerenciar catálogo de estágios do funil e excluir lead/oportunidade'
) f ON TRUE
WHERE m.codigo = 'CRM';

-- ADMIN/GERENTE: tudo.
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo LIKE 'CRM_%'
WHERE p.nome IN ('ADMIN', 'GERENTE');

-- SUPERVISOR/OPERADOR: trabalho comercial do dia a dia (mesmo critério de OS_ABRIR/
-- VENDAS_VENDER — prospectar é operação de risco baixo, diferente de gerenciar catálogo).
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f
  ON f.codigo IN ('CRM_CONSULTAR', 'CRM_PROSPECTAR')
WHERE p.nome IN ('SUPERVISOR', 'OPERADOR');

-- ENTREGADOR não ganha acesso (mesmo critério de sempre: acesso mínimo, só cliente).
