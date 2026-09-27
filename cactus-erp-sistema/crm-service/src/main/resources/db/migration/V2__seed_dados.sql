-- Seed de demonstração: funil de vendas padrão (5 estágios), cobrindo o ciclo comum de
-- prospecção B2B — cada empresa pode reconfigurar/criar o próprio funil via
-- /api/v1/estagios-funil (catálogo livre, ver Javadoc de EstagioFunil).

INSERT INTO estagio_funil (codigo, nome, ordem, tipo_encerramento, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por) VALUES
('PROSPECCAO',  'Prospecção',            1, 'ABERTO',  'A', NOW(6), NOW(6), 'seed', 'seed'),
('QUALIFICACAO','Qualificação',          2, 'ABERTO',  'A', NOW(6), NOW(6), 'seed', 'seed'),
('PROPOSTA',    'Proposta Enviada',      3, 'ABERTO',  'A', NOW(6), NOW(6), 'seed', 'seed'),
('GANHO',       'Fechamento Ganho',      4, 'GANHO',   'A', NOW(6), NOW(6), 'seed', 'seed'),
('PERDIDO',     'Fechamento Perdido',    5, 'PERDIDO', 'A', NOW(6), NOW(6), 'seed', 'seed');
