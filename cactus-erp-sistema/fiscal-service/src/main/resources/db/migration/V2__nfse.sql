-- Módulo Comercial (Fase 9) — NFS-e a partir de uma Ordem de Serviço (os-service, Fase
-- 10), via ACBr Monitor. Validado campo a campo contra o fenix.sql (NFSE_LISTA_SERVICO/
-- NFSE_CABECALHO/NFSE_DETALHE) — construída na sequência prática Fase 8→10→9 (NF-e →
-- OS → NFS-e), diferente da numeração do roadmap, porque NFS-e depende de uma OS
-- existir pra ter algo a emitir, mesma relação que NF-e tinha com Venda.
--
-- os_abertura_id/empresa_id/cliente_id são referências SOLTAS pro os-service/
-- cadastro-base-service. Sem FK cross-schema, sem validação de existência na v1 —
-- mesmo padrão já adotado em todo o monorepo.

CREATE TABLE lista_servico_municipal (
    id                            BIGINT        NOT NULL AUTO_INCREMENT,
    codigo                        VARCHAR(20)   NOT NULL,
    descricao                     VARCHAR(250)  NOT NULL,
    codigo_tributacao_municipio   VARCHAR(20)   NULL,
    aliquota_iss_padrao           DECIMAL(18,6) NULL,
    data_criacao                  DATETIME(6)   NOT NULL,
    data_atualizacao              DATETIME(6)   NOT NULL,
    criado_por                    VARCHAR(100)  NULL,
    atualizado_por                VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_lista_servico_codigo (codigo)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE nota_fiscal_servico (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    os_abertura_id        BIGINT        NOT NULL,
    empresa_id            BIGINT        NOT NULL,
    cliente_id            BIGINT        NOT NULL,
    numero_rps            VARCHAR(15)   NULL,
    serie_rps             VARCHAR(5)    NULL,
    numero                VARCHAR(15)   NULL,
    codigo_verificacao    VARCHAR(9)    NULL,
    competencia           VARCHAR(6)    NULL COMMENT 'AAAAMM',
    data_emissao          DATETIME(6)   NULL,
    ambiente              VARCHAR(15)   NULL COMMENT 'PRODUCAO | HOMOLOGACAO',
    valor_servicos        DECIMAL(18,6) NOT NULL DEFAULT 0,
    valor_iss             DECIMAL(18,6) NOT NULL DEFAULT 0,
    valor_liquido         DECIMAL(18,6) NULL,
    situacao              VARCHAR(15)   NOT NULL COMMENT 'RASCUNHO | ENVIADA | AUTORIZADA | REJEITADA | ERRO | CANCELADA',
    mensagem_sefaz        VARCHAR(500)  NULL,
    data_criacao          DATETIME(6)   NOT NULL,
    data_atualizacao      DATETIME(6)   NOT NULL,
    criado_por            VARCHAR(100)  NULL,
    atualizado_por        VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_nota_fiscal_servico_os (os_abertura_id),
    KEY idx_nota_fiscal_servico_empresa (empresa_id),
    KEY idx_nota_fiscal_servico_cliente (cliente_id),
    KEY idx_nota_fiscal_servico_situacao (situacao)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE nota_fiscal_servico_item (
    id                            BIGINT        NOT NULL AUTO_INCREMENT,
    nota_fiscal_servico_id        BIGINT        NOT NULL,
    lista_servico_id              BIGINT        NOT NULL,
    discriminacao                 VARCHAR(500)  NULL,
    codigo_tributacao_municipio   VARCHAR(20)   NULL,
    valor_servicos                DECIMAL(18,6) NOT NULL,
    aliquota_iss                  DECIMAL(18,6) NULL,
    valor_iss                     DECIMAL(18,6) NULL,
    data_criacao                  DATETIME(6)   NOT NULL,
    data_atualizacao              DATETIME(6)   NOT NULL,
    criado_por                    VARCHAR(100)  NULL,
    atualizado_por                VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_nfse_item_nota (nota_fiscal_servico_id),
    KEY idx_nfse_item_lista_servico (lista_servico_id),
    CONSTRAINT fk_nfse_item_nota FOREIGN KEY (nota_fiscal_servico_id) REFERENCES nota_fiscal_servico (id),
    CONSTRAINT fk_nfse_item_lista_servico FOREIGN KEY (lista_servico_id) REFERENCES lista_servico_municipal (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
