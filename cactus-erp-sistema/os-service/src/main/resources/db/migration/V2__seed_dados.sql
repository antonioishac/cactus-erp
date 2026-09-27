-- Seed de demonstração: catálogo padrão de status de OS (livre, gerenciável — ver
-- Javadoc de OsStatus) cobrindo o ciclo de vida comum de assistência técnica/oficina.

INSERT INTO os_status (codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por) VALUES
  ('AB', 'Aberta',              'A', NOW(6), NOW(6), 'seed', 'seed'),
  ('OR', 'Em Orçamento',        'A', NOW(6), NOW(6), 'seed', 'seed'),
  ('AN', 'Em Andamento',        'A', NOW(6), NOW(6), 'seed', 'seed'),
  ('AP', 'Aguardando Peça',     'A', NOW(6), NOW(6), 'seed', 'seed'),
  ('FN', 'Finalizada',          'A', NOW(6), NOW(6), 'seed', 'seed'),
  ('EN', 'Entregue',            'A', NOW(6), NOW(6), 'seed', 'seed'),
  ('CA', 'Cancelada',           'A', NOW(6), NOW(6), 'seed', 'seed');
