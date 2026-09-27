-- Módulo Comercial (Fase 7) — Vendas + Gestão de Comissões + AFV (Força de Vendas).
-- Validado campo a campo contra o fenix.sql (VENDA_CONDICOES_PAGAMENTO/_PARCELAS,
-- VENDA_ORCAMENTO_CABECALHO/_DETALHE, VENDA_CABECALHO/_DETALHE, VENDA_FRETE,
-- NOTA_FISCAL_MODELO/_TIPO, COMISSAO_PERFIL, VENDEDOR, COMISSAO_OBJETIVO, VENDA_COMISSAO,
-- VENDEDOR_ROTA, VENDEDOR_META) — o BANCO_PDV.md não tem nada de vendas B2B/atacado
-- (front-of-store puro). Ver CLAUDE.md, seção ANÁLISE: MÓDULO COMERCIAL.
--
-- colaborador_id/cliente_id/transportadora_id/produto_id/forma_pagamento_id são
-- referências SOLTAS pro cadastro-base-service; deposito_id é solta pro
-- controle-estoque-service. Sem FK cross-schema, sem validação de existência na v1 —
-- mesmo padrão já adotado em todo o monorepo.

CREATE TABLE comissao_perfil (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    codigo            VARCHAR(3)   NULL,
    nome              VARCHAR(100) NOT NULL,
    situacao          CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE vendedor (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    colaborador_id        BIGINT        NOT NULL,
    comissao_perfil_id    BIGINT        NULL,
    comissao_percentual   DECIMAL(18,6) NULL,
    meta_venda            DECIMAL(18,6) NULL,
    situacao              CHAR(1)       NOT NULL DEFAULT 'A',
    data_criacao          DATETIME(6)   NOT NULL,
    data_atualizacao      DATETIME(6)   NOT NULL,
    criado_por            VARCHAR(100)  NULL,
    atualizado_por        VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_vendedor_colaborador (colaborador_id),
    KEY idx_vendedor_comissao_perfil (comissao_perfil_id),
    CONSTRAINT fk_vendedor_comissao_perfil FOREIGN KEY (comissao_perfil_id) REFERENCES comissao_perfil (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE comissao_objetivo (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    comissao_perfil_id    BIGINT        NOT NULL,
    produto_id            BIGINT        NOT NULL,
    codigo                VARCHAR(3)    NULL,
    nome                  VARCHAR(100)  NOT NULL,
    descricao             VARCHAR(250)  NULL,
    forma_pagamento       VARCHAR(10)   NULL COMMENT 'VISTA | PRAZO',
    taxa_pagamento        DECIMAL(18,6) NULL,
    valor_pagamento       DECIMAL(18,6) NULL,
    valor_meta            DECIMAL(18,6) NULL,
    quantidade            DECIMAL(18,6) NULL,
    data_criacao          DATETIME(6)   NOT NULL,
    data_atualizacao      DATETIME(6)   NOT NULL,
    criado_por            VARCHAR(100)  NULL,
    atualizado_por        VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_comissao_objetivo_perfil (comissao_perfil_id),
    KEY idx_comissao_objetivo_produto (produto_id),
    CONSTRAINT fk_comissao_objetivo_perfil FOREIGN KEY (comissao_perfil_id) REFERENCES comissao_perfil (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE vendedor_rota (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    vendedor_id       BIGINT       NOT NULL,
    cliente_id        BIGINT       NOT NULL,
    posicao           INT          NULL,
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_vendedor_rota_vendedor (vendedor_id),
    CONSTRAINT fk_vendedor_rota_vendedor FOREIGN KEY (vendedor_id) REFERENCES vendedor (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE vendedor_meta (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    vendedor_id       BIGINT        NOT NULL,
    cliente_id        BIGINT        NOT NULL,
    periodo_meta      VARCHAR(10)   NULL COMMENT 'DIARIO | SEMANAL | MENSAL | ANUAL',
    meta_orcada       DECIMAL(18,6) NULL,
    meta_realizada    DECIMAL(18,6) NOT NULL DEFAULT 0,
    data_inicio       DATE          NULL,
    data_fim          DATE          NULL,
    data_criacao      DATETIME(6)   NOT NULL,
    data_atualizacao  DATETIME(6)   NOT NULL,
    criado_por        VARCHAR(100)  NULL,
    atualizado_por    VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_vendedor_meta_vendedor (vendedor_id),
    CONSTRAINT fk_vendedor_meta_vendedor FOREIGN KEY (vendedor_id) REFERENCES vendedor (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE venda_condicoes_pagamento (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    nome                  VARCHAR(50)   NOT NULL,
    descricao             VARCHAR(250)  NULL,
    faturamento_minimo    DECIMAL(18,6) NULL,
    faturamento_maximo    DECIMAL(18,6) NULL,
    indice_correcao       DECIMAL(18,6) NULL,
    dias_tolerancia       INT           NULL,
    valor_tolerancia      DECIMAL(18,6) NULL,
    prazo_medio           INT           NULL,
    vista_prazo           VARCHAR(10)   NULL COMMENT 'VISTA | PRAZO',
    situacao              CHAR(1)       NOT NULL DEFAULT 'A',
    data_criacao          DATETIME(6)   NOT NULL,
    data_atualizacao      DATETIME(6)   NOT NULL,
    criado_por            VARCHAR(100)  NULL,
    atualizado_por        VARCHAR(100)  NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE venda_condicoes_parcela (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    condicoes_pagamento_id BIGINT       NOT NULL,
    parcela               INT           NOT NULL,
    dias                  INT           NULL,
    taxa                  DECIMAL(18,6) NULL,
    data_criacao          DATETIME(6)   NOT NULL,
    data_atualizacao      DATETIME(6)   NOT NULL,
    criado_por            VARCHAR(100)  NULL,
    atualizado_por        VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_condicoes_parcela_condicoes (condicoes_pagamento_id),
    CONSTRAINT fk_condicoes_parcela_condicoes FOREIGN KEY (condicoes_pagamento_id) REFERENCES venda_condicoes_pagamento (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE nota_fiscal_modelo (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    codigo            VARCHAR(2)   NULL,
    descricao         VARCHAR(100) NULL,
    modelo            VARCHAR(10)  NULL,
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_nota_fiscal_modelo_codigo (codigo)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE nota_fiscal_tipo (
    id                    BIGINT       NOT NULL AUTO_INCREMENT,
    nota_fiscal_modelo_id BIGINT       NOT NULL,
    nome                  VARCHAR(50)  NOT NULL,
    descricao             VARCHAR(250) NULL,
    serie                 VARCHAR(3)   NULL,
    serie_scan            VARCHAR(3)   NULL,
    ultimo_numero         INT          NOT NULL DEFAULT 0,
    situacao              CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao          DATETIME(6)  NOT NULL,
    data_atualizacao      DATETIME(6)  NOT NULL,
    criado_por            VARCHAR(100) NULL,
    atualizado_por        VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_nota_fiscal_tipo_modelo (nota_fiscal_modelo_id),
    CONSTRAINT fk_nota_fiscal_tipo_modelo FOREIGN KEY (nota_fiscal_modelo_id) REFERENCES nota_fiscal_modelo (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE venda_orcamento (
    id                     BIGINT        NOT NULL AUTO_INCREMENT,
    condicoes_pagamento_id BIGINT        NOT NULL,
    vendedor_id            BIGINT        NOT NULL,
    cliente_id             BIGINT        NOT NULL,
    transportadora_id      BIGINT        NULL,
    codigo                 VARCHAR(20)   NULL,
    data_cadastro          DATE          NULL,
    data_entrega           DATE          NULL,
    validade               DATE          NULL,
    tipo_frete             VARCHAR(3)    NULL COMMENT 'CIF | FOB',
    valor_subtotal         DECIMAL(18,6) NOT NULL DEFAULT 0,
    valor_frete            DECIMAL(18,6) NULL,
    taxa_comissao          DECIMAL(18,6) NULL,
    valor_comissao         DECIMAL(18,6) NULL,
    taxa_desconto          DECIMAL(18,6) NULL,
    valor_desconto         DECIMAL(18,6) NOT NULL DEFAULT 0,
    valor_total            DECIMAL(18,6) NOT NULL DEFAULT 0,
    observacao             VARCHAR(250)  NULL,
    situacao               VARCHAR(15)   NOT NULL COMMENT 'RASCUNHO | ENVIADO | APROVADO | CONVERTIDO | EXPIRADO | CANCELADO',
    data_criacao           DATETIME(6)   NOT NULL,
    data_atualizacao       DATETIME(6)   NOT NULL,
    criado_por             VARCHAR(100)  NULL,
    atualizado_por         VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_orcamento_condicoes_pagamento (condicoes_pagamento_id),
    KEY idx_orcamento_vendedor (vendedor_id),
    KEY idx_orcamento_cliente (cliente_id),
    KEY idx_orcamento_situacao (situacao),
    CONSTRAINT fk_orcamento_condicoes_pagamento FOREIGN KEY (condicoes_pagamento_id) REFERENCES venda_condicoes_pagamento (id),
    CONSTRAINT fk_orcamento_vendedor FOREIGN KEY (vendedor_id) REFERENCES vendedor (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE venda_orcamento_item (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    orcamento_id      BIGINT        NOT NULL,
    produto_id        BIGINT        NOT NULL,
    quantidade        DECIMAL(18,6) NOT NULL,
    valor_unitario    DECIMAL(18,6) NOT NULL,
    valor_subtotal    DECIMAL(18,6) NULL,
    taxa_desconto     DECIMAL(18,6) NULL,
    valor_desconto    DECIMAL(18,6) NULL,
    valor_total       DECIMAL(18,6) NULL,
    data_criacao      DATETIME(6)   NOT NULL,
    data_atualizacao  DATETIME(6)   NOT NULL,
    criado_por        VARCHAR(100)  NULL,
    atualizado_por    VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_orcamento_item_orcamento (orcamento_id),
    CONSTRAINT fk_orcamento_item_orcamento FOREIGN KEY (orcamento_id) REFERENCES venda_orcamento (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE venda_cabecalho (
    id                     BIGINT        NOT NULL AUTO_INCREMENT,
    empresa_id             BIGINT        NOT NULL COMMENT 'Filial que está vendendo — FK solta pro Empresa do cadastro-base, mesmo padrão de pedido_compra.empresa_id',
    venda_orcamento_id     BIGINT        NULL,
    condicoes_pagamento_id BIGINT        NOT NULL,
    nota_fiscal_tipo_id    BIGINT        NOT NULL,
    vendedor_id            BIGINT        NOT NULL,
    cliente_id             BIGINT        NOT NULL,
    transportadora_id      BIGINT        NULL,
    forma_pagamento_id     BIGINT        NOT NULL COMMENT 'FK solta pro FormaPagamento do cadastro-base (Fase 1c) — substitui o CHAR(1) solto do fenix',
    data_venda             DATE          NULL,
    data_saida             DATE          NULL,
    hora_saida             VARCHAR(8)    NULL,
    numero_fatura          INT           NULL,
    local_entrega          VARCHAR(100)  NULL,
    local_cobranca         VARCHAR(100)  NULL,
    valor_subtotal         DECIMAL(18,6) NOT NULL DEFAULT 0,
    taxa_comissao          DECIMAL(18,6) NULL,
    valor_comissao         DECIMAL(18,6) NOT NULL DEFAULT 0,
    taxa_desconto          DECIMAL(18,6) NULL,
    valor_desconto         DECIMAL(18,6) NOT NULL DEFAULT 0,
    valor_total            DECIMAL(18,6) NOT NULL DEFAULT 0,
    tipo_frete             VARCHAR(3)    NULL COMMENT 'CIF | FOB',
    valor_frete            DECIMAL(18,6) NULL,
    valor_seguro           DECIMAL(18,6) NULL,
    dia_fixo_parcela       INT           NULL,
    observacao             VARCHAR(250)  NULL,
    situacao               VARCHAR(15)   NOT NULL COMMENT 'RASCUNHO | CONFIRMADA | CANCELADA',
    data_criacao           DATETIME(6)   NOT NULL,
    data_atualizacao       DATETIME(6)   NOT NULL,
    criado_por             VARCHAR(100)  NULL,
    atualizado_por         VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_venda_orcamento (venda_orcamento_id),
    KEY idx_venda_condicoes_pagamento (condicoes_pagamento_id),
    KEY idx_venda_nota_fiscal_tipo (nota_fiscal_tipo_id),
    KEY idx_venda_vendedor (vendedor_id),
    KEY idx_venda_cliente (cliente_id),
    KEY idx_venda_situacao (situacao),
    CONSTRAINT fk_venda_orcamento FOREIGN KEY (venda_orcamento_id) REFERENCES venda_orcamento (id),
    CONSTRAINT fk_venda_condicoes_pagamento FOREIGN KEY (condicoes_pagamento_id) REFERENCES venda_condicoes_pagamento (id),
    CONSTRAINT fk_venda_nota_fiscal_tipo FOREIGN KEY (nota_fiscal_tipo_id) REFERENCES nota_fiscal_tipo (id),
    CONSTRAINT fk_venda_vendedor FOREIGN KEY (vendedor_id) REFERENCES vendedor (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE venda_item (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    venda_id          BIGINT        NOT NULL,
    produto_id        BIGINT        NOT NULL,
    deposito_id       BIGINT        NULL COMMENT 'FK solta pro Deposito do controle-estoque-service — enriquecimento (fenix não modela multi-depósito). NULLABLE: item copiado de orçamento nasce sem depósito, obrigatório só na hora de confirmar a venda (validado em VendaService#confirmar)',
    quantidade        DECIMAL(18,6) NOT NULL,
    valor_unitario    DECIMAL(18,6) NOT NULL,
    valor_subtotal    DECIMAL(18,6) NULL,
    taxa_desconto     DECIMAL(18,6) NULL,
    valor_desconto    DECIMAL(18,6) NULL,
    valor_total       DECIMAL(18,6) NULL,
    data_criacao      DATETIME(6)   NOT NULL,
    data_atualizacao  DATETIME(6)   NOT NULL,
    criado_por        VARCHAR(100)  NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_venda_item_venda (venda_id),
    CONSTRAINT fk_venda_item_venda FOREIGN KEY (venda_id) REFERENCES venda_cabecalho (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE venda_frete (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    venda_id              BIGINT        NOT NULL,
    transportadora_id     BIGINT        NULL,
    conhecimento          VARCHAR(20)   NULL,
    responsavel           VARCHAR(3)    NULL COMMENT 'CIF | FOB',
    placa                 VARCHAR(7)    NULL,
    uf_placa              CHAR(2)       NULL,
    selo_fiscal           VARCHAR(20)   NULL,
    quantidade_volume     DECIMAL(18,6) NULL,
    marca_volume          VARCHAR(50)   NULL,
    especie_volume        VARCHAR(20)   NULL,
    peso_bruto            DECIMAL(18,6) NULL,
    peso_liquido          DECIMAL(18,6) NULL,
    data_criacao          DATETIME(6)   NOT NULL,
    data_atualizacao      DATETIME(6)   NOT NULL,
    criado_por            VARCHAR(100)  NULL,
    atualizado_por        VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_venda_frete_venda (venda_id),
    CONSTRAINT fk_venda_frete_venda FOREIGN KEY (venda_id) REFERENCES venda_cabecalho (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE venda_comissao (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    venda_id          BIGINT        NOT NULL,
    vendedor_id       BIGINT        NOT NULL,
    valor_venda       DECIMAL(18,6) NOT NULL,
    tipo_contabil     VARCHAR(10)   NULL COMMENT 'VISTA | PRAZO',
    valor_comissao    DECIMAL(18,6) NOT NULL,
    situacao          VARCHAR(10)   NOT NULL DEFAULT 'PENDENTE' COMMENT 'PENDENTE | PAGO | CANCELADO',
    data_lancamento   DATE          NULL,
    data_criacao      DATETIME(6)   NOT NULL,
    data_atualizacao  DATETIME(6)   NOT NULL,
    criado_por        VARCHAR(100)  NULL,
    atualizado_por    VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_venda_comissao_venda (venda_id),
    KEY idx_venda_comissao_vendedor (vendedor_id),
    CONSTRAINT fk_venda_comissao_venda FOREIGN KEY (venda_id) REFERENCES venda_cabecalho (id),
    CONSTRAINT fk_venda_comissao_vendedor FOREIGN KEY (vendedor_id) REFERENCES vendedor (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
