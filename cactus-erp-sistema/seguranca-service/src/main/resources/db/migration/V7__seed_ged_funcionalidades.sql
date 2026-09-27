-- Funcionalidades do gestao-documentos-service — módulo GED já existia semeado
-- (V3__seed_dados.sql) desde a extração deste serviço, só sem funcionalidades ainda.
-- Grants de ADMIN/GERENTE/OPERADOR/SUPERVISOR explícitos no mesmo arquivo (lição da
-- Fase 3/V6: nunca assumir grant "automático" de uma migration futura).

INSERT INTO funcionalidade (modulo_id, codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT m.id, f.codigo, f.nome, 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM modulo m
JOIN (
  SELECT 'GED_CONSULTAR' AS codigo, 'Consultar documentos, versões, pastas e tipos de documento' AS nome UNION ALL
  SELECT 'GED_GERENCIAR',              'Upload de documento/nova versão/exclusão lógica, gerenciar pastas e tipos de documento'
) f ON TRUE
WHERE m.codigo = 'GED';

-- ADMIN: tudo.
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo LIKE 'GED_%'
WHERE p.nome = 'ADMIN';

-- GERENTE: tudo (nenhum código de GED é SEGURANCA_GERENCIAR, mesmo critério da V3).
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo LIKE 'GED_%'
WHERE p.nome = 'GERENTE';

-- SUPERVISOR: também gerencia documento no dia a dia (mesmo critério de "cadastra/edita"
-- já usado pra outros módulos).
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo = 'GED_GERENCIAR'
WHERE p.nome = 'SUPERVISOR';

-- OPERADOR: só consulta (mesmo critério '%_CONSULTAR' da V3).
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo = 'GED_CONSULTAR'
WHERE p.nome = 'OPERADOR';

-- ENTREGADOR não ganha acesso a GED (mesmo critério da V3: acesso mínimo, só cliente).
