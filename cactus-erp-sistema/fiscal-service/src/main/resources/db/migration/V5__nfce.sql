-- Fase 13, sub-fase 3 — NFC-e (modelo 65) a partir de um Pedido PAGO da Loja Virtual.
-- Validado campo a campo contra cactus-pdv.AcbrComandoFiscalBuilder (layout NFCe.INI já
-- testado contra um ACBrMonitorPLUS real em produção) — ver Javadoc de
-- NotaFiscalConsumidor/NfceIniBuilder e CLAUDE.md.
--
-- pedido_id/loja_id/cliente_loja_id são referências SOLTAS pro loja-virtual-service;
-- empresa_id é solta pro cadastro-base-service. Sem FK cross-schema, mesmo padrão do
-- monorepo.

CREATE TABLE nota_fiscal_consumidor (
    id                      BIGINT        NOT NULL AUTO_INCREMENT,
    pedido_id               BIGINT        NOT NULL,
    loja_id                 BIGINT        NOT NULL,
    empresa_id              BIGINT        NOT NULL,
    cliente_loja_id         BIGINT        NULL,
    modelo                  VARCHAR(2)    NOT NULL DEFAULT '65',
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
    UNIQUE KEY uk_nota_fiscal_consumidor_pedido (pedido_id),
    KEY idx_nota_fiscal_consumidor_loja (loja_id),
    KEY idx_nota_fiscal_consumidor_situacao (situacao)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE nota_fiscal_consumidor_item (
    id                          BIGINT        NOT NULL AUTO_INCREMENT,
    nota_fiscal_consumidor_id   BIGINT        NOT NULL,
    numero_item                 INT           NOT NULL,
    variante_produto_loja_id    BIGINT        NOT NULL,
    descricao                   VARCHAR(150)  NOT NULL,
    ncm                         VARCHAR(8)    NULL,
    csosn                       VARCHAR(3)    NULL,
    quantidade                  DECIMAL(18,6) NOT NULL,
    valor_unitario              DECIMAL(18,6) NOT NULL,
    valor_total                 DECIMAL(18,6) NOT NULL,
    data_criacao                DATETIME(6)   NOT NULL,
    data_atualizacao            DATETIME(6)   NOT NULL,
    criado_por                  VARCHAR(100)  NULL,
    atualizado_por              VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_nota_fiscal_consumidor_item_nota (nota_fiscal_consumidor_id),
    CONSTRAINT fk_nota_fiscal_consumidor_item_nota FOREIGN KEY (nota_fiscal_consumidor_id) REFERENCES nota_fiscal_consumidor (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
