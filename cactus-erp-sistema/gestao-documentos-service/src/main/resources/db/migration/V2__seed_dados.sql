-- Tipos de documento de demonstração — catálogo inicial, empresa pode cadastrar outros.
INSERT INTO tipo_documento (nome, tamanho_maximo_mb, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por) VALUES
  ('Contrato', 20.00, 'A', NOW(6), NOW(6), 'seed', 'seed'),
  ('Nota Fiscal', 10.00, 'A', NOW(6), NOW(6), 'seed', 'seed'),
  ('Foto de Produto', 5.00, 'A', NOW(6), NOW(6), 'seed', 'seed');
