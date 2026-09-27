-- Fase 13 (Loja Virtual B2C, primeira fatia) — novo módulo Loja Virtual
-- (loja-virtual-service): Loja (tenant) + catálogo Categoria/Atributo/Produto/Variante.
-- Módulo novo, mesmo padrão de V9/V11/V12/V13 — insere modulo + funcionalidades +
-- grants explícitos de ADMIN/GERENTE/SUPERVISOR/OPERADOR no mesmo arquivo.
--
-- Nesta fatia só usuários internos Cactus (staff) administram o catálogo via API —
-- autenticação self-service por lojista (admin de cada loja) é peça futura registrada
-- no CLAUDE.md, ainda não construída.

INSERT INTO modulo (codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES ('LOJA_VIRTUAL', 'Loja Virtual', 'A', NOW(6), NOW(6), 'seed', 'seed');

INSERT INTO funcionalidade (modulo_id, codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT m.id, f.codigo, f.nome, 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM modulo m
JOIN (
  SELECT 'LOJA_CONSULTAR' AS codigo, 'Consultar lojas, categorias, atributos, produtos e variantes' AS nome UNION ALL
  SELECT 'LOJA_GERENCIAR',            'Gerenciar lojas (tenants), catálogo (categoria/atributo/produto/variante) e assinatura'
) f ON TRUE
WHERE m.codigo = 'LOJA_VIRTUAL';

-- ADMIN/GERENTE: tudo. Este módulo, diferente dos demais, é administração da
-- PLATAFORMA (staff Cactus provisionando/gerenciando lojas de clientes), não uma
-- operação do dia a dia de vendedor/operador — por isso SUPERVISOR/OPERADOR não
-- ganham nada aqui (diferente de VENDAS/OS/CRM, que são trabalho comercial comum).
INSERT INTO papel_funcionalidade (papel_id, funcionalidade_id)
SELECT p.id, f.id FROM papel p JOIN funcionalidade f ON f.codigo LIKE 'LOJA_%'
WHERE p.nome IN ('ADMIN', 'GERENTE');
