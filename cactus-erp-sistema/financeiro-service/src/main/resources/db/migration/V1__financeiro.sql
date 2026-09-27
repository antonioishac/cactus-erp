-- Financeiro (Contas a Receber/Contas a Pagar) — fecha o gap registrado desde a Fase 7
-- (VendaCondicoesPagamento/Parcela modelavam a condição de pagamento da venda, mas não
-- geravam nenhum lançamento de contas a receber de verdade). Ver Javadoc de
-- ContaReceber/ContaPagar e CLAUDE.md.
--
-- empresa_id/cliente_id/fornecedor_id/origem_id são referências SOLTAS pro
-- cadastro-base-service/vendas-service/gestao-compras-service/loja-virtual-service. Sem
-- FK cross-schema, sem validação de existência — mesmo padrão do monorepo.

CREATE TABLE conta_receber (
    id                      BIGINT        NOT NULL AUTO_INCREMENT,
    empresa_id              BIGINT        NOT NULL,
    cliente_id              BIGINT        NOT NULL,
    origem_tipo             VARCHAR(15)   NOT NULL COMMENT 'VENDA | PEDIDO_LOJA | MANUAL',
    origem_id               BIGINT        NULL,
    numero_documento        VARCHAR(30)   NULL,
    descricao               VARCHAR(250)  NULL,
    data_emissao            DATE          NOT NULL,
    data_vencimento         DATE          NOT NULL,
    valor_original          DECIMAL(18,6) NOT NULL,
    valor_pago              DECIMAL(18,6) NOT NULL DEFAULT 0,
    valor_saldo             DECIMAL(18,6) NOT NULL,
    situacao                VARCHAR(10)   NOT NULL DEFAULT 'ABERTA' COMMENT 'ABERTA | PARCIAL | PAGA | CANCELADA',
    motivo_cancelamento     VARCHAR(250)  NULL,
    data_criacao            DATETIME(6)   NOT NULL,
    data_atualizacao        DATETIME(6)   NOT NULL,
    criado_por              VARCHAR(100)  NULL,
    atualizado_por          VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_conta_receber_empresa (empresa_id),
    KEY idx_conta_receber_cliente (cliente_id),
    KEY idx_conta_receber_situacao (situacao),
    KEY idx_conta_receber_origem (origem_tipo, origem_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE baixa_conta_receber (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    conta_receber_id    BIGINT        NOT NULL,
    data_baixa          DATE          NOT NULL,
    valor_baixa         DECIMAL(18,6) NOT NULL,
    forma_pagamento_id  BIGINT        NULL,
    observacao          VARCHAR(250)  NULL,
    usuario_id          BIGINT        NULL,
    data_criacao        DATETIME(6)   NOT NULL,
    data_atualizacao    DATETIME(6)   NOT NULL,
    criado_por          VARCHAR(100)  NULL,
    atualizado_por      VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_baixa_conta_receber_conta (conta_receber_id),
    CONSTRAINT fk_baixa_conta_receber_conta FOREIGN KEY (conta_receber_id) REFERENCES conta_receber (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE conta_pagar (
    id                      BIGINT        NOT NULL AUTO_INCREMENT,
    empresa_id              BIGINT        NOT NULL,
    fornecedor_id           BIGINT        NOT NULL,
    origem_tipo             VARCHAR(15)   NOT NULL COMMENT 'COMPRA | MANUAL',
    origem_id               BIGINT        NULL,
    numero_documento        VARCHAR(30)   NULL,
    descricao               VARCHAR(250)  NULL,
    data_emissao            DATE          NOT NULL,
    data_vencimento         DATE          NOT NULL,
    valor_original          DECIMAL(18,6) NOT NULL,
    valor_pago              DECIMAL(18,6) NOT NULL DEFAULT 0,
    valor_saldo             DECIMAL(18,6) NOT NULL,
    situacao                VARCHAR(10)   NOT NULL DEFAULT 'ABERTA' COMMENT 'ABERTA | PARCIAL | PAGA | CANCELADA',
    motivo_cancelamento     VARCHAR(250)  NULL,
    data_criacao            DATETIME(6)   NOT NULL,
    data_atualizacao        DATETIME(6)   NOT NULL,
    criado_por              VARCHAR(100)  NULL,
    atualizado_por          VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_conta_pagar_empresa (empresa_id),
    KEY idx_conta_pagar_fornecedor (fornecedor_id),
    KEY idx_conta_pagar_situacao (situacao),
    KEY idx_conta_pagar_origem (origem_tipo, origem_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE baixa_conta_pagar (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    conta_pagar_id      BIGINT        NOT NULL,
    data_baixa          DATE          NOT NULL,
    valor_baixa         DECIMAL(18,6) NOT NULL,
    forma_pagamento_id  BIGINT        NULL,
    observacao          VARCHAR(250)  NULL,
    usuario_id          BIGINT        NULL,
    data_criacao        DATETIME(6)   NOT NULL,
    data_atualizacao    DATETIME(6)   NOT NULL,
    criado_por          VARCHAR(100)  NULL,
    atualizado_por      VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_baixa_conta_pagar_conta (conta_pagar_id),
    CONSTRAINT fk_baixa_conta_pagar_conta FOREIGN KEY (conta_pagar_id) REFERENCES conta_pagar (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
