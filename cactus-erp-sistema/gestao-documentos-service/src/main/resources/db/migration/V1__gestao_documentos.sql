-- Gestão Eletrônica de Documentos (GED) do Cactus ERP: documento com versionamento,
-- armazenamento abstraído por interface (DocumentoStorageService — filesystem hoje,
-- S3/Azure/MinIO plugável depois). Validado campo a campo contra o fenix.sql
-- (GED_TIPO_DOCUMENTO/GED_DOCUMENTO_CABECALHO/GED_DOCUMENTO_DETALHE/GED_VERSAO_DOCUMENTO)
-- — o BANCO_PDV.md não tem nada de GED (front-of-store puro).
--
-- entidade_id é referência SOLTA pra qualquer entidade de qualquer outro serviço
-- (entidade_tipo diz qual, ex: 'PRODUTO'/'FORNECEDOR') — mesmo padrão de referência
-- cross-serviço já usado no monorepo inteiro. colaborador_id é solto pro cadastro-base.

CREATE TABLE tipo_documento (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    nome                VARCHAR(100)  NOT NULL,
    tamanho_maximo_mb   DECIMAL(10,2) NOT NULL,
    situacao            CHAR(1)       NOT NULL DEFAULT 'A',
    data_criacao        DATETIME(6)   NOT NULL,
    data_atualizacao    DATETIME(6)   NOT NULL,
    criado_por          VARCHAR(100)  NULL,
    atualizado_por      VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tipo_documento_nome (nome)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE pasta_documento (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    nome                VARCHAR(100)  NOT NULL,
    descricao           VARCHAR(250)  NULL,
    pasta_pai_id        BIGINT        NULL COMMENT 'Auto-referência — hierarquia de pastas, enriquecimento sobre o fenix (que só tinha CABECALHO flat)',
    data_criacao        DATETIME(6)   NOT NULL,
    data_atualizacao    DATETIME(6)   NOT NULL,
    criado_por          VARCHAR(100)  NULL,
    atualizado_por      VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_pasta_documento_pai (pasta_pai_id),
    CONSTRAINT fk_pasta_documento_pai FOREIGN KEY (pasta_pai_id) REFERENCES pasta_documento (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE documento (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    pasta_id              BIGINT        NULL,
    tipo_documento_id     BIGINT        NOT NULL,
    nome                  VARCHAR(150)  NOT NULL,
    descricao             VARCHAR(250)  NULL,
    palavras_chave        VARCHAR(250)  NULL COMMENT 'Indexação simples pra localização do documento',
    entidade_tipo         VARCHAR(50)   NULL COMMENT 'Ex: PRODUTO, FORNECEDOR, PEDIDO_COMPRA — qual tipo de entidade este documento está anexado, se houver',
    entidade_id           BIGINT        NULL COMMENT 'Id solto da entidade de outro serviço, junto com entidade_tipo',
    permite_exclusao      TINYINT(1)    NOT NULL DEFAULT 1,
    permite_alteracao     TINYINT(1)    NOT NULL DEFAULT 1,
    assinado              TINYINT(1)    NOT NULL DEFAULT 0 COMMENT 'Metadado de assinatura digital já realizada — não é um fluxo de coleta de assinatura',
    data_fim_vigencia     DATE          NULL,
    situacao              CHAR(1)       NOT NULL DEFAULT 'A',
    data_criacao          DATETIME(6)   NOT NULL,
    data_atualizacao      DATETIME(6)   NOT NULL,
    criado_por            VARCHAR(100)  NULL,
    atualizado_por        VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_documento_pasta (pasta_id),
    KEY idx_documento_tipo (tipo_documento_id),
    KEY idx_documento_entidade (entidade_tipo, entidade_id),
    CONSTRAINT fk_documento_pasta FOREIGN KEY (pasta_id) REFERENCES pasta_documento (id),
    CONSTRAINT fk_documento_tipo FOREIGN KEY (tipo_documento_id) REFERENCES tipo_documento (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE documento_versao (
    id                      BIGINT        NOT NULL AUTO_INCREMENT,
    documento_id            BIGINT        NOT NULL,
    usuario_id              BIGINT        NOT NULL COMMENT 'Quem enviou esta versão — vem da claim usuarioId do JWT, nunca do corpo da requisição',
    numero_versao           INT           NOT NULL COMMENT 'Sequencial por documento, começa em 1',
    tipo_acao               VARCHAR(10)   NOT NULL COMMENT 'INCLUSAO | ALTERACAO | EXCLUSAO',
    hash_arquivo            VARCHAR(64)   NULL COMMENT 'SHA-256 do conteúdo — auditoria de integridade, mesma linha do HASH_REGISTRO do PDV',
    chave_armazenamento     VARCHAR(250)  NULL COMMENT 'Opaca — resolvida pela implementação de DocumentoStorageService, nunca um caminho de filesystem exposto direto. Nula em EXCLUSAO (não há novo arquivo)',
    nome_arquivo_original   VARCHAR(255)  NULL,
    tamanho_bytes           BIGINT        NULL,
    content_type            VARCHAR(150)  NULL COMMENT 'MIME type',
    observacao              VARCHAR(250)  NULL,
    data_criacao            DATETIME(6)   NOT NULL,
    data_atualizacao        DATETIME(6)   NOT NULL,
    criado_por              VARCHAR(100)  NULL,
    atualizado_por          VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_documento_versao (documento_id, numero_versao),
    KEY idx_documento_versao_documento (documento_id),
    CONSTRAINT fk_documento_versao_documento FOREIGN KEY (documento_id) REFERENCES documento (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
