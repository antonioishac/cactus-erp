-- Módulo Contábil (Fase 14). Greenfield: sem tabela equivalente no fenix.sql (confirmado
-- por grep exaustivo — só views de leitura pro SPED Fiscal, que ficam no fiscal-service)
-- nem no BANCO_PDV.md. Ver CLAUDE.md, seção ANÁLISE: MÓDULO CONTÁBIL E FISCAL.
--
-- empresa_id é referência SOLTA pro cadastro-base-service. Sem FK cross-schema, sem
-- validação de existência na v1 — mesmo padrão já adotado em todo o monorepo.

CREATE TABLE plano_conta (
    id                          BIGINT        NOT NULL AUTO_INCREMENT,
    empresa_id                  BIGINT        NOT NULL,
    codigo                      VARCHAR(30)   NOT NULL,
    nome                        VARCHAR(150)  NOT NULL,
    conta_pai_id                BIGINT        NULL,
    tipo                        VARCHAR(20)   NOT NULL COMMENT 'ATIVO | PASSIVO | PATRIMONIO_LIQUIDO | RECEITA | DESPESA | RESULTADO',
    natureza                    VARCHAR(10)   NOT NULL COMMENT 'DEVEDORA | CREDORA',
    conta_analitica             TINYINT(1)    NOT NULL DEFAULT 1 COMMENT '1 = recebe lançamento, 0 = sintética/agrupadora',
    codigo_referencial_rfb      VARCHAR(30)   NULL COMMENT 'vínculo opcional com o plano de contas referencial da RFB (registro I051 da ECD)',
    situacao                    CHAR(1)       NOT NULL DEFAULT 'A',
    data_criacao                DATETIME(6)   NOT NULL,
    data_atualizacao            DATETIME(6)   NOT NULL,
    criado_por                  VARCHAR(100)  NULL,
    atualizado_por               VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_plano_conta_empresa_codigo (empresa_id, codigo),
    KEY idx_plano_conta_empresa (empresa_id),
    KEY idx_plano_conta_pai (conta_pai_id),
    CONSTRAINT fk_plano_conta_pai FOREIGN KEY (conta_pai_id) REFERENCES plano_conta (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE centro_custo (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    empresa_id          BIGINT       NOT NULL,
    codigo              VARCHAR(30)  NOT NULL,
    nome                VARCHAR(150) NOT NULL,
    situacao            CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao        DATETIME(6)  NOT NULL,
    data_atualizacao    DATETIME(6)  NOT NULL,
    criado_por          VARCHAR(100) NULL,
    atualizado_por      VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_centro_custo_empresa_codigo (empresa_id, codigo)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE lancamento_contabil (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    empresa_id          BIGINT        NOT NULL,
    data_lancamento     DATE          NOT NULL,
    historico           VARCHAR(250)  NOT NULL,
    numero_documento    VARCHAR(30)   NULL,
    origem_tipo         VARCHAR(15)   NOT NULL DEFAULT 'MANUAL' COMMENT 'MANUAL | VENDA | COMPRA | FINANCEIRO',
    origem_id           BIGINT        NULL,
    situacao            VARCHAR(10)   NOT NULL DEFAULT 'LANCADO' COMMENT 'LANCADO | ESTORNADO',
    estorno_de_id       BIGINT        NULL COMMENT 'preenchido quando ESTE lançamento é o estorno de outro',
    estornado_por_id    BIGINT        NULL COMMENT 'preenchido no ORIGINAL quando ele foi estornado',
    data_criacao        DATETIME(6)   NOT NULL,
    data_atualizacao    DATETIME(6)   NOT NULL,
    criado_por          VARCHAR(100)  NULL,
    atualizado_por      VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_lancamento_empresa_data (empresa_id, data_lancamento),
    KEY idx_lancamento_estorno_de (estorno_de_id),
    CONSTRAINT fk_lancamento_estorno_de FOREIGN KEY (estorno_de_id) REFERENCES lancamento_contabil (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE partida_contabil (
    id                          BIGINT        NOT NULL AUTO_INCREMENT,
    lancamento_contabil_id      BIGINT        NOT NULL,
    conta_id                    BIGINT        NOT NULL,
    centro_custo_id             BIGINT        NULL,
    tipo_partida                VARCHAR(10)   NOT NULL COMMENT 'DEBITO | CREDITO',
    valor                       DECIMAL(18,6) NOT NULL,
    historico_complementar      VARCHAR(250)  NULL,
    data_criacao                DATETIME(6)   NOT NULL,
    data_atualizacao            DATETIME(6)   NOT NULL,
    criado_por                  VARCHAR(100)  NULL,
    atualizado_por              VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_partida_lancamento (lancamento_contabil_id),
    KEY idx_partida_conta (conta_id),
    KEY idx_partida_centro_custo (centro_custo_id),
    CONSTRAINT fk_partida_lancamento FOREIGN KEY (lancamento_contabil_id) REFERENCES lancamento_contabil (id),
    CONSTRAINT fk_partida_conta FOREIGN KEY (conta_id) REFERENCES plano_conta (id),
    CONSTRAINT fk_partida_centro_custo FOREIGN KEY (centro_custo_id) REFERENCES centro_custo (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
