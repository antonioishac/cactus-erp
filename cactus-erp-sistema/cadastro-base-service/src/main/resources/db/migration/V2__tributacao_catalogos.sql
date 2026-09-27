-- Catálogos de referência oficiais (Receita Federal/CONFAZ). Não são a tabela completa
-- (NCM tem ~10 mil códigos, CFOP ~600, CEST milhares) — só uma amostra representativa
-- pra desenvolvimento/demonstração. Importar a tabela oficial completa é rotina de
-- carga de dados (operacional), não migration de schema.

CREATE TABLE ncm (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    codigo            VARCHAR(8)   NOT NULL,
    descricao         VARCHAR(1000) NOT NULL,
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_ncm_codigo (codigo)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE cfop (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    codigo            INT           NOT NULL,
    descricao         VARCHAR(1000) NOT NULL,
    aplicacao         VARCHAR(1000) NULL,
    data_criacao      DATETIME(6)   NOT NULL,
    data_atualizacao  DATETIME(6)   NOT NULL,
    criado_por        VARCHAR(100)  NULL,
    atualizado_por    VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_cfop_codigo (codigo)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE cest (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    codigo            VARCHAR(7)    NOT NULL,
    descricao         VARCHAR(1000) NOT NULL,
    ncm_id            BIGINT        NULL,
    data_criacao      DATETIME(6)   NOT NULL,
    data_atualizacao  DATETIME(6)   NOT NULL,
    criado_por        VARCHAR(100)  NULL,
    atualizado_por    VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_cest_codigo (codigo),
    KEY idx_cest_ncm (ncm_id),
    CONSTRAINT fk_cest_ncm FOREIGN KEY (ncm_id) REFERENCES ncm (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Amostra: refrigerante (bebida, sujeita a ST em vários estados) e um NCM genérico de mercadoria.
INSERT INTO ncm (codigo, descricao, data_criacao, data_atualizacao, criado_por, atualizado_por) VALUES
  ('22021000', 'Águas, incl. as águas minerais e as gaseificadas, adicionadas de açúcar (refrigerantes)', NOW(6), NOW(6), 'seed', 'seed'),
  ('61091000', 'T-shirts e camisetas interiores, de malha, de algodão', NOW(6), NOW(6), 'seed', 'seed'),
  ('84151011', 'Aparelhos de ar condicionado tipo Split', NOW(6), NOW(6), 'seed', 'seed');

INSERT INTO cfop (codigo, descricao, aplicacao, data_criacao, data_atualizacao, criado_por, atualizado_por) VALUES
  (5102, 'Venda de mercadoria adquirida ou recebida de terceiros', 'Venda dentro do estado, mercadoria de revenda', NOW(6), NOW(6), 'seed', 'seed'),
  (6102, 'Venda de mercadoria adquirida ou recebida de terceiros', 'Venda interestadual, mercadoria de revenda', NOW(6), NOW(6), 'seed', 'seed'),
  (5405, 'Venda de mercadoria sujeita ao regime de substituição tributária, na condição de contribuinte substituído', 'Venda dentro do estado, ICMS-ST já retido', NOW(6), NOW(6), 'seed', 'seed'),
  (5101, 'Venda de produção do estabelecimento', 'Venda dentro do estado, produção própria', NOW(6), NOW(6), 'seed', 'seed');

INSERT INTO cest (codigo, descricao, ncm_id, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT '0300100', 'Refrigerante em garrafa', n.id, NOW(6), NOW(6), 'seed', 'seed' FROM ncm n WHERE n.codigo = '22021000';
