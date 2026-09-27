-- Seed de demonstração: um Produto completo, amarrando tudo que a Fase 1b construiu
-- (NCM/CEST/grupo tributário já seedados na V4/V5) — pendência que tinha ficado em
-- aberto no CLAUDE.md ("seed de demonstração de um Produto completo").

INSERT INTO produto_unidade (sigla, descricao, pode_fracionar, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES ('UN', 'Unidade', 0, 'A', NOW(6), NOW(6), 'seed', 'seed');

INSERT INTO produto_marca (nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES ('Cactus Bebidas', 'A', NOW(6), NOW(6), 'seed', 'seed');

INSERT INTO produto_grupo (nome, descricao, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES ('Bebidas', 'Bebidas em geral', 'A', NOW(6), NOW(6), 'seed', 'seed');

INSERT INTO produto_subgrupo (produto_grupo_id, nome, descricao, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT g.id, 'Refrigerantes', 'Refrigerantes em geral', 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM produto_grupo g WHERE g.nome = 'Bebidas';

INSERT INTO produto_tipo (codigo, descricao, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES ('01', 'Normal', 'A', NOW(6), NOW(6), 'seed', 'seed');

-- O produto amarra: NCM 22021000 (V4), CEST 0300100 (V4) e grupo tributário "Revenda
-- sujeita a ICMS-ST" (V5) — os mesmos já seedados pra motor tributário.
INSERT INTO produto (
    produto_unidade_id, produto_marca_id, produto_subgrupo_id, produto_tipo_id,
    gtin, codigo_interno, nome, descricao, descricao_pdv,
    ncm_id, cest_id, grupo_tributario_id, tipo_item_sped, iat, ippt,
    valor_custo, valor_compra, valor_venda, estoque_minimo, estoque_maximo,
    situacao, data_criacao, data_atualizacao, criado_por, atualizado_por
)
SELECT
    un.id, ma.id, sg.id, tp.id,
    '7891234567895', 'PROD-0001', 'Refrigerante Cactus Cola 2L', 'Refrigerante sabor cola, garrafa PET 2 litros', 'REFRI COLA 2L',
    n.id, c.id, g.id, 'MERCADORIA_PARA_REVENDA', 'A', 'P',
    4.500000, 4.800000, 8.990000, 12.000000, 200.000000,
    'A', NOW(6), NOW(6), 'seed', 'seed'
FROM produto_unidade un, produto_marca ma, produto_subgrupo sg, produto_tipo tp, ncm n, cest c, grupo_tributario g
WHERE un.sigla = 'UN' AND ma.nome = 'Cactus Bebidas' AND sg.nome = 'Refrigerantes' AND tp.codigo = '01'
  AND n.codigo = '22021000' AND c.codigo = '0300100' AND g.nome = 'Revenda sujeita a ICMS-ST';

-- Faixa de preço — o mesmo conceito cobrindo os dois casos (ver CLAUDE.md):
-- atacado permanente (quantidade >1, sem vigência) e promoção (quantidade=1, com vigência).
INSERT INTO produto_faixa_preco (produto_id, quantidade_minima, valor_unitario, rotulo, data_inicio, data_fim, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT p.id, 6.000000, 7.990000, 'ATACADO (CX 6 UN)', NULL, NULL, 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM produto p WHERE p.codigo_interno = 'PROD-0001';

INSERT INTO produto_faixa_preco (produto_id, quantidade_minima, valor_unitario, rotulo, data_inicio, data_fim, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT p.id, 1.000000, 7.490000, 'PROMOÇÃO DE LANÇAMENTO', CURRENT_DATE(), DATE_ADD(CURRENT_DATE(), INTERVAL 30 DAY), 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM produto p WHERE p.codigo_interno = 'PROD-0001';

INSERT INTO produto_imagem (produto_id, url, hash_arquivo, principal, ordem, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT p.id, 'https://exemplo.cactusdigital.local/produtos/prod-0001-capa.jpg', NULL, 1, 0, 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM produto p WHERE p.codigo_interno = 'PROD-0001';
