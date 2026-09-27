-- Tesouraria (2026-09-07) — transferência entre contas bancárias próprias e aplicação/
-- resgate financeiro. Ver Javadoc de TransferenciaBancaria/AplicacaoFinanceira e root
-- CLAUDE.md, seção "financeiro-service — Caixa/Bancos e fechamento do mapeamento...".

CREATE TABLE transferencia_bancaria (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    conta_origem_id     BIGINT        NOT NULL,
    conta_destino_id    BIGINT        NOT NULL,
    data_transferencia  DATE          NOT NULL,
    valor               DECIMAL(18,6) NOT NULL,
    historico           VARCHAR(250)  NULL,
    usuario_id          BIGINT        NULL,
    data_criacao        DATETIME(6)   NOT NULL,
    data_atualizacao    DATETIME(6)   NOT NULL,
    criado_por          VARCHAR(100)  NULL,
    atualizado_por      VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_transferencia_bancaria_origem (conta_origem_id),
    KEY idx_transferencia_bancaria_destino (conta_destino_id),
    CONSTRAINT fk_transferencia_bancaria_origem FOREIGN KEY (conta_origem_id) REFERENCES conta_bancaria (id),
    CONSTRAINT fk_transferencia_bancaria_destino FOREIGN KEY (conta_destino_id) REFERENCES conta_bancaria (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE aplicacao_financeira (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    conta_bancaria_id BIGINT        NOT NULL,
    tipo_aplicacao    VARCHAR(15)   NOT NULL COMMENT 'CDB | POUPANCA | FUNDO | TESOURO_DIRETO | OUTRO',
    descricao         VARCHAR(150)  NULL,
    data_aplicacao    DATE          NOT NULL,
    valor_aplicado    DECIMAL(18,6) NOT NULL,
    data_resgate      DATE          NULL,
    valor_resgatado   DECIMAL(18,6) NULL,
    rendimento        DECIMAL(18,6) NULL,
    situacao          VARCHAR(10)   NOT NULL DEFAULT 'ATIVA' COMMENT 'ATIVA | RESGATADA',
    data_criacao      DATETIME(6)   NOT NULL,
    data_atualizacao  DATETIME(6)   NOT NULL,
    criado_por        VARCHAR(100)  NULL,
    atualizado_por    VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_aplicacao_financeira_conta (conta_bancaria_id),
    CONSTRAINT fk_aplicacao_financeira_conta FOREIGN KEY (conta_bancaria_id) REFERENCES conta_bancaria (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
