-- Fornecedor (Fase 1a, gap que tinha ficado pendente ao lado de Cliente/Colaborador) —
-- validado contra fenix.sql.FORNECEDOR, mesmo padrão de cliente (referencia pessoa, não
-- duplica nome/documento). Ver CLAUDE.md.
--
-- Funcionalidades/grants de CADASTRO_FORNECEDOR_* NÃO são semeados aqui — a tabela
-- funcionalidade mora no seguranca-service desde a extração do RBAC (ver
-- V3__seed_dados.sql de lá, já semeado com fornecedor desde o início).

CREATE TABLE fornecedor (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    pessoa_id           BIGINT        NOT NULL,
    fornecedor_desde    DATE          NULL,
    data_cadastro       DATE          NOT NULL,
    prazo_entrega_dias  INT           NULL,
    observacao          VARCHAR(250)  NULL,
    situacao            CHAR(1)       NOT NULL DEFAULT 'A',
    data_criacao        DATETIME(6)   NOT NULL,
    data_atualizacao    DATETIME(6)   NOT NULL,
    criado_por          VARCHAR(100)  NULL,
    atualizado_por      VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_fornecedor_pessoa (pessoa_id),
    KEY idx_fornecedor_situacao (situacao),
    CONSTRAINT fk_fornecedor_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
