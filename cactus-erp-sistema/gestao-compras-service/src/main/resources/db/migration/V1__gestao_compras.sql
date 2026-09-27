-- Gestão de Compras do Cactus ERP: requisição de compra → cotação (opcional,
-- N fornecedores concorrentes) → pedido de compra (com aprovação acima de limiar) →
-- recebimento (gera ENTRADA de estoque via chamada serviço-a-serviço no
-- controle-estoque-service). Validado campo a campo contra o fenix.sql
-- (COMPRA_REQUISICAO/COMPRA_COTACAO/COMPRA_FORNECEDOR_COTACAO/COMPRA_PEDIDO) — o
-- BANCO_PDV.md não tem nada de compras (front-of-store puro).
--
-- produto_id/unidade_compra_id/fornecedor_id/colaborador_id são referências SOLTAS pro
-- cadastro-base-service; deposito_id/lote_id são soltas pro controle-estoque-service;
-- usuario_id/aprovador_id são soltas pro seguranca-service. Sem FK cross-schema, sem
-- validação de existência na v1 — mesmo padrão já adotado em todo o monorepo.

CREATE TABLE requisicao_compra (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    colaborador_id    BIGINT       NOT NULL,
    tipo              VARCHAR(10)  NOT NULL COMMENT 'INTERNA | EXTERNA | MISTA',
    descricao         VARCHAR(100) NULL,
    data_requisicao   DATE         NULL,
    situacao          VARCHAR(15)  NOT NULL COMMENT 'ABERTA | PEDIDO_GERADO | CANCELADA',
    observacao        VARCHAR(250) NULL,
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_requisicao_colaborador (colaborador_id),
    KEY idx_requisicao_situacao (situacao)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE requisicao_compra_item (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    requisicao_id     BIGINT        NOT NULL,
    produto_id        BIGINT        NOT NULL,
    quantidade        DECIMAL(18,6) NOT NULL,
    data_criacao      DATETIME(6)   NOT NULL,
    data_atualizacao  DATETIME(6)   NOT NULL,
    criado_por        VARCHAR(100)  NULL,
    atualizado_por    VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_requisicao_item_requisicao (requisicao_id),
    CONSTRAINT fk_requisicao_item_requisicao FOREIGN KEY (requisicao_id) REFERENCES requisicao_compra (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE cotacao_compra (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    requisicao_id     BIGINT       NOT NULL,
    data_cotacao      DATE         NULL,
    descricao         VARCHAR(100) NULL,
    situacao          VARCHAR(15)  NOT NULL COMMENT 'ABERTA | FINALIZADA | CANCELADA',
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_cotacao_requisicao (requisicao_id),
    CONSTRAINT fk_cotacao_requisicao FOREIGN KEY (requisicao_id) REFERENCES requisicao_compra (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE fornecedor_cotacao (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    cotacao_id            BIGINT        NOT NULL,
    fornecedor_id         BIGINT        NOT NULL,
    prazo_entrega         VARCHAR(50)   NULL,
    condicoes_pagamento   VARCHAR(50)   NULL,
    valor_subtotal        DECIMAL(18,6) NOT NULL DEFAULT 0,
    taxa_desconto         DECIMAL(18,6) NULL,
    valor_desconto        DECIMAL(18,6) NOT NULL DEFAULT 0,
    valor_total           DECIMAL(18,6) NOT NULL DEFAULT 0,
    data_criacao          DATETIME(6)   NOT NULL,
    data_atualizacao      DATETIME(6)   NOT NULL,
    criado_por            VARCHAR(100)  NULL,
    atualizado_por        VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_fornecedor_cotacao_cotacao (cotacao_id),
    KEY idx_fornecedor_cotacao_fornecedor (fornecedor_id),
    CONSTRAINT fk_fornecedor_cotacao_cotacao FOREIGN KEY (cotacao_id) REFERENCES cotacao_compra (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE fornecedor_cotacao_item (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    fornecedor_cotacao_id BIGINT        NOT NULL,
    produto_id            BIGINT        NOT NULL,
    quantidade            DECIMAL(18,6) NOT NULL,
    valor_unitario        DECIMAL(18,6) NOT NULL,
    taxa_desconto         DECIMAL(18,6) NULL,
    valor_desconto        DECIMAL(18,6) NOT NULL DEFAULT 0,
    valor_total           DECIMAL(18,6) NOT NULL,
    data_criacao          DATETIME(6)   NOT NULL,
    data_atualizacao      DATETIME(6)   NOT NULL,
    criado_por            VARCHAR(100)  NULL,
    atualizado_por        VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_fornecedor_cotacao_item_fc (fornecedor_cotacao_id),
    CONSTRAINT fk_fornecedor_cotacao_item_fc FOREIGN KEY (fornecedor_cotacao_id) REFERENCES fornecedor_cotacao (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE pedido_compra (
    id                        BIGINT        NOT NULL AUTO_INCREMENT,
    empresa_id                BIGINT        NOT NULL COMMENT 'Filial que está comprando — resolve qual ConfiguracaoComprasEmpresa (limite de aprovação) se aplica',
    tipo                      VARCHAR(10)   NOT NULL COMMENT 'NORMAL | PLANEJADO | ABERTO',
    fornecedor_id             BIGINT        NOT NULL,
    colaborador_id            BIGINT        NOT NULL COMMENT 'Comprador responsável',
    origem_tipo               VARCHAR(15)   NOT NULL COMMENT 'REQUISICAO | COTACAO | AVULSO',
    origem_id                 BIGINT        NULL COMMENT 'Id da requisicao_compra ou cotacao_compra de origem, se houver',
    data_pedido               DATE          NULL,
    data_prevista_entrega     DATE          NULL,
    data_previsao_pagamento   DATE          NULL,
    local_entrega             VARCHAR(100)  NULL,
    local_cobranca            VARCHAR(100)  NULL,
    contato                   VARCHAR(50)   NULL,
    tipo_frete                VARCHAR(3)    NULL COMMENT 'CIF | FOB',
    indicador_pagamento       VARCHAR(10)   NULL COMMENT 'A_VISTA | A_PRAZO | OUTROS — campo indPag da NF-e',
    quantidade_parcelas       INT           NULL,
    dia_primeiro_vencimento   INT           NULL,
    intervalo_entre_parcelas  INT           NULL,
    dia_fixo_parcela          INT           NULL,
    valor_subtotal            DECIMAL(18,6) NOT NULL DEFAULT 0,
    taxa_desconto             DECIMAL(18,6) NULL,
    valor_desconto            DECIMAL(18,6) NOT NULL DEFAULT 0,
    valor_frete               DECIMAL(18,6) NULL,
    valor_seguro              DECIMAL(18,6) NULL,
    valor_outras_despesas     DECIMAL(18,6) NULL,
    valor_total               DECIMAL(18,6) NOT NULL DEFAULT 0,
    aprovador_id              BIGINT        NULL COMMENT 'Setado automaticamente a partir do JWT de quem aprova — nunca vem do corpo da requisição',
    data_aprovacao            DATETIME(6)   NULL,
    situacao                  VARCHAR(25)   NOT NULL COMMENT 'RASCUNHO | AGUARDANDO_APROVACAO | ENVIADO | PARCIALMENTE_RECEBIDO | RECEBIDO_TOTAL | CANCELADO',
    observacao                VARCHAR(250)  NULL,
    data_criacao              DATETIME(6)   NOT NULL,
    data_atualizacao          DATETIME(6)   NOT NULL,
    criado_por                VARCHAR(100)  NULL,
    atualizado_por            VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_pedido_empresa (empresa_id),
    KEY idx_pedido_fornecedor (fornecedor_id),
    KEY idx_pedido_situacao (situacao),
    KEY idx_pedido_origem (origem_tipo, origem_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE pedido_compra_item (
    id                        BIGINT        NOT NULL AUTO_INCREMENT,
    pedido_id                 BIGINT        NOT NULL,
    produto_id                BIGINT        NOT NULL,
    unidade_compra_id         BIGINT        NULL COMMENT 'FK solta pro ProdutoUnidade do cadastro-base — quando a compra é numa unidade diferente da unidade de estoque (ex: compra em CX, estoca em UN)',
    fator_conversao_estoque   DECIMAL(18,6) NOT NULL DEFAULT 1 COMMENT 'quantidade × fator = quantidade real gerada no controle-estoque-service (que só entende a unidade de estoque)',
    quantidade                DECIMAL(18,6) NOT NULL COMMENT 'Na unidade de compra',
    quantidade_recebida       DECIMAL(18,6) NOT NULL DEFAULT 0 COMMENT 'Acumulador — soma dos recebimentos parciais, na unidade de compra',
    valor_unitario            DECIMAL(18,6) NOT NULL,
    taxa_desconto             DECIMAL(18,6) NULL,
    valor_desconto            DECIMAL(18,6) NOT NULL DEFAULT 0,
    valor_total               DECIMAL(18,6) NOT NULL,
    data_criacao              DATETIME(6)   NOT NULL,
    data_atualizacao          DATETIME(6)   NOT NULL,
    criado_por                VARCHAR(100)  NULL,
    atualizado_por            VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_pedido_item_pedido (pedido_id),
    CONSTRAINT fk_pedido_item_pedido FOREIGN KEY (pedido_id) REFERENCES pedido_compra (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE recebimento_compra (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    pedido_id             BIGINT        NOT NULL,
    usuario_id            BIGINT        NOT NULL COMMENT 'Quem lançou o recebimento — sempre um JWT de usuário humano',
    data_recebimento      DATE          NOT NULL,
    numero_nota_fiscal    VARCHAR(20)   NULL,
    chave_acesso_nfe      VARCHAR(44)   NULL,
    observacao            VARCHAR(250)  NULL,
    data_criacao          DATETIME(6)   NOT NULL,
    data_atualizacao      DATETIME(6)   NOT NULL,
    criado_por            VARCHAR(100)  NULL,
    atualizado_por        VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_recebimento_pedido (pedido_id),
    CONSTRAINT fk_recebimento_pedido FOREIGN KEY (pedido_id) REFERENCES pedido_compra (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE recebimento_compra_item (
    id                      BIGINT        NOT NULL AUTO_INCREMENT,
    recebimento_id          BIGINT        NOT NULL,
    pedido_compra_item_id   BIGINT        NOT NULL,
    quantidade_recebida     DECIMAL(18,6) NOT NULL COMMENT 'Na unidade de compra do item',
    deposito_id             BIGINT        NOT NULL COMMENT 'FK solta pro Deposito do controle-estoque-service',
    lote_id                 BIGINT        NULL COMMENT 'FK solta pro Lote do controle-estoque-service, quando o produto é lote-controlado',
    data_criacao            DATETIME(6)   NOT NULL,
    data_atualizacao        DATETIME(6)   NOT NULL,
    criado_por              VARCHAR(100)  NULL,
    atualizado_por          VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_recebimento_item_recebimento (recebimento_id),
    KEY idx_recebimento_item_pedido_item (pedido_compra_item_id),
    CONSTRAINT fk_recebimento_item_recebimento FOREIGN KEY (recebimento_id) REFERENCES recebimento_compra (id),
    CONSTRAINT fk_recebimento_item_pedido_item FOREIGN KEY (pedido_compra_item_id) REFERENCES pedido_compra_item (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE configuracao_compras_empresa (
    id                       BIGINT        NOT NULL AUTO_INCREMENT,
    empresa_id               BIGINT        NOT NULL,
    limite_aprovacao_pedido  DECIMAL(18,6) NULL COMMENT 'NULL = sem exigência de aprovação (semântica permissiva, mesmo padrão de ConfiguracaoEstoqueEmpresa)',
    data_criacao             DATETIME(6)   NOT NULL,
    data_atualizacao         DATETIME(6)   NOT NULL,
    criado_por               VARCHAR(100)  NULL,
    atualizado_por           VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_configuracao_compras_empresa (empresa_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
