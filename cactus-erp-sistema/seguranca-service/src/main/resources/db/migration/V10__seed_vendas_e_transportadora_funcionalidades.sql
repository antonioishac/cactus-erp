-- Fase 7 (Módulo Comercial): duas frentes nesta migration —
-- (1) CADASTRO_TRANSPORTADORA_* — módulo CADASTRO_BASE já existia semeado (V1), só sem
--     estas funcionalidades ainda (mesmo padrão de V5, que fez o mesmo pra COMPRAS).
-- (2) VENDAS_* — módulo novo (não existia em nenhuma migration anterior), insere modulo +
--     funcionalidades + grants explícitos de ADMIN/GERENTE/SUPERVISOR/OPERADOR no mesmo
--     arquivo (lição fixada desde V5/V6 — nunca assumir grant "automático" de uma
--     migration futura; ADMIN/GERENTE usam CROSS JOIN/NOT LIKE só em V3, tempo de
--     execução daquela migration, não retroagem).

-- (1) Transportadora (cadastro-base-service, gap fechado nesta fase)
INSERT INTO funcionalidade (modulo_id, codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT m.id, f.codigo, f.nome, 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM modulo m
JOIN (
  SELECT 'CADASTRO_TRANSPORTADORA_CONSULTAR' AS codigo, 'Consultar transportadoras' AS nome UNION ALL
  SELECT 'CADASTRO_TRANSPORTADORA_CRIAR',                'Cadastrar transportadoras'             UNION ALL
  SELECT 'CADASTRO_TRANSPORTADORA_EDITAR',               'Editar transportadoras'                UNION ALL
  SELECT 'CADASTRO_TRANSPORTADORA_EXCLUIR',              'Excluir transportadoras'
) f ON TRUE
WHERE m.codigo = 'CADASTRO_BASE';

INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo LIKE 'CADASTRO_TRANSPORTADORA_%'
WHERE p.nome IN ('ADMIN', 'GERENTE');

INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f
  ON f.codigo IN ('CADASTRO_TRANSPORTADORA_CONSULTAR', 'CADASTRO_TRANSPORTADORA_CRIAR', 'CADASTRO_TRANSPORTADORA_EDITAR')
WHERE p.nome = 'SUPERVISOR';

INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo = 'CADASTRO_TRANSPORTADORA_CONSULTAR'
WHERE p.nome = 'OPERADOR';

-- ENTREGADOR não ganha acesso (acesso mínimo, só consulta de cliente — critério de sempre).

-- (2) Módulo VENDAS (novo vendas-service)
INSERT INTO modulo (codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES ('VENDAS', 'Vendas', 'A', NOW(6), NOW(6), 'seed', 'seed');

INSERT INTO funcionalidade (modulo_id, codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT m.id, f.codigo, f.nome, 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM modulo m
JOIN (
  SELECT 'VENDAS_CONSULTAR' AS codigo, 'Consultar orçamentos, vendas, vendedores e comissões' AS nome UNION ALL
  SELECT 'VENDAS_ORCAR',                 'Criar e editar orçamento de venda'                              UNION ALL
  SELECT 'VENDAS_VENDER',                'Gerar venda a partir de orçamento e confirmar venda'            UNION ALL
  SELECT 'VENDAS_CANCELAR',              'Cancelar venda em rascunho'                                     UNION ALL
  SELECT 'VENDAS_GERENCIAR',             'Gerenciar vendedores, perfis de comissão e condições de pagamento'
) f ON TRUE
WHERE m.codigo = 'VENDAS';

-- ADMIN/GERENTE: tudo (nenhum código de VENDAS é SEGURANCA_GERENCIAR).
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo LIKE 'VENDAS_%'
WHERE p.nome IN ('ADMIN', 'GERENTE');

-- SUPERVISOR: opera o dia a dia de vendas, inclusive cancelamento (ação reversa de
-- risco baixo aqui — venda só cancela em RASCUNHO, antes de mexer estoque/comissão),
-- mas não gerencia vendedor/comissão/condição de pagamento (fica em nível gerencial).
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f
  ON f.codigo IN ('VENDAS_CONSULTAR', 'VENDAS_ORCAR', 'VENDAS_VENDER', 'VENDAS_CANCELAR')
WHERE p.nome = 'SUPERVISOR';

-- OPERADOR: consulta, orça e vende — não cancela nem gerencia.
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f
  ON f.codigo IN ('VENDAS_CONSULTAR', 'VENDAS_ORCAR', 'VENDAS_VENDER')
WHERE p.nome = 'OPERADOR';

-- ENTREGADOR não ganha acesso a vendas (mesmo critério de sempre: acesso mínimo, só cliente).
