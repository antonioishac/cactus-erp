-- Dados semente: empresa demo.
--
-- Módulos/funcionalidades/papéis/usuário ADMIN NÃO são semeados aqui — moraram no
-- cadastro-base até a extração do Authorization Server/RBAC pro seguranca-service; ver
-- V3__seed_dados.sql de lá.

-- Empresa demo (CNPJ fictício — trocar pelo CNPJ real da matriz antes de qualquer uso além de dev local).
INSERT INTO empresa (razao_social, nome_fantasia, cnpj, crt, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
VALUES ('Cactus Digital Tecnologia Ltda (DEMO)', 'Cactus Digital', '00000000000000', '3', 'A', NOW(6), NOW(6), 'seed', 'seed');
