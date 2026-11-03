-- Cancelamento (evento 110111) e inutilização de numeração da NF-e, transmitidos direto à SEFAZ.

ALTER TABLE nota_fiscal_eletronica
    ADD COLUMN protocolo_cancelamento     VARCHAR(20)  NULL AFTER protocolo_autorizacao,
    ADD COLUMN data_cancelamento          DATETIME(6)  NULL AFTER protocolo_cancelamento,
    ADD COLUMN justificativa_cancelamento VARCHAR(255) NULL AFTER data_cancelamento,
    ADD KEY idx_nota_fiscal_empresa_serie_numero (empresa_id, serie, numero);

-- procEventoNFe do cancelamento, guardado junto do nfeProc (mesma regra de guarda de 5 anos).
ALTER TABLE nota_fiscal_eletronica_xml
    ADD COLUMN xml_cancelamento MEDIUMTEXT NULL COMMENT 'procEventoNFe (evento 110111 + retorno da SEFAZ)';

-- Toda tentativa fica registrada, homologada ou rejeitada: é trilha de auditoria fiscal.
CREATE TABLE inutilizacao_numeracao_nfe (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    empresa_id        BIGINT        NOT NULL,
    modelo            VARCHAR(2)    NOT NULL DEFAULT '55',
    serie             VARCHAR(3)    NOT NULL,
    numero_inicial    BIGINT        NOT NULL,
    numero_final      BIGINT        NOT NULL,
    ano               SMALLINT      NOT NULL COMMENT 'ano com 2 dígitos, como no pedido',
    ambiente          VARCHAR(15)   NOT NULL,
    justificativa     VARCHAR(255)  NOT NULL,
    situacao          VARCHAR(15)   NOT NULL COMMENT 'HOMOLOGADA | REJEITADA',
    protocolo         VARCHAR(20)   NULL,
    codigo_status_sefaz VARCHAR(3)  NULL,
    mensagem_sefaz    VARCHAR(500)  NULL,
    data_homologacao  DATETIME(6)   NULL,
    xml_retorno       TEXT          NULL COMMENT 'retInutNFe assinado pela SEFAZ',
    data_criacao      DATETIME(6)   NOT NULL,
    data_atualizacao  DATETIME(6)   NOT NULL,
    criado_por        VARCHAR(100)  NULL,
    atualizado_por    VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_inutilizacao_faixa (empresa_id, serie, situacao, numero_inicial, numero_final)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
