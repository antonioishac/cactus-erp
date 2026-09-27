-- Dados semente: catálogo de módulos/funcionalidades/papéis e o usuário ADMIN inicial.
--
-- Os clients OAuth2 (oauth2_registered_client) NÃO são semeados aqui: o formato de
-- client_settings/token_settings é um JSON com metadados de tipo internos do Spring
-- Security, frágil de escrever à mão em SQL. Eles são semeados em código (ver
-- OAuth2ClientSeeder), usando o próprio RegisteredClientRepository do Spring — garante
-- serialização correta e é idempotente entre reinícios.

-- Módulos (um por serviço planejado — SEGURANCA é este próprio serviço).
INSERT INTO modulo (codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por) VALUES
  ('SEGURANCA',     'Segurança', 'A', NOW(6), NOW(6), 'seed', 'seed'),
  ('CADASTRO_BASE', 'Cadastro Base', 'A', NOW(6), NOW(6), 'seed', 'seed'),
  ('COMPRAS',       'Gestão de Compras', 'A', NOW(6), NOW(6), 'seed', 'seed'),
  ('ESTOQUE',       'Controle de Estoque', 'A', NOW(6), NOW(6), 'seed', 'seed'),
  ('GED',           'Gestão de Documentos', 'A', NOW(6), NOW(6), 'seed', 'seed'),
  ('BI',            'Business Intelligence', 'A', NOW(6), NOW(6), 'seed', 'seed');

