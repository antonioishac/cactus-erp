-- Transportadora (Fase 7, Módulo Comercial) — validado contra fenix.sql.TRANSPORTADORA,
-- mesmo padrão de fornecedor (referencia pessoa, não duplica nome/documento). Gap real:
-- VENDA_CABECALHO/VENDA_ORCAMENTO_CABECALHO/VENDA_FRETE do vendas-service referenciam
-- transportadoraId (solto, cross-serviço) e a entidade não existia no cadastro-base
-- até agora. Ver CLAUDE.md.
--
-- Funcionalidades/grants de CADASTRO_TRANSPORTADORA_* NÃO são semeados aqui — a tabela
-- funcionalidade mora no seguranca-service desde a extração do RBAC (ver migration
-- própria lá, seed_vendas_e_transportadora_funcionalidades).

CREATE TABLE transportadora (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    pessoa_id           BIGINT        NOT NULL,
    data_cadastro       DATE          NOT NULL,
    rntrc               VARCHAR(20)   NULL,
    observacao          VARCHAR(250)  NULL,
    situacao            CHAR(1)       NOT NULL DEFAULT 'A',
    data_criacao        DATETIME(6)   NOT NULL,
    data_atualizacao    DATETIME(6)   NOT NULL,
    criado_por          VARCHAR(100)  NULL,
    atualizado_por      VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_transportadora_pessoa (pessoa_id),
    KEY idx_transportadora_situacao (situacao),
    CONSTRAINT fk_transportadora_pessoa FOREIGN KEY (pessoa_id) REFERENCES pessoa (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
