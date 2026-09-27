-- Módulo Comercial (Fase 10) — Ordem de Serviço. Validado campo a campo contra o
-- fenix.sql (OS_STATUS/OS_ABERTURA/OS_EQUIPAMENTO/OS_ABERTURA_EQUIPAMENTO/
-- OS_PRODUTO_SERVICO/OS_EVOLUCAO) — o BANCO_PDV.md não tem nada de ordem de serviço
-- (front-of-store puro). Ver CLAUDE.md, seção ANÁLISE: MÓDULO COMERCIAL.
--
-- cliente_id/colaborador_id/produto_id são referências SOLTAS pro cadastro-base-service.
-- Sem FK cross-schema, sem validação de existência na v1 — mesmo padrão já adotado em
-- todo o monorepo.

CREATE TABLE os_status (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    codigo            VARCHAR(2)   NULL,
    nome              VARCHAR(100) NOT NULL,
    situacao          CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE os_equipamento (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    nome              VARCHAR(100) NOT NULL,
    descricao         VARCHAR(250) NULL,
    situacao          CHAR(1)      NOT NULL DEFAULT 'A',
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE os_abertura (
    id                        BIGINT        NOT NULL AUTO_INCREMENT,
    os_status_id              BIGINT        NOT NULL,
    empresa_id                BIGINT        NOT NULL COMMENT 'Filial que prestou o serviço — FK solta pro Empresa do cadastro-base',
    cliente_id                BIGINT        NOT NULL,
    colaborador_id            BIGINT        NULL COMMENT 'Técnico responsável — FK solta pro Colaborador do cadastro-base',
    numero                    VARCHAR(20)   NULL,
    data_inicio               DATE          NULL,
    hora_inicio               VARCHAR(8)    NULL,
    data_previsao             DATE          NULL,
    hora_previsao             VARCHAR(8)    NULL,
    data_fim                  DATE          NULL,
    hora_fim                  VARCHAR(8)    NULL,
    nome_contato              VARCHAR(50)   NULL,
    fone_contato              VARCHAR(15)   NULL,
    observacao_cliente        VARCHAR(500)  NULL,
    observacao_abertura       VARCHAR(500)  NULL,
    orcamento_aprovado        TINYINT(1)    NOT NULL DEFAULT 0 COMMENT 'Melhoria sobre o fenix (🆕) — fenix não rastreia aprovação do orçamento pelo cliente',
    data_aprovacao_orcamento  DATETIME(6)   NULL,
    valor_total               DECIMAL(18,6) NOT NULL DEFAULT 0,
    data_criacao              DATETIME(6)   NOT NULL,
    data_atualizacao          DATETIME(6)   NOT NULL,
    criado_por                VARCHAR(100)  NULL,
    atualizado_por            VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_os_abertura_status (os_status_id),
    KEY idx_os_abertura_cliente (cliente_id),
    CONSTRAINT fk_os_abertura_status FOREIGN KEY (os_status_id) REFERENCES os_status (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE os_abertura_equipamento (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    os_abertura_id      BIGINT       NOT NULL,
    os_equipamento_id   BIGINT       NOT NULL,
    numero_serie        VARCHAR(50)  NULL,
    tipo_cobertura      CHAR(1)      NULL,
    data_criacao        DATETIME(6)  NOT NULL,
    data_atualizacao    DATETIME(6)  NOT NULL,
    criado_por          VARCHAR(100) NULL,
    atualizado_por      VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_os_abertura_equip_abertura (os_abertura_id),
    KEY idx_os_abertura_equip_equipamento (os_equipamento_id),
    CONSTRAINT fk_os_abertura_equip_abertura FOREIGN KEY (os_abertura_id) REFERENCES os_abertura (id),
    CONSTRAINT fk_os_abertura_equip_equipamento FOREIGN KEY (os_equipamento_id) REFERENCES os_equipamento (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE os_produto_servico (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    os_abertura_id    BIGINT        NOT NULL,
    tipo              VARCHAR(10)   NOT NULL COMMENT 'PRODUTO | SERVICO',
    produto_id        BIGINT        NULL COMMENT 'Obrigatório só quando tipo=PRODUTO',
    complemento       VARCHAR(250)  NULL,
    quantidade        DECIMAL(18,6) NOT NULL,
    valor_unitario    DECIMAL(18,6) NOT NULL,
    valor_subtotal    DECIMAL(18,6) NULL,
    taxa_desconto     DECIMAL(18,6) NULL,
    valor_desconto    DECIMAL(18,6) NULL,
    valor_total       DECIMAL(18,6) NULL,
    data_criacao      DATETIME(6)   NOT NULL,
    data_atualizacao  DATETIME(6)   NOT NULL,
    criado_por        VARCHAR(100)  NULL,
    atualizado_por    VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_os_produto_servico_abertura (os_abertura_id),
    CONSTRAINT fk_os_produto_servico_abertura FOREIGN KEY (os_abertura_id) REFERENCES os_abertura (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE os_evolucao (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    os_abertura_id    BIGINT       NOT NULL,
    data_registro     DATE         NOT NULL,
    hora_registro     VARCHAR(8)   NULL,
    observacao        VARCHAR(500) NULL,
    enviar_email      TINYINT(1)   NOT NULL DEFAULT 0,
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_os_evolucao_abertura (os_abertura_id),
    CONSTRAINT fk_os_evolucao_abertura FOREIGN KEY (os_abertura_id) REFERENCES os_abertura (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
