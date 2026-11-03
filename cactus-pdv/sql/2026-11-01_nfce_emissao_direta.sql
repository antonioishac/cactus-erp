-- NFC-e direto na SEFAZ (biblioteca wmixvideo/nfe), sem ACBr Monitor. SAT/MFE removidos: extintos em 01/01/2026
-- (SP: Portaria SRE 79/2024; CE: Decreto 36.417/2025) — desde então o documento ao consumidor é só NFC-e.
-- Aplicar no schema cactus_pdv. O PDV usa ddl-auto=none: este script é a fonte da mudança (refletida em PDV.md e
-- BANCO_PDV.md).

-- ---------------------------------------------------------------- terminal
-- Numeração da NFC-e é por TERMINAL (série própria + contador local): funciona offline e nunca colide entre caixas.
-- A série de cada terminal tem que ser diferente das séries usadas pelo ERP (fiscal-service/loja virtual).
ALTER TABLE PDV_CONFIGURACAO
    DROP COLUMN ACBR_MONITOR_ENDERECO,
    DROP COLUMN ACBR_MONITOR_PORTA,
    DROP COLUMN MODULO_FISCAL_PRINCIPAL,
    DROP COLUMN MODULO_FISCAL_CONTINGENCIA,
    ADD COLUMN NFCE_HABILITADA     CHAR(1)           NOT NULL DEFAULT 'N' COMMENT 'S=emite NFC-e | N=recibo sem documento fiscal',
    ADD COLUMN NFCE_AMBIENTE       CHAR(1)           NOT NULL DEFAULT '2' COMMENT '1=Producao | 2=Homologacao',
    ADD COLUMN NFCE_SERIE          SMALLINT UNSIGNED NULL     COMMENT 'Serie propria deste terminal (0-999)',
    ADD COLUMN NFCE_PROXIMO_NUMERO INT UNSIGNED      NULL     COMMENT 'Proximo numero livre da serie deste terminal';

-- ---------------------------------------------------------------- cupom
ALTER TABLE PDV_VENDA_CABECALHO
    ADD COLUMN NFCE_SERIE             SMALLINT UNSIGNED NULL AFTER MENSAGEM_FISCAL,
    ADD COLUMN NFCE_NUMERO            INT UNSIGNED      NULL AFTER NFCE_SERIE,
    ADD COLUMN NFCE_TIPO_EMISSAO      CHAR(1)           NULL COMMENT '1=Normal | 9=Contingencia offline' AFTER NFCE_NUMERO,
    ADD COLUMN NFCE_DATA_HORA_EMISSAO DATETIME          NULL AFTER NFCE_TIPO_EMISSAO,
    ADD COLUMN CODIGO_STATUS_SEFAZ    VARCHAR(3)        NULL COMMENT 'cStat do ultimo retorno' AFTER NFCE_DATA_HORA_EMISSAO,
    MODIFY COLUMN STATUS_FISCAL CHAR(1) NULL COMMENT 'N=Nao habilitada (recibo) | E=Autorizada | O=Contingencia offline aguardando transmissao | R=Rejeitada (requer correcao) | D=Denegada | P=Pendente (falha antes de assinar) | C=Venda cancelada antes da emissao',
    ADD KEY idx_pdv_venda_status_fiscal (STATUS_FISCAL),
    ADD KEY idx_pdv_venda_chave_acesso (CHAVE_ACESSO_FISCAL);

-- ---------------------------------------------------------------- XML (guarda obrigatória)
-- Separado do cabeçalho: dezenas de KB por cupom. XML_LOTE_ASSINADO é o documento exato que foi (ou será, na
-- contingência offline) transmitido — a NFC-e offline tem que ir à SEFAZ idêntica ao que foi impresso.
CREATE TABLE PDV_NFCE_XML (
    ID_PDV_VENDA_CABECALHO INT UNSIGNED  NOT NULL,
    XML_LOTE_ASSINADO      MEDIUMTEXT    NOT NULL COMMENT 'enviNFe assinado (tpEmis 1 ou 9)',
    XML_PROCESSADO         MEDIUMTEXT    NULL     COMMENT 'nfeProc (NFC-e + protocolo) apos autorizacao',
    XML_CANCELAMENTO       MEDIUMTEXT    NULL     COMMENT 'procEventoNFe do cancelamento',
    QR_CODE                VARCHAR(1000) NOT NULL COMMENT 'URL do QR Code v3 impresso no DANFE',
    URL_CONSULTA           VARCHAR(250)  NULL     COMMENT 'urlChave impressa no DANFE',
    DATA_CRIACAO           DATETIME      NOT NULL,
    PRIMARY KEY (ID_PDV_VENDA_CABECALHO),
    CONSTRAINT fk_PDV_NFCE_XML_VENDA FOREIGN KEY (ID_PDV_VENDA_CABECALHO) REFERENCES PDV_VENDA_CABECALHO (ID)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ---------------------------------------------------------------- números a regularizar
-- Número de NFC-e que não ficou com o cupom: (a) transmitido sem resposta (timeout) e substituído por uma NFC-e em
-- contingência offline com outro número — se a SEFAZ o autorizou, tem de ser cancelado por substituição (evento
-- 110112); se não, inutilizado (ou só registrado nas UFs que dispensam: Ajuste SINIEF 38/2026); (b) reservado e
-- abandonado por falha antes da assinatura.
CREATE TABLE PDV_NFCE_NUMERO_PENDENTE (
    ID                     INT UNSIGNED NOT NULL AUTO_INCREMENT,
    ID_PDV_CAIXA           INT UNSIGNED NOT NULL,
    ID_PDV_VENDA_CABECALHO INT UNSIGNED NULL     COMMENT 'Cupom que ficou com outro numero (a NFC-e substituta)',
    AMBIENTE               CHAR(1)      NOT NULL COMMENT '1=Producao | 2=Homologacao',
    SERIE                  SMALLINT UNSIGNED NOT NULL,
    NUMERO                 INT UNSIGNED NOT NULL,
    CHAVE_ACESSO           VARCHAR(44)  NULL     COMMENT 'Chave da tentativa sem resposta (nula = nunca transmitido)',
    SITUACAO               CHAR(1)      NOT NULL DEFAULT 'V' COMMENT 'V=Verificar na SEFAZ | S=Cancelada por substituicao | I=Inutilizado | D=Dispensado pela UF | E=Erro (ver mensagem)',
    PROTOCOLO              VARCHAR(30)  NULL,
    MENSAGEM               VARCHAR(500) NULL,
    TENTATIVAS             INT UNSIGNED NOT NULL DEFAULT 0,
    DATA_REGISTRO          DATETIME     NOT NULL,
    DATA_RESOLUCAO         DATETIME     NULL,
    PRIMARY KEY (ID),
    UNIQUE KEY uk_PDV_NFCE_NUMERO_PENDENTE (ID_PDV_CAIXA, SERIE, NUMERO),
    KEY idx_PDV_NFCE_NUMERO_PENDENTE_SITUACAO (SITUACAO),
    CONSTRAINT fk_PDV_NFCE_NUMERO_PENDENTE_CAIXA FOREIGN KEY (ID_PDV_CAIXA) REFERENCES PDV_CAIXA (ID),
    CONSTRAINT fk_PDV_NFCE_NUMERO_PENDENTE_VENDA FOREIGN KEY (ID_PDV_VENDA_CABECALHO) REFERENCES PDV_VENDA_CABECALHO (ID)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
