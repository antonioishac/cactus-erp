-- Conciliação Bancária (2026-09-07) — import de extrato via OFX + matching contra
-- MovimentoCaixaBanco. Ver Javadoc de ConciliacaoBancariaService/OfxParser e root
-- CLAUDE.md.

ALTER TABLE movimento_caixa_banco
    ADD COLUMN conciliado TINYINT(1) NOT NULL DEFAULT 0 AFTER usuario_id;

CREATE TABLE extrato_bancario_importacao (
    id                    BIGINT       NOT NULL AUTO_INCREMENT,
    conta_bancaria_id     BIGINT       NOT NULL,
    nome_arquivo          VARCHAR(250) NULL,
    quantidade_itens      INT          NOT NULL DEFAULT 0,
    quantidade_duplicados INT          NOT NULL DEFAULT 0,
    usuario_id            BIGINT       NULL,
    data_criacao          DATETIME(6)  NOT NULL,
    data_atualizacao      DATETIME(6)  NOT NULL,
    criado_por            VARCHAR(100) NULL,
    atualizado_por        VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_extrato_importacao_conta (conta_bancaria_id),
    CONSTRAINT fk_extrato_importacao_conta FOREIGN KEY (conta_bancaria_id) REFERENCES conta_bancaria (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE extrato_bancario_item (
    id                     BIGINT        NOT NULL AUTO_INCREMENT,
    importacao_id          BIGINT        NOT NULL,
    conta_bancaria_id      BIGINT        NOT NULL,
    data_movimento         DATE          NOT NULL,
    historico              VARCHAR(250)  NULL,
    tipo_ofx               VARCHAR(20)   NULL,
    valor                  DECIMAL(18,6) NOT NULL COMMENT 'Sinal preservado do OFX: negativo=saida, positivo=entrada',
    fit_id                 VARCHAR(100)  NULL,
    conciliado             TINYINT(1)    NOT NULL DEFAULT 0,
    movimento_caixa_banco_id BIGINT      NULL,
    data_criacao           DATETIME(6)   NOT NULL,
    data_atualizacao       DATETIME(6)   NOT NULL,
    criado_por             VARCHAR(100)  NULL,
    atualizado_por         VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_extrato_item_importacao (importacao_id),
    KEY idx_extrato_item_conta_conciliado (conta_bancaria_id, conciliado),
    UNIQUE KEY uk_extrato_item_conta_fit (conta_bancaria_id, fit_id),
    CONSTRAINT fk_extrato_item_importacao FOREIGN KEY (importacao_id) REFERENCES extrato_bancario_importacao (id),
    CONSTRAINT fk_extrato_item_conta FOREIGN KEY (conta_bancaria_id) REFERENCES conta_bancaria (id),
    CONSTRAINT fk_extrato_item_movimento FOREIGN KEY (movimento_caixa_banco_id) REFERENCES movimento_caixa_banco (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
