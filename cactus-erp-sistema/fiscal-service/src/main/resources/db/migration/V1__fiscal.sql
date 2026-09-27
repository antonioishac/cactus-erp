-- Módulo Comercial (Fase 8) — Emissão de NF-e a partir de uma Venda, via ACBr Monitor.
-- Validado campo a campo contra o fenix.sql (NFE_CABECALHO/NFE_DETALHE + tabelas de
-- imposto satélite), simplificado pro cenário de venda de mercadoria comum — ver
-- Javadoc de NotaFiscalEletronica/NotaFiscalEletronicaItem e CLAUDE.md.
--
-- venda_id/vendedor_id são referências SOLTAS pro vendas-service; empresa_id/cliente_id/
-- transportadora_id/produto_id são soltas pro cadastro-base-service. Sem FK cross-schema,
-- sem validação de existência na v1 — mesmo padrão já adotado em todo o monorepo.

CREATE TABLE nota_fiscal_eletronica (
    id                      BIGINT        NOT NULL AUTO_INCREMENT,
    venda_id                BIGINT        NOT NULL,
    empresa_id              BIGINT        NOT NULL,
    cliente_id              BIGINT        NOT NULL,
    transportadora_id       BIGINT        NULL,
    modelo                  VARCHAR(2)    NOT NULL DEFAULT '55',
    serie                   VARCHAR(3)    NULL,
    numero                  BIGINT        NULL,
    chave_acesso            VARCHAR(44)   NULL,
    protocolo_autorizacao   VARCHAR(30)   NULL,
    data_emissao            DATETIME(6)   NULL,
    ambiente                VARCHAR(15)   NULL COMMENT 'PRODUCAO | HOMOLOGACAO',
    valor_total             DECIMAL(18,6) NOT NULL DEFAULT 0,
    situacao                VARCHAR(15)   NOT NULL COMMENT 'RASCUNHO | ENVIADA | AUTORIZADA | REJEITADA | ERRO | CANCELADA',
    mensagem_sefaz          VARCHAR(500)  NULL,
    data_criacao            DATETIME(6)   NOT NULL,
    data_atualizacao        DATETIME(6)   NOT NULL,
    criado_por              VARCHAR(100)  NULL,
    atualizado_por          VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_nota_fiscal_venda (venda_id),
    KEY idx_nota_fiscal_empresa (empresa_id),
    KEY idx_nota_fiscal_cliente (cliente_id),
    KEY idx_nota_fiscal_situacao (situacao)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE nota_fiscal_eletronica_item (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    nota_fiscal_id      BIGINT        NOT NULL,
    numero_item         INT           NOT NULL,
    produto_id          BIGINT        NOT NULL,
    descricao           VARCHAR(120)  NOT NULL,
    gtin                VARCHAR(20)   NULL,
    ncm                 VARCHAR(8)    NULL,
    cfop                VARCHAR(4)    NULL,
    unidade             VARCHAR(6)    NULL,
    quantidade          DECIMAL(18,6) NOT NULL,
    valor_unitario      DECIMAL(18,6) NOT NULL,
    valor_total         DECIMAL(18,6) NOT NULL,
    valor_desconto      DECIMAL(18,6) NULL,
    origem_mercadoria   CHAR(1)       NOT NULL DEFAULT '0',
    cst_icms            VARCHAR(3)    NULL,
    csosn               VARCHAR(3)    NULL,
    aliquota_icms       DECIMAL(18,6) NULL,
    valor_base_icms     DECIMAL(18,6) NULL,
    valor_icms          DECIMAL(18,6) NULL,
    aliquota_ipi        DECIMAL(18,6) NULL,
    valor_ipi           DECIMAL(18,6) NULL,
    cst_pis             VARCHAR(2)    NOT NULL DEFAULT '01',
    aliquota_pis        DECIMAL(18,6) NULL,
    valor_pis           DECIMAL(18,6) NULL,
    cst_cofins          VARCHAR(2)    NOT NULL DEFAULT '01',
    aliquota_cofins     DECIMAL(18,6) NULL,
    valor_cofins        DECIMAL(18,6) NULL,
    data_criacao        DATETIME(6)   NOT NULL,
    data_atualizacao    DATETIME(6)   NOT NULL,
    criado_por          VARCHAR(100)  NULL,
    atualizado_por      VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_nota_fiscal_item_nota (nota_fiscal_id),
    CONSTRAINT fk_nota_fiscal_item_nota FOREIGN KEY (nota_fiscal_id) REFERENCES nota_fiscal_eletronica (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
