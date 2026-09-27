-- Seed de demonstração: uma loja de roupa feminina (o exemplo que motivou o desenho
-- desta fase) com categoria "Vestidos" e os 2 atributos geradores de variação mais
-- comuns do nicho (Tamanho — LISTA com opções fechadas — e Cor — TEXTO livre).

INSERT INTO loja (nome, razao_social, cnpj, subdominio, email_contato, telefone, plano_assinatura, situacao_assinatura, data_inicio_assinatura, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES ('Boutique Maria', 'Maria Fernandes Moda Feminina LTDA', '12345678000199', 'boutique-maria', 'contato@boutiquemaria.com.br', '11988887777', 'BASICO', 'ATIVA', CURDATE(), 'A', NOW(6), NOW(6), 'seed', 'seed');

INSERT INTO categoria_loja (loja_id, categoria_pai_id, nome, ordem, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES (1, NULL, 'Vestidos', 1, 'A', NOW(6), NOW(6), 'seed', 'seed');

INSERT INTO atributo_categoria (categoria_loja_id, nome, tipo, obrigatorio, gera_variacao, ordem, data_criacao, data_atualizacao, criado_por, atualizado_por) VALUES
(1, 'Tamanho', 'LISTA', 1, 1, 1, NOW(6), NOW(6), 'seed', 'seed'),
(1, 'Cor',     'TEXTO', 1, 1, 2, NOW(6), NOW(6), 'seed', 'seed');

INSERT INTO atributo_categoria_opcao (atributo_categoria_id, valor, ordem, data_criacao, data_atualizacao, criado_por, atualizado_por) VALUES
(1, 'P',  1, NOW(6), NOW(6), 'seed', 'seed'),
(1, 'M',  2, NOW(6), NOW(6), 'seed', 'seed'),
(1, 'G',  3, NOW(6), NOW(6), 'seed', 'seed'),
(1, 'GG', 4, NOW(6), NOW(6), 'seed', 'seed');
