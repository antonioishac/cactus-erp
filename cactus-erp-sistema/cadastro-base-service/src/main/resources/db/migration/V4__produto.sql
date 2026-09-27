-- Hierarquia de produto: Unidade / Marca / Grupo / Subgrupo / Tipo / Produto, mais os
-- mecanismos de preço (ProdutoFaixaPreco, TabelaPreco/TabelaPrecoProduto) e a galeria
-- (ProdutoImagem). Roda depois de V4 (ncm/cest) e V5 (grupo_tributario) porque `produto`
-- já nasce com essas FKs — nada aqui é corrigido por ALTER em migration futura.

CREATE TABLE produto_unidade (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    sigla             VARCHAR(10)  NOT NULL,
    descricao         VARCHAR(100) NOT NULL,
    pode_fracionar    TINYINT(1)   NOT NULL DEFAULT 0,
    situacao          CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_produto_unidade_sigla (sigla)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE produto_marca (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    nome              VARCHAR(100) NOT NULL,
    situacao          CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_produto_marca_nome (nome)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE produto_grupo (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    nome              VARCHAR(100) NOT NULL,
    descricao         VARCHAR(250) NULL,
    situacao          CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_produto_grupo_nome (nome)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE produto_subgrupo (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    produto_grupo_id  BIGINT       NOT NULL,
    nome              VARCHAR(100) NOT NULL,
    descricao         VARCHAR(250) NULL,
    situacao          CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_produto_subgrupo_grupo_nome (produto_grupo_id, nome),
    KEY idx_produto_subgrupo_grupo (produto_grupo_id),
    CONSTRAINT fk_produto_subgrupo_grupo FOREIGN KEY (produto_grupo_id) REFERENCES produto_grupo (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Lookup configurável pela empresa (ex: "Normal"/"Kit"/"Insumo") — distinto de Grupo/Subgrupo
-- (categoria comercial) e de `tipo_item_sped` (classificação fiscal fechada da Receita).
CREATE TABLE produto_tipo (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    codigo            VARCHAR(2)   NOT NULL,
    descricao         VARCHAR(100) NOT NULL,
    situacao          CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_produto_tipo_codigo (codigo)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE produto (
    id                     BIGINT        NOT NULL AUTO_INCREMENT,
    produto_unidade_id     BIGINT        NOT NULL,
    produto_marca_id       BIGINT        NULL,
    produto_subgrupo_id    BIGINT        NOT NULL,
    produto_tipo_id        BIGINT        NULL,
    gtin                   VARCHAR(14)   NULL,
    codigo_interno         VARCHAR(50)   NOT NULL,
    nome                   VARCHAR(150)  NOT NULL,
    descricao              VARCHAR(250)  NULL,
    descricao_pdv          VARCHAR(30)   NULL,

    -- Identidade fiscal (motor tributário — ver ANÁLISE TRIBUTÁRIA no CLAUDE.md).
    ncm_id                 BIGINT        NOT NULL,
    cest_id                BIGINT        NULL,
    grupo_tributario_id    BIGINT        NOT NULL,
    tipo_item_sped         VARCHAR(30)   NULL,
    iat                    VARCHAR(1)    NULL,
    ippt                   VARCHAR(1)    NULL,
    totalizador_parcial    VARCHAR(10)   NULL,
    codigo_balanca         INT           NULL,
    paf_p_st               VARCHAR(1)    NULL,

    -- Preço/custo.
    valor_custo            DECIMAL(18,6) NULL,
    valor_compra           DECIMAL(18,6) NULL,
    valor_venda            DECIMAL(18,6) NULL,
    estoque_minimo         DECIMAL(18,6) NULL,
    estoque_maximo         DECIMAL(18,6) NULL,

    -- Tributação resolvida, cacheada (sync rápida com o PDV).
    cst_icms_padrao        VARCHAR(10)   NULL,
    csosn_padrao           VARCHAR(10)   NULL,
    aliquota_icms_padrao   DECIMAL(18,6) NULL,
    aliquota_ipi_padrao    DECIMAL(18,6) NULL,
    aliquota_pis_padrao    DECIMAL(18,6) NULL,
    aliquota_cofins_padrao DECIMAL(18,6) NULL,
    aliquota_issqn_padrao  DECIMAL(18,6) NULL,
    data_resolucao_padrao  DATETIME(6)   NULL,

    situacao               CHAR(1)       NOT NULL DEFAULT 'A',
    data_criacao           DATETIME(6)   NOT NULL,
    data_atualizacao       DATETIME(6)   NOT NULL,
    criado_por             VARCHAR(100)  NULL,
    atualizado_por         VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_produto_codigo_interno (codigo_interno),
    KEY idx_produto_nome (nome),
    KEY idx_produto_gtin (gtin),
    KEY idx_produto_situacao (situacao),
    KEY idx_produto_unidade (produto_unidade_id),
    KEY idx_produto_marca (produto_marca_id),
    KEY idx_produto_subgrupo (produto_subgrupo_id),
    KEY idx_produto_tipo (produto_tipo_id),
    KEY idx_produto_ncm (ncm_id),
    KEY idx_produto_cest (cest_id),
    KEY idx_produto_grupo_tributario (grupo_tributario_id),
    CONSTRAINT fk_produto_unidade FOREIGN KEY (produto_unidade_id) REFERENCES produto_unidade (id),
    CONSTRAINT fk_produto_marca FOREIGN KEY (produto_marca_id) REFERENCES produto_marca (id),
    CONSTRAINT fk_produto_subgrupo FOREIGN KEY (produto_subgrupo_id) REFERENCES produto_subgrupo (id),
    CONSTRAINT fk_produto_tipo FOREIGN KEY (produto_tipo_id) REFERENCES produto_tipo (id),
    CONSTRAINT fk_produto_ncm FOREIGN KEY (ncm_id) REFERENCES ncm (id),
    CONSTRAINT fk_produto_cest FOREIGN KEY (cest_id) REFERENCES cest (id),
    CONSTRAINT fk_produto_grupo_tributario FOREIGN KEY (grupo_tributario_id) REFERENCES grupo_tributario (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- PRODUTO_FAIXA_PRECO do cactus-pdv — um conceito só cobrindo desconto por quantidade E
-- preço promocional por vigência de data (ver seção própria no CLAUDE.md).
CREATE TABLE produto_faixa_preco (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    produto_id        BIGINT        NOT NULL,
    quantidade_minima DECIMAL(18,6) NOT NULL DEFAULT 1,
    valor_unitario    DECIMAL(18,6) NOT NULL,
    rotulo            VARCHAR(50)   NULL,
    data_inicio       DATE          NULL,
    data_fim          DATE          NULL,
    situacao          CHAR(1)       NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)   NOT NULL,
    data_atualizacao  DATETIME(6)   NOT NULL,
    criado_por        VARCHAR(100)  NULL,
    atualizado_por    VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_produto_faixa_preco_produto (produto_id),
    KEY idx_produto_faixa_preco_vigencia (data_inicio, data_fim),
    CONSTRAINT fk_produto_faixa_preco_produto FOREIGN KEY (produto_id) REFERENCES produto (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- PRODUTO_IMAGEM do cactus-pdv — galeria. Guarda só a URL (upload binário depende do
-- futuro GED, Fase 4), com hash pra deduplicar a mesma imagem re-enviada.
CREATE TABLE produto_imagem (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    produto_id        BIGINT       NOT NULL,
    url               VARCHAR(500) NOT NULL,
    hash_arquivo      VARCHAR(64)  NULL,
    principal         TINYINT(1)   NOT NULL DEFAULT 0,
    ordem             INT          NOT NULL DEFAULT 0,
    situacao          CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_produto_imagem_produto (produto_id),
    KEY idx_produto_imagem_hash (hash_arquivo),
    CONSTRAINT fk_produto_imagem_produto FOREIGN KEY (produto_id) REFERENCES produto (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Precificação por canal/perfil (Varejo/Atacado/VIP) — complementar a produto_faixa_preco
-- (desconto por quantidade/promoção). Validado contra fenix.sql.TABELA_PRECO.
CREATE TABLE tabela_preco (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    nome              VARCHAR(100) NOT NULL,
    descricao         VARCHAR(250) NULL,
    tipo_ajuste       VARCHAR(30)  NOT NULL,
    situacao          CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tabela_preco_nome (nome)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE tabela_preco_produto (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    tabela_preco_id     BIGINT        NOT NULL,
    produto_id          BIGINT        NOT NULL,
    valor               DECIMAL(18,6) NULL,
    percentual_ajuste   DECIMAL(18,6) NULL,
    data_criacao        DATETIME(6)   NOT NULL,
    data_atualizacao    DATETIME(6)   NOT NULL,
    criado_por          VARCHAR(100)  NULL,
    atualizado_por      VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tabela_preco_produto (tabela_preco_id, produto_id),
    KEY idx_tabela_preco_produto_produto (produto_id),
    CONSTRAINT fk_tabela_preco_produto_tabela FOREIGN KEY (tabela_preco_id) REFERENCES tabela_preco (id),
    CONSTRAINT fk_tabela_preco_produto_produto FOREIGN KEY (produto_id) REFERENCES produto (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
