-- Ledger append-only de números fiscais reservados (reservar-numero-nfe/cte/nfce/nfse)
-- que nunca chegaram a virar documento real, porque um passo POSTERIOR da saga que os
-- consome falhou (ver common.saga.SagaOrchestrator em fiscal-service). Nunca decrementa
-- o contador correspondente (risco de colisão com reserva concorrente) — só torna o gap
-- consultável pra regularização manual futura via evento de inutilização junto à SEFAZ.

CREATE TABLE numero_fiscal_pulado (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    empresa_id        BIGINT       NOT NULL,
    tipo_documento    VARCHAR(10)  NOT NULL COMMENT 'NFE | NFCE | NFSE | CTE',
    serie             VARCHAR(3)   NULL,
    numero            BIGINT       NOT NULL,
    motivo            VARCHAR(500) NULL,
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_numero_fiscal_pulado_empresa (empresa_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
