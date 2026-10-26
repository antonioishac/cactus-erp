-- IBS/CBS (Reforma Tributária, LC 214/2025) na NF-e — NT 2025.002-RTC v1.50, grupo UB do item e W03 do total.
-- Obrigatório em produção para CRT 3 (Regime Normal) desde 03/08/2026; Simples/MEI aguardam NT futura.
-- Valores com 2 casas (como vão no XML), alíquotas com 4.
ALTER TABLE nota_fiscal_eletronica_item
    ADD COLUMN cst_ibs_cbs                    CHAR(3)       NULL,
    ADD COLUMN classificacao_ibs_cbs          CHAR(6)       NULL COMMENT 'cClassTrib',
    ADD COLUMN valor_base_ibs_cbs             DECIMAL(15,2) NULL COMMENT 'gIBSCBS/vBC (UB16-10)',
    ADD COLUMN aliquota_ibs_uf                DECIMAL(7,4)  NULL,
    ADD COLUMN percentual_reducao_ibs         DECIMAL(7,4)  NULL COMMENT 'pRedAliq do IBS (UF e município)',
    ADD COLUMN aliquota_efetiva_ibs_uf        DECIMAL(7,4)  NULL,
    ADD COLUMN valor_ibs_uf                   DECIMAL(15,2) NULL,
    ADD COLUMN aliquota_ibs_municipio         DECIMAL(7,4)  NULL,
    ADD COLUMN aliquota_efetiva_ibs_municipio DECIMAL(7,4)  NULL,
    ADD COLUMN valor_ibs_municipio            DECIMAL(15,2) NULL,
    ADD COLUMN valor_ibs                      DECIMAL(15,2) NULL COMMENT 'vIBSUF + vIBSMun (UB54a-10)',
    ADD COLUMN aliquota_cbs                   DECIMAL(7,4)  NULL,
    ADD COLUMN percentual_reducao_cbs         DECIMAL(7,4)  NULL,
    ADD COLUMN aliquota_efetiva_cbs           DECIMAL(7,4)  NULL,
    ADD COLUMN valor_cbs                      DECIMAL(15,2) NULL;

ALTER TABLE nota_fiscal_eletronica
    ADD COLUMN valor_base_ibs_cbs   DECIMAL(15,2) NULL COMMENT 'IBSCBSTot/vBCIBSCBS',
    ADD COLUMN valor_ibs_uf         DECIMAL(15,2) NULL,
    ADD COLUMN valor_ibs_municipio  DECIMAL(15,2) NULL,
    ADD COLUMN valor_ibs            DECIMAL(15,2) NULL,
    ADD COLUMN valor_cbs            DECIMAL(15,2) NULL;