-- Funcionalidades de SEGURANCA (gestão de usuário/papel/funcionalidade/módulo — este próprio serviço).
INSERT INTO funcionalidade (modulo_id, codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT m.id, f.codigo, f.nome, 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM modulo m
JOIN (
  SELECT 'SEGURANCA_CONSULTAR' AS codigo, 'Consultar papéis e usuários'             AS nome UNION ALL
  SELECT 'SEGURANCA_GERENCIAR',              'Gerenciar papéis, funcionalidades e usuários'
) f ON TRUE
WHERE m.codigo = 'SEGURANCA';

-- Funcionalidades do cadastro-base — o catálogo central de authorities mora aqui
-- (RBAC é cross-cutting: protege endpoint de qualquer serviço, não só quem o possui),
-- mesmo que a tabela/endpoint protegido por elas viva no cadastro-base-service. Os
-- demais módulos (COMPRAS/ESTOQUE/GED/BI) semeiam as próprias quando forem implementados.
INSERT INTO funcionalidade (modulo_id, codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT m.id, f.codigo, f.nome, 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM modulo m
JOIN (
  SELECT 'CADASTRO_CLIENTE_CONSULTAR'  AS codigo, 'Consultar clientes'          AS nome UNION ALL
  SELECT 'CADASTRO_CLIENTE_CRIAR',         'Cadastrar clientes'                        UNION ALL
  SELECT 'CADASTRO_CLIENTE_EDITAR',        'Editar clientes'                           UNION ALL
  SELECT 'CADASTRO_CLIENTE_EXCLUIR',       'Excluir clientes'                          UNION ALL
  SELECT 'CADASTRO_PRODUTO_CONSULTAR',     'Consultar produtos'                        UNION ALL
  SELECT 'CADASTRO_PRODUTO_CRIAR',         'Cadastrar produtos'                        UNION ALL
  SELECT 'CADASTRO_PRODUTO_EDITAR',        'Editar produtos'                           UNION ALL
  SELECT 'CADASTRO_PRODUTO_EXCLUIR',       'Excluir produtos'                          UNION ALL
  SELECT 'CADASTRO_EMPRESA_CONSULTAR',     'Consultar empresas/filiais'                UNION ALL
  SELECT 'CADASTRO_EMPRESA_CRIAR',         'Cadastrar empresas/filiais'                UNION ALL
  SELECT 'CADASTRO_EMPRESA_EDITAR',        'Editar empresas/filiais'                   UNION ALL
  SELECT 'CADASTRO_EMPRESA_EXCLUIR',       'Excluir empresas/filiais'                  UNION ALL
  SELECT 'CADASTRO_COLABORADOR_CONSULTAR', 'Consultar colaboradores/cargos/setores'    UNION ALL
  SELECT 'CADASTRO_COLABORADOR_CRIAR',     'Cadastrar colaboradores/cargos/setores'    UNION ALL
  SELECT 'CADASTRO_COLABORADOR_EDITAR',    'Editar colaboradores/cargos/setores'       UNION ALL
  SELECT 'CADASTRO_COLABORADOR_EXCLUIR',   'Excluir colaboradores/cargos/setores'      UNION ALL
  SELECT 'CADASTRO_TRIBUTACAO_CONSULTAR',  'Consultar catálogos e regras tributárias'  UNION ALL
  SELECT 'CADASTRO_TRIBUTACAO_CRIAR',      'Cadastrar catálogos e regras tributárias'  UNION ALL
  SELECT 'CADASTRO_TRIBUTACAO_EDITAR',     'Editar catálogos e regras tributárias'     UNION ALL
  SELECT 'CADASTRO_TRIBUTACAO_EXCLUIR',    'Excluir catálogos e regras tributárias'    UNION ALL
  SELECT 'CADASTRO_FORNECEDOR_CONSULTAR',  'Consultar fornecedores'                    UNION ALL
  SELECT 'CADASTRO_FORNECEDOR_CRIAR',      'Cadastrar fornecedores'                    UNION ALL
  SELECT 'CADASTRO_FORNECEDOR_EDITAR',     'Editar fornecedores'                       UNION ALL
  SELECT 'CADASTRO_FORNECEDOR_EXCLUIR',    'Excluir fornecedores'
) f ON TRUE
WHERE m.codigo = 'CADASTRO_BASE';

-- Papéis padrão — validado contra fenix.sql.USUARIO/BANCO_PDV.md.COLABORADOR.NIVEL_AUTORIZACAO
-- (G=Gerente | S=Supervisor | O=Operador | E=Entregador): ADMIN é exclusivo do ERP (acesso
-- de administração do sistema, não é um nível do PDV), os outros 4 mapeiam 1:1 com o PDV.
INSERT INTO papel (nome, descricao, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por) VALUES
  ('ADMIN',      'Administrador do sistema — acesso completo', 'A', NOW(6), NOW(6), 'seed', 'seed'),
  ('GERENTE',    'Gerente — acesso completo aos cadastros, exceto gerenciar segurança', 'A', NOW(6), NOW(6), 'seed', 'seed'),
  ('SUPERVISOR', 'Supervisor — consulta tudo, cadastra/edita cliente/produto/colaborador', 'A', NOW(6), NOW(6), 'seed', 'seed'),
  ('OPERADOR',   'Consulta os cadastros, sem permissão de alteração', 'A', NOW(6), NOW(6), 'seed', 'seed'),
  ('ENTREGADOR', 'Acesso mínimo — só consulta de clientes (endereço de entrega)', 'A', NOW(6), NOW(6), 'seed', 'seed');

INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p CROSS JOIN funcionalidade f WHERE p.nome = 'ADMIN';

INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo NOT LIKE 'SEGURANCA_GERENCIAR'
WHERE p.nome = 'GERENTE';

INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f
  ON f.codigo LIKE '%_CONSULTAR'
  OR f.codigo IN ('CADASTRO_CLIENTE_CRIAR', 'CADASTRO_CLIENTE_EDITAR',
                  'CADASTRO_PRODUTO_CRIAR', 'CADASTRO_PRODUTO_EDITAR',
                  'CADASTRO_COLABORADOR_CRIAR', 'CADASTRO_COLABORADOR_EDITAR',
                  'CADASTRO_FORNECEDOR_CRIAR', 'CADASTRO_FORNECEDOR_EDITAR')
WHERE p.nome = 'SUPERVISOR';

INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo LIKE '%_CONSULTAR'
WHERE p.nome = 'OPERADOR';

INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo = 'CADASTRO_CLIENTE_CONSULTAR'
WHERE p.nome = 'ENTREGADOR';

-- Usuário ADMIN inicial. Senha temporária: TrocarNoPrimeiroAcesso@2026 (BCrypt abaixo) —
-- trocar no primeiro login; isso é seed de desenvolvimento local, nunca reaproveitar.
INSERT INTO usuario (login, senha_hash, nome, email, ativo, bloqueado, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES ('admin', '$2a$10$EjGu/sGKHuyBcy3dqoeNt.7EGTwqfBvtEPfLfat/rB9fqFrCMteH.', 'Administrador', 'admin@cactusdigital.local', 1, 0, NOW(6), NOW(6), 'seed', 'seed');

-- empresa_id hardcoded em 1: sem FK/tabela empresa neste schema (mora no
-- cadastro-base-service), então não dá pra fazer JOIN cross-database — assume que a
-- empresa demo do cadastro-base (V5__seed_dados.sql de lá) é sempre a primeira linha
-- semeada. Premissa documentada aqui de propósito, não um acidente.
INSERT INTO usuario_papel_empresa (usuario_id, papel_id, empresa_id, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT u.id, p.id, 1, NOW(6), NOW(6), 'seed', 'seed'
FROM usuario u, papel p
WHERE u.login = 'admin' AND p.nome = 'ADMIN';
