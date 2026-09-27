-- Funcionalidades do controle-estoque-service — módulo ESTOQUE já existia semeado
-- (V3__seed_dados.sql) desde a extração deste serviço, só sem funcionalidades ainda.
-- Catálogo central de RBAC mora aqui mesmo protegendo endpoint de outro serviço — ver
-- CLAUDE.md, seção da extração do seguranca-service.

INSERT INTO funcionalidade (modulo_id, codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT m.id, f.codigo, f.nome, 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM modulo m
JOIN (
  SELECT 'ESTOQUE_CONSULTAR'           AS codigo, 'Consultar depósitos, saldos e movimentos de estoque' AS nome UNION ALL
  SELECT 'ESTOQUE_MOVIMENTAR',             'Registrar entrada/saída/ajuste/perda/devolução manual'              UNION ALL
  SELECT 'ESTOQUE_TRANSFERIR',             'Criar, enviar e receber transferências entre depósitos'             UNION ALL
  SELECT 'ESTOQUE_INVENTARIO_GERENCIAR',   'Abrir, apontar contagem e finalizar inventário físico'              UNION ALL
  SELECT 'ESTOQUE_GERENCIAR',              'Gerenciar depósitos, kits, lotes e configuração de estoque'
) f ON TRUE
WHERE m.codigo = 'ESTOQUE';

-- ADMIN (CROSS JOIN em todas as funcionalidades) e GERENTE (NOT LIKE 'SEGURANCA_GERENCIAR')
-- já pegam tudo isso automaticamente pelas regras da V3 — não precisam de INSERT extra.

-- SUPERVISOR: além de consultar (já pega via '%_CONSULTAR'), também movimenta e transfere
-- estoque no dia a dia — mas não gerencia depósito/config nem finaliza inventário sozinho.
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f
  ON f.codigo IN ('ESTOQUE_MOVIMENTAR', 'ESTOQUE_TRANSFERIR')
WHERE p.nome = 'SUPERVISOR';

-- OPERADOR já pega ESTOQUE_CONSULTAR de graça (regra '%_CONSULTAR' da V3) — sem INSERT extra.
-- ENTREGADOR não ganha acesso a estoque (mesmo critério da V3: acesso mínimo, só cliente).
