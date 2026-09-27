-- Seed de demonstração: item comum da lista de serviço municipal (manutenção e
-- reparação de equipamentos, item 14.01 da Lista Anexa à LC 116/2003 — usado por
-- praticamente todo município que segue a lista padrão federal).

INSERT INTO lista_servico_municipal (codigo, descricao, codigo_tributacao_municipio, aliquota_iss_padrao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES ('14.01', 'Lubrificação, limpeza, lustração, revisão, carga e recarga, conserto, restauração, blindagem, manutenção e conservação de máquinas, veículos, aparelhos, equipamentos, motores, elevadores ou de qualquer objeto', '1401', 5.000000, NOW(6), NOW(6), 'seed', 'seed');
