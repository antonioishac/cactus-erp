-- Sincronização ERP ↔ cactus-pdv (Fase 6) — ledger de auditoria de push. Pull (ERP→PDV)
-- não persiste nada aqui (ver FASE6_SINCRONIZACAO_PDV.md seção 4.3, "não guarda cópia
-- duradoura do cadastro — não é cache").
--
-- loja_id/dispositivo_id nunca são chave primária (id local do PDV não é reaproveitado
-- como PK no ERP, já registrado no CLAUDE.md) — são só atributos de rastreio.

CREATE TABLE lote_sincronizacao (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    loja_id           BIGINT       NOT NULL,
    dispositivo_id    VARCHAR(30)  NOT NULL,
    tipo_registro     VARCHAR(30)  NOT NULL,
    total_registros   INT          NOT NULL DEFAULT 0,
    total_aceitos     INT          NOT NULL DEFAULT 0,
    total_rejeitados  INT          NOT NULL DEFAULT 0,
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_lote_loja (loja_id),
    KEY idx_lote_dispositivo (dispositivo_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE registro_sincronizacao (
    id                       BIGINT       NOT NULL AUTO_INCREMENT,
    lote_id                  BIGINT       NOT NULL,
    loja_id                  BIGINT       NOT NULL,
    dispositivo_id           VARCHAR(30)  NOT NULL,
    tipo_registro            VARCHAR(30)  NOT NULL,
    id_origem                VARCHAR(60)  NOT NULL COMMENT 'Id local do registro no PDV (não é PK aqui)',
    chave_idempotencia       VARCHAR(150) NOT NULL COMMENT 'lojaId:dispositivoId:tipoRegistro:idOrigem',
    payload                  LONGTEXT     NOT NULL COMMENT 'JSON original recebido do PDV — necessário pro reprocessamento manual',
    status                   VARCHAR(15)  NOT NULL COMMENT 'RECEBIDO | PUBLICADO | PROCESSADO | ERRO',
    tentativas                INT          NOT NULL DEFAULT 0,
    motivo_erro              VARCHAR(500) NULL,
    usuario_reprocessou_id   BIGINT       NULL,
    data_reprocessamento     DATETIME(6)  NULL,
    data_criacao             DATETIME(6)  NOT NULL,
    data_atualizacao         DATETIME(6)  NOT NULL,
    criado_por               VARCHAR(100) NULL,
    atualizado_por           VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_registro_sincronizacao_idempotencia (chave_idempotencia),
    KEY idx_registro_lote (lote_id),
    KEY idx_registro_status (status),
    KEY idx_registro_loja (loja_id),
    CONSTRAINT fk_registro_lote FOREIGN KEY (lote_id) REFERENCES lote_sincronizacao (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
