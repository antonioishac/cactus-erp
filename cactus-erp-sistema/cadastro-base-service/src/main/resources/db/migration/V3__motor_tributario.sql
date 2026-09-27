-- Motor tributário completo (Fase 1b) — substitui o campo solto `codigoNcm` que o
-- Produto tinha antes. Cadeia: GrupoTributario × OperacaoFiscal -> ConfiguracaoTributaria
-- (a célula) -> IcmsConfiguracao (1 por UF de destino) + Pis/Cofins/IpiConfiguracao (1:1).
-- IssConfiguracao é 1:1 direto com OperacaoFiscal (serviço, não passa pela célula).
-- Ver ANÁLISE TRIBUTÁRIA no CLAUDE.md — desenho validado contra fenix.sql.TRIBUT_*.

CREATE TABLE grupo_tributario (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    nome              VARCHAR(100) NOT NULL,
    origem_mercadoria VARCHAR(1)   NOT NULL,
    observacao        TEXT         NULL,
    situacao          CHAR(1)      NOT NULL,
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_grupo_tributario_nome (nome)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE operacao_fiscal (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    nome              VARCHAR(100) NOT NULL,
    descricao_na_nf   VARCHAR(100) NULL,
    cfop_id           BIGINT       NULL,
    observacao        TEXT         NULL,
    situacao          CHAR(1)      NOT NULL,
    data_criacao      DATETIME(6)  NOT NULL,
    data_atualizacao  DATETIME(6)  NOT NULL,
    criado_por        VARCHAR(100) NULL,
    atualizado_por    VARCHAR(100) NULL,
    PRIMARY KEY (id),
    KEY idx_operacao_fiscal_nome (nome),
    KEY idx_operacao_fiscal_cfop (cfop_id),
    CONSTRAINT fk_operacao_fiscal_cfop FOREIGN KEY (cfop_id) REFERENCES cfop (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- 1:1 direto com operacao_fiscal — só existe quando a operação é de prestação de serviço.
CREATE TABLE iss_configuracao (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    operacao_fiscal_id    BIGINT        NOT NULL,
    modalidade_base_calculo VARCHAR(20) NULL,
    porcento_base_calculo   DECIMAL(18,6) NULL,
    aliquota_percentual     DECIMAL(18,6) NULL,
    aliquota_unidade        DECIMAL(18,6) NULL,
    valor_preco_maximo      DECIMAL(18,6) NULL,
    valor_pauta_fiscal       DECIMAL(18,6) NULL,
    item_lista_servico       INT           NULL,
    codigo_tributacao        VARCHAR(20)   NULL,
    data_criacao          DATETIME(6)   NOT NULL,
    data_atualizacao      DATETIME(6)   NOT NULL,
    criado_por            VARCHAR(100)  NULL,
    atualizado_por        VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_iss_configuracao_operacao (operacao_fiscal_id),
    CONSTRAINT fk_iss_configuracao_operacao FOREIGN KEY (operacao_fiscal_id) REFERENCES operacao_fiscal (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- A célula da matriz Grupo Tributário x Operação Fiscal.
CREATE TABLE configuracao_tributaria (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    grupo_tributario_id  BIGINT       NOT NULL,
    operacao_fiscal_id   BIGINT       NOT NULL,
    data_criacao         DATETIME(6)  NOT NULL,
    data_atualizacao     DATETIME(6)  NOT NULL,
    criado_por           VARCHAR(100) NULL,
    atualizado_por       VARCHAR(100) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_trib_grupo_operacao (grupo_tributario_id, operacao_fiscal_id),
    KEY idx_config_trib_grupo (grupo_tributario_id),
    KEY idx_config_trib_operacao (operacao_fiscal_id),
    CONSTRAINT fk_config_trib_grupo FOREIGN KEY (grupo_tributario_id) REFERENCES grupo_tributario (id),
    CONSTRAINT fk_config_trib_operacao FOREIGN KEY (operacao_fiscal_id) REFERENCES operacao_fiscal (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- 1 linha por UF de destino — é aqui que mora a complexidade real do ICMS (varia por estado).
CREATE TABLE icms_configuracao (
    id                            BIGINT        NOT NULL AUTO_INCREMENT,
    configuracao_tributaria_id    BIGINT        NOT NULL,
    uf_destino                    VARCHAR(2)    NOT NULL,
    cst_icms                      VARCHAR(10)   NULL,
    csosn                         VARCHAR(10)   NULL,
    modalidade_base_calculo       VARCHAR(30)   NULL,
    aliquota                      DECIMAL(18,6) NULL,
    valor_pauta                   DECIMAL(18,6) NULL,
    valor_preco_maximo            DECIMAL(18,6) NULL,
    mva                           DECIMAL(18,6) NULL,
    porcento_base_calculo         DECIMAL(18,6) NULL,
    modalidade_base_calculo_st    VARCHAR(30)   NULL,
    aliquota_interna_st           DECIMAL(18,6) NULL,
    aliquota_interestadual_st     DECIMAL(18,6) NULL,
    porcento_base_calculo_st      DECIMAL(18,6) NULL,
    aliquota_icms_st              DECIMAL(18,6) NULL,
    valor_pauta_st                DECIMAL(18,6) NULL,
    valor_preco_maximo_st         DECIMAL(18,6) NULL,
    data_criacao                  DATETIME(6)   NOT NULL,
    data_atualizacao              DATETIME(6)   NOT NULL,
    criado_por                    VARCHAR(100)  NULL,
    atualizado_por                VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_icms_config_uf (configuracao_tributaria_id, uf_destino),
    CONSTRAINT fk_icms_config_config_trib FOREIGN KEY (configuracao_tributaria_id) REFERENCES configuracao_tributaria (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE pis_configuracao (
    id                       BIGINT        NOT NULL AUTO_INCREMENT,
    configuracao_tributaria_id BIGINT      NOT NULL,
    cst                      VARCHAR(10)   NULL,
    modalidade_base_calculo  VARCHAR(20)   NULL,
    porcento_base_calculo    DECIMAL(18,6) NULL,
    aliquota_percentual      DECIMAL(18,6) NULL,
    aliquota_unidade         DECIMAL(18,6) NULL,
    valor_preco_maximo       DECIMAL(18,6) NULL,
    valor_pauta_fiscal       DECIMAL(18,6) NULL,
    data_criacao             DATETIME(6)   NOT NULL,
    data_atualizacao         DATETIME(6)   NOT NULL,
    criado_por               VARCHAR(100)  NULL,
    atualizado_por           VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pis_config_config_trib (configuracao_tributaria_id),
    CONSTRAINT fk_pis_config_config_trib FOREIGN KEY (configuracao_tributaria_id) REFERENCES configuracao_tributaria (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE cofins_configuracao (
    id                       BIGINT        NOT NULL AUTO_INCREMENT,
    configuracao_tributaria_id BIGINT      NOT NULL,
    cst                      VARCHAR(10)   NULL,
    modalidade_base_calculo  VARCHAR(20)   NULL,
    porcento_base_calculo    DECIMAL(18,6) NULL,
    aliquota_percentual      DECIMAL(18,6) NULL,
    aliquota_unidade         DECIMAL(18,6) NULL,
    valor_preco_maximo       DECIMAL(18,6) NULL,
    valor_pauta_fiscal       DECIMAL(18,6) NULL,
    data_criacao             DATETIME(6)   NOT NULL,
    data_atualizacao         DATETIME(6)   NOT NULL,
    criado_por               VARCHAR(100)  NULL,
    atualizado_por           VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_cofins_config_config_trib (configuracao_tributaria_id),
    CONSTRAINT fk_cofins_config_config_trib FOREIGN KEY (configuracao_tributaria_id) REFERENCES configuracao_tributaria (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE ipi_configuracao (
    id                       BIGINT        NOT NULL AUTO_INCREMENT,
    configuracao_tributaria_id BIGINT      NOT NULL,
    cst                      VARCHAR(10)   NULL,
    modalidade_base_calculo  VARCHAR(20)   NULL,
    porcento_base_calculo    DECIMAL(18,6) NULL,
    aliquota_percentual      DECIMAL(18,6) NULL,
    aliquota_unidade         DECIMAL(18,6) NULL,
    valor_preco_maximo       DECIMAL(18,6) NULL,
    valor_pauta_fiscal       DECIMAL(18,6) NULL,
    data_criacao             DATETIME(6)   NOT NULL,
    data_atualizacao         DATETIME(6)   NOT NULL,
    criado_por               VARCHAR(100)  NULL,
    atualizado_por           VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_ipi_config_config_trib (configuracao_tributaria_id),
    CONSTRAINT fk_ipi_config_config_trib FOREIGN KEY (configuracao_tributaria_id) REFERENCES configuracao_tributaria (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Seed de demonstração: revenda de refrigerante (mesmo NCM/CFOP semeados na V6) sujeita a
-- ICMS-ST, vendida a consumidor final dentro de SP e interestadual pra MG.
INSERT INTO grupo_tributario (nome, origem_mercadoria, observacao, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por) VALUES
  ('Revenda sujeita a ICMS-ST', '0', 'Mercadoria nacional de revenda, ICMS recolhido por substituição tributária', 'A', NOW(6), NOW(6), 'seed', 'seed');

INSERT INTO operacao_fiscal (nome, descricao_na_nf, cfop_id, observacao, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT 'Venda a consumidor final (SP)', 'Venda de mercadoria', c.id, 'Operação dentro do estado', 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM cfop c WHERE c.codigo = 5405;

INSERT INTO operacao_fiscal (nome, descricao_na_nf, cfop_id, observacao, situacao, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT 'Venda a consumidor final (interestadual)', 'Venda de mercadoria', c.id, 'Operação interestadual', 'A', NOW(6), NOW(6), 'seed', 'seed'
FROM cfop c WHERE c.codigo = 6102;

INSERT INTO configuracao_tributaria (grupo_tributario_id, operacao_fiscal_id, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT g.id, o.id, NOW(6), NOW(6), 'seed', 'seed'
FROM grupo_tributario g, operacao_fiscal o
WHERE g.nome = 'Revenda sujeita a ICMS-ST' AND o.nome = 'Venda a consumidor final (SP)';

INSERT INTO configuracao_tributaria (grupo_tributario_id, operacao_fiscal_id, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT g.id, o.id, NOW(6), NOW(6), 'seed', 'seed'
FROM grupo_tributario g, operacao_fiscal o
WHERE g.nome = 'Revenda sujeita a ICMS-ST' AND o.nome = 'Venda a consumidor final (interestadual)';

INSERT INTO icms_configuracao (configuracao_tributaria_id, uf_destino, cst_icms, modalidade_base_calculo, aliquota, mva, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT ct.id, 'SP', 'CST_60', 'VALOR_OPERACAO', 18.0000, NULL, NOW(6), NOW(6), 'seed', 'seed'
FROM configuracao_tributaria ct
JOIN grupo_tributario g ON g.id = ct.grupo_tributario_id
JOIN operacao_fiscal o ON o.id = ct.operacao_fiscal_id
WHERE g.nome = 'Revenda sujeita a ICMS-ST' AND o.nome = 'Venda a consumidor final (SP)';

INSERT INTO icms_configuracao (configuracao_tributaria_id, uf_destino, cst_icms, modalidade_base_calculo, aliquota, modalidade_base_calculo_st, mva, aliquota_interna_st, aliquota_interestadual_st, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT ct.id, 'MG', 'CST_10', 'VALOR_OPERACAO', 12.0000, 'MARGEM_VALOR_AGREGADO', 40.0000, 18.0000, 12.0000, NOW(6), NOW(6), 'seed', 'seed'
FROM configuracao_tributaria ct
JOIN grupo_tributario g ON g.id = ct.grupo_tributario_id
JOIN operacao_fiscal o ON o.id = ct.operacao_fiscal_id
WHERE g.nome = 'Revenda sujeita a ICMS-ST' AND o.nome = 'Venda a consumidor final (interestadual)';

INSERT INTO pis_configuracao (configuracao_tributaria_id, cst, modalidade_base_calculo, aliquota_percentual, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT ct.id, 'CST_01', 'PERCENTUAL', 1.6500, NOW(6), NOW(6), 'seed', 'seed'
FROM configuracao_tributaria ct
JOIN grupo_tributario g ON g.id = ct.grupo_tributario_id
JOIN operacao_fiscal o ON o.id = ct.operacao_fiscal_id
WHERE g.nome = 'Revenda sujeita a ICMS-ST' AND o.nome = 'Venda a consumidor final (SP)';

INSERT INTO cofins_configuracao (configuracao_tributaria_id, cst, modalidade_base_calculo, aliquota_percentual, data_criacao, data_atualizacao, criado_por, atualizado_por)
SELECT ct.id, 'CST_01', 'PERCENTUAL', 7.6000, NOW(6), NOW(6), 'seed', 'seed'
FROM configuracao_tributaria ct
JOIN grupo_tributario g ON g.id = ct.grupo_tributario_id
JOIN operacao_fiscal o ON o.id = ct.operacao_fiscal_id
WHERE g.nome = 'Revenda sujeita a ICMS-ST' AND o.nome = 'Venda a consumidor final (SP)';
