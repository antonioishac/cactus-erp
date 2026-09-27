-- Módulo Comercial (Fase 12) — CRM. Greenfield: sem tabela equivalente no fenix.sql
-- (confirmado por grep exaustivo) nem no BANCO_PDV.md. Ver CLAUDE.md, seção ANÁLISE:
-- MÓDULO COMERCIAL.
--
-- cliente_id/vendedor_id/usuario_id são referências SOLTAS pro cadastro-base-service/
-- vendas-service/seguranca-service. Sem FK cross-schema, sem validação de existência
-- na v1 — mesmo padrão já adotado em todo o monorepo.

CREATE TABLE estagio_funil (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    codigo              VARCHAR(30)  NULL,
    nome                VARCHAR(100) NOT NULL,
    ordem               INT          NOT NULL,
    tipo_encerramento   VARCHAR(10)  NOT NULL DEFAULT 'ABERTO' COMMENT 'ABERTO | GANHO | PERDIDO',
    situacao            CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao        DATETIME(6)  NOT NULL,
    data_atualizacao    DATETIME(6)  NOT NULL,
    criado_por          VARCHAR(100) NULL,
    atualizado_por      VARCHAR(100) NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE crm_lead (
    id                        BIGINT        NOT NULL AUTO_INCREMENT,
    nome                      VARCHAR(120)  NOT NULL,
    nome_empresa              VARCHAR(120)  NULL,
    email                     VARCHAR(120)  NULL,
    telefone                  VARCHAR(20)   NULL,
    origem                    VARCHAR(15)   NULL COMMENT 'SITE | INDICACAO | EVENTO | COLD_CALL | REDES_SOCIAIS | OUTROS',
    vendedor_id               BIGINT        NULL COMMENT 'FK solta pro Vendedor do vendas-service',
    cliente_id                BIGINT        NULL COMMENT 'Preenchido só na conversão — FK solta pro Cliente do cadastro-base-service',
    situacao                  VARCHAR(15)   NOT NULL DEFAULT 'NOVO' COMMENT 'NOVO | EM_QUALIFICACAO | QUALIFICADO | DESQUALIFICADO | CONVERTIDO',
    motivo_desqualificacao    VARCHAR(250)  NULL,
    observacao                VARCHAR(500)  NULL,
    data_criacao              DATETIME(6)   NOT NULL,
    data_atualizacao          DATETIME(6)   NOT NULL,
    criado_por                VARCHAR(100)  NULL,
    atualizado_por            VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_lead_situacao (situacao),
    KEY idx_lead_vendedor (vendedor_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE oportunidade (
    id                          BIGINT        NOT NULL AUTO_INCREMENT,
    lead_id                     BIGINT        NULL,
    cliente_id                  BIGINT        NULL COMMENT 'FK solta pro Cliente do cadastro-base-service',
    vendedor_id                 BIGINT        NULL COMMENT 'FK solta pro Vendedor do vendas-service',
    nome                        VARCHAR(120)  NOT NULL,
    valor_estimado              DECIMAL(18,6) NULL,
    probabilidade_percentual    DECIMAL(5,2)  NULL,
    data_previsao_fechamento    DATE          NULL,
    estagio_funil_id            BIGINT        NOT NULL,
    situacao                    VARCHAR(10)   NOT NULL DEFAULT 'ABERTA' COMMENT 'ABERTA | GANHA | PERDIDA',
    motivo_perda                VARCHAR(250)  NULL,
    data_fechamento             DATETIME(6)   NULL,
    observacao                  VARCHAR(500)  NULL,
    data_criacao                DATETIME(6)   NOT NULL,
    data_atualizacao            DATETIME(6)   NOT NULL,
    criado_por                  VARCHAR(100)  NULL,
    atualizado_por              VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_oportunidade_lead (lead_id),
    KEY idx_oportunidade_estagio (estagio_funil_id),
    KEY idx_oportunidade_situacao (situacao),
    CONSTRAINT fk_oportunidade_lead FOREIGN KEY (lead_id) REFERENCES crm_lead (id),
    CONSTRAINT fk_oportunidade_estagio FOREIGN KEY (estagio_funil_id) REFERENCES estagio_funil (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE atividade (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    lead_id             BIGINT       NULL,
    oportunidade_id     BIGINT       NULL,
    tipo                VARCHAR(10)  NOT NULL COMMENT 'LIGACAO | REUNIAO | EMAIL | VISITA | TAREFA',
    responsavel_id      BIGINT       NULL COMMENT 'FK solta pro Usuario/Vendedor responsável',
    data_hora           DATETIME(6)  NOT NULL,
    concluida           TINYINT(1)   NOT NULL DEFAULT 0,
    data_conclusao      DATETIME(6)  NULL,
    descricao           VARCHAR(500) NULL,
    data_criacao        DATETIME(6)  NOT NULL,
    data_atualizacao    DATETIME(6)  NOT NULL,
    criado_por          VARCHAR(100) NULL,
    atualizado_por      VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_atividade_lead (lead_id),
    KEY idx_atividade_oportunidade (oportunidade_id),
    CONSTRAINT fk_atividade_lead FOREIGN KEY (lead_id) REFERENCES crm_lead (id),
    CONSTRAINT fk_atividade_oportunidade FOREIGN KEY (oportunidade_id) REFERENCES oportunidade (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE interacao (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    lead_id             BIGINT        NULL,
    oportunidade_id     BIGINT        NULL,
    tipo                VARCHAR(10)   NOT NULL COMMENT 'LIGACAO | EMAIL | REUNIAO | WHATSAPP | NOTA',
    data_interacao      DATETIME(6)   NOT NULL,
    usuario_id          BIGINT        NULL COMMENT 'FK solta pro Usuario do seguranca-service',
    descricao           VARCHAR(1000) NOT NULL,
    data_criacao        DATETIME(6)   NOT NULL,
    data_atualizacao    DATETIME(6)   NOT NULL,
    criado_por          VARCHAR(100)  NULL,
    atualizado_por      VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_interacao_lead (lead_id),
    KEY idx_interacao_oportunidade (oportunidade_id),
    CONSTRAINT fk_interacao_lead FOREIGN KEY (lead_id) REFERENCES crm_lead (id),
    CONSTRAINT fk_interacao_oportunidade FOREIGN KEY (oportunidade_id) REFERENCES oportunidade (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
