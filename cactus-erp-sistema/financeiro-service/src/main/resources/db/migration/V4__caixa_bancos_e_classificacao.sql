-- Módulo Financeiro — Caixa e Bancos + fechamento do mapeamento campo a campo de
-- Contas a Pagar/Receber contra o fenix.sql (2026-09-06). Ver root CLAUDE.md, seção
-- "financeiro-service" / "Módulo Financeiro — Caixa/Bancos/Tesouraria/Conciliação/EDI".
--
-- Catálogos globais (dado público/de referência, seedado aqui).

CREATE TABLE banco (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    codigo           VARCHAR(3)   NOT NULL,
    nome             VARCHAR(100) NOT NULL,
    data_criacao     DATETIME(6)  NOT NULL,
    data_atualizacao DATETIME(6)  NOT NULL,
    criado_por       VARCHAR(100) NULL,
    atualizado_por   VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_banco_codigo (codigo)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE tipo_documento_financeiro (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    codigo           VARCHAR(10)  NOT NULL,
    descricao        VARCHAR(100) NOT NULL,
    ativo            TINYINT(1)   NOT NULL DEFAULT 1,
    data_criacao     DATETIME(6)  NOT NULL,
    data_atualizacao DATETIME(6)  NOT NULL,
    criado_por       VARCHAR(100) NULL,
    atualizado_por   VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tipo_documento_financeiro_codigo (codigo)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Catálogo por empresa (natureza financeira é dado fino de cada empresa, não global).

CREATE TABLE natureza_financeira (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    empresa_id       BIGINT       NOT NULL,
    codigo           VARCHAR(10)  NOT NULL,
    descricao        VARCHAR(100) NOT NULL,
    tipo             VARCHAR(10)  NOT NULL COMMENT 'RECEITA | DESPESA',
    ativo            TINYINT(1)   NOT NULL DEFAULT 1,
    data_criacao     DATETIME(6)  NOT NULL,
    data_atualizacao DATETIME(6)  NOT NULL,
    criado_por       VARCHAR(100) NULL,
    atualizado_por   VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_natureza_financeira_empresa (empresa_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Caixa e Bancos — núcleo do ledger.

CREATE TABLE conta_bancaria (
    id                 BIGINT        NOT NULL AUTO_INCREMENT,
    empresa_id         BIGINT        NOT NULL,
    banco_id           BIGINT        NULL,
    tipo               VARCHAR(15)   NOT NULL COMMENT 'CORRENTE | POUPANCA | CAIXA_INTERNO | APLICACAO',
    nome               VARCHAR(100)  NOT NULL,
    agencia            VARCHAR(20)   NULL,
    agencia_digito     VARCHAR(2)    NULL,
    numero             VARCHAR(20)   NULL,
    numero_digito      VARCHAR(2)    NULL,
    cedente_nome       VARCHAR(150)  NULL,
    cedente_documento  VARCHAR(20)   NULL,
    data_saldo_inicial DATE          NOT NULL,
    saldo_inicial      DECIMAL(18,6) NOT NULL,
    saldo_atual        DECIMAL(18,6) NOT NULL,
    ativo              TINYINT(1)    NOT NULL DEFAULT 1,
    data_criacao       DATETIME(6)   NOT NULL,
    data_atualizacao   DATETIME(6)   NOT NULL,
    criado_por         VARCHAR(100)  NULL,
    atualizado_por     VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_conta_bancaria_empresa (empresa_id),
    CONSTRAINT fk_conta_bancaria_banco FOREIGN KEY (banco_id) REFERENCES banco (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE movimento_caixa_banco (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    conta_bancaria_id BIGINT        NOT NULL,
    tipo_movimento    VARCHAR(25)   NOT NULL COMMENT 'ENTRADA | SAIDA | TRANSFERENCIA_ENTRADA | TRANSFERENCIA_SAIDA',
    origem_tipo       VARCHAR(25)   NOT NULL,
    origem_id         BIGINT        NULL,
    data_movimento    DATE          NOT NULL,
    historico         VARCHAR(250)  NULL,
    valor             DECIMAL(18,6) NOT NULL,
    saldo_anterior    DECIMAL(18,6) NOT NULL,
    saldo_posterior   DECIMAL(18,6) NOT NULL,
    usuario_id        BIGINT        NULL,
    data_criacao      DATETIME(6)   NOT NULL,
    data_atualizacao  DATETIME(6)   NOT NULL,
    criado_por        VARCHAR(100)  NULL,
    atualizado_por    VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_movimento_caixa_banco_conta (conta_bancaria_id, data_movimento DESC, id DESC),
    CONSTRAINT fk_movimento_caixa_banco_conta FOREIGN KEY (conta_bancaria_id) REFERENCES conta_bancaria (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Cheque (emitido/recebido) — controle de caixa e bancos, forma de pagamento/recebimento.

CREATE TABLE cheque_emitido (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    conta_bancaria_id BIGINT        NOT NULL,
    numero            VARCHAR(20)   NOT NULL,
    data_emissao      DATE          NOT NULL,
    bom_para          DATE          NULL,
    valor             DECIMAL(18,6) NOT NULL,
    nominal_a         VARCHAR(150)  NULL,
    situacao          VARCHAR(15)   NOT NULL DEFAULT 'EMITIDO' COMMENT 'EMITIDO | COMPENSADO | DEVOLVIDO | CANCELADO',
    data_situacao     DATE          NULL,
    motivo_situacao   VARCHAR(250)  NULL,
    data_criacao      DATETIME(6)   NOT NULL,
    data_atualizacao  DATETIME(6)   NOT NULL,
    criado_por        VARCHAR(100)  NULL,
    atualizado_por    VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_cheque_emitido_conta (conta_bancaria_id),
    CONSTRAINT fk_cheque_emitido_conta FOREIGN KEY (conta_bancaria_id) REFERENCES conta_bancaria (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE cheque_recebido (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    cliente_id        BIGINT        NULL,
    cpf_cnpj          VARCHAR(20)   NULL,
    nome_emitente     VARCHAR(150)  NOT NULL,
    banco_codigo      VARCHAR(3)    NULL,
    agencia           VARCHAR(20)   NULL,
    conta             VARCHAR(20)   NULL,
    numero            VARCHAR(20)   NULL,
    data_emissao      DATE          NOT NULL,
    bom_para          DATE          NULL,
    valor             DECIMAL(18,6) NOT NULL,
    situacao          VARCHAR(15)   NOT NULL DEFAULT 'EM_CARTEIRA' COMMENT 'EM_CARTEIRA | DEPOSITADO | COMPENSADO | DEVOLVIDO | DESCONTADO',
    data_situacao     DATE          NULL,
    motivo_situacao   VARCHAR(250)  NULL,
    conta_bancaria_id BIGINT        NULL,
    data_criacao      DATETIME(6)   NOT NULL,
    data_atualizacao  DATETIME(6)   NOT NULL,
    criado_por        VARCHAR(100)  NULL,
    atualizado_por    VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_cheque_recebido_cliente (cliente_id),
    CONSTRAINT fk_cheque_recebido_conta FOREIGN KEY (conta_bancaria_id) REFERENCES conta_bancaria (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Fechamento do mapeamento campo a campo de ContaPagar/ContaReceber contra
-- FIN_LANCAMENTO_PAGAR/FIN_LANCAMENTO_RECEBER/FIN_PARCELA_PAGAR/FIN_PARCELA_RECEBER do
-- fenix.sql — ver Javadoc de ContaPagar/ContaReceber pro detalhamento de cada campo.
-- Nenhum NOT NULL aqui pra natureza_financeira_id/tipo_documento_id (schema já rodou com
-- dado de teste em sessão anterior) — obrigatoriedade fica na validação da API pra
-- título NOVO (ContaPagarRequest/ContaReceberRequest), títulos já existentes ficam
-- "não classificados" até alguém rodar PUT .../classificacao.

ALTER TABLE conta_pagar
    ADD COLUMN natureza_financeira_id     BIGINT        NULL AFTER descricao,
    ADD COLUMN tipo_documento_id          BIGINT        NULL AFTER natureza_financeira_id,
    ADD COLUMN conta_bancaria_id          BIGINT        NULL AFTER tipo_documento_id,
    ADD COLUMN desconto_ate               DATE          NULL AFTER data_vencimento,
    ADD COLUMN percentual_desconto        DECIMAL(8,4)  NULL AFTER desconto_ate,
    ADD COLUMN percentual_multa_atraso    DECIMAL(8,4)  NULL AFTER percentual_desconto,
    ADD COLUMN percentual_juros_dia_atraso DECIMAL(8,4) NULL AFTER percentual_multa_atraso,
    ADD COLUMN lote_id                    BIGINT        NULL AFTER percentual_juros_dia_atraso,
    ADD COLUMN valor_desconto             DECIMAL(18,6) NOT NULL DEFAULT 0 AFTER valor_pago,
    MODIFY COLUMN situacao                VARCHAR(12)   NOT NULL DEFAULT 'ABERTA',
    CHANGE COLUMN motivo_cancelamento motivo_encerramento VARCHAR(250) NULL,
    ADD KEY idx_conta_pagar_natureza (natureza_financeira_id),
    ADD KEY idx_conta_pagar_lote (lote_id);

ALTER TABLE conta_receber
    ADD COLUMN natureza_financeira_id     BIGINT        NULL AFTER descricao,
    ADD COLUMN tipo_documento_id          BIGINT        NULL AFTER natureza_financeira_id,
    ADD COLUMN conta_bancaria_id          BIGINT        NULL AFTER tipo_documento_id,
    ADD COLUMN desconto_ate               DATE          NULL AFTER data_vencimento,
    ADD COLUMN percentual_desconto        DECIMAL(8,4)  NULL AFTER desconto_ate,
    ADD COLUMN percentual_multa_atraso    DECIMAL(8,4)  NULL AFTER percentual_desconto,
    ADD COLUMN percentual_juros_dia_atraso DECIMAL(8,4) NULL AFTER percentual_multa_atraso,
    ADD COLUMN lote_id                    BIGINT        NULL AFTER percentual_juros_dia_atraso,
    ADD COLUMN valor_desconto             DECIMAL(18,6) NOT NULL DEFAULT 0 AFTER valor_pago,
    MODIFY COLUMN situacao                VARCHAR(12)   NOT NULL DEFAULT 'ABERTA',
    CHANGE COLUMN motivo_cancelamento motivo_encerramento VARCHAR(250) NULL,
    ADD KEY idx_conta_receber_natureza (natureza_financeira_id),
    ADD KEY idx_conta_receber_lote (lote_id);

-- OrigemContaPagar/OrigemContaReceber ganharam RENEGOCIACAO (12 chars) — cabe nos
-- VARCHAR(15) já existentes de origem_tipo, sem precisar de MODIFY.

ALTER TABLE baixa_conta_pagar
    ADD COLUMN conta_bancaria_id BIGINT        NULL AFTER forma_pagamento_id,
    ADD COLUMN cheque_emitido_id BIGINT        NULL AFTER conta_bancaria_id,
    ADD COLUMN valor_desconto    DECIMAL(18,6) NOT NULL DEFAULT 0 AFTER cheque_emitido_id;

ALTER TABLE baixa_conta_receber
    ADD COLUMN conta_bancaria_id  BIGINT        NULL AFTER forma_pagamento_id,
    ADD COLUMN cheque_recebido_id BIGINT        NULL AFTER conta_bancaria_id,
    ADD COLUMN valor_desconto     DECIMAL(18,6) NOT NULL DEFAULT 0 AFTER cheque_recebido_id;

-- Seed — bancos brasileiros mais comuns (código FEBRABAN, dado público oficial).

INSERT INTO banco (codigo, nome, data_criacao, data_atualizacao, criado_por, atualizado_por) VALUES
('001', 'Banco do Brasil', NOW(6), NOW(6), 'seed', 'seed'),
('033', 'Santander', NOW(6), NOW(6), 'seed', 'seed'),
('104', 'Caixa Econômica Federal', NOW(6), NOW(6), 'seed', 'seed'),
('237', 'Bradesco', NOW(6), NOW(6), 'seed', 'seed'),
('341', 'Itaú Unibanco', NOW(6), NOW(6), 'seed', 'seed'),
('077', 'Banco Inter', NOW(6), NOW(6), 'seed', 'seed'),
('260', 'Nu Pagamentos (Nubank)', NOW(6), NOW(6), 'seed', 'seed'),
('336', 'Banco C6', NOW(6), NOW(6), 'seed', 'seed'),
('212', 'Banco Original', NOW(6), NOW(6), 'seed', 'seed'),
('756', 'Sicoob', NOW(6), NOW(6), 'seed', 'seed'),
('748', 'Sicredi', NOW(6), NOW(6), 'seed', 'seed'),
('422', 'Banco Safra', NOW(6), NOW(6), 'seed', 'seed'),
('070', 'Banco de Brasília (BRB)', NOW(6), NOW(6), 'seed', 'seed'),
('136', 'Unicred', NOW(6), NOW(6), 'seed', 'seed'),
('323', 'Mercado Pago', NOW(6), NOW(6), 'seed', 'seed'),
('290', 'PagSeguro (PagBank)', NOW(6), NOW(6), 'seed', 'seed'),
('380', 'PicPay', NOW(6), NOW(6), 'seed', 'seed');

-- Seed — tipos de documento financeiro, espelhando o comentário de FIN_DOCUMENTO_ORIGEM
-- do fenix.sql ("NF, CHQ, NFe, DP, NP, CTe, CT, CF, CFe").

INSERT INTO tipo_documento_financeiro (codigo, descricao, ativo, data_criacao, data_atualizacao, criado_por, atualizado_por) VALUES
('NF', 'Nota Fiscal', 1, NOW(6), NOW(6), 'seed', 'seed'),
('NFE', 'Nota Fiscal Eletrônica', 1, NOW(6), NOW(6), 'seed', 'seed'),
('NFCE', 'Nota Fiscal de Consumidor Eletrônica', 1, NOW(6), NOW(6), 'seed', 'seed'),
('NFSE', 'Nota Fiscal de Serviço Eletrônica', 1, NOW(6), NOW(6), 'seed', 'seed'),
('CTE', 'Conhecimento de Transporte Eletrônico', 1, NOW(6), NOW(6), 'seed', 'seed'),
('DP', 'Duplicata', 1, NOW(6), NOW(6), 'seed', 'seed'),
('NP', 'Nota Promissória', 1, NOW(6), NOW(6), 'seed', 'seed'),
('BOL', 'Boleto Bancário', 1, NOW(6), NOW(6), 'seed', 'seed'),
('CHQ', 'Cheque', 1, NOW(6), NOW(6), 'seed', 'seed'),
('REC', 'Recibo', 1, NOW(6), NOW(6), 'seed', 'seed'),
('CTR', 'Contrato', 1, NOW(6), NOW(6), 'seed', 'seed'),
('OUTRO', 'Outro', 1, NOW(6), NOW(6), 'seed', 'seed');
