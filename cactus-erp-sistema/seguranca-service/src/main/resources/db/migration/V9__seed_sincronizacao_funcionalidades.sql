-- Módulo SINCRONIZACAO (Fase 6, novo sincronizacao-service) — diferente de
-- V4__seed_estoque_funcionalidades.sql/V7/V8 (cujo módulo já existia semeado desde a
-- V3), este módulo não existia ainda: insere modulo + funcionalidades + grants
-- explícitos de ADMIN/GERENTE/OPERADOR no mesmo arquivo (lição fixada desde
-- V5__seed_compras_funcionalidades.sql/V6 — nunca assumir grant "automático" de uma
-- migration futura).

INSERT INTO modulo (codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES ('SINCRONIZACAO', 'Sincronização ERP ↔ PDV', 'A', NOW(6), NOW(6), 'seed', 'seed');

INSERT INTO funcionalidade (modulo_id, codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT m.id, f.codigo, f.nome, 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM modulo m
JOIN (
  SELECT 'SINCRONIZACAO_CONSULTAR' AS codigo, 'Consultar lotes/registros de sincronização e seu status' AS nome UNION ALL
  SELECT 'SINCRONIZACAO_GERENCIAR',              'Reprocessar registro em erro e disparar ações administrativas de sincronização'
) f ON TRUE
WHERE m.codigo = 'SINCRONIZACAO';

-- ADMIN: tudo.
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo LIKE 'SINCRONIZACAO_%'
WHERE p.nome = 'ADMIN';

-- GERENTE: tudo (nenhum código de SINCRONIZACAO é SEGURANCA_GERENCIAR).
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo LIKE 'SINCRONIZACAO_%'
WHERE p.nome = 'GERENTE';

-- SUPERVISOR/OPERADOR: só consulta (suporte de loja acompanha o status do sync, não
-- reprocessa em erro sozinho — reprocessamento fica em nível gerencial).
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo = 'SINCRONIZACAO_CONSULTAR'
WHERE p.nome IN ('SUPERVISOR', 'OPERADOR');

-- ENTREGADOR não ganha acesso (mesmo critério de sempre: acesso mínimo, só cliente).
