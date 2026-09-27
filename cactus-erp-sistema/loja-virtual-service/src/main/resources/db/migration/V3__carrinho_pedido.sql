-- Fase 13, sub-fase 2 — Carrinho (público, sem OAuth2, ver ResourceServerConfig) →
-- Checkout (guest, captura ClienteLoja inline) → Pedido (atrás do RBAC de sempre). Ver
-- CLAUDE.md, "Fase 13, sub-fase 2 — decisão de segurança".

CREATE TABLE cliente_loja (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    loja_id           BIGINT       NOT NULL,
    nome              VARCHAR(120) NOT NULL,
    email             VARCHAR(120) NOT NULL,
    telefone          VARCHAR(20)  NULL,
    cpf               VARCHAR(11)  NULL,
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_cliente_loja_loja_email (loja_id, email)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE carrinho (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    loja_id           BIGINT       NOT NULL,
    token             VARCHAR(36)  NOT NULL COMMENT 'UUID — credencial de posse do carrinho, ver ResourceServerConfig (rota pública)',
    situacao          VARCHAR(15)  NOT NULL DEFAULT 'ABERTO' COMMENT 'ABERTO | FINALIZADO | ABANDONADO',
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_carrinho_token (token),
    KEY idx_carrinho_loja (loja_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE item_carrinho (
    id                          BIGINT        NOT NULL AUTO_INCREMENT,
    carrinho_id                 BIGINT        NOT NULL,
    variante_produto_loja_id    BIGINT        NOT NULL,
    quantidade                  DECIMAL(18,6) NOT NULL,
    preco_unitario               DECIMAL(18,6) NOT NULL,
    data_criacao                DATETIME(6)   NOT NULL,
    data_atualizacao             DATETIME(6)   NOT NULL,
    criado_por                  VARCHAR(100)  NULL,
    atualizado_por               VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_item_carrinho_carrinho (carrinho_id),
    KEY idx_item_carrinho_variante (variante_produto_loja_id),
    CONSTRAINT fk_item_carrinho_carrinho FOREIGN KEY (carrinho_id) REFERENCES carrinho (id),
    CONSTRAINT fk_item_carrinho_variante FOREIGN KEY (variante_produto_loja_id) REFERENCES variante_produto_loja (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE pedido (
    id                     BIGINT        NOT NULL AUTO_INCREMENT,
    loja_id                BIGINT        NOT NULL,
    cliente_loja_id        BIGINT        NOT NULL,
    carrinho_id            BIGINT        NULL,
    logradouro_entrega     VARCHAR(150)  NULL,
    numero_entrega         VARCHAR(20)   NULL,
    complemento_entrega    VARCHAR(60)   NULL,
    bairro_entrega         VARCHAR(80)   NULL,
    cep_entrega            VARCHAR(8)    NULL,
    municipio_entrega      VARCHAR(80)   NULL,
    uf_entrega             CHAR(2)       NULL,
    forma_pagamento        VARCHAR(15)   NOT NULL COMMENT 'PIX | CARTAO_CREDITO | BOLETO — sem gateway real integrado nesta fatia',
    valor_subtotal         DECIMAL(18,6) NOT NULL DEFAULT 0,
    valor_frete            DECIMAL(18,6) NULL,
    valor_total            DECIMAL(18,6) NOT NULL DEFAULT 0,
    situacao               VARCHAR(20)   NOT NULL DEFAULT 'AGUARDANDO_PAGAMENTO' COMMENT 'AGUARDANDO_PAGAMENTO | PAGO | EM_SEPARACAO | ENVIADO | ENTREGUE | CANCELADO',
    data_pagamento         DATETIME(6)   NULL,
    motivo_cancelamento    VARCHAR(250)  NULL,
    data_criacao           DATETIME(6)   NOT NULL,
    data_atualizacao       DATETIME(6)   NOT NULL,
    criado_por             VARCHAR(100)  NULL,
    atualizado_por         VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_pedido_loja (loja_id),
    KEY idx_pedido_cliente (cliente_loja_id),
    KEY idx_pedido_situacao (situacao),
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_loja_id) REFERENCES cliente_loja (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE item_pedido (
    id                          BIGINT        NOT NULL AUTO_INCREMENT,
    pedido_id                   BIGINT        NOT NULL,
    variante_produto_loja_id    BIGINT        NOT NULL COMMENT 'Solto, não FK — histórico precisa sobreviver mesmo se a variante for excluída depois',
    sku_snapshot                VARCHAR(50)   NOT NULL,
    nome_snapshot               VARCHAR(150)  NOT NULL,
    quantidade                  DECIMAL(18,6) NOT NULL,
    preco_unitario              DECIMAL(18,6) NOT NULL,
    valor_total                 DECIMAL(18,6) NOT NULL,
    data_criacao                DATETIME(6)   NOT NULL,
    data_atualizacao            DATETIME(6)   NOT NULL,
    criado_por                  VARCHAR(100)  NULL,
    atualizado_por              VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_item_pedido_pedido (pedido_id),
    CONSTRAINT fk_item_pedido_pedido FOREIGN KEY (pedido_id) REFERENCES pedido (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
