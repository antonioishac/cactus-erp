-- EDI Bancário (2026-09-07) — geração de remessa CNAB 240 (registro de título em
-- carteira de cobrança) e leitura de retorno CNAB 240 (baixa automática por nosso
-- número). Só CNAB 240 (decisão confirmada com o usuário). Ver Javadoc de
-- Cnab240RemessaBuilder/Cnab240RetornoParser e root CLAUDE.md.

ALTER TABLE conta_bancaria
    ADD COLUMN codigo_convenio_cobranca VARCHAR(20) NULL AFTER cedente_documento,
    ADD COLUMN codigo_cedente_cobranca  VARCHAR(20) NULL AFTER codigo_convenio_cobranca,
    ADD COLUMN carteira_cobranca        VARCHAR(5)  NULL AFTER codigo_cedente_cobranca,
    ADD COLUMN proximo_nosso_numero     BIGINT      NULL AFTER carteira_cobranca;

CREATE TABLE remessa_bancaria (
    id                         BIGINT        NOT NULL AUTO_INCREMENT,
    conta_bancaria_id         BIGINT        NOT NULL,
    empresa_id                BIGINT        NOT NULL,
    data_geracao              DATE          NOT NULL,
    numero_sequencial_arquivo BIGINT        NOT NULL,
    quantidade_titulos        INT           NOT NULL DEFAULT 0,
    valor_total               DECIMAL(18,6) NOT NULL DEFAULT 0,
    conteudo_arquivo          MEDIUMTEXT    NOT NULL,
    data_criacao              DATETIME(6)   NOT NULL,
    data_atualizacao          DATETIME(6)   NOT NULL,
    criado_por                VARCHAR(100)  NULL,
    atualizado_por            VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_remessa_bancaria_conta (conta_bancaria_id),
    CONSTRAINT fk_remessa_bancaria_conta FOREIGN KEY (conta_bancaria_id) REFERENCES conta_bancaria (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE remessa_bancaria_item (
    id                       BIGINT        NOT NULL AUTO_INCREMENT,
    remessa_id               BIGINT        NOT NULL,
    conta_receber_id         BIGINT        NOT NULL,
    nosso_numero             VARCHAR(20)   NOT NULL,
    numero_documento         VARCHAR(15)   NULL,
    data_vencimento          DATE          NOT NULL,
    valor_titulo             DECIMAL(18,6) NOT NULL,
    sacado_tipo_inscricao    VARCHAR(4)    NULL,
    sacado_numero_inscricao  VARCHAR(20)   NULL,
    sacado_nome              VARCHAR(100)  NULL,
    sacado_logradouro        VARCHAR(100)  NULL,
    sacado_bairro            VARCHAR(50)   NULL,
    sacado_cep               VARCHAR(10)   NULL,
    sacado_cidade            VARCHAR(50)   NULL,
    sacado_uf                VARCHAR(2)    NULL,
    data_criacao             DATETIME(6)   NOT NULL,
    data_atualizacao         DATETIME(6)   NOT NULL,
    criado_por               VARCHAR(100)  NULL,
    atualizado_por           VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_remessa_bancaria_item_remessa (remessa_id),
    KEY idx_remessa_bancaria_item_nosso_numero (nosso_numero),
    CONSTRAINT fk_remessa_bancaria_item_remessa FOREIGN KEY (remessa_id) REFERENCES remessa_bancaria (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE retorno_bancario (
    id                    BIGINT       NOT NULL AUTO_INCREMENT,
    conta_bancaria_id     BIGINT       NOT NULL,
    nome_arquivo          VARCHAR(250) NULL,
    quantidade_registros  INT          NOT NULL DEFAULT 0,
    data_criacao          DATETIME(6)  NOT NULL,
    data_atualizacao      DATETIME(6)  NOT NULL,
    criado_por            VARCHAR(100) NULL,
    atualizado_por        VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_retorno_bancario_conta (conta_bancaria_id),
    CONSTRAINT fk_retorno_bancario_conta FOREIGN KEY (conta_bancaria_id) REFERENCES conta_bancaria (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE retorno_bancario_item (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    retorno_id        BIGINT        NOT NULL,
    nosso_numero      VARCHAR(20)   NULL,
    numero_documento  VARCHAR(15)   NULL,
    codigo_movimento  VARCHAR(2)    NULL,
    data_ocorrencia   DATE          NULL,
    valor_titulo      DECIMAL(18,6) NULL,
    valor_pago        DECIMAL(18,6) NULL,
    conta_receber_id  BIGINT        NULL,
    processado        TINYINT(1)    NOT NULL DEFAULT 0,
    observacao        VARCHAR(250)  NULL,
    data_criacao      DATETIME(6)   NOT NULL,
    data_atualizacao  DATETIME(6)   NOT NULL,
    criado_por        VARCHAR(100)  NULL,
    atualizado_por    VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_retorno_bancario_item_retorno (retorno_id),
    CONSTRAINT fk_retorno_bancario_item_retorno FOREIGN KEY (retorno_id) REFERENCES retorno_bancario (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
