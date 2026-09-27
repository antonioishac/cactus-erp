-- Módulo Contábil e Fiscal (Fase 16) — Manifesto Eletrônico de Documentos Fiscais
-- (MDF-e, modelo 58), agrupando N CT-e já AUTORIZADOS (cte_cabecalho, Fase 11) numa
-- única viagem, via ACBr Monitor. Ver Javadoc de MdfeCabecalho/MdfeIniBuilder e
-- CLAUDE.md pro detalhamento completo do que ficou fora de escopo (só modal
-- Rodoviário, só viagem intra-UF — mesma simplificação já em vigor pro CT-e desta
-- sessão — sem encerramento/cancelamento/complementar).
--
-- empresa_id é referência SOLTA pro cadastro-base-service, mesmo padrão do monorepo.
-- cte_cabecalho_id É FK de banco de verdade (mesmo schema/serviço que CteCabecalho).

CREATE TABLE mdfe_cabecalho (
    id                                  BIGINT        NOT NULL AUTO_INCREMENT,
    empresa_id                          BIGINT        NOT NULL,
    modelo                               VARCHAR(2)    NOT NULL DEFAULT '58',
    modal                                VARCHAR(2)    NOT NULL DEFAULT '01' COMMENT '01=Rodoviário — único modal suportado nesta fase',
    serie                                VARCHAR(3)    NULL,
    numero                               BIGINT        NULL,
    chave_acesso                         VARCHAR(44)   NULL,
    protocolo_autorizacao                VARCHAR(30)   NULL,
    data_emissao                         DATETIME(6)   NULL,
    ambiente                             VARCHAR(15)   NULL COMMENT 'PRODUCAO | HOMOLOGACAO',
    uf                                   CHAR(2)       NOT NULL COMMENT 'UF de início E fim da viagem — só intra-estadual suportado nesta fase (mesma simplificação do CT-e/Fase 11: só CFOP interno)',
    rntrc                                VARCHAR(8)    NULL,
    veiculo_placa                        VARCHAR(7)    NULL,
    veiculo_uf                           CHAR(2)       NULL,
    veiculo_renavam                      VARCHAR(11)   NULL,
    veiculo_tara_kg                      DECIMAL(18,6) NULL,
    veiculo_capacidade_kg                DECIMAL(18,6) NULL,
    condutor_nome                        VARCHAR(60)   NULL,
    condutor_cpf                         VARCHAR(11)   NULL,
    municipio_carregamento_codigo_ibge   INT           NULL,
    municipio_carregamento_nome          VARCHAR(60)   NULL,
    data_inicio_viagem                   DATE          NULL,
    valor_total_carga                    DECIMAL(18,6) NULL,
    peso_bruto_total                     DECIMAL(18,6) NULL,
    unidade_medida_carga                 VARCHAR(2)    NOT NULL DEFAULT '01' COMMENT '01=KG; 02=TON',
    situacao                             VARCHAR(15)   NOT NULL COMMENT 'RASCUNHO | ENVIADA | AUTORIZADA | REJEITADA | ERRO | CANCELADA',
    mensagem_sefaz                       VARCHAR(500)  NULL,
    data_criacao                         DATETIME(6)   NOT NULL,
    data_atualizacao                     DATETIME(6)   NOT NULL,
    criado_por                           VARCHAR(100)  NULL,
    atualizado_por                       VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    KEY idx_mdfe_empresa (empresa_id),
    KEY idx_mdfe_situacao (situacao)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE mdfe_documento_vinculado (
    id                                BIGINT        NOT NULL AUTO_INCREMENT,
    mdfe_cabecalho_id                 BIGINT        NOT NULL,
    cte_cabecalho_id                  BIGINT        NOT NULL,
    municipio_descarga_codigo_ibge    INT           NULL,
    municipio_descarga_nome           VARCHAR(60)   NULL,
    data_criacao                      DATETIME(6)   NOT NULL,
    data_atualizacao                  DATETIME(6)   NOT NULL,
    criado_por                        VARCHAR(100)  NULL,
    atualizado_por                    VARCHAR(100)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_mdfe_documento_cte (cte_cabecalho_id) COMMENT 'Um CT-e só pode estar vinculado a um MDF-e por vez',
    KEY idx_mdfe_documento_cabecalho (mdfe_cabecalho_id),
    CONSTRAINT fk_mdfe_documento_cabecalho FOREIGN KEY (mdfe_cabecalho_id) REFERENCES mdfe_cabecalho (id),
    CONSTRAINT fk_mdfe_documento_cte FOREIGN KEY (cte_cabecalho_id) REFERENCES cte_cabecalho (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
