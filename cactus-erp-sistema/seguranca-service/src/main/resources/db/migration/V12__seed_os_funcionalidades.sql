-- Fase 10 (Módulo Comercial) — novo módulo OS (os-service): abertura, equipamentos,
-- itens (peça/mão de obra) e evolução de Ordem de Serviço. Módulo novo (não existia em
-- nenhuma migration anterior), mesmo padrão de V9/V11 — insere modulo + funcionalidades
-- + grants explícitos de ADMIN/GERENTE/SUPERVISOR/OPERADOR no mesmo arquivo (nunca
-- assumir grant "automático" de uma migration futura).

INSERT INTO modulo (codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES ('OS', 'Ordem de Serviço', 'A', NOW(6), NOW(6), 'seed', 'seed');

INSERT INTO funcionalidade (modulo_id, codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT m.id, f.codigo, f.nome, 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM modulo m
JOIN (
  SELECT 'OS_CONSULTAR' AS codigo, 'Consultar ordens de serviço, status e tipos de equipamento' AS nome UNION ALL
  SELECT 'OS_ABRIR',                 'Abrir OS, adicionar equipamento/item/evolução, trocar status, aprovar orçamento e encerrar' UNION ALL
  SELECT 'OS_GERENCIAR',             'Gerenciar catálogo de status e tipos de equipamento'
) f ON TRUE
WHERE m.codigo = 'OS';

-- ADMIN/GERENTE: tudo.
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo LIKE 'OS_%'
WHERE p.nome IN ('ADMIN', 'GERENTE');

-- SUPERVISOR: opera o dia a dia da OS (abrir/atender), não gerencia os catálogos.
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f
  ON f.codigo IN ('OS_CONSULTAR', 'OS_ABRIR')
WHERE p.nome = 'SUPERVISOR';

-- OPERADOR: também abre/atende OS no dia a dia (mesmo critério de VENDAS_VENDER —
-- atendimento é operação de risco baixo, diferente de gerenciar catálogo).
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f
  ON f.codigo IN ('OS_CONSULTAR', 'OS_ABRIR')
WHERE p.nome = 'OPERADOR';

-- ENTREGADOR não ganha acesso (mesmo critério de sempre: acesso mínimo, só cliente).
