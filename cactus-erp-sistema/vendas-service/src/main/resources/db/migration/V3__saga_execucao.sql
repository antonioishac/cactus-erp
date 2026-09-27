-- Trilha de auditoria da saga orquestrada de VendaService#confirmar (ver Javadoc de
-- common.saga.SagaOrchestrator/entity.SagaExecucao) — cabeçalho + ledger append-only de
-- passos. referencia_tipo/referencia_id são soltos (sem FK), mesmo critério de toda
-- referência cross-agregado deste monorepo.

CREATE TABLE saga_execucao (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    saga_nome           VARCHAR(60)  NOT NULL,
    referencia_tipo     VARCHAR(30)  NOT NULL,
    referencia_id       BIGINT       NOT NULL,
    situacao            VARCHAR(20)  NOT NULL DEFAULT 'EM_EXECUCAO' COMMENT 'EM_EXECUCAO | CONCLUIDA | COMPENSADA | FALHA_COMPENSACAO',
    passo_falhou_nome   VARCHAR(80)  NULL,
    motivo_erro         VARCHAR(500) NULL,
    data_inicio         DATETIME(6)  NOT NULL,
    data_fim            DATETIME(6)  NULL,
    data_criacao        DATETIME(6)  NOT NULL,
    data_atualizacao    DATETIME(6)  NOT NULL,
    criado_por          VARCHAR(100) NULL,
    atualizado_por      VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_saga_execucao_referencia (referencia_tipo, referencia_id),
    KEY idx_saga_execucao_situacao (situacao)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE saga_execucao_passo (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    saga_execucao_id    BIGINT       NOT NULL,
    nome                VARCHAR(80)  NOT NULL,
    situacao            VARCHAR(20)  NOT NULL COMMENT 'EXECUTADO | COMPENSADO | FALHOU_AO_COMPENSAR',
    motivo_erro         VARCHAR(500) NULL,
    data_evento         DATETIME(6)  NOT NULL,
    data_criacao        DATETIME(6)  NOT NULL,
    data_atualizacao    DATETIME(6)  NOT NULL,
    criado_por          VARCHAR(100) NULL,
    atualizado_por      VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_saga_execucao_passo_saga (saga_execucao_id),
    CONSTRAINT fk_saga_execucao_passo_saga FOREIGN KEY (saga_execucao_id) REFERENCES saga_execucao (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
