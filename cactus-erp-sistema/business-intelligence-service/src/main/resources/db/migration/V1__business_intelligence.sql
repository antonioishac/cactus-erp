-- Schema de leitura do Business Intelligence — alimentado por ETL agendado
-- (EtlService), nunca escrito por chamada de API de negócio direta. Sem tabela de
-- referência no fenix.sql (não existe nada de BI/dashboard lá) — desenho novo.
--
-- Dimensões: cache local denormalizado (nome já resolvido), reconstruído a cada ETL —
-- diferente do resto do monorepo, aqui cachear nome é deliberado (BI serve relatório
-- rápido sem depender de chamada síncrona a outro serviço em tempo de leitura).
-- Fatos/snapshots: histórico por data de referência, idempotentes (ETL deleta e
-- reinsere a mesma chave de referência a cada execução — reprocessável sem duplicar).

CREATE TABLE produto_dim (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    produto_id        BIGINT       NOT NULL COMMENT 'Id do produto no cadastro-base-service',
    nome              VARCHAR(150) NOT NULL,
    codigo_interno    VARCHAR(50)  NULL,
    situacao          CHAR(1)      NOT NULL,
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_produto_dim_produto (produto_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE fornecedor_dim (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    fornecedor_id     BIGINT       NOT NULL COMMENT 'Id do fornecedor no cadastro-base-service',
    nome              VARCHAR(150) NOT NULL,
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_fornecedor_dim_fornecedor (fornecedor_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE deposito_dim (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    deposito_id       BIGINT       NOT NULL COMMENT 'Id do depósito no controle-estoque-service',
    nome              VARCHAR(100) NOT NULL,
    empresa_id        BIGINT       NOT NULL,
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_deposito_dim_deposito (deposito_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE estoque_valor_snapshot (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    data_referencia       DATE          NOT NULL,
    deposito_id           BIGINT        NOT NULL,
    produto_id            BIGINT        NOT NULL,
    quantidade            DECIMAL(18,6) NOT NULL,
    custo_medio_ponderado DECIMAL(18,6) NOT NULL,
    valor_total           DECIMAL(18,6) NOT NULL,
    data_criacao          DATETIME(6)   NOT NULL,
    data_atualizacao      DATETIME(6)   NOT NULL,
    criado_por            VARCHAR(100)  NULL,
    atualizado_por        VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_estoque_valor_snapshot (data_referencia, deposito_id, produto_id),
    KEY idx_estoque_valor_snapshot_data (data_referencia)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE curva_abc_snapshot (
    id                        BIGINT        NOT NULL AUTO_INCREMENT,
    data_referencia           DATE          NOT NULL,
    deposito_id               BIGINT        NOT NULL,
    produto_id                BIGINT        NOT NULL,
    valor_movimentado         DECIMAL(18,6) NOT NULL,
    percentual_participacao   DECIMAL(9,4)  NOT NULL,
    percentual_acumulado      DECIMAL(9,4)  NOT NULL,
    classificacao             CHAR(1)       NOT NULL COMMENT 'A | B | C',
    data_criacao              DATETIME(6)   NOT NULL,
    data_atualizacao          DATETIME(6)   NOT NULL,
    criado_por                VARCHAR(100)  NULL,
    atualizado_por            VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_curva_abc_snapshot (data_referencia, deposito_id, produto_id),
    KEY idx_curva_abc_snapshot_data (data_referencia)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE compras_fornecedor_resumo (
    id                     BIGINT        NOT NULL AUTO_INCREMENT,
    ano_mes                CHAR(7)       NOT NULL COMMENT 'Formato YYYY-MM',
    fornecedor_id          BIGINT        NOT NULL,
    quantidade_pedidos     INT           NOT NULL,
    valor_total_comprado   DECIMAL(18,6) NOT NULL,
    data_criacao           DATETIME(6)   NOT NULL,
    data_atualizacao       DATETIME(6)   NOT NULL,
    criado_por             VARCHAR(100)  NULL,
    atualizado_por         VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_compras_fornecedor_resumo (ano_mes, fornecedor_id),
    KEY idx_compras_fornecedor_resumo_periodo (ano_mes)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
