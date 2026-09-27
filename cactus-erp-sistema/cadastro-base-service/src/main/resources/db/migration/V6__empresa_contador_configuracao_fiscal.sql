-- Fase 1c (parte 2): Contador (fenix.sql.CONTADOR, N:1 com Empresa) e configuração
-- fiscal por filial (certificado digital + série/numeração NFe/NFCe/NFSe + ambiente
-- SEFAZ + CSC) — ver ANÁLISE: EMPRESA MULTI-FILIAL no CLAUDE.md.
--
-- Esta é a primeira migration criada DEPOIS da aplicação já ter rodado contra um banco
-- real (V1-V7 já aplicadas em cactus_cadastro) — a partir daqui, migration aplicada não
-- se edita mais, só se corrige com uma nova (ver nota no CLAUDE.md).

CREATE TABLE contador (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    nome              VARCHAR(150) NOT NULL,
    crc               VARCHAR(20)  NOT NULL,
    email             VARCHAR(250) NULL,
    telefone          VARCHAR(15)  NULL,
    situacao          CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Coluna nova, opcional — empresa já pode ter linhas (seed da V7), por isso NULL sem
-- valor de transição (nenhuma empresa existente é obrigada a ter contador retroativo).
ALTER TABLE empresa
    ADD COLUMN contador_id BIGINT NULL AFTER codigo_cnae_principal,
    ADD KEY idx_empresa_contador (contador_id),
    ADD CONSTRAINT fk_empresa_contador FOREIGN KEY (contador_id) REFERENCES contador (id);

CREATE TABLE empresa_configuracao_fiscal (
    id                     BIGINT        NOT NULL AUTO_INCREMENT,
    empresa_id             BIGINT        NOT NULL,
    tipo_certificado       VARCHAR(2)    NULL,
    certificado_alias      VARCHAR(100)  NULL,
    certificado_validade   DATE          NULL,
    ambiente_fiscal        VARCHAR(20)   NOT NULL,
    serie_nfe              VARCHAR(3)    NULL,
    proximo_numero_nfe     BIGINT        NULL,
    serie_nfce              VARCHAR(3)   NULL,
    proximo_numero_nfce    BIGINT        NULL,
    serie_nfse              VARCHAR(3)   NULL,
    proximo_numero_nfse    BIGINT        NULL,
    csc                    VARCHAR(100)  NULL,
    csc_id_token           VARCHAR(10)   NULL,
    data_criacao           DATETIME(6)   NOT NULL,
    data_atualizacao       DATETIME(6)   NOT NULL,
    criado_por             VARCHAR(100)  NULL,
    atualizado_por         VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_empresa_configuracao_fiscal_empresa (empresa_id),
    CONSTRAINT fk_empresa_configuracao_fiscal_empresa FOREIGN KEY (empresa_id) REFERENCES empresa (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
