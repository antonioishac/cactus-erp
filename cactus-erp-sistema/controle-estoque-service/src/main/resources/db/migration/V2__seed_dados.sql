-- Depósito principal e configuração padrão pra empresa demo (empresa_id=1) — mesma
-- premissa já documentada no seguranca-service: sem tabela empresa neste schema (mora no
-- cadastro-base-service, outro banco), então não dá pra fazer JOIN cross-database; assume
-- que a empresa demo do cadastro-base é sempre a primeira linha semeada.

INSERT INTO deposito (empresa_id, codigo, nome, tipo, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES (1, 'PRINCIPAL', 'Depósito Principal', 'PRINCIPAL', 'A', NOW(6), NOW(6), 'seed', 'seed');

INSERT INTO configuracao_estoque_empresa (empresa_id, metodo_custeio, limite_aprovacao_ajuste, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES (1, 'MEDIA_PONDERADA', 500.00, NOW(6), NOW(6), 'seed', 'seed');
