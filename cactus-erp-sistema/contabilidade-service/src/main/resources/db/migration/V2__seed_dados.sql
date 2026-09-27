-- Seed de demonstração — plano de contas simplificado (padrão contábil brasileiro:
-- Ativo/Passivo/Patrimônio Líquido/Receitas/Despesas) pra empresa_id=1 (a empresa demo
-- semeada desde a Fase 1a do cadastro-base-service — mesma premissa já usada em outros
-- seeds cross-serviço do monorepo, ex: V3__seed_dados.sql do seguranca-service).
-- `SET @var := LAST_INSERT_ID()` entre INSERTs — Flyway executa cada migration numa
-- única conexão/sessão, então a variável de sessão do MySQL sobrevive entre statements.

-- 1 — ATIVO
INSERT INTO plano_conta (empresa_id, codigo, nome, conta_pai_id, tipo, natureza, conta_analitica, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES (1, '1', 'ATIVO', NULL, 'ATIVO', 'DEVEDORA', 0, 'A', NOW(6), NOW(6), 'seed', 'seed');
SET @ativo := LAST_INSERT_ID();

INSERT INTO plano_conta (empresa_id, codigo, nome, conta_pai_id, tipo, natureza, conta_analitica, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES (1, '1.1', 'ATIVO CIRCULANTE', @ativo, 'ATIVO', 'DEVEDORA', 0, 'A', NOW(6), NOW(6), 'seed', 'seed');
SET @ativo_circulante := LAST_INSERT_ID();

INSERT INTO plano_conta (empresa_id, codigo, nome, conta_pai_id, tipo, natureza, conta_analitica, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES
  (1, '1.1.01', 'Caixa e Bancos', @ativo_circulante, 'ATIVO', 'DEVEDORA', 1, 'A', NOW(6), NOW(6), 'seed', 'seed'),
  (1, '1.1.02', 'Clientes a Receber', @ativo_circulante, 'ATIVO', 'DEVEDORA', 1, 'A', NOW(6), NOW(6), 'seed', 'seed');

-- 2 — PASSIVO
INSERT INTO plano_conta (empresa_id, codigo, nome, conta_pai_id, tipo, natureza, conta_analitica, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES (1, '2', 'PASSIVO', NULL, 'PASSIVO', 'CREDORA', 0, 'A', NOW(6), NOW(6), 'seed', 'seed');
SET @passivo := LAST_INSERT_ID();

INSERT INTO plano_conta (empresa_id, codigo, nome, conta_pai_id, tipo, natureza, conta_analitica, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES (1, '2.1', 'PASSIVO CIRCULANTE', @passivo, 'PASSIVO', 'CREDORA', 0, 'A', NOW(6), NOW(6), 'seed', 'seed');
SET @passivo_circulante := LAST_INSERT_ID();

INSERT INTO plano_conta (empresa_id, codigo, nome, conta_pai_id, tipo, natureza, conta_analitica, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES (1, '2.1.01', 'Fornecedores a Pagar', @passivo_circulante, 'PASSIVO', 'CREDORA', 1, 'A', NOW(6), NOW(6), 'seed', 'seed');

-- 3 — PATRIMÔNIO LÍQUIDO
INSERT INTO plano_conta (empresa_id, codigo, nome, conta_pai_id, tipo, natureza, conta_analitica, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES (1, '3', 'PATRIMÔNIO LÍQUIDO', NULL, 'PATRIMONIO_LIQUIDO', 'CREDORA', 0, 'A', NOW(6), NOW(6), 'seed', 'seed');
SET @pl := LAST_INSERT_ID();

INSERT INTO plano_conta (empresa_id, codigo, nome, conta_pai_id, tipo, natureza, conta_analitica, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES (1, '3.1', 'Capital Social', @pl, 'PATRIMONIO_LIQUIDO', 'CREDORA', 1, 'A', NOW(6), NOW(6), 'seed', 'seed');

-- 4 — RECEITAS
INSERT INTO plano_conta (empresa_id, codigo, nome, conta_pai_id, tipo, natureza, conta_analitica, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES (1, '4', 'RECEITAS', NULL, 'RECEITA', 'CREDORA', 0, 'A', NOW(6), NOW(6), 'seed', 'seed');
SET @receitas := LAST_INSERT_ID();

INSERT INTO plano_conta (empresa_id, codigo, nome, conta_pai_id, tipo, natureza, conta_analitica, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES (1, '4.1', 'Receita de Vendas', @receitas, 'RECEITA', 'CREDORA', 1, 'A', NOW(6), NOW(6), 'seed', 'seed');

-- 5 — DESPESAS
INSERT INTO plano_conta (empresa_id, codigo, nome, conta_pai_id, tipo, natureza, conta_analitica, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES (1, '5', 'DESPESAS', NULL, 'DESPESA', 'DEVEDORA', 0, 'A', NOW(6), NOW(6), 'seed', 'seed');
SET @despesas := LAST_INSERT_ID();

INSERT INTO plano_conta (empresa_id, codigo, nome, conta_pai_id, tipo, natureza, conta_analitica, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES (1, '5.1', 'Despesas Operacionais', @despesas, 'DESPESA', 'DEVEDORA', 1, 'A', NOW(6), NOW(6), 'seed', 'seed');

-- Centros de custo demo
INSERT INTO centro_custo (empresa_id, codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES
  (1, 'ADM', 'Administração', 'A', NOW(6), NOW(6), 'seed', 'seed'),
  (1, 'COM', 'Comercial', 'A', NOW(6), NOW(6), 'seed', 'seed');
