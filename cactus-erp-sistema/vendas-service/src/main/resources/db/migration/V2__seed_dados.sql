-- Seed de demonstração: catálogo de modelo/tipo de NF (Ato Cotepe 46/10 — só os modelos
-- relevantes pro Módulo Comercial desta fase) e um perfil de comissão padrão.

INSERT INTO nota_fiscal_modelo (codigo, descricao, modelo, data_criacao, data_atualizacao, criado_por, atualizado_por) VALUES
  ('55', 'Nota Fiscal Eletrônica',                 'NF-e',      NOW(6), NOW(6), 'seed', 'seed'),
  ('65', 'Nota Fiscal de Consumidor Eletrônica',    'NFC-e',     NOW(6), NOW(6), 'seed', 'seed'),
  ('59', 'Cupom Fiscal Eletrônico (SAT)',           'SAT/CF-e',  NOW(6), NOW(6), 'seed', 'seed');

INSERT INTO nota_fiscal_tipo (nota_fiscal_modelo_id, nome, descricao, serie, serie_scan, ultimo_numero, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT id, 'NF-e Venda Padrão', 'Série padrão para venda de mercadoria (modelo 55)', '1', NULL, 0, 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM nota_fiscal_modelo WHERE codigo = '55';

INSERT INTO comissao_perfil (codigo, nome, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por) VALUES
  ('001', 'Vendedor Padrão', 'A', NOW(6), NOW(6), 'seed', 'seed');
