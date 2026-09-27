-- Cactus ERP — provisionamento no MySQL local já existente (container `cactus-mysql`).
--
-- Não sobe nenhum container novo, não mexe em `cactus_pdv` nem nos outros bancos que já
-- estão lá — só acrescenta os schemas do ERP e um usuário de aplicação por schema
-- (least privilege: cada serviço só enxerga o próprio schema).
--
-- Como rodar (uma vez), a partir da raiz do repo:
--   docker exec -i cactus-mysql mysql -uroot -p < provisionamento-mysql-local.sql
-- (vai pedir a senha do root do próprio container — a que você já usa hoje.)
--
-- ATENÇÃO: senhas de aplicação abaixo são só para desenvolvimento LOCAL.

-- cactus_seguranca — seguranca-service (RBAC: Usuario/Papel/Funcionalidade/Modulo/
-- UsuarioPapelEmpresa, e Authorization Server OAuth2/JWT do ecossistema — os demais
-- serviços validam token contra o JWKS exposto aqui)
CREATE DATABASE IF NOT EXISTS cactus_seguranca CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'cactus_seguranca_app'@'%' IDENTIFIED BY 'cactus_seguranca_local';
GRANT ALL PRIVILEGES ON cactus_seguranca.* TO 'cactus_seguranca_app'@'%';

-- cactus_cadastro — cadastro-base-service (cadastros base: Pessoa/Cliente/Fornecedor/
-- Colaborador/Empresa/Produto/tributação — Resource Server puro, sem RBAC/Authorization
-- Server desde a extração pro seguranca-service)
CREATE DATABASE IF NOT EXISTS cactus_cadastro CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'cactus_cadastro_app'@'%' IDENTIFIED BY 'cactus_cadastro_local';
GRANT ALL PRIVILEGES ON cactus_cadastro.* TO 'cactus_cadastro_app'@'%';

-- cactus_estoque — controle-estoque-service (depósitos, ledger de movimentação,
-- lote/série, transferência, inventário, kit, curva ABC)
CREATE DATABASE IF NOT EXISTS cactus_estoque CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'cactus_estoque_app'@'%' IDENTIFIED BY 'cactus_estoque_local';
GRANT ALL PRIVILEGES ON cactus_estoque.* TO 'cactus_estoque_app'@'%';

-- cactus_compras — gestao-compras-service (Fase 3, porta 8084)
CREATE DATABASE IF NOT EXISTS cactus_compras CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'cactus_compras_app'@'%' IDENTIFIED BY 'cactus_compras_local';
GRANT ALL PRIVILEGES ON cactus_compras.* TO 'cactus_compras_app'@'%';

-- cactus_ged — gestao-documentos-service (fase futura)
CREATE DATABASE IF NOT EXISTS cactus_ged CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'cactus_ged_app'@'%' IDENTIFIED BY 'cactus_ged_local';
GRANT ALL PRIVILEGES ON cactus_ged.* TO 'cactus_ged_app'@'%';

-- cactus_bi — business-intelligence-service (Fase 5, porta 8086)
CREATE DATABASE IF NOT EXISTS cactus_bi CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'cactus_bi_app'@'%' IDENTIFIED BY 'cactus_bi_local';
GRANT ALL PRIVILEGES ON cactus_bi.* TO 'cactus_bi_app'@'%';

-- cactus_sincronizacao — sincronizacao-service (Fase 6, porta 8087)
CREATE DATABASE IF NOT EXISTS cactus_sincronizacao CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'cactus_sincronizacao_app'@'%' IDENTIFIED BY 'cactus_sincronizacao_local';
GRANT ALL PRIVILEGES ON cactus_sincronizacao.* TO 'cactus_sincronizacao_app'@'%';

-- cactus_vendas — vendas-service (Fase 7, Módulo Comercial, porta 8088)
CREATE DATABASE IF NOT EXISTS cactus_vendas CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'cactus_vendas_app'@'%' IDENTIFIED BY 'cactus_vendas_local';
GRANT ALL PRIVILEGES ON cactus_vendas.* TO 'cactus_vendas_app'@'%';

-- cactus_fiscal — fiscal-service (Fase 8, Módulo Comercial, porta 8089)
CREATE DATABASE IF NOT EXISTS cactus_fiscal CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'cactus_fiscal_app'@'%' IDENTIFIED BY 'cactus_fiscal_local';
GRANT ALL PRIVILEGES ON cactus_fiscal.* TO 'cactus_fiscal_app'@'%';

-- cactus_os — os-service (Fase 10, Módulo Comercial, porta 8090)
CREATE DATABASE IF NOT EXISTS cactus_os CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'cactus_os_app'@'%' IDENTIFIED BY 'cactus_os_local';
GRANT ALL PRIVILEGES ON cactus_os.* TO 'cactus_os_app'@'%';

-- cactus_crm — crm-service (Fase 12, Módulo Comercial, porta 8091)
CREATE DATABASE IF NOT EXISTS cactus_crm CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'cactus_crm_app'@'%' IDENTIFIED BY 'cactus_crm_local';
GRANT ALL PRIVILEGES ON cactus_crm.* TO 'cactus_crm_app'@'%';

-- cactus_loja — loja-virtual-service (Fase 13, porta 8092)
CREATE DATABASE IF NOT EXISTS cactus_loja CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'cactus_loja_app'@'%' IDENTIFIED BY 'cactus_loja_local';
GRANT ALL PRIVILEGES ON cactus_loja.* TO 'cactus_loja_app'@'%';

-- cactus_financeiro — financeiro-service (Contas a Receber/Contas a Pagar, porta 8093)
CREATE DATABASE IF NOT EXISTS cactus_financeiro CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'cactus_financeiro_app'@'%' IDENTIFIED BY 'cactus_financeiro_local';
GRANT ALL PRIVILEGES ON cactus_financeiro.* TO 'cactus_financeiro_app'@'%';

-- cactus_contabilidade — contabilidade-service (Fase 14, Módulo Contábil, porta 8094)
CREATE DATABASE IF NOT EXISTS cactus_contabilidade CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'cactus_contabilidade_app'@'%' IDENTIFIED BY 'cactus_contabilidade_local';
GRANT ALL PRIVILEGES ON cactus_contabilidade.* TO 'cactus_contabilidade_app'@'%';

FLUSH PRIVILEGES;
