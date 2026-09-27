-- Fase 13 (Loja Virtual B2C, primeira fatia) — Loja (tenant SaaS por assinatura) +
-- catálogo genérico Categoria/Atributo/Produto/Variante, capaz de atender qualquer
-- nicho (roupa, vinho, doce, infantil, construção) sem tabela nova por vertical. Ver
-- CLAUDE.md, seção ANÁLISE: LOJA VIRTUAL B2C — WHITE LABEL MULTI-NICHO. Greenfield —
-- sem equivalente no fenix.sql/BANCO_PDV.md.
--
-- Modo standalone desta fatia: este catálogo é a fonte de verdade (sem sincronização
-- com cadastro-base-service ainda). loja_id é o discriminador de tenant em toda tabela
-- de catálogo — mesmo padrão que empresa_id já cumpre pra multi-filial no resto do
-- monorepo, só que agora isolando lojistas DIFERENTES e mutuamente estranhos.

CREATE TABLE loja (
    id                        BIGINT       NOT NULL AUTO_INCREMENT,
    nome                      VARCHAR(120) NOT NULL,
    razao_social              VARCHAR(150) NULL,
    cnpj                      VARCHAR(14)  NULL,
    subdominio                VARCHAR(60)  NOT NULL,
    email_contato             VARCHAR(120) NULL,
    telefone                  VARCHAR(20)  NULL,
    plano_assinatura          VARCHAR(15)  NOT NULL DEFAULT 'BASICO' COMMENT 'BASICO | INTERMEDIARIO | PREMIUM',
    situacao_assinatura       VARCHAR(15)  NOT NULL DEFAULT 'ATIVA' COMMENT 'ATIVA | INADIMPLENTE | CANCELADA',
    data_inicio_assinatura    DATE         NULL,
    situacao                  CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao              DATETIME(6)  NOT NULL,
    data_atualizacao          DATETIME(6)  NOT NULL,
    criado_por                VARCHAR(100) NULL,
    atualizado_por            VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_loja_subdominio (subdominio)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE categoria_loja (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    loja_id              BIGINT       NOT NULL,
    categoria_pai_id     BIGINT       NULL,
    nome                 VARCHAR(100) NOT NULL,
    ordem                INT          NOT NULL DEFAULT 0,
    situacao             CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao         DATETIME(6)  NOT NULL,
    data_atualizacao     DATETIME(6)  NOT NULL,
    criado_por           VARCHAR(100) NULL,
    atualizado_por       VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_categoria_loja_loja (loja_id),
    KEY idx_categoria_loja_pai (categoria_pai_id),
    CONSTRAINT fk_categoria_loja_pai FOREIGN KEY (categoria_pai_id) REFERENCES categoria_loja (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE atributo_categoria (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    categoria_loja_id    BIGINT       NOT NULL,
    nome                 VARCHAR(60)  NOT NULL,
    tipo                 VARCHAR(10)  NOT NULL COMMENT 'TEXTO | NUMERO | LISTA',
    obrigatorio          TINYINT(1)   NOT NULL DEFAULT 0,
    gera_variacao        TINYINT(1)   NOT NULL DEFAULT 0,
    ordem                INT          NOT NULL DEFAULT 0,
    data_criacao         DATETIME(6)  NOT NULL,
    data_atualizacao     DATETIME(6)  NOT NULL,
    criado_por           VARCHAR(100) NULL,
    atualizado_por       VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_atributo_categoria_categoria (categoria_loja_id),
    CONSTRAINT fk_atributo_categoria_categoria FOREIGN KEY (categoria_loja_id) REFERENCES categoria_loja (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE atributo_categoria_opcao (
    id                       BIGINT       NOT NULL AUTO_INCREMENT,
    atributo_categoria_id    BIGINT       NOT NULL,
    valor                    VARCHAR(60)  NOT NULL,
    ordem                    INT          NOT NULL DEFAULT 0,
    data_criacao             DATETIME(6)  NOT NULL,
    data_atualizacao         DATETIME(6)  NOT NULL,
    criado_por               VARCHAR(100) NULL,
    atualizado_por           VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_atributo_categoria_opcao_atributo (atributo_categoria_id),
    CONSTRAINT fk_atributo_categoria_opcao_atributo FOREIGN KEY (atributo_categoria_id) REFERENCES atributo_categoria (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE produto_loja (
    id                   BIGINT        NOT NULL AUTO_INCREMENT,
    loja_id              BIGINT        NOT NULL,
    categoria_loja_id    BIGINT        NOT NULL,
    nome                 VARCHAR(150)  NOT NULL,
    descricao            VARCHAR(2000) NULL,
    marca                VARCHAR(60)   NULL,
    situacao             CHAR(1)       NOT NULL DEFAULT 'A',
    data_criacao         DATETIME(6)   NOT NULL,
    data_atualizacao     DATETIME(6)   NOT NULL,
    criado_por           VARCHAR(100)  NULL,
    atualizado_por       VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_produto_loja_loja (loja_id),
    KEY idx_produto_loja_categoria (categoria_loja_id),
    CONSTRAINT fk_produto_loja_categoria FOREIGN KEY (categoria_loja_id) REFERENCES categoria_loja (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE variante_produto_loja (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    produto_loja_id       BIGINT        NOT NULL,
    sku                   VARCHAR(50)   NOT NULL,
    gtin                  VARCHAR(20)   NULL,
    preco_venda           DECIMAL(18,6) NOT NULL,
    preco_promocional     DECIMAL(18,6) NULL,
    estoque_quantidade    DECIMAL(18,6) NOT NULL DEFAULT 0 COMMENT 'Contador flat — modo standalone, ver Javadoc de VarianteProdutoLoja',
    peso_kg               DECIMAL(10,3) NULL,
    situacao              CHAR(1)       NOT NULL DEFAULT 'A',
    data_criacao          DATETIME(6)   NOT NULL,
    data_atualizacao      DATETIME(6)   NOT NULL,
    criado_por            VARCHAR(100)  NULL,
    atualizado_por        VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_variante_produto_loja_produto (produto_loja_id),
    CONSTRAINT fk_variante_produto_loja_produto FOREIGN KEY (produto_loja_id) REFERENCES produto_loja (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE variante_atributo_valor (
    id                        BIGINT       NOT NULL AUTO_INCREMENT,
    variante_produto_loja_id  BIGINT       NOT NULL,
    atributo_categoria_id     BIGINT       NOT NULL,
    valor                     VARCHAR(60)  NOT NULL,
    data_criacao              DATETIME(6)  NOT NULL,
    data_atualizacao          DATETIME(6)  NOT NULL,
    criado_por                VARCHAR(100) NULL,
    atualizado_por            VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_variante_atributo_valor_variante (variante_produto_loja_id),
    KEY idx_variante_atributo_valor_atributo (atributo_categoria_id),
    CONSTRAINT fk_variante_atributo_valor_variante FOREIGN KEY (variante_produto_loja_id) REFERENCES variante_produto_loja (id),
    CONSTRAINT fk_variante_atributo_valor_atributo FOREIGN KEY (atributo_categoria_id) REFERENCES atributo_categoria (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
