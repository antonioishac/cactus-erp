-- BUG REAL encontrado (não escondido): V4__seed_estoque_funcionalidades.sql inseriu as 5
-- funcionalidades ESTOQUE_* na tabela `funcionalidade`, mas o comentário "ADMIN/GERENTE já
-- pegam tudo automaticamente" estava ERRADO — os INSERTs de papel_funcionalidade pra
-- ADMIN (CROSS JOIN)/GERENTE (NOT LIKE 'SEGURANCA_GERENCIAR')/OPERADOR (LIKE '%_CONSULTAR')
-- da V3__seed_dados.sql já tinham rodado e commitado ANTES de V4 existir — SQL não
-- reavalia retroativamente um SELECT já executado. ADMIN/GERENTE/OPERADOR nunca
-- receberam as ESTOQUE_* authorities de verdade. Passou despercebido até agora porque
-- @EnableMethodSecurity também nunca tinha sido declarado em lugar nenhum do monorepo
-- (ver common.security.MethodSecurityConfig) — @PreAuthorize nunca era avaliado, então
-- nenhuma authority fazia diferença. Com os dois problemas corrigidos juntos, este
-- backfill garante que ADMIN/GERENTE/OPERADOR continuem com o acesso pretendido.
-- SUPERVISOR não precisa de backfill — V4 já tinha inserido ESTOQUE_MOVIMENTAR/
-- TRANSFERIR pra ele explicitamente.

INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p CROSS JOIN funcionalidade f
WHERE p.nome = 'ADMIN' AND f.codigo LIKE 'ESTOQUE_%'
  AND NOT EXISTS (
    SELECT 1 FROM papel_funcionalidade pf WHERE pf.papel_id = p.id AND pf.funcionalidade_id = f.id
  );

INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p CROSS JOIN funcionalidade f
WHERE p.nome = 'GERENTE' AND f.codigo LIKE 'ESTOQUE_%'
  AND NOT EXISTS (
    SELECT 1 FROM papel_funcionalidade pf WHERE pf.papel_id = p.id AND pf.funcionalidade_id = f.id
  );

INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p CROSS JOIN funcionalidade f
WHERE p.nome = 'OPERADOR' AND f.codigo = 'ESTOQUE_CONSULTAR'
  AND NOT EXISTS (
    SELECT 1 FROM papel_funcionalidade pf WHERE pf.papel_id = p.id AND pf.funcionalidade_id = f.id
  );
